/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import buffer.DataBuffer;
import buffer.DataReference;
import dispatch.AudioAvailableEvent;
import dispatch.AudioListener;
import dispatch.EventDispatcher;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author mjbrooks
 */
public class BasicAudioFileInput extends AbstractComponent implements AudioInput {

    private AudioInputStream inputStream;
    private AudioFormat format;
    private AudioReaderThread readThread;
    private EventDispatcher<AudioAvailableEvent> eventSystem;

    private String filename;

    public BasicAudioFileInput(String filename) {
        this.filename = filename;
    }
    
    @Override
    public void initialize(){
        
        File inputFile = new File(filename);

        try {
            inputStream = AudioSystem.getAudioInputStream(inputFile);
        } catch (IOException e) {
            ErrorHandler.pushWarning(e, "Could not open the audio file.");
            return;
        } catch (UnsupportedAudioFileException e) {
            ErrorHandler.pushWarning(e, "The audio file format is unsupported.");
            return;
        }
        
        format = inputStream.getFormat();
        getAudioComponents().setAudioInputFormat(format);

        eventSystem = new EventDispatcher<AudioAvailableEvent>(Executors.newFixedThreadPool(2));

        readThread = new AudioReaderThread(format, inputStream, getAudioComponents().getAudioChunkSize());

        this.setInitialized(true);
    }

    @Override
    public void start() {
        readThread.start();
    }

    @Override
    public void stop() {

        readThread.setRunning(false);

        try {
            readThread.join();
        } catch (InterruptedException ex) {
            ErrorHandler.pushWarning(ex, "Error waiting for input thread to die.");
        }

        eventSystem.shutdown();
    }

    public double samplesToTime(long samples) {
        return samples / format.getSampleRate();
    }

    public double timeToSamples(double time) {
        return time * format.getSampleRate();
    }

    public void addListener(AudioListener<AudioAvailableEvent> listener) {
        eventSystem.addListener(listener);
    }

    private void notifyListeners(DataReference<Double> ref) {
        ref.shareInstances(eventSystem.getListenerCount());

        AudioAvailableEvent event = new AudioAvailableEvent(ref);
        eventSystem.fireEvent(event);
    }

    public long samplesToNanoTime(long samples) {
        return (long)((samples * 1000000000L) / format.getSampleRate());
    }


    class AudioReaderThread extends ComponentThread {

        private AudioFormat format;
        private AudioInputStream inputStream;
        private int chunkSize;
        private DataBuffer<Double> dataBuffer;

        public AudioReaderThread(AudioFormat format, AudioInputStream stream, int chunkSize) {
            super("AudioReaderThread");

            this.format = format;
            this.inputStream = stream;
            this.chunkSize = chunkSize;
            dataBuffer = new DataBuffer<Double>(chunkSize, 10);
        }


        @Override
        public void run() {
            int numBytes = AudioConverter.getBytes(format, chunkSize);
            int sampleTime = 0;
            byte[] buffer = new byte[numBytes];
            int nFrameSize = format.getFrameSize();
            long bufferMillis = (long)(1 * samplesToNanoTime(chunkSize) / 1000000);

            while (isRunning()) {

                try {
                    inputStream.read(buffer, 0, buffer.length);
                } catch (IOException ex) {
                    ErrorHandler.pushError(ex, "Failure to read audio.");
                    setRunning(false);
                    continue;
                } 

                DataReference<Double> ref = dataBuffer.allocate();
                ref.setUserData((Integer)sampleTime);
                ref.enable();
                AudioConverter.convertToDouble(format, buffer, ref);

                sampleTime += chunkSize;

                synchronized(ref) {
                    notifyListeners(ref);

                    try {
                        ref.wait();
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        }
    }

}

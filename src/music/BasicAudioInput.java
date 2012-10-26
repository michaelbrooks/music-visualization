/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */
package music;

import dispatch.AudioListener;
import buffer.DataBuffer;
import buffer.DataReference;
import dispatch.AudioAvailableEvent;
import dispatch.EventDispatcher;
import java.io.IOException;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author michael
 */
class BasicAudioInput extends AbstractComponent implements AudioInput {

    public BasicAudioInput() {
    }

    private TargetDataLine dataLine;
    private AudioInputStream inputStream;
    private AudioFormat format;
    private AudioReaderThread readThread;
    private EventDispatcher<AudioAvailableEvent> eventSystem;
    
    @Override
    public void initialize(){
        
        format = getAudioComponents().getAudioInputFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format, getAudioComponents().getAudioChunkSize() * 8);

        dataLine = null;

        try {
            dataLine = (TargetDataLine)AudioSystem.getLine(info);
            
            dataLine.open(format);
        }
        catch (LineUnavailableException e)
        {
            ErrorHandler.pushWarning(e, "Could not get data line for BasicAudioInput");
            return;
        }

        inputStream = new AudioInputStream(dataLine);

        eventSystem = new EventDispatcher<AudioAvailableEvent>(Executors.newFixedThreadPool(1));

        readThread = new AudioReaderThread(format, inputStream, getAudioComponents().getAudioChunkSize());

        this.setInitialized(true);
    }

    @Override
    public void start() {

        dataLine.start();

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

        dataLine.stop();
        dataLine.close();
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
            int newBytes = 3 * numBytes / 4;
            int oldBytes = numBytes - newBytes;
            byte[] oldbuffer = new byte[oldBytes];
            byte[] newbuffer = new byte[newBytes];
            byte[] exportbuffer = new byte[numBytes];
            int nFrameSize = format.getFrameSize();
            
            while (isRunning()) {
                
                try {
                    inputStream.read(newbuffer, 0, newbuffer.length);
                } catch (IOException ex) {
                    ErrorHandler.pushError(ex, "Failure to read audio.");
                    setRunning(false);
                    continue;
                }
                
//                long time0 = System.nanoTime();
//                System.out.println("BasicAudioInput, audio thread, publishing, " + time0);

                System.arraycopy(oldbuffer, 0, exportbuffer, 0, oldBytes);
                System.arraycopy(newbuffer, 0, exportbuffer, oldBytes, newBytes);
                System.arraycopy(newbuffer, newBytes - oldBytes, oldbuffer, 0, oldBytes);
                
                DataReference<Double> ref = dataBuffer.allocate();
                ref.setUserData((Integer)sampleTime);
                ref.enable();
                AudioConverter.convertToDouble(format, exportbuffer, ref);

                sampleTime += chunkSize;

                notifyListeners(ref);
            }
        }
    }
}
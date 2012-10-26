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
import java.util.Random;
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
class AudioGenerator extends AbstractComponent implements AudioInput {

    public AudioGenerator() {
    }

    private TargetDataLine dataLine;
    private AudioInputStream inputStream;
    private AudioFormat format;
    private AudioGeneratorThread readThread;
    private EventDispatcher<AudioAvailableEvent> eventSystem;

    @Override
    public void initialize(){

        format = getAudioComponents().getAudioInputFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
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

        readThread = new AudioGeneratorThread(format, inputStream, getAudioComponents().getAudioChunkSize());

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

    class AudioGeneratorThread extends ComponentThread {

        private AudioFormat format;
        private AudioInputStream inputStream;
        private int chunkSize;
        private DataBuffer<Double> dataBuffer;

        public AudioGeneratorThread(AudioFormat format, AudioInputStream stream, int chunkSize) {
            super("AudioGeneratorThread");

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

            Random random = new Random();

            int lastIndex = 0;
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

                double baseFreq = 400;
                double phaseShiftFreq = 0.2;
                double noiseFreq = 0.06;

                double value;
                double freq;
                for (int i = 0; i < chunkSize; i++) {
                    lastIndex++;
                    //value = 0.4 * getOscFrequency(lastIndex, phaseShiftFreq, baseFreq);
                    value = 0.5 * getOsc(lastIndex, baseFreq);
                    //A slow oscillating random component
                    value += (random.nextDouble() - 0.5) * .1;
                            //0.5 * getOsc(lastIndex, noiseFreq);
                    ref.set(i, value);
                }

                sampleTime += chunkSize;

                notifyListeners(ref);
            }
        }

        private double getOsc(int sample, double frequency) {
            double t = 2 * Math.PI * sample / format.getSampleRate();
            return Math.sin(frequency * t);
        }

        private double getOscFrequency(int sample, double phaseShiftFreq, double baseFreq) {
            double t = 2 * Math.PI * sample / format.getSampleRate();
            return Math.sin(baseFreq * (t + 4 * phaseShiftFreq * Math.sin(phaseShiftFreq * t)));
        }
    }
}
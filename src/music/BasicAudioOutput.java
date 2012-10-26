/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import buffer.DataReference;
import dispatch.AudioAvailableEvent;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import music.data.Wav;

/**
 *
 * @author michael
 */
class BasicAudioOutput extends AbstractComponent implements AudioOutput {

    private AudioFormat format;
    private SourceDataLine dataLine;

    public BasicAudioOutput() {
    }

    @Override
    public void initialize() {

        format = getAudioComponents().getAudioOutputFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        dataLine = null;

        try {
            dataLine = (SourceDataLine)AudioSystem.getLine(info);
            dataLine.open(format);
        }
        catch (LineUnavailableException e)
        {
            ErrorHandler.pushWarning(e, "Could not get data line for BasicAudioOutput");
            return;
        }
    }

    @Override
    public void start()
    {
        dataLine.start();
    }

    public synchronized void play(Wav wav) {
        System.out.println("playback, " + System.nanoTime());
        byte[] buffer = new byte[AudioConverter.getBytes(format, wav.size())];
        AudioConverter.convertToBytes(format, wav.getRawData(), buffer);
        dataLine.write(buffer, 0, buffer.length);
    }

    public synchronized void eventFired(AudioAvailableEvent event, Object userData) {
        DataReference<Double> ref = event.getAudioData();
        byte[] buffer = new byte[AudioConverter.getBytes(format, ref.size())];
        AudioConverter.convertToBytes(format, ref, buffer);
        dataLine.write(buffer, 0, buffer.length);
        ref.returnInstance();
    }
}
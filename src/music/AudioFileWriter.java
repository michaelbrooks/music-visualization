/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import buffer.DataReference;
import dispatch.AudioAvailableEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import music.data.Wav;

/**
 *
 * @author michael
 */
public class AudioFileWriter extends AbstractComponent implements AudioOutput {

    private AudioFormat format;
    private File tempFile;
    private BufferedOutputStream tempStream;
    private File outputFile;
    private AudioFileFormat.Type fileFormat;
    private int numBytesRead;
    
    @Override
    public void initialize() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateTime = sdf.format(cal.getTime());

        tempFile = new File("output." + dateTime + ".wav.temp");
        
        try {
            tempStream = new BufferedOutputStream(new FileOutputStream(tempFile));
        } catch (FileNotFoundException e) {
            ErrorHandler.pushWarning(e, "Could not write to temp file.");
        }
        
        outputFile = new File("output." + dateTime + ".wav");

        format = getAudioComponents().getAudioOutputFormat();
        fileFormat = AudioFileFormat.Type.WAVE;

        super.initialize();
    }


    @Override
    public void destroy() {

        //Close the temp stream
        try {
            tempStream.close();
        } catch (IOException e) {
            ErrorHandler.pushWarning(e, "Could not close the temporary file.");
        }

        //Read from the temp stream
        BufferedInputStream tempInput = null;
        try {
            tempInput = new BufferedInputStream(new FileInputStream(tempFile));
        } catch (FileNotFoundException e) {
            ErrorHandler.pushError(e, "Could not read from temp file.");
            return;
        }
        
        //Convert to an audio input stream
        AudioInputStream audioIn = new AudioInputStream(tempInput, format, numBytesRead / format.getFrameSize());

        try {
            AudioSystem.write(audioIn, fileFormat, outputFile);
        }
        catch (IOException e) {
            ErrorHandler.pushWarning(e, "Could not read data from the temp file.");
            return;
        }

        try {
            audioIn.close();
            tempInput.close();
        } catch (IOException e) {
            ErrorHandler.pushWarning(e, "Could not close the STREAMS!!!.");
            return;
        }

        tempFile.delete();
    }

    public void play(Wav wav) {
        byte[] data = new byte[AudioConverter.getBytes(format, wav.size())];
        AudioConverter.convertToBytes(format, wav.getRawData(), data);
        try {
            tempStream.write(data, 0, 3 * data.length / 4);
            numBytesRead += data.length;
        } catch (IOException ex) {
            ErrorHandler.pushError(ex, "Could not write to temp file.");
        }

    }

    public void eventFired(AudioAvailableEvent event, Object userData) {
        DataReference<Double> ref = event.getAudioData();

        byte[] data = new byte[AudioConverter.getBytes(format, ref.size())];
        AudioConverter.convertToBytes(format, ref, data);
        try {
            tempStream.write(data, 0, 3 * data.length / 4);
            numBytesRead += data.length;
        } catch (IOException ex) {
            ErrorHandler.pushError(ex, "Could not write to temp file.");
        }

        ref.returnInstance();
    }

}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */
package music;

import javax.sound.sampled.AudioFormat;

/**
 *
 * @author michael
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
        String fname = null;
        if (args.length > 0) {
            fname = args[0];
        }
        //fname = "samples/combo.wav";
        
        //Create all the components
        Recorder recorder = new QueueRecorder();
        Engine engine = new BasicEngine();
        AudioInput audioSource;
        if (fname != null) {
            audioSource = new BasicAudioFileInput(fname);
        } else {
            audioSource = new BasicAudioInput();
        }
        AudioOutput audioOut = new AudioFileWriter();
        GUIManager gui = new InterviewGUIManager();
        NoiseFilter noise = new SimpleNoiseFilter();
        
        //Configure the components
        AudioFormat audioInputFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0F, 16, 1, 2, 44100.0F, false);
        AudioFormat audioOutputFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0F, 16, 1, 2, 44100.0F, false);

        AudioComponents components = new AudioComponents();
        components.setAudioChunkSize(1024);
        components.setAudioInputFormat(audioInputFormat);
        components.setAudioOutputFormat(audioOutputFormat);
        components.setRecorder(recorder);
        components.setAudioInput(audioSource);
        components.setEngine(engine);
        components.setGUIManager(gui);
        components.setAudioOutput(audioOut);
        components.setNoiseFilter(noise);

        noise.setEnabled(false);
        
        //Run the components

        components.initialize();

        components.start();

    }
}

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
public class AudioComponents {

    private static AudioComponents instance;
    
    public static AudioComponents getInstance() {
        return instance;
    }

    public AudioComponents() {
        instance = this;
    }

    private Recorder recorder;

    void setRecorder(Recorder recorder) {
        this.recorder = recorder;
        this.recorder.setAudioComponents(this);
    }

    public Recorder getRecorder() {
        return recorder;
    }

    private AudioOutput audioOut;

    void setAudioOutput(AudioOutput audioOut) {
        this.audioOut = audioOut;
        this.audioOut.setAudioComponents(this);
    }

    AudioOutput getAudioOutput() {
        return audioOut;
    }
    private GUIManager guiManager;

    void setGUIManager(GUIManager gui) {
        this.guiManager = gui;
        this.guiManager.setAudioComponents(this);
    }

    GUIManager getGUIManager() {
        return guiManager;
    }
    private Engine engine;

    void setEngine(Engine engine) {
        this.engine = engine;
        this.engine.setAudioComponents(this);
    }

    Engine getEngine() {
        return engine;
    }
    private AudioInput audioInput;

    void setAudioInput(AudioInput audioInput) {
        this.audioInput = audioInput;
        this.audioInput.setAudioComponents(this);
    }

    AudioInput getAudioInput() {
        return audioInput;
    }

    private NoiseFilter noiseFilter;

    void setNoiseFilter(NoiseFilter noiseFilter) {
        this.noiseFilter = noiseFilter;
        this.noiseFilter.setAudioComponents(this);
    }

    NoiseFilter getNoiseFilter() {
        return noiseFilter;
    }

    void initialize() {
        this.recorder.initialize();
        this.audioInput.initialize();
        this.engine.initialize();
        this.guiManager.initialize();
        this.audioOut.initialize();
        this.noiseFilter.initialize();

        this.audioInput.addListener(engine);
    }

    void destroy()
    {
        this.audioInput.destroy();
        this.engine.destroy();
        this.guiManager.destroy();
        this.audioOut.destroy();
        this.recorder.destroy();
        this.noiseFilter.destroy();
    }

    void start() {
        getRecorder().start();
        getAudioInput().start();
        getEngine().start();
        getGUIManager().start();
        getAudioOutput().start();
        getNoiseFilter().destroy();
    }

    void stop() {
        this.audioInput.stop();
        this.engine.stop();
        this.guiManager.stop();
        this.audioOut.stop();
        this.recorder.stop();
        this.noiseFilter.stop();
    }

    private AudioFormat audioInputFormat;
    private AudioFormat audioOutputFormat;

    void setAudioInputFormat(AudioFormat audioInputFormat) {
        this.audioInputFormat = audioInputFormat;
    }
    void setAudioOutputFormat(AudioFormat audioOutputFormat) {
        this.audioOutputFormat = audioOutputFormat;
    }

    AudioFormat getAudioInputFormat() {
        return audioInputFormat;
    }

    private int audioChunkSize;

    public int getAudioChunkSize() {
        return audioChunkSize;
    }

    public void setAudioChunkSize(int size) {
        this.audioChunkSize = size;
    }

    AudioFormat getAudioOutputFormat() {
        return audioOutputFormat;
    }
}

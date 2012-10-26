/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

/**
 *
 * @author michael
 */
public class AudioChunk {
    private double[] wav;
    private long nanoTime;
    private long sampleTime;

    public AudioChunk(double[] wav, long nanoTime, long startSampleTime)
    {
        this.wav = wav;
        this.nanoTime = nanoTime;
        this.sampleTime = startSampleTime;
    }

    public double[] getData(){
        return wav;
    }

    public long getNanoTime()
    {
        return nanoTime;
    }

    public long getSampleTime()
    {
        return sampleTime;
    }

    public int size() {
        return wav.length;
    }
}

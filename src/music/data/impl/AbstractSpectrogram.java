/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import java.util.Arrays;
import music.data.Spectrogram;

/**
 *
 * @author michael
 */
public abstract class AbstractSpectrogram implements Spectrogram {

    private double sampleRate;
    private int size;
    private int n;
    
    public AbstractSpectrogram(int n, int size, double sampleRate) {
        this.n = n;
        this.size = size;
        this.sampleRate = sampleRate;
    }

    protected boolean isValidBin(int bin) {
        return bin >= 0 && bin < size();
    }

    public double getFrequency(double bin) {
        return (double) bin * getSampleRate() / getN();
    }

    public double getBin(double frequency) {
        return getN() * frequency / getSampleRate();
    }

    public int getN() {
        return n;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.toString(getRawData());
    }

}

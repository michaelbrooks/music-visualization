/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import java.util.Arrays;
import music.data.Correlogram;

/**
 *
 * @author michael
 */
public abstract class AbstractCorrelogram implements Correlogram {

    private int size;
    private double sampleRate;
    private int n;

    protected AbstractCorrelogram(int n, int size, double sampleRate) {
        this.size = size;
        this.n = n;
        this.sampleRate = sampleRate;
    }

    public double getFrequency(double bin) {
        return 1.0 / getPeriod(bin);
    }

    public double getBinForFrequency(double frequency) {
        return getSampleRate() / frequency;
    }

    public int getN() {
        return n;
    }

    public double getPeriod(double bin) {
        return (bin / getSampleRate());
    }

    public double getSampleRate() {
        return sampleRate;
    }

    protected boolean isValidBin(int bin) {
        return bin >= 0 && bin < size();
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.toString(getRawData());
    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.FFT;

/**
 *
 * @author michael
 */
public class jtFFT extends AbstractSpectrogram implements FFT {

    private double[] fftData;

    protected jtFFT(int n, double sampleRate, double[] fftData) {
        super(n, fftData.length / 2, sampleRate);
        this.fftData = fftData;
    }

    public double getReal(int bin) {
        if (!isValidBin(bin))
            throw new IndexOutOfBoundsException();
        return fftData[2 * bin];
    }

    public double getImaginary(int bin) {
        if (!isValidBin(bin))
            throw new IndexOutOfBoundsException();
        return fftData[2 * bin + 1];
    }

    public double getDC() {
        return getReal(0);
    }

    public double[] getRawData() {
        return fftData;
    }

}

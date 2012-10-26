/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.PS;

/**
 *
 * @author michael
 */
public class DefaultPowerSpectrum extends AbstractSpectrogram implements PS {

    private double[] spectrum;
    
    protected DefaultPowerSpectrum(int n, double sampleRate, double[] spectrum) {
        super(n, spectrum.length, sampleRate);
        this.spectrum = spectrum;
    }

    public double getPower(int bin) {
        if (!isValidBin(bin))
            throw new IndexOutOfBoundsException();
        return spectrum[bin];
    }

    public double[] getRawData() {
        return spectrum;
    }

    
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.ACF;
import music.data.WACF;

/**
 *
 * @author michael
 */
public class DefaultACF extends AbstractCorrelogram implements WACF, ACF {

    private double[] acf;

    /**
     * Construct a new WACF.
     * @param n The number of samples over which the acf was taken.
     * @param sampleRate The sample rate of the audio.
     * @param acf The resulting correlation values.
     */
    protected DefaultACF(int n, double sampleRate, double[] acf) {
        super(n, acf.length, sampleRate);
        this.acf = acf;
    }
    
    public double getValue(int bin) {
        if (!isValidBin(bin))
            throw new IndexOutOfBoundsException();

        return acf[bin];
    }

    public double[] getRawData() {
        return acf;
    }

}

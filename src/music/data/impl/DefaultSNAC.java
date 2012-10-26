/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.SNAC;
import music.data.WSNAC;

/**
 *
 * @author michael
 */
public class DefaultSNAC extends AbstractCorrelogram implements WSNAC, SNAC {

    private double[] snac;

    /**
     * Construct a new SNAC.
     * @param n The number of samples over which the snac was taken.
     * @param sampleRate The sample rate of the audio.
     * @param snac The resulting correlation values.
     */
    protected DefaultSNAC(int n, double sampleRate, double[] snac) {
        super(n, snac.length, sampleRate);
        this.snac = snac;
    }
    
    public double getValue(int bin) {
        if (!isValidBin(bin))
            throw new IndexOutOfBoundsException();

        return snac[bin];
    }

    public double[] getRawData() {
        return snac;
    }

}

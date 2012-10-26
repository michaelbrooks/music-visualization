/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.AMDF;

/**
 *
 * @author michael
 */
public class DefaultAMDF extends AbstractCorrelogram implements AMDF {

    private double[] amdf;

    protected DefaultAMDF(int n, double sampleRate, double[] amdf) {
        super(n, amdf.length, sampleRate);
        this.amdf = amdf;
    }

    public double getValue(int bin) {
        if (!isValidBin(bin))
            throw new IndexOutOfBoundsException();

        return amdf[bin];
    }

    public double[] getRawData() {
        return amdf;
    }
}

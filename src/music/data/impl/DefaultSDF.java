/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.SDF;

/**
 *
 * @author michael
 */
class DefaultSDF extends AbstractCorrelogram implements SDF {

    private double[] sdf;

    public DefaultSDF(int n, double sampleRate, double[] sdfData) {
        super(n, sdfData.length, sampleRate);

        this.sdf = sdfData;
    }

    public double[] getRawData() {
        return sdf;
    }

    public double getValue(int bin) {
        if (!isValidBin(bin))
            throw new IndexOutOfBoundsException();
        return sdf[bin];
    }

}

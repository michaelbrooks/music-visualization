/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

import java.io.Serializable;

/**
 * Describes a generic lag-space correlation function, such as ACF or AMDF.
 * @author michael
 */
public interface Correlogram extends Serializable {

    /**
     * Gets the frequency associated with the given period bin.
     * @param bin
     * @return
     */
    public abstract double getFrequency(double bin);
    
    /**
     * Gets the number of samples over which the correllogram was taken.
     * @return
     */
    int getN();

    /**
     * Gets the period associated with the given period bin.
     * @param bin
     * @return
     */
    public abstract double getPeriod(double bin);

    /**
     * Gets the raw correllogram data.
     * Do not modify.
     * Use with caution.
     * @return
     */
    double[] getRawData();

    /**
     * Gets the sample rate of the original audio
     * @return
     */
    double getSampleRate();

    /**
     * Gets the correllation of the audio at the given period shift bin.
     * @param bin
     * @return
     */
    double getValue(int bin);

    /**
     * Gets the number of period bins
     * @return
     */
    int size();

    /**
     * Gets the bin for the given frequency.
     * @param harmonic
     * @return
     */
    public double getBinForFrequency(double frequency);

}

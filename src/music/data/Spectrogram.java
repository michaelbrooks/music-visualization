/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

/**
 *
 * @author michael
 */
public interface Spectrogram {

    /**
     * Gets the frequency represented by the given bin.
     * @param bin
     * @return
     */
    public abstract double getFrequency(double bin);

    /**
     * Gets the bin for the given frequency.
     * @param frequency
     * @return
     */
    double getBin(double frequency);

    /**
     * Gets the number of samples over which the spectrum was taken.
     * @return
     */
    int getN();

    /**
     * Gets the raw power data.
     * Do not modify.
     * Use with caution.
     * @return
     */
    double[] getRawData();

    /**
     * Gets the sample rate of the input
     * @return
     */
    double getSampleRate();

    /**
     * Gets the number of bins in the spectrum.
     * @return
     */
    int size();

}

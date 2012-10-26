/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

/**
 * Describes a power spectrum.
 * The power spectrum is a fully real-valued
 * spectrum, where each value is calculated
 * from the respective component of the FFT:
 * p_i = sqrt(re_i * re_i + im_i + im_i)
 * 
 * @author michael
 */
public interface PS extends Spectrogram {

    /**
     * Gets the power of the given component
     * @param bin
     * @return
     */
    double getPower(int bin);
}

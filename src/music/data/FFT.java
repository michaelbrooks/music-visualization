/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

import java.io.Serializable;

/**
 * Represents a Fourier transform of sampled audio.
 * The input to the FFT is real, and the output is complex.
 * Because of this, the output's 0th component is
 * only real (called the DC value) as is
 * the component representing the Nyquist frequency
 * (bin N/2 where N is the number of input samples,
 * or bin M where M is the number of FFT bins).
 * All other components may have real and imaginary parts.
 * If there are N input samples, there will be N/2 + 1 output
 * components.
 * @author michael
 */
public interface FFT extends Spectrogram {

    /**
     * Gets the real part of the given component
     * @param bin
     * @return
     */
    double getReal(int bin);

    /**
     * Gets the imaginary part of the given component
     * @param bin
     * @return
     */
    double getImaginary(int bin);

    /**
     * Gets the number of bins in the FFT.
     * @return
     */
    int size();

    /**
     * Gets the number of samples over which the FFT was taken.
     * @return
     */
    int getN();

    /**
     * Gets the frequency represented by the given bin.
     * @param bin
     * @return
     */
    public abstract double getFrequency(double bin);

    /**
     * Gets the sample rate of the input
     * @return
     */
    double getSampleRate();

    /**
     * Gets the DC component.
     * Equivalent to getReal(0);
     * @return
     */
    double getDC();

    /**
     * Gets the raw FFT data.
     * Do not modify.
     * Use with caution.
     * @return
     */
    double[] getRawData();
}

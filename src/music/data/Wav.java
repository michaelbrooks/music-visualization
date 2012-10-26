/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

import java.io.Serializable;

/**
 * Represents a segment of sampled audio.
 * Samples are mono, double, and between -1 and 1 (inclusive).
 * @author michael
 */
public interface Wav extends Serializable {

    /**
     * Gets the amplitude of the given sample.
     * @param sample
     * @return
     */
    double getSample(int sample);

    /**
     * Gets the sample rate of the audio in samples per second.
     * @return
     */
    double getSampleRate();

    /**
     * Gets the number of samples in this audio segment.
     * @return
     */
    int size();

    /**
     * Gets the duration of the audio in seconds.
     * @return
     */
    double duration();

    /**
     * Gets the raw data for this wav.
     * Use carefully, and do not alter.
     * @return
     */
    double[] getRawData();

    /**
     * Gets the windowed raw data for this wav.
     * Use carefully, and do not modify.
     * @return
     */
    double[] getWindowedData();
    
}

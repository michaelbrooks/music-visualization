/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

import java.io.Serializable;

/**
 *
 * @author michael
 */
public interface DataFrame extends Serializable, Comparable<Long> {

    /**
     * Gets the id of the session to which this frame belongs.
     * @return
     */
    long getSessionID();

    /**
     * Gets the time of the frame in nanoseconds,
     * relative to the start of the session.
     * @return
     */
    long getTime();

    /**
     * Gets the raw sampled audio associated with this data.
     * @return
     */
    Wav getWav();

    /**
     * Gets an object that describes pitch info about the frame.
     * @return
     */
    PitchInfo getPitchInfo();

    /**
     * Gets an object that describes quality info about the frame.
     * @return
     */
    QualityInfo getQualityInfo();

    /**
     * Gets an object describing properties of the sound for this frame.
     * @return
     */
    SM getSM();

    /**
     * Gets the power spectrum of this audio.
     * @return
     */
    PS getPS();

    /**
     * Gets the power spectrum of the windowed audio.
     * @return
     */
    PS getWPS();

    /**
     * Gets the Fourier transform of the sampled audio.
     * @return
     */
    FFT getFFT();

    /**
     * Gets the Fourier transform of the windowed audio.
     * @return
     */
    FFT getWFFT();

    /**
     * Gets the unwindowed autocorrelation function of the sampled audio.
     * @return
     */
    WACF getWACF();

    /**
     * Gets the unwindowed autocorrelation function of the sampled audio.
     * @return
     */
    ACF getACF();
    
    /**
     * Gets the unwindowed Special Normalized Autocorrelation of the sampled audio.
     * @return
     */
    SNAC getSNAC();

    /**
     * Gets the Windowed Special Normalized Autocorrelation of the sampled audio.
     * @return
     */
    WSNAC getWSNAC();

    /**
     * Gets the squared difference function of the sampled audio.
     * @return
     */
    SDF getSDF();
    
    /**
     * Gets the average magnitude difference function of the sampled audio.
     * @return
     */
    AMDF getAMDF();

}

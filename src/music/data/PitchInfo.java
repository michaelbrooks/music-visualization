/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

/**
 *
 * @author michael
 */
public interface PitchInfo {
    
    double getPrimaryFrequency();
    double getPrimaryPitch();

    double getSmoothedPitch();
    double getSmoothedFrequency();

    double getIntonation();
    double getIntonation(double[] noteFrequencies);
}

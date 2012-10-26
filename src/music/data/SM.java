/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

/**
 *
 * @author michael
 */
public interface SM {

    double getAverageAmplitude();
    double getPowerDB();
    double getMeanSquaredAmplitude();
    double getMaxAmplitude();
    double getMinAmplitude();
    double getLoudness();

    double getSmoothedAverageAmplitude();
    double getSmoothedPowerDB();
    double getSmoothedMeanSquaredAmplitude();
    double getSmoothedMaxAmplitude();
    double getSmoothedMinAmplitude();
    double getSmoothedLoudness();
    
    boolean isSilence();
}

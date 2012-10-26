/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

/**
 *
 * @author michael
 */
public interface QualityInfo {

    double getPrimaryCorrelation();
    double getSmoothedCorrelation();

    double getSpectralFlux();
    double getSmoothedFlux();

    double getSpectralCentroidFrequency();
    double getSmoothedSpectralCentroidFrequency();
}

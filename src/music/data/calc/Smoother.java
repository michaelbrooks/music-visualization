/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

/**
 *
 * @author michael
 */
public interface Smoother {

    double getValue();

    double newValue(double value);

}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public interface Animated {

    /**
     * Updates the animated object.
     * @param dt The fixed timestep.
     * @param realDt The wall-clock timestep since last updated.
     */
    void update(double dt, double realDt);
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Graphics2D;

/**
 *
 * @author michael
 */
public interface Painter {

    /**
     * Paints this object's data onto the graphics object.
     * @param g
     */
    public void paint(Graphics2D g);
}

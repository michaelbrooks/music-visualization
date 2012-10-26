/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public abstract class ArrayGraphPanel extends GraphPanel {

    public ArrayGraphPanel(String title, int width, int height) {
        super(title, width, height);
    }

    public abstract void graph(double[] data);

}

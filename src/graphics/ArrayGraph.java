/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public abstract class ArrayGraph implements Painter {

    boolean connectPoints = false;

    public ArrayGraph() {
    }

    public abstract void graph(double[] newData);

    public void setConnectPoints(boolean value) {
        this.connectPoints = value;
    }

}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;

/**
 *
 * @author michael
 */
public class ArrayRingGraphPanel extends ArrayGraphPanel {

    ArrayRingGraph graph;

    public ArrayRingGraphPanel(String title, int width, int height)
    {
        super(title, width, height);

        graph = new ArrayRingGraph();
        panel.addPainter(graph);
    }

    public ArrayRingGraph getGraph()
    {
        return graph;
    }

    public void graph(double[] data) {
        graph.graph(data);
        panel.repaint();
    }

    public void graph(double[] data, boolean reverse) {
        graph.graph(data, reverse);
        panel.repaint();
    }
}

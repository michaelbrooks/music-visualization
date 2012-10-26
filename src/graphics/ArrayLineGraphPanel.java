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
public class ArrayLineGraphPanel extends ArrayGraphPanel {

    ArrayLineGraph graph;
    
    public ArrayLineGraphPanel(String title, int width, int height)
    {
        super(title, width, height);
        
        graph = new ArrayLineGraph();
        panel.addPainter(graph);
    }

    public ArrayLineGraph getGraph()
    {
        return graph;
    }

    @Override
    public void graph(double[] data) {
        graph.graph(data);
        panel.repaint();
    }
}

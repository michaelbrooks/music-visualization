/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public class VerticalQualityGraphPanel extends Graph1DPanel{

    VerticalQualityGraph graph;

    public VerticalQualityGraphPanel(String title, int width, int height) {
        super(title, width, height);

        graph = new VerticalQualityGraph();
        panel.addPainter(graph);
    }

    public void graph(double quality) {
        graph.graph(quality);
        panel.repaint();
    }

    public VerticalQualityGraph getGraph() {
        return graph;
    }
}

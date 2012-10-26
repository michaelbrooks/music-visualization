/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public class WeightedColorLineGraphPanel extends GraphPanel
{
    WeightedColorLineGraph graph;

    public WeightedColorLineGraphPanel(int points, String title, int width, int height) {
        super(title, width, height);

        graph = new WeightedColorLineGraph(points);
        panel.addPainter(graph);
    }

    public void graph(double value, double color, double weight, double opacity) {
        graph.graph(value, color, weight, opacity);
    }
    
    public void repaint() {
        panel.repaint();
    }

    public WeightedColorLineGraph getGraph() {
        return graph;
    }
}

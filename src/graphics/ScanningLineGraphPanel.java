/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public class ScanningLineGraphPanel extends Graph1DPanel
{
    ScanningLineGraph graph;

    public ScanningLineGraphPanel(int points, String title, int width, int height) {
        super(title, width, height);

        graph = new ScanningLineGraph(points);
        panel.addPainter(graph);
    }

    @Override
    public void graph(double value) {
        graph.graph(value);
    }
    public void repaint() {
        panel.repaint();
    }

    public ScanningLineGraph getGraph() {
        return graph;
    }
}

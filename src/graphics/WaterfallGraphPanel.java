/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author mjbrooks
 */
public class WaterfallGraphPanel extends GraphPanel {

    WaterfallGraph graph;

    public WaterfallGraphPanel(String title, int width, int height, double rowHeight, double phase) {
        super(title, width, height);

        graph = new WaterfallGraph(rowHeight, phase);
        panel.addPainter(graph);
    }

    public WaterfallGraphPanel(String title, int width, int height) {
        super(title, width, height);

        graph = new WaterfallGraph();
        panel.addPainter(graph);
    }

    public void graph(double[] data, double color) {
        graph.graph(data, color);
    }
    public void repaint() {
        panel.repaint(1);
    }

    public WaterfallGraph getGraph() {
        return graph;
    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public class VerticalPitchGraphPanel extends Graph1DPanel {
    VerticalPitchGraph graph;

    public VerticalPitchGraphPanel(String title, int width, int height) {
        super(title, width, height);

        graph = new VerticalPitchGraph();
        panel.addPainter(graph);
    }

    public void graph(double quality) {
        graph.graph(quality);
        panel.repaint();
    }

    public VerticalPitchGraph getGraph() {
        return graph;
    }

    public void setConfidence(double confidence) {
        graph.setConfidence(confidence);
    }
}

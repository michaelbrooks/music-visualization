/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public class VerticalNoteGraphPanel extends Graph1DPanel {
    VerticalNoteGraph graph;

    public VerticalNoteGraphPanel(String title, int width, int height) {
        super(title, width, height);

        graph = new VerticalNoteGraph();
        panel.addPainter(graph);
    }

    public void graph(double quality) {
        graph.graph(quality);
        panel.repaint();
    }

    public VerticalNoteGraph getGraph() {
        return graph;
    }

    public void setConfidence(double confidence) {
        graph.setConfidence(confidence);
    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author michael
 */
public class AnimatedNGraphPanel extends GraphPanel implements ActionListener {
    AnimatedNGraph graph;
    Timer animator;
    double dt;
    long previousUpdateTime;
    
    public AnimatedNGraphPanel(AnimatedNGraph graph, int animationInterval, String title, int width, int height) {
        super(title, width, height);
        
        this.graph = graph;
        panel.addPainter(graph);

        dt = animationInterval / 1000.0;
        animator = new Timer(animationInterval, this);
    }

    @Override
    public void start() {
        super.start();

        animator.start();
    }

    @Override
    public void stop() {
        super.stop();

        animator.stop();
    }

    public AnimatedNGraph getGraph() {
        return graph;
    }


    public void actionPerformed(ActionEvent e) {
        long time = System.nanoTime();
        double realDt = (time - previousUpdateTime) / 1000000000.0;
        previousUpdateTime = time;
        
        graph.update(dt, realDt);
        panel.repaint();
    }
}

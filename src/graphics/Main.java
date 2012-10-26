/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author michael
 */
public class Main {

    public static void main(String[] args) {

        ArrayLineGraph graph = new ArrayLineGraph();
        graph.setVerticalRange(-1, 1);
        graph.setConnectPoints(true);
        
        ChartPanel panel = new ChartPanel();
        panel.addPainter(graph);
        panel.setPreferredSize(new Dimension(200, 100));
        
        JFrame frame = new JFrame("ArrayGraph");
        frame.add(panel);

        frame.pack();
        frame.setVisible(true);

        PaintManager pm = new PaintManager(panel, 0.5);
        pm.start();

        double times = 0;
        while (true) {

            times += 0.1;
            
            double[] data = new double[100];
            for (int i = 0; i < data.length; i++) {
                data[i] = Math.sin(i / (2 * Math.PI * times));
            }

            graph.graph(data);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author michael
 */
public class ScanningLineGraph implements Painter {

    double bottom = 0;
    double top = 1;
    double[] dataToGraph = new double[1];
    int writeIndex = 0;

    boolean verticalRangeAuto = false;
    boolean connectPoints = false;

    public ScanningLineGraph(int points) {
        this.dataToGraph = new double[points];
    }
    
    public void setVerticalRange(double bottom, double top)
    {
        this.top = top;
        this.bottom = bottom;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public void graph(double newValue) {
        if (verticalRangeAuto) {
            if (top < newValue)
                top = newValue;
            if (bottom > newValue)
                bottom = newValue;
        }
        
        dataToGraph[writeIndex++] = scaleValue(newValue);
        if (writeIndex >= dataToGraph.length)
            writeIndex -= dataToGraph.length;
    }

    private double scaleValue(double value) {
        return (value - bottom) / (top - bottom);
    }

    private Color drawColor = Color.white;
    public Color getColor() {
        return drawColor;
    }

    public void setColor(Color color) {
        this.drawColor = color;
    }

    public void paint(Graphics2D g) {
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        g.setColor(drawColor);

        double verticalPos;
        double horizontalPos;
        int hpos;
        int vpos;
        if (connectPoints) {
            int prevHpos = 0;
            int prevVpos = 0;
            for (int i = 0; i < dataToGraph.length; i++) {
                verticalPos = dataToGraph[i];
                horizontalPos = (double)i / (dataToGraph.length - 1);

                horizontalPos *= windowWidth;
                verticalPos *= windowHeight;
                verticalPos = windowHeight - verticalPos;

                hpos = (int)Math.round(horizontalPos);
                vpos = (int)Math.round(verticalPos);

                if (i == writeIndex)
                    g.setColor(Color.red);

                g.drawLine(prevHpos, prevVpos, hpos, vpos);

                if (i == writeIndex)
                    g.setColor(drawColor);
                
                prevHpos = hpos;
                prevVpos = vpos;
            }
        }
        else {
            for (int i = 0; i < dataToGraph.length; i++) {
                verticalPos = dataToGraph[i];
                horizontalPos = (double)i / dataToGraph.length;

                horizontalPos *= windowWidth;
                verticalPos *= windowHeight;
                verticalPos = windowHeight - verticalPos;

                hpos = (int)Math.round(horizontalPos);
                vpos = (int)Math.round(verticalPos);

                g.fillOval(hpos - 1, vpos - 1, 2, 2);
            }
        }

        g.setColor(Color.white);

        char[] botChars = (bottom + "").toCharArray();
        char[] topChars = (top + "").toCharArray();
        int fontHeight = g.getFontMetrics().getHeight();
        int belowSpace = g.getFontMetrics().getDescent();
        g.drawChars(botChars, 0, botChars.length, 0, (int)windowHeight - belowSpace);
        g.drawChars(topChars, 0, topChars.length, 0, fontHeight);
    }
    
    public void setVerticalRangeAuto(boolean value) {
        verticalRangeAuto = value;
    }

    public void setConnectPoints(boolean value) {
        this.connectPoints = value;
    }
}

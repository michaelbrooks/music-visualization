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
public class ArrayLineGraph extends ArrayGraph {

    int left = -1;
    int right = -1;
    double bottom = 0;
    double top = 100;
    double[] dataToGraph = new double[1];
    
    public void setVerticalRange(double bottom, double top)
    {
        this.top = top;
        this.bottom = bottom;
    }

    /**
     * A value of -1 indicates to graph all the way to the right.
     * @param left
     * @param right
     */
    public void setHorizontalRange(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public int getLeftIndex() {
        return left;
    }

    public int getRightIndex() {
        return right;
    }


    public void graph(double[] newData) {

        int effectiveRight = newData.length;
        if (right != -1) {
            effectiveRight = right;
        }
        
        int effectiveLeft = 0;
        if (left != -1) {
            effectiveLeft = left;
        }

        int effectiveLength = effectiveRight - effectiveLeft;
        
        if (dataToGraph.length != effectiveLength) {
            dataToGraph = new double[effectiveLength];
        }

        if (verticalRangeAuto) {
            bottom = Double.MAX_VALUE;
            top = Double.MIN_VALUE;
            for (int i = effectiveLeft; i < effectiveRight; i++) {
                if (newData[i] > top)
                    top = newData[i];
                if (newData[i] < bottom)
                    bottom = newData[i];
            }
        }

        for (int i = effectiveLeft; i < effectiveRight; i++) {

            double value = newData[i];

            value -= bottom;
            value /= (top - bottom);

            dataToGraph[i - effectiveLeft] = value;
        }
    }

    private Color drawColor = Color.white;
    public Color getColor() {
        return drawColor;
    }

    public void setColor(Color color) {
        this.drawColor = color;
    }

    public void paint(Graphics2D g) {

        drawData(g, dataToGraph, drawColor);

        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        g.setColor(Color.white);
        
        char[] botChars = (bottom + "").toCharArray();
        char[] topChars = (top + "").toCharArray();
        int fontHeight = g.getFontMetrics().getHeight();
        int belowSpace = g.getFontMetrics().getDescent();
        g.drawChars(botChars, 0, botChars.length, 0, (int)windowHeight - belowSpace);
        g.drawChars(topChars, 0, topChars.length, 0, fontHeight);
    }

    private void drawData(Graphics2D g, double[] data, Color color) {
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        g.setColor(color);

        double verticalPos;
        double horizontalPos;
        int hpos;
        int vpos;
        if (connectPoints) {
            int prevHpos = 0;
            int prevVpos = 0;
            for (int i = 0; i < data.length; i++) {
                verticalPos = data[i];
                horizontalPos = (double)i / (data.length - 1);

                horizontalPos *= windowWidth;
                verticalPos *= windowHeight;
                verticalPos = windowHeight - verticalPos;

                hpos = (int)Math.round(horizontalPos);
                vpos = (int)Math.round(verticalPos);

                if (i != 0)
                    g.drawLine(prevHpos, prevVpos, hpos, vpos);

                prevHpos = hpos;
                prevVpos = vpos;
            }
        }
        else {
            for (int i = 0; i < data.length; i++) {
                verticalPos = data[i];
                horizontalPos = (double)i / data.length;

                horizontalPos *= windowWidth;
                verticalPos *= windowHeight;
                verticalPos = windowHeight - verticalPos;

                hpos = (int)Math.round(horizontalPos);
                vpos = (int)Math.round(verticalPos);

                g.fillOval(hpos - 1, vpos - 1, 2, 2);
            }
        }
    }

    private boolean verticalRangeAuto = false;
    public void setVerticalRangeAuto(boolean value) {
        verticalRangeAuto = value;
    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;

/**
 *
 * @author michael
 */
public class ArrayRingGraph extends ArrayGraph {

    private int left = -1;
    private int right = -1;
    private double high = Double.NEGATIVE_INFINITY;
    private double low = Double.POSITIVE_INFINITY;

    private double[] dataToGraph = new double[2];

    boolean autoHighLowValues = true;
    
    /**
     * A value of -1 indicates to graph all the way to the respective edge.
     * @param left
     * @param right
     */
    public void setLeftRightBounds(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public void setHighLowValues(double high, double low) {
        this.high = high;
        this.low = low;
        this.autoHighLowValues = false;
    }

    @Override
    public void graph(double[] newData) {

        graph(newData, false);
        
        /*
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

        
        for (int i = effectiveLeft; i < effectiveRight; i++) {
            if (low > newData[i])
                low = newData[i];
            if (high < newData[i])
                high = newData[i];
        }

        for (int i = effectiveLeft; i < effectiveRight; i++) {

            double value = newData[i];

            value -= low;
            value /= (high - low);

            dataToGraph[i - effectiveLeft] = value;
        }
         *
         */
    }

    public void paint(Graphics2D g) {
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();
        
        int centerX = (int)Math.round(windowWidth / 2);
        int centerY = (int)Math.round(windowHeight / 2);
        
        double radius;
        int rad;

        double intensity;
        Color colorToDraw;
        
        
        if (connectPoints) {

            //generate the gradient
            float[] fractions = new float[dataToGraph.length + 1];
            Color[] colors = new Color[dataToGraph.length + 1];
            for (int i = 0; i < dataToGraph.length; i++) {
                intensity = Math.min(1, Math.max(dataToGraph[i], 0));
                colors[i] = new Color(Color.HSBtoRGB(0.4f, 0, (float)intensity));

                fractions[i] = (float)i / dataToGraph.length;
            }
            fractions[fractions.length -1] = 1;
            colors[colors.length - 1] = Color.black;

            RadialGradientPaint paint = new RadialGradientPaint(centerX, centerY, centerX, fractions, colors);
            g.setPaint(paint);
            g.fillRect(0, 0, (int)windowWidth, (int)windowHeight);
//            for (int i = 0; i < dataToGraph.length; i++) {
//
//                intensity = Math.min(1, Math.max(dataToGraph[i], 0));
//                colorToDraw = new Color(Color.HSBtoRGB((float)intensity, 1, 1));
//
//                g.setColor(colorToDraw);
//
//                radius = (double)i / dataToGraph.length;
//
//                radius *= windowWidth / 2;
//
//                rad = (int)Math.round(radius);
//
//                if (i != 0)
//                    g.drawOval(centerX - rad, centerY - rad, 2 * rad, 2 * rad);
//
//
//            }
        }
        else {
            for (int i = 0; i < dataToGraph.length; i++) {
                //clamp between low and high
                intensity = Math.min(1, Math.max(dataToGraph[i], 0));
                
                colorToDraw = new Color(Color.HSBtoRGB(1, 1, (float)intensity));

                g.setColor(colorToDraw);

                radius = (double)i / dataToGraph.length;

                radius *= windowWidth / 2;
                
                rad = (int)Math.round(radius);

                g.drawOval(centerX - rad, centerY - rad, 2 * rad, 2 * rad);
            }
        }
    }

    public int getRight() {
        return right;
    }

    public void graph(double[] newData, boolean reverse) {
        
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


        for (int i = effectiveLeft; i < effectiveRight; i++) {
            if (low > newData[i])
                low = newData[i];
            if (high < newData[i])
                high = newData[i];
        }
        
        for (int i = effectiveLeft; i < effectiveRight; i++) {

            double value = 0;
            if (reverse)
                value = newData[effectiveRight - i];
            else
                value = newData[i];

            value -= low;
            value /= (high - low);

            dataToGraph[i - effectiveLeft] = value;
        }
    }

}

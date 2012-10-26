/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author mjbrooks
 */
public class WaterfallGraph implements Painter {

    double rowThickness = 2;
    double colorPhase = 0;

    public WaterfallGraph(double rowThickness, double colorPhase) {
        this.rowThickness = rowThickness;
        this.colorPhase = colorPhase;
    }
    public WaterfallGraph() {
        
    }

    ArrayList<double[]> data = new ArrayList<double[]>();
    double minData = Double.POSITIVE_INFINITY;
    double maxData = Double.NEGATIVE_INFINITY;
    double allTimeMax = Double.NEGATIVE_INFINITY;
    double allTimeMin = Double.POSITIVE_INFINITY;
    
    boolean dataAxisAuto = false;

    ArrayList<Double> color = new ArrayList<Double>();
    double minColor = 0;
    double maxColor = 1;

    boolean colorAxisAuto = false;

    BufferedImage imageBuffer = null;

    public boolean isColorAxisAuto() {
        return colorAxisAuto;
    }

    public void setColorAxisAuto(boolean colorAxisAuto) {
        this.colorAxisAuto = colorAxisAuto;
    }

    public double getRowThickness() {
        return rowThickness;
    }

    public void setRowThickness(double rowThickness) {
        this.rowThickness = rowThickness;
    }

    public boolean isDataAxisAuto() {
        return dataAxisAuto;
    }

    public synchronized void setDataAxisAuto(boolean dataAxisAuto) {
        this.dataAxisAuto = dataAxisAuto;
    }

    public synchronized void setDataMinMax(double min, double max) {
        this.maxData = max;
        this.minData = min;
    }
    public synchronized void setColorMinMax(double min, double max) {
        minColor = min;
        maxColor = max;
        colorAxisAuto = false;
    }

    public synchronized void graph(double[] data, double color) {
//        long time0 = System.nanoTime();
//        System.out.println("WaterfallGraph, graph, start, " + time0);
        if (dataAxisAuto) {
            //minData = Double.POSITIVE_INFINITY;
            //maxData = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < data.length; i++) {
                if (data[i] < minData)
                    minData = data[i];
                if (data[i] > maxData)
                    maxData = data[i];
            }

            if (allTimeMax < maxData)
                allTimeMax = maxData;
            if (allTimeMin > minData)
                allTimeMin = minData;
        }
        
        if (colorAxisAuto) {
            if (color < minColor)
                minColor = color;
            if (color > maxColor)
                maxColor = color;
        }

        this.data.add(scaleData(data));
        this.color.add(scaleColor(color));
    }

    public synchronized void paint(Graphics2D g) {
//        long time0 = System.nanoTime();
//        System.out.println("WaterfallGraph, paint, start, " + time0);

        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();
        
        if (imageBuffer == null ||
                imageBuffer.getWidth() != (int)windowWidth ||
                imageBuffer.getHeight() != (int)windowHeight) {
            imageBuffer = new BufferedImage((int)windowWidth, (int)windowHeight, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D iG = imageBuffer.createGraphics();
        
        //Copy down on the buffer
        int copyDist = (int)Math.round(rowThickness * data.size());
        iG.copyArea(0, 0, (int)windowWidth, (int)windowHeight, 0, copyDist);

        //Draw on the buffer
        int cellX, cellY, cellWidth, cellHeight;
        for (int i = 0; i < data.size(); i++) {
            double[] dataRow = data.get(i);
            double dCellWidth = windowWidth / dataRow.length;
            cellWidth = (int)Math.round(dCellWidth);
            cellHeight = (int)Math.round(rowThickness);
            cellY = (int)Math.round(rowThickness * (data.size() - i - 1));

            //generate the gradient

            Color[] colors = new Color[dataRow.length];
            float[] fractions = new float[dataRow.length];
            for (int j = 0; j < dataRow.length; j++) {
                double dColor = (colorPhase + color.get(i)) % 1;

                float d = (float)dataRow[j];
                colors[j] = new Color(Color.HSBtoRGB((float)dColor, 1, d));

                fractions[j] = (float)j / dataRow.length;
            }
            LinearGradientPaint gradient = new LinearGradientPaint(0, 0, (float)windowWidth, 0, fractions, colors);

            iG.setPaint(gradient);
            iG.fillRect(0, cellY, (int)windowWidth, cellHeight);


//            for (int j = 0; j < dataRow.length; j++) {
//                double dColor = (colorPhase + color.get(i)) % 1;
//
//                float d = (float)dataRow[j];
//                Color c = new Color(Color.HSBtoRGB((float)dColor, 1, d));
//
//                iG.setColor(c);
//                cellX = (int)Math.round(j * dCellWidth);
//                iG.fillRect(cellX, cellY, cellWidth + 1, cellHeight);
//            }
        }
        
        color.clear();
        data.clear();

        //Copy the buffer to the screen
        g.drawImage(imageBuffer, null, null);

        //reduce the data max
        //maxData *= 0.95;
        //maxData = Math.max(maxData, allTimeMax * 0.2);
    }

    private double[] scaleData(double[] data) {
        double[] newData = new double[data.length];

        for (int i = 0; i < data.length; i++) {
            newData[i] = Math.min(1, (2 * data[i] - minData) / (maxData - minData));
        }

        return newData;
    }

    private double scaleColor(double color) {
        return (color - minColor) / (maxColor - minColor);
    }
}

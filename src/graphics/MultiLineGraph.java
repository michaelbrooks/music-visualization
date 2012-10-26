/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import graphics.Painter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;

/**
 *
 * @author michael
 */
public class MultiLineGraph implements Painter {

    int numberOfLines = 1;

    ArrayList<double[]> dataSet;

    ArrayList<Double> lowerBounds;
    ArrayList<Double> upperBounds;
    ArrayList<Double> thickness;
    ArrayList<Double> zeroes;
    ArrayList<Boolean> verticalRangeAuto;
    ArrayList<Boolean> connectPoints;
    ArrayList<Boolean> plotMinValue;
    ArrayList<String> labels;
    
    ArrayList<Color> drawColors;
    
    int pixelsBetween;
    int horizontalPixels;
    int writeIndex = 0;
    
    public MultiLineGraph(int numberOfLines, int pixelsBetween, int horizontalPixels) {
        this.pixelsBetween = pixelsBetween;
        this.numberOfLines = numberOfLines;
        this.horizontalPixels = horizontalPixels;
        dataSet = new ArrayList<double[]>();
        lowerBounds = new ArrayList<Double>();
        upperBounds = new ArrayList<Double>();
        verticalRangeAuto = new ArrayList<Boolean>();
        plotMinValue = new ArrayList<Boolean>();
        connectPoints = new ArrayList<Boolean>();
        drawColors = new ArrayList<Color>();
        thickness = new ArrayList<Double>();
        labels = new ArrayList<String>();
        zeroes = new ArrayList<Double>();

        for (int i = 0; i < numberOfLines; i++) {
            dataSet.add(new double[horizontalPixels]);
            lowerBounds.add(Double.POSITIVE_INFINITY);
            upperBounds.add(Double.NEGATIVE_INFINITY);
            thickness.add(2.0);
            labels.add(null);
            verticalRangeAuto.add(true);
            connectPoints.add(true);
            plotMinValue.add(true);
            drawColors.add(Color.red);
            zeroes.add(0.0);
        }
    }

    public void setVerticalRange(int graph, double bottom, double top)
    {
        this.upperBounds.set(graph, top);
        this.lowerBounds.set(graph, bottom);
        this.verticalRangeAuto.set(graph, false);
    }

    public synchronized void setZero(int graph, double zero) {
        zeroes.set(graph, zero);
    }

    public void plotMinValue(int graph, boolean plot) {
        plotMinValue.set(graph, plot);
    }

    public Color getColor(int graph) {
        return drawColors.get(graph);
    }

    public synchronized void setColor(int graph, Color color) {
        this.drawColors.set(graph, color);
    }

    public synchronized void graph(ArrayList<Double> values) {
        for (int i = 0; i < values.size(); i++) {
            double newValue = values.get(i);

            if (verticalRangeAuto.get(i)) {
                if (upperBounds.get(i) < newValue) {
                    upperBounds.set(i, newValue);
                }
                if (lowerBounds.get(i) > newValue) {
                    lowerBounds.set(i, newValue);
                }
            }

            dataSet.get(i)[writeIndex] = newValue;//scaleValue(i, newValue);
        }

        writeIndex++;
        if (writeIndex >= horizontalPixels) {
            writeIndex -= horizontalPixels;
        }
    }

    private double scaleValue(int graphNum, double value) {
        return (value - lowerBounds.get(graphNum)) / (upperBounds.get(graphNum) - lowerBounds.get(graphNum));
    }


    public synchronized void paint(Graphics2D g) {
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        double panelHeight = (windowHeight - (numberOfLines) * pixelsBetween) / numberOfLines;
        double panelWidth = windowWidth;

        int originX = 0;
        int originY = pixelsBetween;

        for (int graph = 0; graph < dataSet.size(); graph++) {

            g.setColor(Color.darkGray);
            g.fillRect(originX, originY - pixelsBetween, (int)panelWidth, pixelsBetween);

            g.setColor(drawColors.get(graph));

            double verticalPos;
            double horizontalPos;
            int hpos;
            int vpos;

            double zeroHeight = scaleValue(graph, zeroes.get(graph));
            zeroHeight *= panelHeight;
            zeroHeight = panelHeight - zeroHeight;
            zeroHeight = Math.min(panelHeight, Math.max(0, zeroHeight));
            int zHeight = (int)Math.round(zeroHeight) + originY;
            
            double thick = thickness.get(graph);
            Stroke stroke = new BasicStroke((float)thick);
            Stroke defaultStroke = g.getStroke();
            g.setStroke(stroke);
            double[] dataToGraph = dataSet.get(graph);

            if (connectPoints.get(graph)) {
                int prevHpos = originX;
                int prevVpos = originY;
                
                for (int i = 0; i < dataToGraph.length; i++) {
                    verticalPos = scaleValue(graph, dataToGraph[i]);
                    horizontalPos = (double) i / (dataToGraph.length - 1);

                    horizontalPos *= panelWidth;
                    verticalPos *= panelHeight;
                    verticalPos = panelHeight - verticalPos;

                    //limit
                    horizontalPos = Math.min(panelWidth, Math.max(0, horizontalPos));
                    verticalPos = Math.min(panelHeight, Math.max(0, verticalPos));

                    hpos = (int) Math.round(horizontalPos);
                    vpos = (int) Math.round(verticalPos);

                    //translate
                    hpos += originX;
                    vpos += originY;

                    if (i == writeIndex) {
                            g.setColor(Color.red);
                            g.setStroke(defaultStroke);
                            g.drawLine(hpos, originY, hpos, (int)(originY + panelHeight));
                            g.setStroke(stroke);
                            g.setColor(drawColors.get(graph));
                    }

                    if (plotMinValue.get(graph) || vpos != prevVpos || verticalPos != panelHeight) {

                        g.drawLine(prevHpos, prevVpos, hpos, vpos);
                    }

                    prevHpos = hpos;
                    prevVpos = vpos;
                }
            } else {
                for (int i = 0; i < dataToGraph.length; i++) {
                    verticalPos = scaleValue(graph, dataToGraph[i]);
                    horizontalPos = (double) i / dataToGraph.length;

                    horizontalPos *= panelWidth;
                    verticalPos *= panelHeight;
                    verticalPos = panelHeight - verticalPos;
                    
                    //limit
                    horizontalPos = Math.min(panelWidth, Math.max(0, horizontalPos));
                    verticalPos = Math.min(panelHeight, Math.max(0, verticalPos));

                    hpos = (int) Math.round(horizontalPos - thick / 2);
                    vpos = (int) Math.round(verticalPos - thick / 2);

                    //translate
                    hpos += originX;
                    vpos += originY;

                    if (i == writeIndex) {
                        g.setColor(Color.red);
                        g.setStroke(defaultStroke);
                        g.drawLine(hpos, originY, hpos, (int)(originY + panelHeight));
                        g.setStroke(stroke);
                        g.setColor(drawColors.get(graph));
                    }

                    if (plotMinValue.get(graph) || verticalPos != panelHeight) {
                        g.drawLine(hpos, vpos, hpos, zHeight);
                        //g.fillOval(hpos, vpos, (int)thick, (int)thick);
                    }
                }
            }

            g.setColor(Color.white);

            if (labels.get(graph) != null) {
                char[] chars = labels.get(graph).toCharArray();
                int fontHeight = g.getFontMetrics().getHeight();
                g.drawChars(chars, 0, chars.length, originX + 5, originY - 12);
            }
//            char[] botChars = (lowerBounds.get(graph) + "").toCharArray();
//            char[] topChars = (upperBounds.get(graph) + "").toCharArray();
//            int fontHeight = g.getFontMetrics().getHeight();
//            int belowSpace = g.getFontMetrics().getDescent();
//            g.drawChars(botChars, 0, botChars.length, (int)originX, (int) originY + (int) panelHeight - belowSpace);
//            g.drawChars(topChars, 0, topChars.length, (int)originX, (int) originY + fontHeight);

            originY += panelHeight;            
            originY += pixelsBetween;

        }
    }

    public void setConnectPoints(int i, boolean b) {
        connectPoints.set(i, b);
    }

    public void setLabel(int i, String string) {
        labels.set(i, string);
    }
}

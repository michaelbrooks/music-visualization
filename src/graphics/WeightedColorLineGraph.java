/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author michael
 */
public class WeightedColorLineGraph implements Painter {

    double bottom = 0;
    double top = 1;
    double[] heightData = new double[1];
    Color lowColor = new Color(0, 255, 128);
    Color highColor = new Color(255, 0, 128);
    double minColor = 0;
    double maxColor = 1;
    int redRange = 0;
    int greenRange = 0;
    int blueRange = 0;
    boolean colorRangeCalculated = false;

    Color[] colorData = new Color[1];
    double lowThickness = 1;
    double highThickness = 10;
    double minThickness = 0;
    double maxThickness = 1;
    double[] thicknessData = new double[1];

    double minOpacity = 0;
    double maxOpacity = 1;

    boolean verticalRangeAuto = false;
    boolean colorRangeAuto = false;
    boolean opacityRangeAuto = false;
    boolean thicknessRangeAuto = false;

    boolean connectPoints = false;

    int firstFilled = -1;
    
    public boolean isColorRangeAuto() {
        return colorRangeAuto;
    }

    public void setColorRangeAuto(boolean colorRangeAuto) {
        this.colorRangeAuto = colorRangeAuto;
    }

    public boolean isThicknessRangeAuto() {
        return thicknessRangeAuto;
    }

    public void setThicknessRangeAuto(boolean thicknessRangeAuto) {
        this.thicknessRangeAuto = thicknessRangeAuto;
    }

    public void setOpacityRangeAuto(boolean opacityRangeAuto) {
        this.opacityRangeAuto = opacityRangeAuto;
    }

    public WeightedColorLineGraph(int points) {
        this.heightData = new double[points];
        this.colorData = new Color[points];
        this.thicknessData = new double[points];
        firstFilled = points;
    }

    public void setVerticalRange(double bottom, double top)
    {
        this.top = top;
        this.bottom = bottom;
    }

    public void setColorRange(double min, double max) {
        this.minColor = min;
        this.maxColor = max;
    }

    public void setWeightRange(double min, double max) {
        this.minThickness = min;
        this.maxThickness = max;
    }

    public void setOpacityRange(double min, double max) {
        this.minOpacity = min;
        this.maxOpacity = max;
    }

    public void setColorSpectrum(Color lowColor, Color highColor) {
        this.lowColor = lowColor;
        this.highColor = highColor;
        updateColorRange();
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public void graph(double height, double color, double weight, double opacity) {
        
        if (verticalRangeAuto) {
            if (top < height)
                top = height;
            if (bottom > height)
                bottom = height;
        }
        height = Math.max(bottom, Math.min(top, height));

        if (colorRangeAuto) {
            if (maxColor < color)
                maxColor = color;
            if (minColor > color)
                minColor = color;
        }
        color = Math.max(minColor, Math.min(maxColor, color));

        if (thicknessRangeAuto) {
            if (maxThickness < weight)
                maxThickness = weight;
            if (minThickness > weight)
                minThickness = weight;
        }
        weight = Math.max(minThickness, Math.min(maxThickness, weight));

        if (opacityRangeAuto) {
            if (maxOpacity < opacity)
                maxOpacity = opacity;
            if (minOpacity > opacity)
                minOpacity = opacity;
        }
        opacity = Math.max(minOpacity, Math.min(maxOpacity, opacity));
        
        copyBack();

        int writeIndex = heightData.length - 1;
        
        heightData[writeIndex] = scaleValue(height);
        colorData[writeIndex] = createColor(scaleColor(color), scaleOpacity(opacity));
        thicknessData[writeIndex] = scaleWeight(weight);
        //System.out.println(heightData[writeIndex] + " " + colorData[writeIndex] + " " + thicknessData[writeIndex]);
    }

    private double scaleValue(double value) {
        return (value - bottom) / (top - bottom);
    }

    public void paint(Graphics2D g) {
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();
        
        double verticalPos;
        double horizontalPos;
        int hpos;
        int vpos;
        if (connectPoints) {
            int prevHpos = 0;
            int prevVpos = 0;
            for (int i = firstFilled; i < heightData.length; i++) {
                verticalPos = heightData[i];
                horizontalPos = (double)i / (heightData.length - 1);

                horizontalPos *= windowWidth;
                verticalPos *= windowHeight;
                verticalPos = windowHeight - verticalPos;

                hpos = (int)Math.round(horizontalPos);
                vpos = (int)Math.round(verticalPos);

                g.setColor(colorData[i]);

                BasicStroke stroke = new BasicStroke((float)thicknessData[i]);
                g.setStroke(stroke);
                
                g.drawLine(prevHpos, prevVpos, hpos, vpos);

                prevHpos = hpos;
                prevVpos = vpos;
            }
        }
        else {
            for (int i = firstFilled; i < heightData.length; i++) {
                verticalPos = heightData[i];
                horizontalPos = (double)i / heightData.length;

                horizontalPos *= windowWidth;
                verticalPos *= windowHeight;
                verticalPos = windowHeight - verticalPos;

                hpos = (int)Math.round(horizontalPos);
                vpos = (int)Math.round(verticalPos);

                g.setColor(colorData[i]);

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

    private void updateColorRange() {
        redRange = highColor.getRed() - lowColor.getRed();
        greenRange = highColor.getGreen() - lowColor.getGreen();
        blueRange = highColor.getBlue() - lowColor.getBlue();
    }

    private Color createColor(double scale, double opacity) {
    
        Color c1 = Color.getHSBColor((float) scale, 1, 1);
        Color c2 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), (int)Math.round(255 * opacity));
        return c2;
        
        /*
        int red = (int)Math.round(redRange * scale + lowColor.getRed());
        int green = (int)Math.round(greenRange * scale + lowColor.getGreen());
        int blue = (int)Math.round(blueRange * scale + lowColor.getBlue());
        
        return new Color(red, green, blue);
         * 
         */
    }

    private void copyBack() {
        for (int i = firstFilled; i < heightData.length - 1; i++) {
            heightData[i] = heightData[i + 1];
            colorData[i] = colorData[i + 1];
            thicknessData[i] = thicknessData[i + 1];
        }
        if (firstFilled > 0) {
            firstFilled --;
        }
    }

    private double scaleColor(double color) {
        return (color - minColor) / (maxColor - minColor);
    }

    private double scaleWeight(double weight) {
        return (highThickness - lowThickness) * (weight - minThickness) / (maxThickness - minThickness) + lowThickness;
    }

    private double scaleOpacity(double opacity) {
        return (opacity - minOpacity) / (maxOpacity - minOpacity);
    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 *
 * @author michael
 */
public class VerticalQualityGraph extends Graph1D implements Painter {

    private Color highColor;
    private Color lowColor;
    private double highValue;
    private double lowValue;
    private double barWidth = 0.1;
    
    private double currentQuality = 0;

    public Color getHighColor() { return highColor; }
    public Color getLowColor() { return lowColor; }

    public void setHighColor(Color c) {
        this.highColor = c;
    }

    public void setLowColor(Color c) {
        this.lowColor = c;
    }
    
    public void setHighLowColors(Color high, Color low) {
        this.highColor = high;
        this.lowColor = low;
    }

    public double getHighValue() { return highValue; }
    public double getLowValue() { return lowValue; }

    public void setHighValue(double value) { highValue = value; }
    public void setLowValue(double value) { lowValue = value; }

    public void setHighLowValues(double high, double low) {
        this.highValue = high;
        this.lowValue = low;
    }

    public double getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(double percentHeight) {
        this.barWidth = percentHeight;
    }

    public void graph(double quality) {
        quality -= lowValue;
        quality /= (highValue - lowValue);
        quality = Math.max(0, Math.min(1, quality));
        this.currentQuality = quality;
    }

    Color shade = new Color(0, 0, 0, 0.3f);
    
    public void paint(Graphics2D g) {
        
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        Point2D top = new Point2D.Double(0, 0);
        Point2D bottom = new Point2D.Double(0, windowHeight);
        
        GradientPaint gradient = new GradientPaint(top, highColor, bottom, lowColor);
        
        g.setPaint(gradient);

        g.fillRect(0, 0, (int)windowWidth, (int)windowHeight);

        double barCenter = windowHeight * (1 - currentQuality);
        double realBarWidth = barWidth * windowHeight;
        double topShadeBottom = barCenter - realBarWidth * 0.5;
        double bottomShadeTop = barCenter + realBarWidth * 0.5;

        
        g.setColor(shade);
        g.fillRect(0, 0, (int)Math.round(windowWidth), (int)Math.round(topShadeBottom));
        g.fillRect(0, (int)Math.round(bottomShadeTop), (int)windowWidth, (int)windowHeight);
        
    }

}

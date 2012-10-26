/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author michael
 */
public class ChartPanel extends JPanel {
    
    public ChartPanel()
    {
        this.setBackground(Color.black);
        this.setIgnoreRepaint(true);
    }

    ArrayList<Painter> painters = new ArrayList<Painter>();

    public synchronized void addPainter(Painter p)
    {
        painters.add(p);
    }
    
    public synchronized void removePainter(Painter p)
    {
        painters.remove(p);
    }

    public void setClearsOnPaint(boolean value)
    {
        willClear = value;
    }

    public boolean clearsOnPaint() {
        return willClear;
    }
    
    private boolean willClear = true;

    @Override
    public synchronized void paintComponent(Graphics g)
    {
        Graphics2D graphics = (Graphics2D)g;

        if (willClear)
        {
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }

        for (Painter p : painters)
        {
            p.paint(graphics);
        }
        
    }
}

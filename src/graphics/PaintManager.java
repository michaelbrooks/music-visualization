/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Component;

/**
 * Controls a TimeSeriesChart, i.e. schedules regular repaints.
 * @author michael
 */
public class PaintManager {

    double repaintInterval;
    Component component;
    Thread updateThread;
    boolean repainting = false;
    boolean threadRunning = false;

    
    public PaintManager(Component repaintable, double interval)
    {
        this.component = repaintable;
        setInterval(interval);

        reset();
    }

    /**
     * Starts the manager repainting the component.
     */
    public void start()
    {
        repainting = true;
    }

    /**
     * Stops the manager from repainting the component.
     */
    public void stop()
    {
        repainting = false;
    }

    /**
     * Checks if the manager is repainting the component.
     * @return
     */
    public boolean isRunning()
    {
        return repainting;
    }

    /**
     * Renders a manager inactive, halting its timer thread.
     * To re-activate, call reset().
     */
    public void dispose()
    {
        threadRunning = false;
        updateThread.interrupt();
    }

    /**
     * Returns true if the manager is able to start and stop repainting
     * the component. This is true upon creation or reset, but false after
     * the manager has been disposed.
     * @return
     */
    public boolean isActive()
    {
        return threadRunning;
    }

    /**
     * Gets the update interval in seconds of the manager.
     * @return
     */
    public double getInterval()
    {
        return repaintInterval;
    }

    /**
     * Sets the update interval in seconds of the manager.
     * @param interval
     */
    public void setInterval(double interval)
    {
        if (interval > 0)
           repaintInterval = interval;
        else repaintInterval = 0.05;
    }

    /**
     * Resets the manager.
     */
    public void reset()
    {
        if (isActive())
            dispose();
        
        repainting = false;
        
        updateThread = new Thread(new Runnable() {
            public void run() {
                threadRunning = true;
                while (threadRunning)
                {
                    if (repainting)
                    {
                        component.repaint();
                    }

                    try {
                        Thread.sleep((long)(1000 * repaintInterval));
                    } catch (InterruptedException e)
                    { }
                }
            }
        });

        updateThread.start();
    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import graphics.GraphPanel;
import graphics.Visualization;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author michael
 */
public class AbstractGUIManager extends AbstractComponent implements GUIManager {

    private ArrayList<GraphPanel> graphPanels = new ArrayList<GraphPanel>();
    private HashMap<String, List<GraphPanel>> panelLookup = new HashMap<String, List<GraphPanel>>();
    
    private boolean continuePolling = true;
    private Thread pollingThread;

    private boolean paused = false;

    private ExitDialog exitDialog;

    
    private final int topBorder = 5;
    private final int bottomBorder = 5;
    private final int sideBorder = 5;
    private int usedX = sideBorder;
    private int usedY = topBorder;
    private int widestInColumn = 0;

    private void resetPositions() {
        usedX = sideBorder;
        usedY = topBorder;
        widestInColumn = 0;

        positionWindow(exitDialog);
    }

    public void stopPolling() {
        continuePolling = false;
    }

    public final boolean polling() {
        return continuePolling;
    }

    public void startPolling() {
        continuePolling = true;
    }

    protected final void addVisualization(String name, Visualization visual) {
        GraphPanel g = visual.createGraphPanel();
        addGraphPanel(name, g);
        positionGraphPanel(g);
    }

    protected final void addGraphPanel(GraphPanel panel) {
        addGraphPanel("default", panel);
    }
    
    protected final void addGraphPanel(String name, GraphPanel panel) {
        graphPanels.add(panel);
        nameGraphPanel(name, panel);
    }

    protected final void nameGraphPanel(String name, GraphPanel panel) {
        List<GraphPanel> list = panelLookup.get(name);
        if (list == null) {
            list = new ArrayList<GraphPanel>();
        }
        list.add(panel);
        panelLookup.put(name, list);
        exitDialog.getVisualizationBox().addItem(name);
    }

    protected final void setupThread(Runnable runnable) {
        pollingThread = new Thread(runnable);
    }

    protected final void positionGraphPanel(GraphPanel panel) {
        positionWindow(panel.getFrame());
    }

    protected final void positionWindow(Window w) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = w.getSize();

        int width = (int)size.getWidth();
        int height = (int)size.getHeight();

        int totalHeight = (int) screen.getHeight();

        width += 2 * sideBorder;
        height += topBorder + bottomBorder;

        if (usedY + height < totalHeight) {
            widestInColumn = Math.max(widestInColumn, width);
            w.setLocation(usedX, usedY);
            usedY += height;
        } else {
            positionNewColumn();
            widestInColumn = width;
            w.setLocation(usedX, usedY);
            usedY += height;
        }
    }

    protected final void positionNewColumn() {
        usedY = topBorder;
        usedX += widestInColumn;
        widestInColumn = 0;
    }

    protected final void closeVisualizations() {
        for (GraphPanel p : graphPanels) {
            p.stop();
        }

        resetPositions();
    }

    protected final void openVisualization(String name) {
        List<GraphPanel> panels = panelLookup.get(name);
        for (GraphPanel p : panels) {
            positionGraphPanel(p);
            p.start();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        
        exitDialog = new ExitDialog(getAudioComponents());

        exitDialog.getVisualizationBox().addItem("");
        
        exitDialog.getVisualizationBox().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (exitDialog.getVisualizationBox().getSelectedItem().equals("")) {
                    closeVisualizations();
                }
                else {
                    closeVisualizations();
                    openVisualization((String)exitDialog.getVisualizationBox().getSelectedItem());
                }
            }

        });
        
        exitDialog.pack();

        positionWindow(exitDialog);
    }

    @Override
    public void destroy() {
        super.destroy();
        
        stopPolling();
        try {
            pollingThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        for (GraphPanel p : graphPanels) {
            p.destroy();
        }

        super.destroy();
    }

    @Override
    public void start() {

        pollingThread.start();

        exitDialog.setVisible(true);

        super.start();
    }

    @Override
    public void stop() {

        stopPolling();

        for (GraphPanel p : graphPanels) {
            p.stop();
        }

        super.stop();
    }

    public boolean paused() {
        return paused;
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }


}

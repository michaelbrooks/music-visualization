/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import graphics.ArrayRingGraphPanel;
import music.data.DataFrame;
import music.data.PS;

/**
 *
 * @author michael
 */
public class RingsGUIManager extends AbstractComponent implements GUIManager {

    ArrayRingGraphPanel fftGraphPanel;

    @Override
    public void initialize() {
        fftGraphPanel = new ArrayRingGraphPanel("Log Power Spectrum", 400, 400);
        fftGraphPanel.getFrame().setLocation(0, 0);
        fftGraphPanel.getGraph().setHighLowValues(10, 0);
        //fftGraphPanel.getGraph().setConnectPoints(true);

        pollingThread = new Thread(new Runnable() {

            Recorder rec;
            long lastGraphedTime = 0;

            public void run() {
                rec = getAudioComponents().getRecorder();

                while (continuePolling) {

                    DataFrame frame = rec.getLastFrame();
                    if (frame != null) {
                        if (frame.getTime() != lastGraphedTime) {

                            //Draw the FFT
                            PS ps = frame.getPS();
                            int width = ps.getRawData().length / 4;
                            if (fftGraphPanel.getGraph().getRight() != width)
                                fftGraphPanel.getGraph().setLeftRightBounds(-1, width);
                            fftGraphPanel.graph(ps.getRawData());

                        }

                        lastGraphedTime = frame.getTime();
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });
    }

    @Override
    public void destroy() {

        continuePolling = false;
        try {
            pollingThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        fftGraphPanel.destroy();

        super.destroy();
    }

    boolean continuePolling = true;
    Thread pollingThread;

    @Override
    public void start() {

        fftGraphPanel.start();

        pollingThread.start();

        super.start();
    }

    @Override
    public void stop() {

        fftGraphPanel.stop();

        super.stop();
    }

    public void stopPolling() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean polling() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void startPolling() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean paused() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void pause() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unpause() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}

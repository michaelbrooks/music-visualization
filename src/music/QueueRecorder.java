/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import music.data.DataFrame;

/**
 *
 * @author michael
 */
public class QueueRecorder extends AbstractComponent implements Recorder {

    LinkedList<DataFrame> dataQueue = new LinkedList<DataFrame>();
    private long lastAddedTime;
    
    public synchronized void addFrame(DataFrame frame) {
        //Check the ordering
        if (dataQueue.size() > 0) {
            if (lastAddedTime >= frame.getTime())
                throw new IllegalArgumentException("The inserted frame should be the newest frame.");
        }

        lastAddedTime = frame.getTime();
        dataQueue.add(frame);
    }

    public synchronized DataFrame getLastFrame() {
        if (dataQueue.isEmpty())
            return null;

        return dataQueue.getLast();
    }

    public synchronized List<DataFrame> popFrames() {

        List<DataFrame> result = new ArrayList<DataFrame>(dataQueue.size());
        for (DataFrame frame : dataQueue) {
            result.add(frame);
        }

        dataQueue.clear();
        
        return result;
    }

}

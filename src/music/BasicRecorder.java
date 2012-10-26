/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import music.data.DataFrame;

/**
 *
 * @author michael
 */
public class BasicRecorder extends AbstractComponent implements Recorder {
    private int lastPoppedIndex = 0;
    private long lastAddedTime;
    private ArrayList<DataFrame> dataQueue = new ArrayList<DataFrame>();

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
        
        return dataQueue.get(dataQueue.size() - 1);
    }

    public synchronized List<DataFrame> getLastFrames(long fromTimeAgo) {
        long from = lastAddedTime - fromTimeAgo;
        long to = lastAddedTime + 1;
        return getFrames(from, to);
    }

    public synchronized List<DataFrame> getFrames(long from, long to) {
        if (to < from) {
            throw new IllegalArgumentException();
        }
        
        int fromIndex = Collections.binarySearch(dataQueue, from);
        int toIndex = Collections.binarySearch(dataQueue, to);

        if (fromIndex < 0)
            fromIndex = -(fromIndex + 1) - 1;
        else fromIndex -= 1;
        if (toIndex < 0)
            toIndex = -(toIndex + 1);
        else toIndex += 1;
        
        return dataQueue.subList(fromIndex, toIndex);
    }

    public synchronized List<DataFrame> popFrames() {

        List<DataFrame> result = new ArrayList<DataFrame>();
        for (int i = lastPoppedIndex; i < dataQueue.size(); i++) {
            result.add(dataQueue.get(i));
            lastPoppedIndex++;
        }

        return result;
    }
}

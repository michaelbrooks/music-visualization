/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package dispatch;

/**
 *
 * @author michael
 */
public class AudioEvent {

    long nanoCreationTime;

    public AudioEvent() {
        nanoCreationTime = System.nanoTime();
    }

    public long getCreationTimeNano() {
        return nanoCreationTime;
    }
}

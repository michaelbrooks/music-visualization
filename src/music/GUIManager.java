/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

/**
 *
 * @author michael
 */
public interface GUIManager extends Component {

    void stopPolling();
    void startPolling();
    boolean polling();
    boolean paused();
    void pause();
    void unpause();
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import java.util.List;
import music.data.DataFrame;

/**
 *
 * @author michael
 */
public interface Recorder extends Component {

    void addFrame(DataFrame frame);

    DataFrame getLastFrame();

    List<DataFrame> popFrames();

}

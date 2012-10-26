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
public interface NoiseFilter extends Component {

    boolean floorInitialized();
    void addToNoiseFloor(DataFrame sample, int needed);
    void setNoiseFloor(List<DataFrame> samples);
    DataFrame filter(DataFrame input);
    boolean isEnabled();
    void setEnabled(boolean value);
    double[] getFrequencyFloors();
}

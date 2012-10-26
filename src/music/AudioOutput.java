/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import music.data.Wav;

/**
 *
 * @author michael
 */
public interface AudioOutput extends Component {

    public void play(Wav wav);

}

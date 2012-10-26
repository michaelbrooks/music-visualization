/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

/**
 *
 * @author michael
 */
public interface Component {

    void initialize();
    void start();
    void stop();
    void destroy();
    
    boolean isInitialized();
    void setInitialized(boolean initialized);

    AudioComponents getAudioComponents();
    void setAudioComponents(AudioComponents components);

    

}

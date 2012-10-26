/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

/**
 *
 * @author michael
 */
public abstract class NoteConverter {

    public static double frequencyToPitch(double frequency) {
        double note = 69 + 12*Math.log(frequency/440)/Math.log(2);
        return note;
    }

    public static double pitchToFrequency(double pitch) {
        double frequency = 440 * Math.pow(2, (pitch-69)/12);
        return frequency;
    }
}

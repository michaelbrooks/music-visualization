/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import java.util.List;

/**
 *
 * @author mjbrooks
 */
public class Temperaments {


    private static double[] evenTemperament440 = null;
    public static double[] getEvenTemperament440() {
        if (evenTemperament440 == null) {
            evenTemperament440 = new double[12 * 8];

            for (int n = 0; n < evenTemperament440.length; n++) {
                evenTemperament440[n] = 440 * Math.pow(2, (n - 49) / 12.0);
            }
        }

        return evenTemperament440;

    }
}

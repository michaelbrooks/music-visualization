/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.DataFrame;
import music.data.FFT;
import music.data.Wav;

/**
 *
 * @author michael
 */
public class DataFactory {

    public static DataFrame newDataFrame(Wav wav, long time, long sessionID) {
        return new SynchronizedDataFrame(new DefaultDataFrame(wav, time, sessionID));
    }

    public static DataFrame newDataFrame(FFT fft, long time, long sessionID) {
        return new SynchronizedDataFrame(new DefaultDataFrame(fft, time, sessionID));
    }

    public static Wav newWav(double[] data, double sampleRate) {
        return new DefaultWav(data, sampleRate);
    }

    public static FFT newFFT(double[] complexData, int n, double sampleRate) {
        return new jtFFT(n, sampleRate, complexData);
    }
}

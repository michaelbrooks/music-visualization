/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import java.util.Arrays;
import music.data.Wav;
import music.data.calc.DataWindow;
import music.data.calc.DataWindow.WindowFunction;

/**
 *
 * @author michael
 */
public class DefaultWav implements Wav {

    private double[] wavData;
    private double[] windowedData;
    private double[] paddedData;
    
    private double sampleRate;

    protected DefaultWav(double[] data, double sampleRate) {
        this.wavData = data;
        this.sampleRate = sampleRate;
    }

    private boolean indexInRange(int index) {
        return index >= 0;
    }

    public double getSample(int sample) {
        if (!indexInRange(sample))
            throw new IndexOutOfBoundsException();
        if (sample >= wavData.length)
            return 0;
        
        return wavData[sample];
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public int size() {
        return wavData.length;
    }

    public double duration() {
        return size() / getSampleRate();
    }

    public double[] getRawData() {
        return wavData;
    }

    public double[] getWindowedData() {
        if (windowedData == null) {
            windowedData = DataWindow.applyWindow(WindowFunction.Hamming, wavData, false);
        }
        return windowedData;
    }
}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import music.data.DataFrame;
import music.data.FFT;
import music.data.PS;
import music.data.impl.DataFactory;

/**
 *
 * @author michael
 */
public class SimpleNoiseFilter extends AbstractComponent implements NoiseFilter {


    boolean enabled = false;
    boolean floorInitialized = false;
    
    double silentMeanSquaredAmplitude = 0;
    static double silentMSA = 0;
    
    ArrayList<DataFrame> silenceSamples = new ArrayList<DataFrame>();

    public boolean floorInitialized() {
        return floorInitialized;
    }

    public void addToNoiseFloor(DataFrame sample, int needed) {
        if (!floorInitialized) {
            silenceSamples.add(sample);
            if (silenceSamples.size() > needed) {
                setNoiseFloor(silenceSamples);
            }
        }
    }

    public void setNoiseFloor(List<DataFrame> samples) {

        for (DataFrame frame : samples) {
            silentMeanSquaredAmplitude += frame.getSM().getMeanSquaredAmplitude();
        }
        
        silentMeanSquaredAmplitude /= samples.size();
        silentMSA = silentMeanSquaredAmplitude;

        floorInitialized = true;
    }

    public DataFrame filter(DataFrame input) {
        if (isEnabled()) {
            FFT fft = input.getFFT();
            PS ps = input.getPS();

            double[] fftData = Arrays.copyOf(fft.getRawData(), fft.getRawData().length);
            double[] psData = ps.getRawData();

            int lowPassBin = (int)fft.getBin(12500);
            int highPassBin = (int)fft.getBin(70);

            for (int i = 0; i < psData.length; i++) {
                if (i < highPassBin || i > lowPassBin) {
                    fftData[2 * i] = 0;
                    fftData[2 * i + 1] = 0;
                }
            }

            FFT newFFT = DataFactory.newFFT(fftData, fft.getN(), fft.getSampleRate());

            DataFrame frame = DataFactory.newDataFrame(newFFT, input.getTime(), input.getSessionID());

            return frame;
        }

        return input;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public double[] getFrequencyFloors() {
        return null;
    }

    public double getSilentMeanSquareAmplitude() {
        return silentMeanSquaredAmplitude;
    }

    public static double getSilentMSA() {
        return silentMSA;
    }

}

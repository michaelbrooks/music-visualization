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
import music.data.Wav;
import music.data.calc.jtFFTCalculator;
import music.data.impl.DataFactory;

/**
 *
 * @author michael
 */
public class BasicNoiseFilter extends AbstractComponent implements NoiseFilter {

    private double[] frequencyFloors;
    private boolean floorInitialized = false;
    private boolean enabled = true;
    
    public boolean floorInitialized() {
        return floorInitialized;
    }

    private List<DataFrame> mySamples = new ArrayList<DataFrame>();
    public void addToNoiseFloor(DataFrame sample, int needed) {
        if (!floorInitialized()) {
            mySamples.add(sample);
            if (needed < mySamples.size()) {
                setNoiseFloor(mySamples);
                floorInitialized = true;
                mySamples.clear();
                mySamples = null;
            }
        }
    }

    public double[] getFrequencyFloors() {
        return frequencyFloors;
    }

    public void setNoiseFloor(List<DataFrame> samples) {
        for (DataFrame frame : samples) {

            PS ps = frame.getPS();

            double[] psData = ps.getRawData();

            if (frequencyFloors == null) {
                frequencyFloors = new double[psData.length];
                //Arrays.fill(frequencyFloors, Double.NEGATIVE_INFINITY);
            }

            for (int i = 0; i < psData.length; i++) {
                frequencyFloors[i] += Math.log(psData[i]);
            }
        }

        for (int i = 0; i < frequencyFloors.length; i++) {
            frequencyFloors[i] /= samples.size();
        }
    }

    double[] smoothing;
    
    public DataFrame filter(DataFrame input) {
        if (frequencyFloors != null && isEnabled()) {
            
            FFT fft = input.getFFT();
            PS ps = input.getPS();

            double[] fftData = Arrays.copyOf(fft.getRawData(), fft.getRawData().length);
            double[] psData = ps.getRawData();

            if (smoothing == null)
                smoothing = new double[psData.length];

            //System.out.println("ps = " + Arrays.toString(psData) + ";");
            //System.out.println("before = " + Arrays.toString(fftData) + ";");
            
            double logpower, floor;
            double level = 8;

            int lowPassBin = (int)fft.getBin(12500);
            int highPassBin = (int)fft.getBin(70);
            //Apply the threshold and smooth between samples
            for (int i = 0; i < psData.length; i++) {

                if (i < highPassBin || i > lowPassBin)
                    smoothing[i] = 0;
                else {
                    //smoothing[i] = 1;

                    logpower = Math.log(psData[i]);
                    floor = frequencyFloors[i];

                    double smooth = 1;
                    if (logpower <= floor + level * 0.5) {
                        smooth = 0;
                    }
                    smoothing[i] = smooth;
                    //smoothing[i] = smooth * 0.75 + smoothing[i] * 0.5;
                }
            }

            double previousValue = 0;
            double prevPreviousValue = 0;
            //Smooth across the window
            for (int i = 1; i < smoothing.length - 2; i++) {
                if (i > 2)
                    smoothing[i - 2] = prevPreviousValue;
                if (i > 1)
                    prevPreviousValue = previousValue;

                previousValue = (
                        smoothing[i] +
                        0.5 * (smoothing[i - 1] + smoothing[i + 1])
                        ) * 0.5;
            }

            //Apply the filter
            for (int i = 0; i < fft.size(); i++) {
                fftData[2 * i] *= smoothing[i];
                fftData[2 * i + 1] *= smoothing[i];
            }

            FFT newFFT = DataFactory.newFFT(fftData, fft.getN(), fft.getSampleRate());

            DataFrame frame = DataFactory.newDataFrame(newFFT, input.getTime(), input.getSessionID());

            return frame;
        }

        return input;
    }

    double[] prevWavEnd = null;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

}

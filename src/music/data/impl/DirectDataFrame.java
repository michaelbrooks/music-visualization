/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.ACF;
import music.data.AMDF;
import music.data.FFT;
import music.data.PS;
import music.data.SDF;
import music.data.SNAC;
import music.data.WACF;
import music.data.WSNAC;
import music.data.Wav;
import music.data.calc.DataWindow;
import music.data.calc.DataWindow.WindowFunction;
import music.data.calc.jtFFTCalculator;

/**
 *
 * @author michael
 */
public class DirectDataFrame extends AbstractDataFrame {

    private WACF myWACF = null;
    private ACF myACF = null;
    private FFT myFFT = null;
    private PS myPowerSpectrum = null;
    private AMDF myAMDF = null;
    private SDF mySDF = null;
    private SNAC mySNAC = null;
    private WSNAC myWSNAC = null;

    private final WindowFunction windowFunction = WindowFunction.Hann;

    public DirectDataFrame(Wav wav, long time, long sessionID)
    {
        super(wav, time, sessionID);
    }

    public DirectDataFrame(FFT unwindowedFFT, long time, long sessionID)
    {
        super(time, sessionID);
        
        this.myFFT = unwindowedFFT;

        double[] fftData = unwindowedFFT.getRawData();
        double[] jtFFTData = jtFFTCalculator.complexArrayToJTFormat(unwindowedFFT.getN(), fftData);
        jtFFTCalculator.realInverseInPlace(jtFFTData, true);

        Wav wav = new DefaultWav(jtFFTData, unwindowedFFT.getSampleRate());

        super.setWav(wav);
    }

    public FFT getFFT() {
        if (myFFT == null) {
            myFFT = calculateFFT();
        }
        return myFFT;
    }

    public WACF getWACF() {
        if (myWACF == null) {
            myWACF = calculateWACFDirectly();
        }
        return myWACF;
    }

    public ACF getACF() {
        if (myACF == null) {
            myACF = calculateACFDirectly();
        }
        return myACF;
    }

    public AMDF getAMDF() {
        if (myAMDF == null) {
            myAMDF = calculateAMDFDirectly();
        }
        return myAMDF;
    }

    public PS getPS() {
        if (myPowerSpectrum == null) {
            myPowerSpectrum = calculatePS();
        }
        return myPowerSpectrum;
    }

    public SDF getSDF() {
        if (mySDF == null) {
            mySDF = calculateSDFDirectly();
        }
        return mySDF;
    }

    public SNAC getSNAC() {
        if (mySNAC == null) {
            mySNAC = calculateSNACDirectly();
        }
        return mySNAC;
    }

    public WSNAC getWSNAC() {
        if (myWSNAC == null) {
            myWSNAC = calculateWSNACDirectly();
        }
        return myWSNAC;
    }

    private PS calculatePS() {
        FFT fft = getFFT();

        double[] fftData = fft.getRawData();

        double[] powerData = new double[fft.size()];

        double re, im;
        for (int i = 0; i < powerData.length; i++) {
            re = fftData[2 * i];
            im = fftData[2 * i + 1];

            powerData[i] = re * re + im * im;
        }

        PS spec = new DefaultPowerSpectrum(fft.getN(), fft.getSampleRate(), powerData);
        return spec;
    }

    private PS calculateWPS() {
        FFT fft = getWFFT();

        double[] fftData = fft.getRawData();

        double[] powerData = new double[fft.size()];

        double re, im;
        for (int i = 0; i < powerData.length; i++) {
            re = fftData[2 * i];
            im = fftData[2 * i + 1];

            powerData[i] = re * re + im * im;
        }

        PS spec = new DefaultPowerSpectrum(fft.getN(), fft.getSampleRate(), powerData);
        return spec;
    }

    private AMDF calculateAMDFDirectly() {

        Wav wav = getWav();

        int W = wav.size();
        int p = W / 2;
        
        double[] wavData = wav.getRawData();
        double[] amdfData = new double[p];

        for (int t = 0; t < p; t++) {

            double sum = 0;

            for (int j = 0; j < W - t; j++) {
                sum += Math.abs(wavData[j] -
                        wavData[j + t]);
            }

            amdfData[t] = sum / W;
        }

        return new DefaultAMDF(wav.size(), wav.getSampleRate(), amdfData);
    }

    private double[] calculateMDirectly() {
        Wav wav = getWav();

        double[] wavData = wav.getRawData();

        int W = wav.size();
        int p = W / 2;

        double[] mData = new double[p];

        for (int t = 0; t < p; t++) {
            double straightM = 0;
            int altIdx;
            for (int j = 0; j < W - t; j++) {
                altIdx = j + t;
                straightM += (wavData[j] * wavData[j]  + wavData[altIdx] * wavData[altIdx]);
            }
            mData[t] = straightM;
        }

        return mData;
    }

    private double[] calculateWindowedMDirectly() {
        Wav wav = getWav();

        int W = wav.size();
        int p = W / 2;

        double[] wavData = wav.getRawData();
        double[] window = DataWindow.getRawWindow(windowFunction, W);

        double[] mData = new double[p];

        for (int t = 0; t < p; t++) {
            double m = 0;
            int altIdx;
            for (int j = 0; j < W - t; j++) {
                altIdx = j + t;
                m += window[j] * window[altIdx] * (wavData[j] * wavData[j]  + wavData[altIdx] * wavData[altIdx]);
            }
            mData[t] = m;
        }

        return mData;
    }


    private SNAC calculateSNACDirectly() {
        Wav wav = getWav();

        int W = wav.size();
        int p = W / 2;

        double[] mData = calculateMDirectly();
        double[] rData = calculateACFDirectly().getRawData();

        double[] snacData = new double[p];

        for (int t = 0; t < p; t++) {
            snacData[t] = 2 * rData[t] / mData[t];
        }

        return new DefaultSNAC(W, wav.getSampleRate(), snacData);
    }

        public WACF calculateWACFDirectly() {
        Wav wav = getWav();
        double[] wavData = wav.getWindowedData();

        int W = wav.size();
        int p = W / 2;

        double[] acfData = new double[p];

        double sum = 0;
        int altIndex = 0;
        for (int t = 0; t < p; t++) {
            sum = 0;

            for (int j = 0; j < W - t; j++) {
                altIndex = j + t;
                sum += wavData[j] * wavData[altIndex];
            }

            acfData[t] = sum;
        }

        return new DefaultACF(wav.size(), wav.getSampleRate(), acfData);
    }


    private ACF calculateACFDirectly() {
        Wav wav = getWav();
        double[] wavData = wav.getRawData();

        int W = wav.size();
        int p = W / 2;

        double[] acfData = new double[p];

        double sum = 0;
        int altIndex = 0;
        for (int t = 0; t < p; t++) {
            sum = 0;

            for (int j = 0; j < W - t; j++) {
                altIndex = j + t;
                sum += wavData[j] * wavData[altIndex];
            }

            acfData[t] = sum;
        }

        return new DefaultACF(wav.size(), wav.getSampleRate(), acfData);
    }

    private FFT calculateWFFT() {
        Wav wav = getWav();
        double[] rawData = wav.getWindowedData();
        double[] fftData = fftData = jtFFTCalculator.realForward(rawData);

        fftData = jtFFTCalculator.jtArrayToComplex(wav.size(), fftData);
        FFT fft = new jtFFT(wav.size(), wav.getSampleRate(), fftData);
        return fft;
    }

    private FFT calculateFFT() {
        Wav wav = getWav();
        double[] rawData = wav.getRawData();
        double[] fftData = fftData = jtFFTCalculator.realForward(rawData);

        fftData = jtFFTCalculator.jtArrayToComplex(wav.size(), fftData);
        FFT fft = new jtFFT(wav.size(), wav.getSampleRate(), fftData);
        return fft;
    }

    private WSNAC calculateWSNACDirectly() {
        Wav wav = getWav();

        int W = wav.size();
        int p = W / 2;

        double[] rData = calculateWACFDirectly().getRawData();
        double[] mData = calculateWindowedMDirectly();

        double[] snacData = new double[p];

        for (int t = 0; t < p; t++) {
            snacData[t] = 2 * rData[t] / mData[t];
        }

        return new DefaultSNAC(W, wav.getSampleRate(), snacData);
    }

    private SDF calculateSDFDirectly() {
        Wav wav = getWav();
        int W = wav.size();
        int p = W / 2;
        
        double[] wavData = wav.getRawData();

        double[] sdfData = new double[p];

        double[] mData = calculateMDirectly();
        
        for (int t = 0; t < p; t++) {

            double sum = 0;

            for (int j = 0; j < W - t; j++) {
                sum += (wavData[j] - wavData[j + t]) * (wavData[j] - wavData[j + t]);
            }

            sdfData[t] = sum;
        }

        return new DefaultSDF(W, wav.getSampleRate(), sdfData);
    }

    public PS getWPS() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FFT getWFFT() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

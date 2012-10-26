/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import java.util.Arrays;
import music.data.ACF;
import music.data.AMDF;
import music.data.FFT;
import music.data.PS;
import music.data.SDF;
import music.data.SNAC;
import music.data.WSNAC;
import music.data.WACF;
import music.data.Wav;
import music.data.calc.DataWindow;
import music.data.calc.DataWindow.WindowFunction;
import music.data.calc.jtFFTCalculator;

/**
 *
 * @author michael
 */
public class DefaultDataFrame extends AbstractDataFrame {
    
    private WACF myWACF = null;
    private ACF myACF = null;
    private FFT myFFT = null;
    private FFT myWFFT = null;
    private PS myWPS = null;
    private PS myPS = null;
    private AMDF myAMDF = null;
    private SDF mySDF = null;
    private SNAC mySNAC = null;
    private WSNAC myWSNAC = null;
    
    private final WindowFunction windowFunction = WindowFunction.Hamming;

    protected DefaultDataFrame(FFT unwindowedFFT, long time, long sessionID) {

        super(time, sessionID);
        
        this.myFFT = unwindowedFFT;

        double[] fftData = unwindowedFFT.getRawData();
        double[] jtFFTData = jtFFTCalculator.complexArrayToJTFormat(unwindowedFFT.getN(), fftData);
        jtFFTCalculator.realInverseInPlace(jtFFTData, true);

        Wav wav = new DefaultWav(jtFFTData, unwindowedFFT.getSampleRate());

        super.setWav(wav);
    }

    protected DefaultDataFrame(Wav wav, long time, long sessionID)
    {
        super(wav, time, sessionID);
    }

    private FFT calculateFFT() {
        Wav wav = getWav();
        double[] rawData = wav.getRawData();
        double[] fftData = jtFFTCalculator.realForward(rawData);

        fftData = jtFFTCalculator.jtArrayToComplex(wav.size(), fftData);
        FFT fft = new jtFFT(wav.size(), wav.getSampleRate(), fftData);
        return fft;
    }

    private FFT calculateWFFT() {
        Wav wav = getWav();
        double[] rawData = wav.getWindowedData();
        double[] fftData = jtFFTCalculator.realForward(rawData);

        fftData = jtFFTCalculator.jtArrayToComplex(wav.size(), fftData);
        FFT fft = new jtFFT(wav.size(), wav.getSampleRate(), fftData);
        return fft;
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

    public double[] calculateCC(double[] va, double[] vb) {
        int W = va.length;
        int p = W / 2;

        //Post-pad va, pre-pad vb, both by W
        va = Arrays.copyOf(va, 2 * W);
        double[] padvb = new double[2 * W];
        for (int i = 0; i < W; i++) {
            padvb[W + i] = vb[i];
        }
        vb = padvb;
        
        jtFFTCalculator.realForwardInPlace(va);
        jtFFTCalculator.realForwardInPlace(vb);

        va = jtFFTCalculator.jtArrayToComplex(2 * W, va);
        vb = jtFFTCalculator.jtArrayToComplex(2 * W, vb);

        //Multiply va[i] by conj(vb[i])
        double vaRe, vaIm, vbRe, vbIm;
        for (int i = 0; i < va.length / 2; i++) {
            vaRe = va[2 * i];
            vaIm = va[2 * i + 1];
            vbRe = vb[2 * i];
            vbIm = -vb[2 * i + 1];

            va[2 * i] = vaRe * vbRe - vaIm * vbIm;
            va[2 * i + 1] = vaRe * vbIm + vaIm * vbRe;
        }

        va = jtFFTCalculator.complexArrayToJTFormat(2 * W, va);
        
        jtFFTCalculator.realInverseInPlace(va, true);

        return Arrays.copyOfRange(va, W - p, W + p + 1);
    }


    private ACF calculateACF() {
        Wav wav = getWav();
        int p = wav.size() / 2;

        double[] paddedWav = Arrays.copyOf(wav.getRawData(), wav.size() + p);

        jtFFTCalculator.realForwardInPlace(paddedWav);
        double[] fft = jtFFTCalculator.jtArrayToComplex(paddedWav.length, paddedWav);

        double re, im;
        for (int i = 0; i < fft.length / 2; i++) {
            re = fft[2 * i];
            im = fft[2 * i + 1];
            fft[2 * i] = re * re + im * im;
            fft[2 * i + 1] = 0;
        }

        //Do the inverse transform
        double[] jtFFTData = jtFFTCalculator.complexArrayToJTFormat(paddedWav.length, fft);
        jtFFTCalculator.realInverseInPlace(jtFFTData, true);

        jtFFTData = Arrays.copyOfRange(jtFFTData, 0, wav.size() / 2);

        ACF acf = new DefaultACF(wav.size(), getWav().getSampleRate(), jtFFTData);

        return acf;
    }

    private WACF calculateWACF() {
        Wav wav = getWav();
        int p = wav.size() / 2;

        double[] paddedWav = Arrays.copyOf(wav.getWindowedData(), wav.size() + p);
        
        jtFFTCalculator.realForwardInPlace(paddedWav);
        double[] fft = jtFFTCalculator.jtArrayToComplex(paddedWav.length, paddedWav);

        double re, im;
        for (int i = 0; i < fft.length / 2; i++) {
            re = fft[2 * i];
            im = -fft[2 * i + 1];
            fft[2 * i] = (re * re + im * im);
            fft[2 * i + 1] = 0;
        }
        
        //Do the inverse transform
        double[] jtFFTData = jtFFTCalculator.complexArrayToJTFormat(paddedWav.length, fft);
        jtFFTCalculator.realInverseInPlace(jtFFTData, true);

        jtFFTData = Arrays.copyOfRange(jtFFTData, 0, wav.size() / 2);

        WACF wacf = new DefaultACF(wav.size(), getWav().getSampleRate(), jtFFTData);

        return wacf;
    }

    private SDF calculateSDF() {
        Wav wav = getWav();
        
        int W = wav.size();
        int p = W / 2;

        ACF acf = getACF();
        double[] acfData = acf.getRawData();

        double[] mData = calculateMIncrementally();
        
        double[] sdfData = new double[p];

        for (int t = 0; t < p; t++) {
            sdfData[t] = mData[t] - 2 * acfData[t];
        }

        SDF sdf = new DefaultSDF(wav.size(), getWav().getSampleRate(), sdfData);
        return sdf;
    }

    private double[] calculateWindowedM() {
        Wav wav = getWav();

        int W = wav.size();
        int p = W / 2;

        double[] wavData = Arrays.copyOf(wav.getRawData(), W);
        double[] window = DataWindow.getRawWindow(windowFunction, W);

        for (int i = 0; i < window.length; i++) {
            wavData[i] = wavData[i] * wavData[i] * window[i];
        }

        double[] wavCCWindow = calculateCC(wavData, window);
        double[] m = new double[p];
        for (int t = 0; t < p; t++) {
            m[t] = wavCCWindow[p + t] + wavCCWindow[p - t];
        }

        return m;
    }


    private SNAC calculateSNAC() {

        Wav wav = getWav();
        ACF acf = getACF();
        
        int W = wav.size();
        int p = W / 2;

        double[] acfData = acf.getRawData();
        double[] mSeries = calculateMIncrementally();

        double[] snacData = new double[p];

        for (int i = 0; i < p; i++) {
            snacData[i] = 2 * acfData[i] / mSeries[i];
        }

        SNAC snac = new DefaultSNAC(wav.size(), wav.getSampleRate(), snacData);
        return snac;
    }


    private double[] calculateMIncrementally() {
        Wav wav = getWav();
        
        int W = wav.size();
        int p = W / 2;

        double[] wavData = wav.getRawData();

        double[] mData = new double[p];

        double sumOfSquares = 0;
        for (int i = 0; i < W; i++) {
            sumOfSquares += wavData[i] * wavData[i];
        }
        
        sumOfSquares *= 2;

        mData[0] = sumOfSquares;
        for (int t = 1; t < p; t++) {
            mData[t] = mData[t - 1] -
                    wavData[t - 1] * wavData[t - 1] -
                    wavData[W - t] * wavData[W - t];
        }

        return mData;
    }

    private WSNAC calculateWSNAC() {
        Wav wav = getWav();

        int W = wav.size();
        int p = W / 2;

        double[] snacData = new double[p];

        double[] mSeries = calculateWindowedM();
        double[] rSeries = getWACF().getRawData();

        for (int t = 0; t < p; t++) {
            snacData[t] = 2 * rSeries[t] / mSeries[t];
        }
        
        return new DefaultSNAC(W, wav.getSampleRate(), snacData);
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

    
    public FFT getFFT() {
        if (myFFT == null) {
            myFFT = calculateFFT();
        }
        return myFFT;
    }

    public WACF getWACF() {
        if (myWACF == null) {
            myWACF = calculateWACF();
        }
        return myWACF;
    }

    public ACF getACF() {
        if (myACF == null) {
            myACF = calculateACF();
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
        if (myPS == null) {
            myPS = calculatePS();
        }
        return myPS;
    }

    public SDF getSDF() {
        if (mySDF == null) {
            mySDF = calculateSDF();
        }
        return mySDF;
    }

    public SNAC getSNAC() {
        if (mySNAC == null) {
            mySNAC = calculateSNAC();
        }
        return mySNAC;
    }

    public WSNAC getWSNAC() {
        if (myWSNAC == null) {
            myWSNAC = calculateWSNAC();
        }
        return myWSNAC;
    }

    public PS getWPS() {
        if (myWPS == null) {
            myWPS = calculateWPS();
        }
        return myWPS;
    }

    public FFT getWFFT() {
        if (myWFFT == null) {
            myWFFT = calculateWFFT();
        }
        return myWFFT;
    }
}

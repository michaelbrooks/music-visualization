/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author michael
 */
public class jtFFTCalculator {

    private jtFFTCalculator() { }

    private static HashMap<Integer, DoubleFFT_1D> jffts = new HashMap<Integer, DoubleFFT_1D>();;
    
    private static DoubleFFT_1D getCalculator(int size) {
        if (!jffts.containsKey(size))
            jffts.put(size, new DoubleFFT_1D(size));
        
        return jffts.get(size);
    }

    /**
     * Transforms an array of entirely real complex numbers (i.e.
     * no imaginary components included), expands to JTransforms
     * representation.
     * @param n
     * @param realFFTData
     * @return
     */
    public static double[] realArrayToJTFormat(int n, double[] realFFTData) {
        int numBins = realFFTData.length;

        double[] jtFFTData;
        int mapIndex;

        if (n % 2 == 0) { // if even

            jtFFTData = new double[2 * realFFTData.length - 2];

            for (int i = 0; i < realFFTData.length; i++) {
                mapIndex = 2 * i;
                if (i == realFFTData.length - 1)
                    mapIndex = 1;
                
                jtFFTData[mapIndex] = realFFTData[i];
            }
        }
        else { // if odd

            jtFFTData = new double[2 * realFFTData.length - 1];

            for (int i = 0; i < realFFTData.length; i++) {
                mapIndex = 2 * i;
                jtFFTData[mapIndex] = realFFTData[i];
            }
        }

        return jtFFTData;
    }

    /**
     * Given an array with alternating real and imaginary
     * parts that represent the output of a fourier transform,
     * translates into the format used by JTransforms.
     * Note that the imaginary part of the 0th component
     * is assumed to be 0.
     * Also assumed to be 0 is the imaginary part of the N/2 component
     * (if N is even)
     * @param data
     * @return
     */
    public static double[] complexArrayToJTFormat(int n, double[] fftData) {
        
        int numBins = fftData.length / 2;

        double[] jtFFTData;
        int mapIndex;
        
        if (n % 2 == 0) { // if even

            jtFFTData = new double[fftData.length - 2];
            
            for (int i = 0; i < jtFFTData.length; i++) {
                mapIndex = i;
                if (i == 1) 
                    mapIndex = n;
                
                jtFFTData[i] = fftData[mapIndex];
            }
        }
        else { // if odd

            jtFFTData = new double[fftData.length - 1];

            for (int i = 0; i < jtFFTData.length; i++) {
                mapIndex = i;
                if (i == 1)
                    mapIndex = n;
                
                jtFFTData[i] = fftData[mapIndex];
            }
        }

        return jtFFTData;
    }

   
    
    /**
     * Given an array result from JTransforms, translates
     * it into alternating real and complex parts.
     * @param data
     * @return
     */
    public static double[] jtArrayToComplex(int n, double[] jtFFTData) {

        double[] fftData;
        int mapIndex;

        if (n % 2 == 0) { // if even

            fftData = new double[jtFFTData.length + 2];

            for (int i = 0; i < jtFFTData.length; i++) {
                mapIndex = i;
                if (i == 1)
                    mapIndex = n;
                fftData[mapIndex] = jtFFTData[i];
            }
            fftData[1] = 0;
            fftData[n + 1] = 0;
        }
        else { // if odd
            fftData = new double[jtFFTData.length + 1];

            for (int i = 0; i < jtFFTData.length; i++) {
                mapIndex = i;
                if (i == 1)
                    mapIndex = n;
                fftData[mapIndex] = jtFFTData[i];
            }
            fftData[1] = 0;
        }

        return fftData;
    }

    public static void realForwardInPlace(double[] data) {
        DoubleFFT_1D jfft = getCalculator(data.length);
        jfft.realForward(data);
    }
    public static double[] realForward(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        realForwardInPlace(copy);
        return copy;
    }
    
    public static double[] realInverse(double[] jtFFTData, boolean scale) {
        double[] copy = Arrays.copyOf(jtFFTData, jtFFTData.length);
        realInverseInPlace(copy, scale);
        return copy;
    }

    public static void realInverseInPlace(double[] jtFFTData, boolean scale) {
        DoubleFFT_1D jfft = getCalculator(jtFFTData.length);
        jfft.realInverse(jtFFTData, scale);
    }
    
}

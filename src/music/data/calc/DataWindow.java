/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

import java.util.Arrays;
import java.util.HashMap;
import music.data.calc.DataWindow.WindowFunction;

/**
 *
 * @author michael
 */
public class DataWindow {

    private DataWindow() { }

    private static HashMap<Integer, WindowSet> windows = new HashMap<Integer, WindowSet>();

    private static WindowSet getWindowSet(int size) {
        if (!windows.containsKey(size)) {
            windows.put(size, new WindowSet(size));
        }
        return windows.get(size);
    }
    
    public static void applyWindowInplace(WindowFunction wfunction, double[] data, boolean invert) {
        WindowSet window = getWindowSet(data.length);
        window.apply(wfunction, data, invert);
    }

    public static double[] applyWindow(WindowFunction wfunction, double[] input, boolean invert) {
        double[] copy = Arrays.copyOf(input, input.length);
        applyWindowInplace(wfunction, copy, invert);
        return copy;
    }

    public static double[] getRawWindow(WindowFunction wfunction, int size) {
        WindowSet window = getWindowSet(size);
        return window.getRawWindow(wfunction);
    }

    public enum WindowFunction {
        Hamming,
        Cosine,
        Hann,
        Blackman,
    }
}

class WindowSet {

    private int size;
    double[][] arrays;
    
    public WindowSet(int size) {
        this.size = size;
        init();
    }

    private void init() {
        WindowFunction[] functions = WindowFunction.values();
        arrays = new double[functions.length][size];

        for (WindowFunction function : functions) {
            int ord = function.ordinal();

            switch (function) {
                case Hamming:
                    initHamming(ord);
                    break;
                case Cosine:
                    initCosine(ord);
                    break;
                case Hann:
                    initHann(ord);
                    break;
                case Blackman:
                    initBlackman(ord);
                    break;
                default:
                    throw new UnsupportedOperationException(function.name() + " is not as supported window.");
            }
        }
    }

    private void initHamming(int position) {
        for (int i = 0; i < size; i++) {
            arrays[position][i] = 0.54 - 0.46 * Math.cos(2 * Math.PI * i / size);
        }
    }

    private void initCosine(int position) {
        for (int i = 0; i < size; i++) {
            arrays[position][i] = Math.sin(Math.PI * i / size);
        }
    }

    private void initHann(int position) {
        for (int i = 0; i < size; i++) {
            arrays[position][i] = 0.5 * (1 - Math.cos(2 * Math.PI * i / size));
        }
    }

    private void initBlackman(int position) {
        double alpha = 0.16;
        double a0, a1, a2;
        a0 = (1 - alpha) * 0.5;
        a1 = 0.5;
        a2 = alpha * 0.5;

        double d;
        for (int i = 0; i < size; i++) {
            d = a0;
            d = d - a1 * Math.cos(2 * Math.PI * i / size);
            d = d + a2 * Math.cos(4 * Math.PI * i / size);

            arrays[position][i] = d;
        }
    }

    void apply(WindowFunction windowFunction, double[] data, boolean invert) {
        if (data.length != size) {
            throw new IllegalArgumentException("Data should be length of window.");
        }

        int ord = windowFunction.ordinal();
        if (invert) {
            for (int i = 0; i < size; i++) {
                data[i] = data[i] / arrays[ord][i];
            }
        }
        else {
            for (int i = 0; i < size; i++) {
                data[i] = data[i] * arrays[ord][i];
            }
        }
    }

    double[] getRawWindow(WindowFunction windowFunction) {
        int ord = windowFunction.ordinal();
        return arrays[ord];
    }
}
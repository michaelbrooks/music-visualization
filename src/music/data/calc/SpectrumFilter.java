/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

import java.util.Arrays;
import music.data.PS;

/**
 *
 * @author michael
 */
public class SpectrumFilter {

    private static double getHarmonicWindow(double x, double center, double height, double width) {
        return height * Math.exp(
                -(x - center) * (x - center) /
                (2 * width * width)
                );
    }

    private static double getNormalizedWindow(double x, double center, double width) {
        return Math.exp(
                -(x - center) * (x - center) /
                (2 * width * width)
                ) /
                Math.sqrt(2 * Math.PI * width * width);
    }

    public static double[] getHarmonics(PS spectrum, double f0, int hCount) {
        double[] data = spectrum.getRawData();

        double[] harmonicData = new double[data.length];

        double[] harmonicHeights = new double[hCount];
        double harmonic = f0;
        for (int h = 0; h < hCount; h++) {
            int bin = (int)Math.round(spectrum.getBin(harmonic));
            if (bin > data.length - 1 || bin < 1)
                continue;
            harmonicHeights[h] = data[bin];
            harmonic *= 2;
        }

        harmonic = f0;
        double width = 2;
        double pitch, pitchCenter, height;
        for (int h = 0; h < hCount; h++) {
            pitchCenter = NoteConverter.frequencyToPitch(harmonic);
            width = 2 / (h + 1);
            height = harmonicHeights[h];
            if (height > 0.01) {
                for (int i = 1; i < harmonicData.length; i++) {
                    pitch = NoteConverter.frequencyToPitch(spectrum.getFrequency(i));
                    if (Math.abs(pitch - pitchCenter) < width * 4)
                        harmonicData[i] += getHarmonicWindow(pitch, pitchCenter, height, width);
                }
            }
            harmonic *= 2;
        }

        return harmonicData;
    }

    public static double[] removeHarmonics(PS spectrum, double f0, int hCount) {
        double[] filtered = new double[spectrum.size()];
        double[] spectrumData = spectrum.getRawData();
        double[] harmonics = getHarmonics(spectrum, f0, hCount);

        for (int i = 0; i < filtered.length; i++) {
            filtered[i] = Math.max(0, spectrumData[i] - harmonics[i]);
        }

        return filtered;
    }

    public static double getNonHarmonicRatio(PS spectrum, double f0, int hCount) {
        double[] filtered = removeHarmonics(spectrum, f0, hCount);
        double[] spectrumData = spectrum.getRawData();

        double unfilteredTotal = 0;
        double filteredTotal = 0;
        for (int i = 0; i < spectrumData.length; i++) {
            unfilteredTotal += spectrumData[i];
            filteredTotal += filtered[i];
        }
        double noisePercent = filteredTotal / unfilteredTotal;

        return noisePercent;
    }

}

/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

import java.util.ArrayList;
import java.util.List;
import music.data.Correlogram;

/**
 *
 * @author michael
 */
public abstract class PeakPicker {

    public static double THRESHOLD_CONSTANT = 0.8;
    
    public static DPoint findFirstPeriodic(List<DPoint> primaries) {
        if (primaries.size() == 0) {
            return null;
        }

        double maxHeight = Double.NEGATIVE_INFINITY;
        DPoint thisPoint;
        for (int i = 0; i < primaries.size(); i++) {
            thisPoint = primaries.get(i);
            if (thisPoint.getY() > maxHeight) {
                maxHeight = thisPoint.getY();
            }
        }

        double threshold = THRESHOLD_CONSTANT * maxHeight;

        for (int i = 0; i < primaries.size(); i++) {
            thisPoint = primaries.get(i);
            if (thisPoint.getY() > threshold) {
                return thisPoint;
            }
        }

        //This should never happen, because at least some point is greater than c * max;
        return null;
    }

    public static DPoint findFirstPeriodic(Correlogram correllogram) {

        List<DPoint> primaries = findPrimaryPeaks(correllogram);

        return findFirstPeriodic(primaries);
    }

    public static List<DPoint> findPrimaryPeaks(Correlogram correllogram) {
        ArrayList<DPoint> primaries = new ArrayList<DPoint>();

        double[] data = correllogram.getRawData();

        //states
        int searchingForUpwardZero = 0;
        int searchingForMax = 1;

        int state = searchingForUpwardZero;

        int largestIndex = -1;
        for (int i = 1; i < data.length; i++) {

            if (state == searchingForUpwardZero) {
                //did we just cross zero going up?
                double lastVal = data[i - 1];
                double thisVal = data[i];
                if (lastVal < 0 && thisVal > 0) {
                    largestIndex = i;
                    state = searchingForMax;
                }
            }
            else if (state == searchingForMax) {
                //is this the new max value
                double currMax = data[largestIndex];
                double thisVal = data[i];
                if (thisVal > currMax) {
                    largestIndex = i;
                }

                //did we just cross zero going down?
                //or possibly run out of data above 0?
                if (thisVal < 0 || i == data.length - 1) {
                    //the current max must be the max for this interval
                    if (largestIndex != data.length - 1 && largestIndex > 0) {
                        primaries.add(getInterpolatedMax(data, largestIndex));
                    }
                    state = searchingForUpwardZero;
                }
            }
        }

        return primaries;
    }

    private static DPoint getInterpolatedMax(double[] data, int peakIndex) {

        DPoint left = new DPoint(peakIndex - 1, data[peakIndex - 1]);
        DPoint mid = new DPoint(peakIndex, data[peakIndex]);
        DPoint right = new DPoint(peakIndex + 1, data[peakIndex + 1]);

        Parabola parabola = Parabola.interpolate(left, mid, right);

        return parabola.getExtreme();
    }
}

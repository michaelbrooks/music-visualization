/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

/**
 *
 * @author michael
 */
public class SmootherOneR implements Smoother {
    double truePrevValue = 0;
    double trueCurrValue = 0;
    double weightedPrevValue = 0;
    double weightedCurrValue = 0;

    double weightPrev;
    double weightCurr;

    public SmootherOneR(double prevWeight) {
        this.weightPrev = prevWeight;
        this.weightCurr = 1 - prevWeight;
    }

    public double newValue(double value) {
        truePrevValue = trueCurrValue;
        weightedPrevValue = weightedCurrValue;
        trueCurrValue = value;
        weightedCurrValue = weightPrev * weightedPrevValue + weightCurr * trueCurrValue;
        return getValue();
    }

    public double getValue() {
        return weightedCurrValue;
    }
}

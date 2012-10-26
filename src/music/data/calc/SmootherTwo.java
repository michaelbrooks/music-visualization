/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

/**
 *
 * @author michael
 */
public class SmootherTwo implements Smoother {

    double trueM2Value = 0, trueM1Value = 0, trueM0Value = 0;
    double smoothM2Value = 0, smoothM1Value = 0, smoothM0Value = 0;

    double m2Weight, m1Weight, m0Weight;

    public SmootherTwo(double m2Weight, double m1Weight) {
        this.m2Weight = m2Weight;
        this.m1Weight = m1Weight;
        this.m0Weight = 1 - (m2Weight + m1Weight);
    }

    public double getValue() {
        return smoothM0Value;
    }

    public double newValue(double value) {
        trueM2Value = trueM1Value;
        trueM1Value = trueM0Value;
        trueM0Value = value;
        smoothM2Value = smoothM1Value;
        smoothM1Value = smoothM0Value;
        smoothM0Value = m2Weight * trueM2Value + m1Weight * trueM1Value + m0Weight * value;
        return getValue();
    }


}

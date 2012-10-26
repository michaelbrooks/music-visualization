/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

/**
 *
 * @author michael
 */
public abstract class AnimatedNGraph implements Painter, Animated {

    int dimensions;
    double[] currentData;
    double[] upperLimits;
    double[] lowerLimits;

    public AnimatedNGraph(int dimensions) {
        this.dimensions = dimensions;
        this.currentData = new double[dimensions];
        this.upperLimits = new double[dimensions];
        this.lowerLimits = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            upperLimits[i] = 1;
            upperLimits[i] = 0;
        }
    }

    public void setData(int dimension, double value) {
        this.currentData[dimension] = scaleData(dimension, value);
    }

    public double getData(int dimension) {
        return this.currentData[dimension];
    }

    public void setData(double[] data) {
        for (int i = 0; i < data.length; i++) {
            setData(i, data[i]);
        }
    }

    public double getUpperLimit(int dimension) {
        return this.upperLimits[dimension];
    }
    public double getLowerLimit(int dimension) {
        return this.lowerLimits[dimension];
    }
    public void setUpperLimit(int dimension, double upperLimit) {
        this.upperLimits[dimension] = upperLimit;
    }
    public void setLowerLimit(int dimension, double lowerLimit) {
        this.upperLimits[dimension] = lowerLimit;
    }

    public int getDimensions() {
        return dimensions;
    }

    protected double scaleData(int dimension, double value) {
        double upper = upperLimits[dimension];
        double lower = lowerLimits[dimension];

        return (value - lower) / (upper - lower);
    }
}

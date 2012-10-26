/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

/**
 *
 * @author michael
 */
public class DPoint {

    private double x, y;

    public DPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DPoint other = (DPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "x: " + x + ", y: " + y;
    }

    public DPoint subtract(DPoint other) {
        return new DPoint(x - other.x, y - other.y);
    }

    public DPoint add(DPoint other) {
        return new DPoint(x + other.x, y + other.y);
    }

    public DPoint multiply(double scalar) {
        return new DPoint(x * scalar, y * scalar);
    }

    public DPoint divide(double scalar) {
        return new DPoint(x / scalar, y / scalar);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public void normalize() {
        double len = length();
        x /= len;
        y /= len;
    }

    public void limit(double length) {
        double len = length();
        if (len > length) {
            x = x * length / len;
            y = y * length / len;
        }
    }

    public DPoint scale(double xScalar, double yScalar) {
        return new DPoint(x * xScalar, y * yScalar);
    }


}

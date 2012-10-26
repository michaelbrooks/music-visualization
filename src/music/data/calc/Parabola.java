/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

/**
 *
 * @author michael
 */
public class Parabola {

    private double a, b, c;
    private DPoint extreme = null;

    public Parabola(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    private Parabola(double a, double b, double c, double extX, double extY) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.extreme = new DPoint(extX, extY);
    }

    public double getValue(double x) {
        return a * x * x + b * x + c;
    }

    public DPoint getExtreme() {
        if (extreme == null) {
            double x = -b / (2 * a);
            double y = getValue(x);
            extreme = new DPoint(x, y);
        }
        return extreme;
    }

    /**
     * Creates the best parabola to fit the three values given.
     * It is assumed that left.getX() < mid.getX() < right.getX()
     * The algorithm was adapted (copied) from http://physics.holsoft.nl/physics/parabfit1.htm
     * @param left
     * @param mid
     * @param right
     * @return
     */
    public static Parabola interpolate(DPoint left, DPoint mid, DPoint right) {
        double y3 = right.getY(), x3 = right.getX();
        double y2 = mid.getY(), x2 = mid.getX();
        double y1 = left.getY(), x1 = left.getX();

        double h31 = (y3-y1)/(x3 - x1);
        double h21 = (y2 - y1) / (x2 - x1);

        double p1 = (h31 - h21) / (x3 - x2);
        double p2 = h21 - p1 * (x2 + x1);
        double p3 = y1 - p1 * x1 * x1 - p2 * x1;

        double xap = -p2 / 2 / p1;
        double yap = p3 - p2 * p2 / 4 / p1;

        return new Parabola(p1, p2, p3, xap, yap);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Parabola other = (Parabola) obj;
        if (this.a != other.a) {
            return false;
        }
        if (this.b != other.b) {
            return false;
        }
        if (this.c != other.c) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.a) ^ (Double.doubleToLongBits(this.a) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.b) ^ (Double.doubleToLongBits(this.b) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.c) ^ (Double.doubleToLongBits(this.c) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "a:" + a + ", b:" + b + ", c:" + c;
    }
}

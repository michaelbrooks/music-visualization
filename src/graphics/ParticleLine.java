/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import music.data.calc.DPoint;

/**
 *
 * @author michael
 */
public class ParticleLine extends AnimatedNGraph {

    int numberOfParticles;
    Random random = new Random();
    ArrayList<Particle> particles = new ArrayList<Particle>();

    double moveRatio = 1;
    double maxJump = 50;

    public ParticleLine(int numberOfParticles) {
        super(2);

        this.numberOfParticles = numberOfParticles;

        createParticles();
    }

    public void paint(Graphics2D g) {
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        DPoint center = new DPoint(windowWidth * 0.5, windowHeight * 0.5);

        g.setColor(Color.white);

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);

            DPoint position = p.getPosition();
            position = position.scale(windowWidth, windowHeight);

            int x = (int)(Math.round(position.getX() - 1));
            int y = (int)(Math.round(position.getY() - 1));
            g.drawOval(x, y, 3, 3);
        }
    }

    public void update(double dt, double realDt) {
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            updateParticle(p, dt, realDt);
        }
    }

    private void createParticles() {
        for (int i = 0; i < numberOfParticles; i++) {
            addParticle();
        }
    }

    private double getSpread() {
        return 1 - getData(0);
    }

    private void addParticle() {
        double offset = random.nextDouble() - 0.5;
        double xcoor = random.nextDouble();

        DPoint position = getPosition(offset, xcoor);

        Particle p = new Particle(position, offset, xcoor);

        particles.add(p);
    }

    private DPoint getPosition(double offset, double xcoor) {
        double y = 0.5 - offset;
        DPoint pos = new DPoint(xcoor, y);
        return pos;
    }

    void updateParticle(Particle p, double dt, double realDt) {

        double offset = p.getOffset();
        double xcoor = p.getXCoor();

        offset *= getSpread();

        p.setPosition(getPosition(offset, xcoor));
    }

    class Particle {

        double offset;
        double xcoor;
        DPoint position;

        public Particle(DPoint position, double offset, double xcoor) {
            this.position = position;
            this.offset = offset;
            this.xcoor = xcoor;
        }

        double getOffset() {
            return offset;
        }

        double getXCoor() {
            return xcoor;
        }

        DPoint getPosition() {
            return position;
        }

        void setPosition(DPoint point) {
            position = point;
        }
    }
}

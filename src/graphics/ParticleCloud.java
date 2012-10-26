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
public class ParticleCloud extends AnimatedNGraph {

    int numberOfParticles;
    Random random = new Random();
    ArrayList<Particle> particles = new ArrayList<Particle>();

    double moveRatio = 0.3;
    double maxJump = 50;

    public ParticleCloud(int numberOfParticles) {
        super(3);

        this.numberOfParticles = numberOfParticles;

        createParticles();
    }

    private void createParticles() {
        for (int i = 0; i < numberOfParticles; i++) {
            addParticle();
        }
    }

    private double getSpread() {
        return 1 - getData(0);
    }

    private double getRotation() {
        return getData(1) * Math.PI * 2;
    }

    private double getSpeed() {
        return getData(2) * Math.PI * 2;
    }

    private void addParticle() {
        double radius = random.nextDouble();
        double angle = random.nextDouble() * Math.PI * 2;

        double maxRotation = Math.PI / 8;
        double angularVel = (random.nextDouble() - 0.5) * maxRotation * 2;

        DPoint position = getPosition(radius, angle);

        Particle p = new Particle(position, radius, angle, angularVel);

        particles.add(p);
    }

    private DPoint getPosition(double radius, double angle) {
        double x = radius * Math.cos(angle);
        double y = radius * Math.sin(angle);
        DPoint pos = new DPoint(x, y);
        return pos;
    }

    public void update(double dt, double realDt) {
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            updateParticle(p, dt, realDt);
        }
    }

    void updateParticle(Particle p, double dt, double realDt) {
        p.setAngularVelMultiplier(getSpeed());
        
        p.update(dt, realDt);

        double radius = p.getRadius();
        double angle = p.getAngle();

        radius *= getSpread();
        angle += getRotation();

        p.pushTowards(getPosition(radius, angle), moveRatio, maxJump);
    }

    public void paint(Graphics2D g) {
        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        double effectiveRadius = 0.5 * Math.min(windowWidth, windowHeight);

        DPoint center = new DPoint(windowWidth * 0.5, windowHeight * 0.5);

        g.setColor(Color.white);
        
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);

            DPoint position = p.getPosition();
            position = position.multiply(effectiveRadius);
            position = position.add(center);

            int x = (int) (Math.round(position.getX() - 1));
            int y = (int) (Math.round(position.getY() - 1));
            g.drawOval(x, y, 3, 3);
        }
    }

    class Particle {

        double radius;
        double angle;
        double angularVel;
        DPoint position;

        public Particle(DPoint position, double radius, double angle, double angularVel) {
            this.position = position;
            this.radius = radius;
            this.angle = angle;
            this.angularVel = angularVel;
        }

        double getRadius() {
            return radius;
        }

        double getAngle() {
            return angle;
        }

        double getAngularVel() {
            return angularVel;
        }

        DPoint getPosition() {
            return position;
        }

        void setPosition(DPoint point) {
            position = point;
        }

        void setAngularVel(double value) {
            angularVel = value;
        }

        void update(double dt, double realDt) {
            angle += angularVel * angularVelMult * dt;
        }

        void pushTowards(DPoint target, double ratio, double maxJump) {
            DPoint jump = target.subtract(position);
            jump = jump.multiply(ratio);
            jump.limit(maxJump);
            position = position.add(jump);
        }
        double angularVelMult = 1;

        void setAngularVelMultiplier(double mult) {
            angularVelMult = mult;
        }
    }

}


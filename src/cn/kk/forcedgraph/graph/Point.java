/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
package cn.kk.forcedgraph.graph;

import java.util.LinkedList;
import java.util.List;

public class Point {
    private static final List<Point> POINTS = new LinkedList<Point>();
    private static int nextPointId;
    private static final double REPULSION_CONSTANT = 100.0; // repulsion constant
    private static final double DAMPING_CONSTANT = 0.5; // damping constant, points lose velocity over time

    /**
     * points are slightly repulsed by other points
     */
    public static void applyCoulombsLaw() {
        for (Point p1 : POINTS) {
            for (Point p2 : POINTS) {
                if (p1 != p2) {
                    final Vector d = Vector.subtract(p1.position, p2.position);
                    final double distance = d.magnitude() + 1.0;
                    // apply force to each end point
                    d.normalize().multiply(REPULSION_CONSTANT / distance / distance);
                    p1.applyForce(Vector.divide(d, 0.5));
                    p2.applyForce(d.divide(-0.5));
                }
            }
        }
    }

    public static final void remove(Point p) {
        POINTS.remove(p);
    }

    public static void updatePosition(final double timestep) {
        for (Point p : POINTS) {
            p.position.add(Vector.multiply(p.velocity, timestep));
        }
    }

    public static void updateVelocity(final double timeStep) {
        for (Point p : POINTS) {
            p.velocity.add(p.force.multiply(timeStep)).multiply(DAMPING_CONSTANT);
            p.force.clear();
        }
    }

    private final int id;

    private Vector position;

    private double mass;

    private Vector velocity;

    private Vector force;

    public Point(final Vector position, final double mass) {
        this.id = nextPointId++;
        this.position = position;
        this.mass = mass;
        this.velocity = new Vector(0d, 0d);
        this.force = new Vector(0d, 0d);

        POINTS.add(this);
    }

    public void applyForce(final Vector force) {
        this.force.add(force.divide(this.mass));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Point other = (Point) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    /**
     * @return the force
     */
    public final Vector getForce() {
        return force;
    }

    /**
     * @return the mass
     */
    public final double getMass() {
        return mass;
    }

    /**
     * @return the position
     */
    public final Vector getPosition() {
        return position;
    }

    /**
     * @return the velocity
     */
    public final Vector getVelocity() {
        return velocity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.id;
    }
}

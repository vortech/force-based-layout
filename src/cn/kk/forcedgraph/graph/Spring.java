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

import java.util.HashSet;
import java.util.Set;

public final class Spring {
    private static final Set<Spring> SPRINGS = new HashSet<Spring>();
    private static int nextSpringId;

    public static void applyHookesLaw() {
        for (Spring s : SPRINGS) {
            // the direction of the spring
            final Vector d = Vector.subtract(s.point2.getPosition(), s.point1.getPosition());
            final double displacement = s.length - d.magnitude();
            // apply force to each end point
            d.normalize().multiply(s.springStiffness * displacement);
            s.point1.applyForce(Vector.multiply(d, -0.5));
            s.point2.applyForce(d.multiply(0.5));
        }
    }

    public static final void remove(Spring s) {
        SPRINGS.remove(s);
    }

    private final int id;
    private final Point point1;
    private final Point point2;

    private final double length;

    // spring constant (See Hooke's law) .. how stiff the spring is
    private final double springStiffness;

    public Spring(final Point point1, final Point point2, final double length, final double k) {
        this.id = nextSpringId++;
        this.point1 = point1;
        this.point2 = point2;
        this.length = length;
        this.springStiffness = k;
        SPRINGS.add(this);
    }

    /**
     * @return the length
     */
    public final double getLength() {
        return this.length;
    }

    /**
     * @return the point1
     */
    public final Point getPoint1() {
        return this.point1;
    }

    /**
     * @return the point2
     */
    public final Point getPoint2() {
        return this.point2;
    }

    /**
     * @return the springStiffness
     */
    public double getSpringStiffness() {
        return this.springStiffness;
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

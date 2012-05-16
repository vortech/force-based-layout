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

public final class Vector {
    public static Vector add(final Vector v1, final Vector v2) {
        return new Vector(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vector divide(final Vector v1, final double n) {
        if (n == 0d) {
            return new Vector(0d, 0d);
        } else {
            return new Vector(v1.x / n, v1.y / n);
        }
    }

    public static Vector multiply(final Vector v1, final double n) {
        return new Vector(v1.x * n, v1.y * n);
    }

    public static Vector normal(Vector vector) {
        return new Vector(-vector.getY(), vector.getX());
    }

    public static Vector normalize(Vector vector) {
        return Vector.divide(vector, vector.magnitude());
    }

    public static Vector random() {
        return new Vector(10.0 * (Math.random() - 0.5), 10.0 * (Math.random() - 0.5));
    }

    public static Vector subtract(final Vector v1, final Vector v2) {
        return new Vector(v1.x - v2.x, v1.y - v2.y);
    }

    private double x;

    private double y;

    public Vector(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(final Vector other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector clear() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    public Vector divide(final double n) {
        if (n == 0) {
            this.x = 0;
            this.y = 0;
        } else {
            this.x /= n;
            this.y /= n;
        }
        return this;
    }

    /**
     * @return the x
     */
    public final double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public final double getY() {
        return y;
    }

    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector multiply(final double n) {
        this.x *= n;
        this.y *= n;
        return this;
    }

    public Vector normal() {
        final double tmp = this.x;
        this.x = -this.y;
        this.y = tmp;
        return this;
    }

    public Vector normalize() {
        return this.divide(magnitude());
    }

    public void setX(final double x) {
        this.x = x;
    }

    public void setY(final double y) {
        this.y = y;

    }

    public Vector substract(final Vector other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
}

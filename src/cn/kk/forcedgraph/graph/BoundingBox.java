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

public final class BoundingBox {
    private final Vector bottomLeft;
    private final Vector topRight;

    /**
     * @param bottomLeft
     * @param topRight
     */
    public BoundingBox(Vector bottomLeft, Vector topRight) {
        super();
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    /**
     * @return the bottomLeft
     */
    public final Vector getBottomLeft() {
        return this.bottomLeft;
    }

    public final Vector getSize() {
        return Vector.subtract(this.topRight, this.bottomLeft);
    }

    /**
     * @return the topRight
     */
    public final Vector getTopRight() {
        return this.topRight;
    }

    public final void update(final BoundingBox bb) {
        final Vector bottomLeft2 = bb.getBottomLeft();
        this.bottomLeft.setX(bottomLeft2.getX());
        this.bottomLeft.setY(bottomLeft2.getY());
        final Vector topRight2 = bb.getTopRight();
        this.topRight.setX(topRight2.getX());
        this.topRight.setY(topRight2.getY());
    }

}

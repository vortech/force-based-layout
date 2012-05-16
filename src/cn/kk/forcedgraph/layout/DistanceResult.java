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
package cn.kk.forcedgraph.layout;

import cn.kk.forcedgraph.graph.Node;
import cn.kk.forcedgraph.graph.Point;

public class DistanceResult {
    private Node node;
    private Point point;
    private double distance = -1;

    public DistanceResult() {
    }

    public DistanceResult(Node node, Point point, double distance) {
        super();
        this.node = node;
        this.point = point;
        this.distance = distance;
    }

    /**
     * @return the distance
     */
    public final double getDistance() {
        return distance;
    }

    /**
     * @return the node
     */
    public final Node getNode() {
        return node;
    }

    /**
     * @return the point
     */
    public final Point getPoint() {
        return point;
    }

    public void update(final Node n, final Point p, final double d) {
        this.node = n;
        this.point = p;
        this.distance = d;
    }
}

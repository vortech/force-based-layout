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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import cn.kk.forcedgraph.graph.Edge;
import cn.kk.forcedgraph.graph.Graph;
import cn.kk.forcedgraph.graph.Node;
import cn.kk.forcedgraph.graph.Vector;

public abstract class PainterBase {
    public static final void drawCenteredString(final Graphics2D g, final FontMetrics fm, final String label,
            final double x1, final double y1, final double x2, final double y2) {
        final int w = fm.stringWidth(label);
        final int h = fm.getHeight() + fm.getAscent();
        final int x = (int) ((x1 + x2) / 2 - w / 2);
        final int y = (int) ((y1 + y2) / 2 - h);
        g.drawString(label, x, y);
    }

    public static final Vector intersectLineBox(final Vector p1, final Vector p2, final Vector p3, final double width,
            final double height) {
        final Vector tl = new Vector(p3.getX(), p3.getY());
        final Vector tr = new Vector(p3.getX() + width, p3.getY());
        final Vector bl = new Vector(p3.getX(), p3.getY() + height);
        final Vector br = new Vector(p3.getX() + width, p3.getY() + height);
        Vector result;
        if (null != (result = intersectLineLine(p1, p2, tl, tr))) {
            // top
            return result;
        }
        if (null != (result = intersectLineLine(p1, p2, tr, br))) {
            // right
            return result;
        }
        if (null != (result = intersectLineLine(p1, p2, br, bl))) {
            // bottom
            return result;
        }
        if (null != (result = intersectLineLine(p1, p2, bl, tl))) {
            // left
            return result;
        }
        return null;
    }

    public static final Vector intersectLineLine(Vector p1, Vector p2, Vector p3, Vector p4) {
        final double denom = ((p4.getY() - p3.getY()) * (p2.getX() - p1.getX()) - (p4.getX() - p3.getX())
                * (p2.getY() - p1.getY()));

        // lines are parallel
        if (denom == 0) {
            return null;
        }

        final double ua = ((p4.getX() - p3.getX()) * (p1.getY() - p3.getY()) - (p4.getY() - p3.getY())
                * (p1.getX() - p3.getX()))
                / denom;
        final double ub = ((p2.getX() - p1.getX()) * (p1.getY() - p3.getY()) - (p2.getY() - p1.getY())
                * (p1.getX() - p3.getX()))
                / denom;

        if (ua < 0 || ua > 1 || ub < 0 || ub > 1) {
            return null;
        }

        return new Vector(p1.getX() + ua * (p2.getX() - p1.getX()), p1.getY() + ua * (p2.getY() - p1.getY()));
    }

    private Color background = new Color(0xf6f6f6);

    protected BufferedImage bufferedImage;

    protected int width;

    protected int height;

    public PainterBase(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        clear(getGraphics());
        disposeGraphics();
    }

    public abstract void clear(Graphics2D g);

    public abstract void drawEdge(Graphics2D g, final GraphPanel panel, final Graph graph, final Edge edge,
            final Vector sp1, final Vector sp2);

    public abstract void drawNode(Graphics2D g, final GraphPanel panel, final Node node, final Vector sp);

    /**
     * @return the background
     */
    public Color getBackground() {
        return this.background;
    }

    /**
     * @return the height
     */
    public final int getHeight() {
        return this.height;
    }

    /**
     * @return the width
     */
    public final int getWidth() {
        return this.width;
    }

    public void paint(final Graphics g, final int x, final int y) {
        synchronized (this.bufferedImage) {
            g.drawImage(this.bufferedImage, x, y, null);
        }
    }

    /**
     * @param background
     *            the background to set
     */
    public void setBackground(Color background) {
        this.background = background;
    }

    public void setSize(int width, int height) {
        synchronized (this.bufferedImage) {
            this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            clear(getGraphics());
            disposeGraphics();
        }
        this.width = width;
        this.height = height;
    }

    private Graphics2D g;

    public final Graphics2D getGraphics() {
        if (g == null) {
            g = this.bufferedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return g;
    }

    public final void disposeGraphics() {
        g.dispose();
        g = null;
    }
}

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

import java.awt.Graphics2D;

import cn.kk.forcedgraph.callback.EdgeCallback;
import cn.kk.forcedgraph.callback.NodeCallback;
import cn.kk.forcedgraph.graph.Edge;
import cn.kk.forcedgraph.graph.Graph;
import cn.kk.forcedgraph.graph.Node;
import cn.kk.forcedgraph.graph.Point;
import cn.kk.forcedgraph.graph.Spring;
import cn.kk.forcedgraph.graph.Vector;
import cn.kk.forcedgraph.listener.GraphChangedListener;
import cn.kk.forcedgraph.listener.GraphFinishedListener;
import cn.kk.forcedgraph.listener.LayoutRenderListener;

public final class Renderer implements GraphChangedListener, LayoutRenderListener, EdgeCallback, NodeCallback,
        GraphFinishedListener {
    private final long interval;
    private final ForceDirectedLayout layout;
    private final GraphPanel panel;
    private Graphics2D g;

    /**
     * @param panel
     * @param interval
     * @param layout
     * @param clear
     * @param drawEdge
     * @param drawNode
     */
    public Renderer(final GraphPanel panel, final long interval, final ForceDirectedLayout layout) {
        this.panel = panel;
        this.interval = interval;
        this.layout = layout;

        this.layout.getGraph().addGraphChangedListener(this);
    }

    @Override
    public void call(Edge edge, Spring spring) {
        drawEdge(edge, spring.getPoint1().getPosition(), spring.getPoint2().getPosition());
    }

    @Override
    public void call(Node node, Point point) {
        drawNode(node, point.getPosition());
    }

    public void drawEdge(Edge edge, Vector p1, Vector p2) {
        final Graph graph = this.layout.getGraph();
        final Vector sp1 = this.panel.toScreen(p1);
        final Vector sp2 = this.panel.toScreen(p2);
        this.panel.getPainter().drawEdge(g, this.panel, graph, edge, sp1, sp2);
    }

    public void drawNode(Node node, Vector p) {
        final Vector sp = this.panel.toScreen(p);
        this.panel.getPainter().drawNode(g, this.panel, node, sp);
    }

    /**
     * @return the interval
     */
    public long getInterval() {
        return this.interval;
    }

    @Override
    public void onChanged() {
        this.panel.start();
    }

    @Override
    public void onFinished() {
        repaint();
    }

    @Override
    public void onRender() {
        repaint();
    }

    private static final int MAX_FPS = 46;
    private long lastPaint;

    private final void repaint() {
        final long now = System.currentTimeMillis();
        if (now - lastPaint > 1000 / MAX_FPS) {
            final PainterBase painter = this.panel.getPainter();
            synchronized (painter) {
                g = painter.getGraphics();
                painter.clear(g);
                this.layout.eachEdge(this);
                this.layout.eachNode(this);
                painter.disposeGraphics();
            }
            this.panel.repaint();
            lastPaint = now;
        }
    }
}

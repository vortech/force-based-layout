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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JPanel;

import cn.kk.forcedgraph.bean.EdgeData;
import cn.kk.forcedgraph.bean.NodeData;
import cn.kk.forcedgraph.graph.BoundingBox;
import cn.kk.forcedgraph.graph.Edge;
import cn.kk.forcedgraph.graph.Graph;
import cn.kk.forcedgraph.graph.Node;
import cn.kk.forcedgraph.graph.Vector;
import cn.kk.forcedgraph.listener.GraphFinishedListener;
import cn.kk.forcedgraph.listener.NodeSelectedListener;

public class GraphPanel extends JPanel implements GraphFinishedListener {
    private static final long serialVersionUID = 7657030046133585681L;
    private static final double ZOOM_FACTOR = 0.9;
    private static final int RENDERER_INTERVAL = 10;
    public static final Font DEFAULT_LABEL_FONT = new Font("Helvetica", Font.PLAIN, 12);
    public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final GraphicsEnvironment GRAPHICS_ENV = GraphicsEnvironment.getLocalGraphicsEnvironment();
    public static final GraphicsConfiguration GRAPHICS_CONF = GRAPHICS_ENV.getDefaultScreenDevice()
            .getDefaultConfiguration();

    private final ForceDirectedLayout layout;
    private final BoundingBox currentBB;
    private PainterBase painter;
    private FontMetrics fontMetrics;
    private final Renderer renderer;
    private Node selectedNode;
    private Node nearestNode;
    private boolean drawAnimation = true;
    private final List<NodeSelectedListener> selectedListeners;
    private final List<GraphFinishedListener> finishedListeners;

    public GraphPanel() {
        this(new Graph());
    }

    public GraphPanel(final ForceDirectedLayout layout, final int width, final int height) {
        super(true);
        setPreferredSize(new Dimension(width, height));
        this.layout = layout;
        // calculate bounding box of graph layout.. with ease-in
        this.currentBB = this.layout.getBoundingBox();
        this.painter = new PainterImpl(width, height);
        this.renderer = new Renderer(this, RENDERER_INTERVAL, this.layout);
        this.selectedListeners = new LinkedList<NodeSelectedListener>();
        this.finishedListeners = new LinkedList<GraphFinishedListener>();

        final GraphPanel panel = this;
        final MouseAdapter panelMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                panel.nearestNode = null;
                panel.start();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (panel.drawAnimation) {
                    DistanceResult r = nearest(e);
                    if (r != null) {
                        panel.nearestNode = r.getNode();
                        panel.start();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                DistanceResult r = nearest(e);
                if (r != null) {
                    panel.selectedNode = r.getNode();
                    panel.nearestNode = r.getNode();
                    panel.start();
                    synchronized (panel.selectedListeners) {
                        for (NodeSelectedListener l : panel.selectedListeners) {
                            l.onSelect(e, panel, r.getNode());
                        }
                    }
                }
            }
        };
        addMouseListener(panelMouseAdapter);
        addMouseMotionListener(panelMouseAdapter);
        setDefaultFont(DEFAULT_LABEL_FONT);
    }

    public GraphPanel(Graph graph) {
        this(graph, 320, 200);
    }

    public GraphPanel(Graph graph, final int width, final int height) {
        this(new ForceDirectedLayout(graph), width, height);
    }

    public Edge addEdge(Node node, Node otherNode, EdgeData edgeData) {
        return this.layout.addEdge(node, otherNode, edgeData);
    }

    public final void addGraphFinishedListener(final GraphFinishedListener l) {
        synchronized (this.finishedListeners) {
            this.finishedListeners.add(l);
        }
    }

    public Node addNode(NodeData nodeData) {
        return this.layout.addNode(nodeData);
    }

    public final void addNodeSelectedListener(final NodeSelectedListener l) {
        synchronized (this.selectedListeners) {
            this.selectedListeners.add(l);
        }
    }

    public final double calculateStringWidth(String text) {
        return this.fontMetrics.stringWidth(text);
    }

    public final Vector fromScreen(Vector s) {
        unzoom(s, ZOOM_FACTOR);
        final Vector size = this.currentBB.getSize();
        final double px = (s.getX() / this.painter.getWidth()) * size.getX() + this.currentBB.getBottomLeft().getX();
        final double py = (s.getY() / this.painter.getHeight()) * size.getY() + this.currentBB.getBottomLeft().getY();
        return new Vector(px, py);
    }

    /**
     * @return the nearestNode
     */
    public final Node getNearestNode() {
        return this.nearestNode;
    }

    public final PainterBase getPainter() {
        return this.painter;
    }

    /**
     * @return the selectedNode
     */
    public final Node getSelectedNode() {
        return this.selectedNode;
    }

    /**
     * @return the drawAnimation
     */
    public boolean isDrawAnimation() {
        return this.drawAnimation;
    }

    public final DistanceResult nearest(MouseEvent e) {
        return this.layout.nearest(fromScreen(new Vector(e.getX(), e.getY())));
    }

    @Override
    public void onFinished() {
        this.renderer.onFinished();
        synchronized (this.finishedListeners) {
            for (GraphFinishedListener l : this.finishedListeners) {
                l.onFinished();
            }
        }
    }

    private static final int MAX_FPS = 46;
    private long lastPaint;

    @Override
    protected void paintComponent(Graphics g) {
        final long now = System.currentTimeMillis();
        if (now - lastPaint > 1000 / MAX_FPS) {
            this.painter.paint(g, 0, 0);
            lastPaint = now;
        }
    }

    public void removeEdge(Edge edge) {
        this.layout.removeEdge(edge);
    }

    public final void removeGraphFinishedListener(final GraphFinishedListener l) {
        synchronized (this.finishedListeners) {
            this.finishedListeners.remove(l);
        }
    }

    public void removeNode(Node node) {
        this.layout.removeNode(node);
        if (node == this.selectedNode) {
            this.selectedNode = null;
        }
    }

    public final void removeNodeSelectedListener(final NodeSelectedListener l) {
        synchronized (this.selectedListeners) {
            this.selectedListeners.remove(l);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        synchronized (this.painter) {
            this.painter.setSize(width, height);
        }
        start();
    }

    private void setDefaultFont(Font font) {
        this.fontMetrics = this.getFontMetrics(font);
    }

    /**
     * @param drawAnimation
     *            the drawAnimation to set
     */
    public void setDrawAnimation(boolean drawAnimation) {
        this.drawAnimation = drawAnimation;
        this.nearestNode = null;
        stop();
        start();
    }

    /**
     * @param painter
     * @param canvas
     *            the canvas to set
     */
    public void setPainter(PainterBase painter) {
        this.painter = painter;
    }

    public void start() {
        if (this.drawAnimation) {
            this.layout.startRendering(this.renderer.getInterval(), this.renderer, this, this.currentBB);
        } else {
            this.layout.startRendering(0, null, this, this.currentBB);
        }
    }

    public void stop() {
        this.layout.stopRendering();
    }

    /**
     * convert to/from screen coordinates
     * 
     * @param p
     * @return
     */
    public final Vector toScreen(Vector p) {
        final Vector size = this.currentBB.getSize();
        final double sx = Vector.subtract(p, this.currentBB.getBottomLeft()).divide(size.getX()).getX()
                * this.painter.getWidth();
        final double sy = Vector.subtract(p, this.currentBB.getBottomLeft()).divide(size.getY()).getY()
                * this.painter.getHeight();
        return zoom(new Vector(sx, sy), ZOOM_FACTOR);
    }

    private final Vector unzoom(final Vector vector, final double factor) {
        vector.setX((vector.getX() - getWidth() * (1d - factor) / 2d) / factor);
        vector.setY((vector.getY() - getHeight() * (1d - factor) / 2d) / factor);
        return vector;
    }

    private final Vector zoom(final Vector vector, final double factor) {
        vector.setX(vector.getX() * factor + getWidth() * (1d - factor) / 2d);
        vector.setY(vector.getY() * factor + getHeight() * (1d - factor) / 2d);
        return vector;
    }
}
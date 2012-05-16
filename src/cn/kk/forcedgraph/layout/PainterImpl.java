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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import cn.kk.forcedgraph.bean.EdgeData;
import cn.kk.forcedgraph.bean.NodeData;
import cn.kk.forcedgraph.graph.Edge;
import cn.kk.forcedgraph.graph.Graph;
import cn.kk.forcedgraph.graph.Node;
import cn.kk.forcedgraph.graph.Vector;

public final class PainterImpl extends PainterBase {

    private static final Paint SELECTED_PAINT = Color.BLACK;
    private static final Color SELECTED_LABEL_COLOR = Color.BLACK;
    private static final Color SELECTED_BG_COLOR = Color.WHITE;
    private static final Color SELECTED_BOX_COLOR = new Color(0xF00019);
    private static final Stroke SELECTED_STROKE = new BasicStroke(3.0f);

    private static final Paint NEAREST_PAINT = Color.DARK_GRAY;
    private static final Color NEAREST_LABEL_COLOR = Color.BLACK;
    private static final Color NEAREST_BG_COLOR = new Color(0xFBFFF7);
    private static final Color NEAREST_BOX_COLOR = NEAREST_BG_COLOR.darker();
    private static final Stroke NEAREST_STROKE = new BasicStroke(2.5f);

    public PainterImpl(final int width, final int height) {
        super(width, height);
    }

    @Override
    public void clear(Graphics2D g) {
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.width, this.height);
    }

    private final void drawArrow(final double x1, final double y1, final double x2, final double y2,
            Vector intersection, final double arrowWidth, final double arrowLength, final Graphics2D g,
            GeneralPath polyLine) {
        final AffineTransform oldTransform = g.getTransform();
        AffineTransform arrowTransform = new AffineTransform();
        arrowTransform.translate(intersection.getX(), intersection.getY());
        arrowTransform.rotate(Math.atan2(y2 - y1, x2 - x1));
        g.transform(arrowTransform);
        polyLine.moveTo(-arrowLength, arrowWidth);
        polyLine.lineTo(0, 0);
        polyLine.lineTo(-arrowLength, -arrowWidth);
        polyLine.lineTo(-arrowLength * 0.8, -0);
        polyLine.closePath();
        g.fill(polyLine);
        g.setTransform(oldTransform);
    }

    @Override
    public void drawEdge(Graphics2D g, final GraphPanel panel, final Graph graph, final Edge edge, final Vector sp1,
            final Vector sp2) {
        final Node edgeTarget = edge.getTarget();
        final Node edgeSource = edge.getSource();
        final EdgeData edgeData = edge.getData();

        // links
        final List<Edge> from = graph.getEdges(edgeSource, edgeTarget);
        final List<Edge> to = graph.getEdges(edgeTarget, edgeSource);
        final int fromSize = from.size();
        final int toSize = to.size();
        final double total = fromSize + toSize;

        final double x1 = sp1.getX();
        final double y1 = sp1.getY();
        final double x2 = sp2.getX();
        final double y2 = sp2.getY();

        final Vector direction = new Vector(x2 - x1, y2 - y1);
        final Vector normal = Vector.normal(direction).normalize();

        // Figure out edge's position in relation to other edges between the same nodes
        double n = 0d;
        for (int i = 0; i < fromSize; i++) {
            if (edge.equals(from.get(i))) {
                n = i;
                break;
            }
        }

        final double spacing = 6.0;
        // Figure out how far off center the line should be drawn
        final Vector offset = normal.multiply(-((total - 1) * spacing) / 2.0 + n * spacing);

        final Vector s1 = sp1.add(offset);
        final Vector s2 = sp2.add(offset);

        final double boxWidth = edgeTarget.getWidth(panel);
        final double boxHeight = edgeTarget.getHeight(panel);

        Vector intersection = intersectLineBox(s1, s2, new Vector(x2 - boxWidth / 2.0, y2 - boxHeight / 2.0),
                boxWidth, boxHeight);
        if (intersection == null) {
            intersection = s2;
        }

        final boolean directional = edgeData.isDirectional();
        final float weight = edgeData.getWeight();
        final float lineWidth;
        final double arrowWidth;
        final double arrowLength;

        final Node selectedNode = panel.getSelectedNode();
        boolean selected = false;
        if (selectedNode != null && (selectedNode == edgeSource)) { // || selectedNode == edgeTarget
            lineWidth = Math.max(weight * 2f, 1f);
            arrowWidth = lineWidth * 2;
            arrowLength = lineWidth * 4d;
            selected = true;
        } else {
            lineWidth = Math.max(weight * 1f, 0.5f);
            arrowWidth = lineWidth * 2;
            arrowLength = lineWidth * 4d;
        }

        final FontMetrics fm = g.getFontMetrics();

        // line
        final Vector lineEnd;
        if (directional) {
            lineEnd = Vector.subtract(intersection, direction.normalize().multiply(arrowLength * 0.5));
        } else {
            lineEnd = s2;
        }

        GeneralPath polyLine = new GeneralPath();
        polyLine.moveTo(s1.getX(), s1.getY());
        polyLine.lineTo(lineEnd.getX(), lineEnd.getY());
        polyLine.closePath();

        if (selected) {
            AffineTransform lineTransform = new AffineTransform();
            g.setStroke(new BasicStroke(lineWidth));
            g.setColor(edgeData.getColor());
            lineTransform.translate(0.5d, 0.5d);
            polyLine.transform(lineTransform);
            g.draw(polyLine);
            // arrow
            if (directional) {
                drawArrow(x1, y1, x2, y2, intersection, arrowWidth, arrowLength, g, polyLine);
            }
            g.setStroke(edgeData.getLineStroke());
            lineTransform.translate(-0.5d, -0.5d);
            polyLine.transform(lineTransform);
            g.draw(polyLine);
        } else {
            g.setStroke(new BasicStroke(lineWidth));
            g.setColor(edgeData.getColor());
            g.draw(polyLine);
            // arrow
            if (directional) {
                drawArrow(x1, y1, x2, y2, intersection, arrowWidth, arrowLength, g, polyLine);
            }
        }

        // label
        if (edgeData.getLabel() != null) {
            g.setFont(edgeData.getFont());
            g.setColor(edgeData.getLabelColor());
            drawCenteredString(g, fm, edgeData.getLabel(), x1, y1, x2, y2);
        }
    }

    @Override
    public void drawNode(final Graphics2D g, final GraphPanel panel, final Node node, final Vector sp) {
        final NodeData nodeData = node.getData();

        final FontMetrics fm = g.getFontMetrics();
        final int h = fm.getAscent();

        // AffineTransform oldTransform = g.getTransform();
        final double boxWidth = node.getWidth(panel);
        final double boxHeight = node.getHeight(panel);

        final double x = sp.getX();
        final double y = sp.getY();

        final double boxY = y - 10d;
        final double boxX = x - boxWidth / 2d;

        // fill background
        RoundRectangle2D box = new RoundRectangle2D.Double(boxX, boxY, boxWidth, boxHeight, 6, 6);
        if (node == panel.getSelectedNode()) {
            g.setPaint(SELECTED_PAINT);
            g.setColor(SELECTED_BG_COLOR);
            g.fill(box);
            g.setColor(SELECTED_BOX_COLOR);
            g.setStroke(SELECTED_STROKE);
            g.draw(box);
            g.setColor(SELECTED_LABEL_COLOR);
            g.setFont(nodeData.getFont().deriveFont(Font.ITALIC));
        } else if (node == panel.getNearestNode()) {
            g.setPaint(NEAREST_PAINT);
            g.setColor(NEAREST_BG_COLOR);
            g.fill(box);
            g.setStroke(NEAREST_STROKE);
            g.setColor(NEAREST_BOX_COLOR);
            g.draw(box);
            g.setColor(NEAREST_LABEL_COLOR);
            g.setFont(nodeData.getFont());
        } else {
            g.setColor(nodeData.getBackgroundColor());
            g.fill(box);
            g.setStroke(nodeData.getStroke());
            g.setColor(nodeData.getBoxColor());
            g.draw(box);
            g.setColor(nodeData.getLabelColor());
            g.setFont(nodeData.getFont());
        }

        // draw label
        g.drawString(nodeData.getLabel(), (int) (x - boxWidth / 2 + 5), (int) (y - 8) + h);

        // g.setTransform(oldTransform);
    }

}

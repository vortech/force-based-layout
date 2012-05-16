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
package cn.kk.forcedgraph.bean;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import cn.kk.forcedgraph.layout.GraphPanel;

public class NodeData {
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
    public static final Font DEFAULT_FONT = GraphPanel.DEFAULT_LABEL_FONT;
    public static final Color DEFAULT_BG_COLOR = new Color(0xE8FFFB);
    public static final Color DEFAULT_BOX_COLOR = DEFAULT_BG_COLOR.darker();
    public static final Color DEFAULT_LABEL_COLOR = Color.BLACK;

    private Font font = DEFAULT_FONT;
    private Color bgColor = DEFAULT_BG_COLOR;
    private Color boxColor = DEFAULT_BOX_COLOR;
    private Color labelColor = DEFAULT_LABEL_COLOR;
    private Stroke stroke = DEFAULT_STROKE;

    private double mass = 1.0;
    private String label;

    public NodeData() {

    }

    /**
     * @param color
     *            the color to set
     */
    public final NodeData backgroundColor(Color color) {
        this.bgColor = color;
        return this;
    }

    /**
     * @param boxColor
     *            the boxColor to set
     */
    public final NodeData boxColor(Color boxColor) {
        this.boxColor = boxColor;
        return this;
    }

    /**
     * @param font
     *            the font to set
     */
    public final NodeData font(Font font) {
        this.font = font;
        return this;
    }

    /**
     * @return the color
     */
    public final Color getBackgroundColor() {
        return bgColor;
    }

    /**
     * @return the boxColor
     */
    public final Color getBoxColor() {
        return this.boxColor;
    }

    /**
     * @return the font
     */
    public final Font getFont() {
        return font;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the labelColor
     */
    public final Color getLabelColor() {
        return labelColor;
    }

    /**
     * @return the mass
     */
    public double getMass() {
        return mass;
    }

    /**
     * @return the stroke
     */
    public final Stroke getStroke() {
        return stroke;
    }

    /**
     * @param label
     *            the label to set
     */
    public NodeData label(String label) {
        this.label = label;
        return this;
    }

    /**
     * @param labelColor
     *            the labelColor to set
     */
    public final NodeData labelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    /**
     * @param mass
     *            the mass to set
     */
    public NodeData mass(double mass) {
        this.mass = mass;
        return this;
    }

    /**
     * @param stroke
     *            the stroke to set
     */
    public final NodeData stroke(Stroke stroke) {
        this.stroke = stroke;
        return this;
    }
}

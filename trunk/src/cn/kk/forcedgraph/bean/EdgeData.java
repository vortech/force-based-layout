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
import java.awt.Paint;
import java.awt.Stroke;

import cn.kk.forcedgraph.layout.GraphPanel;

public class EdgeData {
    public static final Stroke DEFAULT_STROKE = new BasicStroke(0.5f);
    public static final Paint DEFAULT_PAINT = Color.BLACK;
    public static final Font DEFAULT_FONT = GraphPanel.DEFAULT_LABEL_FONT.deriveFont(GraphPanel.DEFAULT_LABEL_FONT
            .getSize2D() - 2f);
    public static final Color DEFAULT_COLOR = new Color(0x007C23);
    public static final Color DEFAULT_LABEL_COLOR = new Color(91, 166, 236);

    private double length = 1.0;
    private float weight = 1.0f;
    private boolean directional = true;
    private Stroke lineStroke = DEFAULT_STROKE;
    private String label;
    private Font font = DEFAULT_FONT;
    private Color color = DEFAULT_COLOR;
    private Color labelColor = DEFAULT_LABEL_COLOR;

    public EdgeData() {

    }

    /**
     * @param color
     *            the color to set
     */
    public final EdgeData color(Color color) {
        this.color = color;
        return this;
    }

    /**
     * @param directional
     *            the directional to set
     */
    public final EdgeData directional(boolean directional) {
        this.directional = directional;
        return this;
    }

    /**
     * @param font
     *            the font to set
     */
    public EdgeData font(Font font) {
        this.font = font;
        return this;
    }

    /**
     * @return the color
     */
    public final Color getColor() {
        return color;
    }

    /**
     * @return the font
     */
    public Font getFont() {
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
    public Color getLabelColor() {
        return labelColor;
    }

    /**
     * @return the length
     */
    public final double getLength() {
        return length;
    }

    /**
     * @return the stroke
     */
    public Stroke getLineStroke() {
        return lineStroke;
    }

    /**
     * @return the weight
     */
    public final float getWeight() {
        return this.weight;
    }

    /**
     * @return the directional
     */
    public final boolean isDirectional() {
        return directional;
    }

    /**
     * @param label
     *            the label to set
     */
    public EdgeData label(String label) {
        this.label = label;
        return this;
    }

    /**
     * @param labelColor
     *            the labelColor to set
     */
    public EdgeData labelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    /**
     * @param length
     *            the length to set
     */
    public final EdgeData length(double length) {
        this.length = length;
        return this;
    }

    /**
     * @param stroke
     *            the stroke to set
     */
    public EdgeData lineStroke(Stroke stroke) {
        this.lineStroke = stroke;
        return this;
    }

    /**
     * @param weight
     *            the weight to set
     */
    public final EdgeData weight(float weight) {
        this.weight = weight;
        return this;
    }
}

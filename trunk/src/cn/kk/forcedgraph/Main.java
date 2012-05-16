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
package cn.kk.forcedgraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.kk.forcedgraph.bean.EdgeData;
import cn.kk.forcedgraph.bean.NodeData;
import cn.kk.forcedgraph.graph.Graph;
import cn.kk.forcedgraph.graph.Node;
import cn.kk.forcedgraph.layout.GraphPanel;
import cn.kk.forcedgraph.listener.GraphFinishedListener;
import cn.kk.forcedgraph.listener.NodeSelectedListener;

public class Main extends JFrame implements NodeSelectedListener, GraphFinishedListener {
    private final ArrayList<Node> nodes;
    private final GraphPanel panel;
    private final JCheckBox cbAnimated;
    private final JButton btnAdd;
    private final JButton btnRemove;
    private final JButton btnReset;
    private final JLabel lblCounter;
    private final JPanel cmdPanel;

    private static final String[] NAMES = { "Mohamed", "Ahmed", "Mohammed", "Said", "Rachid", "Mustapha", "Youssef",
            "Hassan", "Abdel-salam", "Ali", "Fatima", "Khadija", "Aicha", "Malika", "Naima", "Rachida", "Nadia",
            "Karima", "Amina", "Saida", "Ren", "Hiroto", "Sōta", "Yūma", "Sora", "Yūto  Yūto", "Kotarō", "Taiga",
            "Itsuki", "Yua", "Yui", "Aoi", "Hina", "Riko", "Rin", "Sakura", "Yuna", "Miu", "Misaki", "Ben", "Leon",
            "Lucas", "Finn", "Jonas", "Maximilian", "Luis", "Paul", "Felix", "Luca", "Oliver", "Jack", "Harry",
            "Alfie", "Charlie", "Thomas", "William", "Joshua", "George", "James", "Francesco", "Alessandro", "Andrea",
            "Lorenzo", "Matteo", "Mattia", "Gabriele", "Riccardo", "Davide", "Leonardo", "Nathan", "Lucas", "Jules",
            "Enzo", "Gabriel", "Louis", "Arthur", "Raphael", "Mathis", "Ethan", "Daniel", "Alejandro", "Pablo", "Hugo",
            "álvaro", "Adrián", "David", "Javier", "Diego", "Mario", "Olivia", "Sophie", "Emily", "Lily", "Amelia",
            "Jessica", "Ruby", "Chloe", "Grace", "Evie", "Mia", "Emma", "Hannah", "Anna", "Leah", "Leonie", "Lina",
            "Marie", "Sophia", "Lena", "Emma", "Jade", "Zoé", "Chloé", "Léa", "Manon", "Inès", "Louise", "Lilou",
            "Sofia", "Giulia", "Sara", "Martina", "Giorgia", "Chiara", "Aurora", "Alice", "Emma", "Alessia", "Lucía",
            "Paula", "María", "Daniela", "Sara", "Carla", "Claudia", "Sofía", "Alba", "Irene", "Aiden", "Jackson",
            "Mason", "Liam", "Jacob", "Jayden", "Ethan", "Noah", "Lucas", "Logan", "Sophia", "Emma", "Isabella",
            "Olivia", "Ava", "Lily", "Chloe", "Madison", "Emily", "Abigail" };

    private static final Color[] DARK_COLORS = { new Color(0x00008B), new Color(0x008B8B), new Color(0xB8860B),
            new Color(0x69A909), new Color(0x006400), new Color(0x7DB70B), new Color(0x8B008B), new Color(0x556B0F),
            new Color(0x7F8C00), new Color(0x9902CC), new Color(0x8B0000), new Color(0xE9067A), new Color(0x8F0C8F),
            new Color(0x480D8B), new Color(0x0F474F), new Color(0x00CED1), new Color(0x9400D3), new Color(0x7F0493) };

    private static final Color[] LIGHT_COLORS = { new Color(0xFFFACD), new Color(0xcDe8f6), new Color(0xF0c0c0),
            new Color(0xE0FFFF), new Color(0xFAFAD2), new Color(0x9cEEc0), new Color(0xFFc6C1), new Color(0xFFA0cA),
            new Color(0xc7eEFA), new Color(0xFFE4C4), new Color(0xFFFFE0), new Color(0xFFEBCD), new Color(0xFFFFF0),
            new Color(0xcFEEEE), new Color(0xF0F8FF), new Color(0xFFFAFA), new Color(0xFFE4E1), new Color(0xFFF5EE),
            new Color(0xF0FFFF), new Color(0xFFF0F5), new Color(0xFDF5E6), new Color(0xF5FFFA) };

    private static final Color[] MEDIUM_COLORS = { new Color(0x7FFFD4), new Color(0x20B2AA), new Color(0x8A2BE2),
            new Color(0xA52A2A), new Color(0x5F9EA0), new Color(0x7FFF00), new Color(0xD2691E), new Color(0xB0C4DE),
            new Color(0xFF7F50), new Color(0x6495ED), new Color(0xDC143C), new Color(0x1E90FF), new Color(0xB22222),
            new Color(0x228B22), new Color(0xFFD700), new Color(0xDAA520), new Color(0xADFF2F), new Color(0xFF69B4),
            new Color(0xCD5C5C), new Color(0x4B0082), new Color(0xF0E68C), new Color(0x7CFC00), new Color(0x32CD32),
            new Color(0xFAF0E6), new Color(0x66CDAA), new Color(0x0000CD), new Color(0xBA55D3), new Color(0x9370DB),
            new Color(0x3CB371), new Color(0x7B68EE), new Color(0x00FA9A), new Color(0x48D1CC), new Color(0xC71585),
            new Color(0x191970), new Color(0xFFE4B5), new Color(0x6B8E23), new Color(0xFFA500), new Color(0xFF4500),
            new Color(0xCD853F), new Color(0x4169E1), new Color(0x8B4513), new Color(0xF4A460), new Color(0x2E8B57),
            new Color(0xA0522D), new Color(0x6A5ACD), new Color(0x00FF7F), new Color(0x4682B4), new Color(0xFF6347),
            new Color(0x40E0D0), };

    private static final String[] EMOTIONAL_VERBS = { "envies", "fears", "dislikes", "hates", "likes", "loves",
            "prefers", "wants" };

    /**
     * @param args
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.reset();

        // panel.setDrawAnimation(false);
        main.setVisible(true);
    }

    public Main() {
        super("Forced Graph Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.nodes = new ArrayList<Node>();
        this.panel = new GraphPanel(new Graph(), 640, 480);
        this.panel.addNodeSelectedListener(this);
        final ActionMap actionMap = this.panel.getActionMap();
        final InputMap inputMap = this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "reset");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "add");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "add");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "add");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "remove");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "remove");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "remove");
        actionMap.put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        actionMap.put("add", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode();
            }
        });
        actionMap.put("remove", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Main.this.nodes.isEmpty()) {
                    final Node selectedNode = Main.this.panel.getSelectedNode();
                    if (selectedNode == null) {
                        removeNode(Main.this.nodes.get((int) (Math.random() * Main.this.nodes.size())));
                    } else {
                        removeNode(selectedNode);
                    }
                }
            }
        });

        this.cbAnimated = new JCheckBox("动画");
        this.cbAnimated.setSelected(true);
        this.cbAnimated.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setAnimated(Main.this.cbAnimated.isSelected());
            }
        });
        this.btnAdd = new JButton("(+) 添加");
        this.btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode();
            }
        });
        this.btnRemove = new JButton("(-) 删除");
        this.btnRemove.setEnabled(false);
        this.btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeNode(Main.this.panel.getSelectedNode());
            }
        });
        this.btnReset = new JButton("(ESC) 重置");
        this.btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        this.lblCounter = new JLabel("（顶点数目：" + 0 + "）");

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this.panel, BorderLayout.CENTER);

        this.cmdPanel = new JPanel();
        this.cmdPanel.add(this.cbAnimated);
        this.cmdPanel.add(new JSeparator());
        this.cmdPanel.add(this.btnAdd);
        this.cmdPanel.add(this.btnRemove);
        this.cmdPanel.add(this.btnReset);
        this.cmdPanel.add(new JSeparator());
        this.cmdPanel.add(this.lblCounter);

        contentPane.add(this.cmdPanel, BorderLayout.PAGE_END);
        pack();

        this.panel.addGraphFinishedListener(this);
    }

    public void addNode() {
        (new Thread() {
            @Override
            public void run() {
                synchronized (nodes) {
                    final String label = NAMES[(int) (Math.random() * NAMES.length)];
                    final Color labelColor = DARK_COLORS[(int) (Math.random() * DARK_COLORS.length)];
                    final Color nodeColor = LIGHT_COLORS[(int) (Math.random() * LIGHT_COLORS.length)];
                    final Color boxColor = nodeColor.darker();
                    final double mass = Math.random() * 10 + 1;
                    final Node node = panel.addNode(new NodeData().label(label).labelColor(labelColor)
                            .backgroundColor(nodeColor).boxColor(boxColor).mass(mass));

                    final int size = nodes.size();
                    if (size > 0) {
                        final Color edgeColor = MEDIUM_COLORS[(int) (Math.random() * MEDIUM_COLORS.length)];
                        final Color edgeLabelColor = edgeColor.darker();
                        final int edges = (int) Math.round(Math.random() * Math.min(size, 20)) / 2;
                        final float weight = (float) (Math.random() * 3 + 1);
                        if (edges > 0) {
                            for (int i = edges; i != 0; i--) {
                                final Node otherNode = nodes.get((int) (Math.random() * size));
                                final boolean directed = 0 != (int) (Math.random() * 5);
                                final String edgeLabel;
                                if (directed && 1 == Math.round(Math.random())) {
                                    edgeLabel = EMOTIONAL_VERBS[(int) (Math.random() * EMOTIONAL_VERBS.length)];
                                } else {
                                    edgeLabel = null;
                                }
                                panel.addEdge(node, otherNode,
                                        new EdgeData().color(edgeColor).labelColor(edgeLabelColor).weight(weight)
                                                .directional(directed).label(edgeLabel));
                            }
                        }
                    }
                    nodes.add(node);
                    lblCounter.setText("（顶点数目：" + nodes.size() + "）");
                }
            }
        }).start();
    }

    @Override
    public void onFinished() {
        // System.out.println("运算结束。");
    }

    @Override
    public void onSelect(final MouseEvent e, final GraphPanel panel, final Node node) {
        this.btnRemove.setEnabled(true);
    }

    public void removeNode(final Node node) {
        (new Thread() {
            @Override
            public void run() {
                synchronized (nodes) {
                    if (node != null) {
                        nodes.remove(node);
                        panel.removeNode(node);
                    }
                    btnRemove.setEnabled(false);
                    lblCounter.setText("（顶点数目：" + nodes.size() + "）");
                }
            }
        }).start();
    }

    public void reset() {
        synchronized (nodes) {
            List<Node> tmp = new ArrayList<Node>(nodes);
            for (Node n : tmp) {
                removeNode(n);
            }
            for (int i = 0; i < 10; i++) {
                addNode();
            }
        }
    }

    public void setAnimated(boolean animated) {
        synchronized (this.panel) {
            this.panel.setDrawAnimation(animated);
        }
    }

}

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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.kk.forcedgraph.bean.EdgeData;
import cn.kk.forcedgraph.bean.NodeData;
import cn.kk.forcedgraph.filter.EdgeFilter;
import cn.kk.forcedgraph.filter.NodeFilter;
import cn.kk.forcedgraph.listener.EdgeRemovedListener;
import cn.kk.forcedgraph.listener.GraphChangedListener;
import cn.kk.forcedgraph.listener.NodeRemovedListener;

public final class Graph {
    private static final List<Edge> EMPTY_EDGES = Collections.emptyList();
    private final Set<Node> nodes;
    private final Set<Edge> edges;
    private final Map<Node, Map<Node, List<Edge>>> adjacency;
    private final List<GraphChangedListener> eventListeners;
    private NodeRemovedListener nodeRemovedListener;
    private EdgeRemovedListener edgeRemovedListener;

    private int nextNodeId;
    private int nextEdgeId;

    public Graph() {
        this.eventListeners = new LinkedList<GraphChangedListener>();
        this.nodes = new HashSet<Node>();
        this.edges = new HashSet<Edge>();
        this.adjacency = new HashMap<Node, Map<Node, List<Edge>>>();
    }

    public Edge addEdge(final Edge edge) {
        if (this.edges.add(edge)) {
            final Node src = edge.getSource();
            final Node tgt = edge.getTarget();
            if (this.nodes.containsAll(Arrays.asList(src, tgt))) {
                Map<Node, List<Edge>> srcAdjacency = this.adjacency.get(src);
                if (srcAdjacency == null) {
                    srcAdjacency = new HashMap<Node, List<Edge>>();
                    this.adjacency.put(src, srcAdjacency);
                }
                List<Edge> adjacencyEdges = srcAdjacency.get(tgt);
                if (adjacencyEdges == null) {
                    adjacencyEdges = new LinkedList<Edge>();
                    srcAdjacency.put(tgt, adjacencyEdges);
                }
                adjacencyEdges.add(edge);

                fireGraphChanged();
            } else {
                this.edges.remove(edge);
                throw new IllegalArgumentException("Node/s in the given edge is/are unknown: " + edge.toString());
            }
        }
        return edge;
    }

    public void addGraphChangedListener(final GraphChangedListener l) {
        synchronized (this.eventListeners) {
            this.eventListeners.add(l);
        }
    }

    public Node addNode(final Node node) {
        if (this.nodes.add(node)) {
            fireGraphChanged();
        }
        return node;
    }

    public void detachNode(final Node node) {
        final Set<Edge> tmpEdges = new HashSet<Edge>(this.edges);
        for (Edge e : tmpEdges) {
            if (node.equals(e.getSource()) || node.equals(e.getTarget())) {
                removeEdge(e);
            }
        }

        fireGraphChanged();
    }

    public void filterEdges(final EdgeFilter filter) {
        final Set<Edge> tmpEdges = new HashSet<Edge>(this.edges);
        for (Edge e : tmpEdges) {
            if (!filter.accept(e)) {
                removeEdge(e);
            }
        }
    }

    public void filterNodes(final NodeFilter filter) {
        final Set<Node> tmpNodes = new HashSet<Node>(this.nodes);
        for (Node n : tmpNodes) {
            if (!filter.accept(n)) {
                removeNode(n);
            }
        }
    }

    private final void fireGraphChanged() {
        synchronized (this.eventListeners) {
            for (GraphChangedListener l : this.eventListeners) {
                l.onChanged();
            }
        }
    }

    /**
     * @return the adjacency
     */
    public final Map<Node, Map<Node, List<Edge>>> getAdjacency() {
        return adjacency;
    }

    /**
     * @return the edgeRemovedListener
     */
    public EdgeRemovedListener getEdgeRemovedListener() {
        return this.edgeRemovedListener;
    }

    /**
     * @return the edges
     */
    public final Set<Edge> getEdges() {
        return edges;
    }

    public List<Edge> getEdges(final Node srcNode, final Node tgtNode) {
        final Map<Node, List<Edge>> srcAdjacency = this.adjacency.get(srcNode);
        final List<Edge> adjacencyEdges;
        if (srcAdjacency != null && (adjacencyEdges = srcAdjacency.get(tgtNode)) != null) {
            return adjacencyEdges;
        }
        return EMPTY_EDGES;
    }

    /**
     * @return the eventListeners
     */
    public final List<GraphChangedListener> getEventListeners() {
        return eventListeners;
    }

    /**
     * @return the nodeRemovedListener
     */
    public NodeRemovedListener getNodeRemovedListener() {
        return this.nodeRemovedListener;
    }

    /**
     * @return the nodes
     */
    public final Set<Node> getNodes() {
        return this.nodes;
    }

    public void merge(final Set<Node> nodes, final Set<Edge> edges) {
        this.nodes.addAll(nodes);
        this.edges.addAll(edges);
    }

    public Edge newEdge(final Node source, final Node target, final EdgeData data) {
        return addEdge(new Edge(this.nextEdgeId++, source, target, data));
    }

    public Node newNode(final NodeData data) {
        return addNode(new Node(this.nextNodeId++, data));
    }

    public void removeEdge(final Edge e) {
        if (this.edges.remove(e)) {
            final Set<Node> srcs = this.adjacency.keySet();
            Map<Node, List<Edge>> srcAdjacency;
            Set<Node> tgts;

            for (Node src : srcs) {
                srcAdjacency = this.adjacency.get(src);
                tgts = srcAdjacency.keySet();
                for (Node tgt : tgts) {
                    srcAdjacency.get(tgt).remove(e);
                }
            }
            fireGraphChanged();
            if (this.edgeRemovedListener != null) {
                this.edgeRemovedListener.onRemoved(e);
            }
        }
    }

    public void removeGraphChangedListener(final GraphChangedListener l) {
        synchronized (this.eventListeners) {
            this.eventListeners.remove(l);
        }
    }

    public void removeNode(final Node node) {
        this.nodes.remove(node);
        detachNode(node);
        if (this.nodeRemovedListener != null) {
            this.nodeRemovedListener.onRemoved(node);
        }
    }

    /**
     * @param edgeRemovedListener
     *            the edgeRemovedListener to set
     */
    public void setEdgeRemovedListener(EdgeRemovedListener edgeRemovedListener) {
        this.edgeRemovedListener = edgeRemovedListener;
    }

    /**
     * @param nodeRemovedListener
     *            the nodeRemovedListener to set
     */
    public void setNodeRemovedListener(NodeRemovedListener nodeRemovedListener) {
        this.nodeRemovedListener = nodeRemovedListener;
    }
}

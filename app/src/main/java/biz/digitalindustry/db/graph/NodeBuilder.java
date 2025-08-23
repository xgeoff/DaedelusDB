package biz.digitalindustry.db.graph;

import java.util.List;
import java.util.ArrayList;

public class NodeBuilder {
    private final GraphDatabase graphDb;
    private final GraphNode node;

    public NodeBuilder(GraphDatabase graphDb, String id) {
        this.graphDb = graphDb;
        this.node = graphDb.getOrCreateNode(id);
    }

    public NodeBuilder label(String label) {
        node.label = label;
        return this;
    }

    public EdgeBuilder connectTo(String targetId) {
        GraphNode target = graphDb.getOrCreateNode(targetId);
        return new EdgeBuilder(graphDb, node, target);
    }

    public GraphNode done() {
        return node;
    }

    public NodeQuery outgoing() {
        List<GraphNode> neighbors = new ArrayList<>();
        for (GraphEdge edge : node.outgoing) {
            neighbors.add(edge.to);
        }
        return new NodeQuery(neighbors);
    }

    public EdgeQuery outgoingEdges() {
        return new EdgeQuery(node.outgoing);
    }

    public NodeQuery incoming() {
        List<GraphNode> neighbors = new ArrayList<>();
        for (GraphEdge edge : node.incoming) {
            neighbors.add(edge.from);
        }
        return new NodeQuery(neighbors);
    }

    public EdgeQuery incomingEdges() {
        return new EdgeQuery(node.incoming);
    }

}

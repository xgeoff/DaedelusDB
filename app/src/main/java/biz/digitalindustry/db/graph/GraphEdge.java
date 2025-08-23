package biz.digitalindustry.db.graph;

import org.garret.perst.*;

public class GraphEdge extends Persistent {
    public GraphNode from;
    public GraphNode to;
    public String type;
    public double weight;

    public GraphEdge(GraphNode from, GraphNode to, String type, double weight) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.weight = weight;
    }

    public String toString() {
        return "Edge(" + from.id + " -[" + type + "]-> " + to.id + ")";
    }
}

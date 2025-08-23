package biz.digitalindustry.db.graph;


import org.garret.perst.*;
import java.util.*;

public class GraphNode extends Persistent {
    public String id;
    public String label;
    public Link<GraphEdge> outgoing;
    public Link<GraphEdge> incoming;

    public GraphNode() {
        outgoing = storage.createLink();  // returns LinkImpl
        incoming = storage.createLink();

    }

    public GraphNode(String id, String label) {
        this();
        this.id = id;
        this.label = label;
    }

    public String toString() {
        return "Node(" + id + ", " + label + ")";
    }
}

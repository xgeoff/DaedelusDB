package biz.digitalindustry.db.graph;


import org.garret.perst.*;
import java.util.*;

import org.garret.perst.*;

public class GraphNode extends Persistent {
    public String id;
    public String label;
    public Link<GraphEdge> outgoing;
    public Link<GraphEdge> incoming;

    // no-arg constructor for Perst to instantiate when loading
    public GraphNode() {}

    public GraphNode(Storage storage, String id, String label) {
        super(storage);                   // sets the storage field in superclass
        this.id = id;
        this.label = label;
        outgoing = getStorage().createLink();  // returns LinkImpl
        incoming = getStorage().createLink();
    }
}

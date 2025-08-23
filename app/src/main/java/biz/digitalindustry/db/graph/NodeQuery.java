package biz.digitalindustry.db.graph;

import java.util.*;
import java.util.stream.*;

public class NodeQuery {
    private final List<GraphNode> nodes;

    public NodeQuery(Collection<GraphNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    public NodeQuery filterByLabel(String label) {
        return new NodeQuery(nodes.stream()
                .filter(n -> label.equals(n.label))
                .collect(Collectors.toList()));
    }

    public List<GraphNode> collect() {
        return nodes;
    }
}

package biz.digitalindustry.db.graph;

import java.util.*;
import java.util.stream.*;

public class EdgeQuery {
    private final List<GraphEdge> edges;

    public EdgeQuery(Collection<GraphEdge> edges) {
        this.edges = new ArrayList<>(edges);
    }

    public EdgeQuery filterByType(String type) {
        return new EdgeQuery(edges.stream()
                .filter(e -> type.equals(e.type))
                .collect(Collectors.toList()));
    }

    public EdgeQuery withWeightAbove(double minWeight) {
        return new EdgeQuery(edges.stream()
                .filter(e -> e.weight > minWeight)
                .collect(Collectors.toList()));
    }

    public List<GraphEdge> collect() {
        return edges;
    }
}

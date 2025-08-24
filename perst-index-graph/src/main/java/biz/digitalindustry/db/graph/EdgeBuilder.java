package biz.digitalindustry.db.graph;

public class EdgeBuilder {
    private final GraphDatabase graphDb;
    private final GraphNode from;
    private final GraphNode to;
    private String type = "RELATED";
    private double weight = 1.0;

    public EdgeBuilder(GraphDatabase graphDb, GraphNode from, GraphNode to) {
        this.graphDb = graphDb;
        this.from = from;
        this.to = to;
    }

    public EdgeBuilder as(String type) {
        this.type = type;
        return this;
    }

    public EdgeBuilder weight(double weight) {
        this.weight = weight;
        return this;
    }

    public NodeBuilder connect() {
        graphDb.createEdge(from, to, type, weight);
        return new NodeBuilder(graphDb, to.id);
    }

    public GraphEdge done() {
        return graphDb.createEdge(from, to, type, weight);
    }
}

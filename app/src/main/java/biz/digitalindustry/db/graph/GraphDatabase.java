package biz.digitalindustry.db.graph;

import org.garret.perst.*;

public class GraphDatabase {
    private Storage db;
    private FieldIndex<GraphNode> nodeIndex;

    public GraphDatabase(String path) {
        db = StorageFactory.getInstance().createStorage();
        db.open(path, Storage.DEFAULT_PAGE_POOL_SIZE);

        if (db.getRoot() == null) {
            Root root = new Root();
            db.setRoot(root);
        }

        nodeIndex = ((Root) db.getRoot()).nodeIndex;
    }

    public void close() {
        db.close();
    }

    public GraphNode createNode(String id, String label) {
        GraphNode node = new GraphNode(db, id, label);
        ((Root) db.getRoot()).nodeIndex.put(node);  // only pass the object
        return node;
    }

    public GraphEdge createEdge(GraphNode from, GraphNode to, String type, double weight) {
        GraphEdge edge = new GraphEdge(from, to, type, weight);
        from.outgoing.add(edge);
        to.incoming.add(edge);
        return edge;
    }

    public GraphNode getNode(String id) {
        return nodeIndex.get(id);
    }

    public void commit() {
        db.commit();
    }

    static class Root extends Persistent {
        FieldIndex<GraphNode> nodeIndex = StorageFactory.getInstance().createStorage().createFieldIndex(GraphNode.class, "id", true);
    }

    public NodeBuilder node(String id) {
        return new NodeBuilder(this, id);
    }

    protected GraphNode getOrCreateNode(String id) {
        GraphNode node = getNode(id);
        if (node == null) {
            node = new GraphNode(db, id, null);
            ((Root) db.getRoot()).nodeIndex.put(node);
        }
        return node;
    }
}

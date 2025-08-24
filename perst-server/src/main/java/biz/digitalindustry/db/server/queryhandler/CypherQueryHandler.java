package biz.digitalindustry.db.server.queryhandler;

import biz.digitalindustry.db.server.model.Node;
import biz.digitalindustry.db.server.model.QueryResponse;
import jakarta.inject.Singleton;
import org.garret.perst.GraphQueryEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class CypherQueryHandler implements QueryHandler {
    private final GraphQueryEngine engine;

    public CypherQueryHandler(GraphQueryEngine engine) {
        this.engine = engine;
    }

    @Override
    public QueryResponse handle(String query) {
        // Execute the query using the pluggable graph engine implementation
        List<Map<String, Object>> rawResults = engine.execute(query);

        // Map the raw results into structured Node instances
        List<Map<String, Node>> mappedResults = new ArrayList<>();
        for (Map<String, Object> row : rawResults) {
            Map<String, Node> mappedRow = new HashMap<>();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                // TODO: replace this placeholder with real mapping logic
                Map<String, Object> props = entry.getValue() instanceof Map
                        ? (Map<String, Object>) entry.getValue()
                        : Map.of("value", entry.getValue());

                Node node = new Node(UUID.randomUUID().toString(), props);
                mappedRow.put(entry.getKey(), node);
            }
            mappedResults.add(mappedRow);
        }

        return new QueryResponse(mappedResults);
    }
}

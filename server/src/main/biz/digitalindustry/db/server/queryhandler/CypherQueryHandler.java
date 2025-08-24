package biz.digitalindustry.db.server.queryhandler;

import biz.digitalindustry.db.server.model.Node;
import biz.digitalindustry.db.server.model.QueryResponse;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class CypherQueryHandler implements QueryHandler {
    @Override
    public QueryResponse handle(String query) {
        // Placeholder for executing the Cypher query against the graph engine
        List<Map<String, Object>> rawResults = executeCypher(query);

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

    /**
     * Simulates execution of a Cypher query. Replace with real integration.
     */
    private List<Map<String, Object>> executeCypher(String query) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("result", "Processed Cypher query: " + query);

        Map<String, Object> row = new HashMap<>();
        row.put("node", properties);

        return List.of(row);
    }
}

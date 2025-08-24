package biz.digitalindustry.db.server.queryhandler;

import jakarta.inject.Singleton;
import org.garret.perst.GraphQueryEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple in-memory implementation of {@link GraphQueryEngine} used for
 * testing and as a placeholder until a real graph engine is provided.
 */
@Singleton
public class InMemoryGraphQueryEngine implements GraphQueryEngine {
    @Override
    public List<Map<String, Object>> execute(String query) {
        Map<String, Object> props = new HashMap<>();
        props.put("result", "Processed Cypher query: " + query);

        Map<String, Object> row = new HashMap<>();
        row.put("node", props);

        return List.of(row);
    }
}

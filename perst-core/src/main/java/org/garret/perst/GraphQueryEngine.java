package org.garret.perst;

import java.util.List;
import java.util.Map;

/**
 * Minimal abstraction for executing graph queries.
 * Implementations are provided by specific index modules.
 */
public interface GraphQueryEngine {
    /**
     * Execute a query returning a list of result rows represented as maps.
     *
     * @param query query string
     * @return query results
     */
    List<Map<String, Object>> execute(String query);
}

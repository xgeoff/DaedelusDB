package biz.digitalindustry.db.server.queryhandler;

import biz.digitalindustry.db.server.model.Node;
import biz.digitalindustry.db.server.model.QueryResponse;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Simple SQL query handler used for testing.
 */
@Singleton
public class SqlQueryHandler implements QueryHandler {
    @Override
    public QueryResponse handle(String query) {
        Node node = new Node(UUID.randomUUID().toString(),
                Map.of("result", "Processed SQL query: " + query));
        return new QueryResponse(List.of(Map.of("row", node)));
    }
}


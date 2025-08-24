package biz.digitalindustry.db.server.queryhandler;

import biz.digitalindustry.db.server.model.Node;
import biz.digitalindustry.db.server.model.QueryResponse;
import jakarta.inject.Singleton;
import java.util.*;

@Singleton
public class CypherQueryHandler implements QueryHandler {
    @Override
    public QueryResponse handle(String query) {
        // TODO: call your graph engine here
        Map<String, Object> properties = new HashMap<>();
        properties.put("result", "Processed Cypher query: " + query);

        Node node = new Node("1", properties);
        Map<String, Node> result = new HashMap<>();
        result.put("node", node);

        return new QueryResponse(List.of(result));
    }
}

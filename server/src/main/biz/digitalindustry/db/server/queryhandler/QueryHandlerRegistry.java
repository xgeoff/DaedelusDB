package biz.digitalindustry.db.server.queryhandler;

import jakarta.inject.Singleton;
import java.util.*;

@Singleton
public class QueryHandlerRegistry {
    private final Map<String, QueryHandler> handlers = new HashMap<>();

    public QueryHandlerRegistry(CypherQueryHandler cypherHandler) {
        handlers.put("cypher", cypherHandler);
        // Future: handlers.put("sql", sqlHandler), etc.
    }

    public Optional<QueryHandler> getHandler(String key) {
        return Optional.ofNullable(handlers.get(key));
    }
}

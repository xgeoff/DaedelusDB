package biz.digitalindustry.db.server.controller;

import handler.QueryHandlerRegistry;
import model.QueryRequest;
import model.QueryResponse;
import io.micronaut.http.annotation.*;

import java.util.*;

@Controller("/query")
public class QueryController {
    private final QueryHandlerRegistry registry;

    public QueryController(QueryHandlerRegistry registry) {
        this.registry = registry;
    }

    @Post
    public QueryResponse handleQuery(@Body QueryRequest request) {
        Map<String, String> queries = request.getQuery();

        if (queries == null || queries.isEmpty()) {
            throw new IllegalArgumentException("Missing query payload");
        }

        Map.Entry<String, String> entry = queries.entrySet().iterator().next();
        String queryType = entry.getKey();
        String query = entry.getValue();

        return registry.getHandler(queryType)
                .map(handler -> new QueryResponse(handler.handle(query)))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported query type: " + queryType));
    }
}

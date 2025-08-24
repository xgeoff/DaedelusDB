package biz.digitalindustry.db.server.controller;

import handler.QueryHandlerRegistry;
import model.QueryRequest;
import model.QueryResponse;
import io.micronaut.http.annotation.*;

@Controller("/query")
public class QueryController {
    private final QueryHandlerRegistry registry;

    public QueryController(QueryHandlerRegistry registry) {
        this.registry = registry;
    }

    @Post
    public QueryResponse handleQuery(@Body QueryRequest request) {
        String query = request.getCypher();

        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Missing query payload");
        }

        return registry.getHandler("cypher")
                .map(handler -> new QueryResponse(handler.handle(query)))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported query type: cypher"));
    }
}

package biz.digitalindustry.db.server.controller;

import biz.digitalindustry.db.server.model.QueryRequest;
import biz.digitalindustry.db.server.model.QueryResponse;
import biz.digitalindustry.db.server.queryhandler.QueryHandlerRegistry;
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
                .map(handler -> handler.handle(query))
                .orElseThrow();
    }
}

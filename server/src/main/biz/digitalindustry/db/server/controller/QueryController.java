package biz.digitalindustry.db.server.controller;

import biz.digitalindustry.db.server.model.QueryRequest;
import biz.digitalindustry.db.server.model.QueryResponse;
import biz.digitalindustry.db.server.queryhandler.QueryHandlerRegistry;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

@Controller("/query")
public class QueryController {
    private final QueryHandlerRegistry registry;

    public QueryController(QueryHandlerRegistry registry) {
        this.registry = registry;
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public QueryResponse handleQuery(@Body QueryRequest request) {
        String queryType = request.getQueryType();
        String query = request.getQuery();

        if (queryType == null || query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Missing query payload");
        }

        return registry.getHandler(queryType)
                .map(handler -> handler.handle(query))
                .orElseThrow();
    }
}

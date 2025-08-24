package biz.digitalindustry.db.server.queryhandler;

import jakarta.inject.Singleton;

@Singleton
public class CypherQueryHandler implements QueryHandler {
    @Override
    public String handle(String query) {
        // TODO: call your graph engine here
        return "{\"result\": \"Processed Cypher query: " + query + "\"}";
    }
}

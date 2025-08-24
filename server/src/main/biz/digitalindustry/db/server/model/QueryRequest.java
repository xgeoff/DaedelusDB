package biz.digitalindustry.db.server.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class QueryRequest {
    private String cypher;

    public String getCypher() {
        return cypher;
    }

    public void setCypher(String cypher) {
        this.cypher = cypher;
    }
}

package biz.digitalindustry.db.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class QueryResponse {
    private List<Map<String, Node>> results;

    public QueryResponse() {
        this.results = new ArrayList<>();
    }

    @JsonCreator
    public QueryResponse(@JsonProperty("results") List<Map<String, Node>> results) {
        this.results = results;
    }

    public List<Map<String, Node>> getResults() {
        return results;
    }
}

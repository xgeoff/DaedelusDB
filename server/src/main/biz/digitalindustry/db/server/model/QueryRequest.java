package biz.digitalindustry.db.server.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.micronaut.core.annotation.Introspected;

import java.util.LinkedHashMap;
import java.util.Map;

@Introspected
public class QueryRequest {
    private final Map<String, String> queries = new LinkedHashMap<>();

    @JsonAnySetter
    public void addQuery(String key, String value) {
        queries.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getQueries() {
        return queries;
    }

    public String getQueryType() {
        return queries.keySet().stream().findFirst().orElse(null);
    }

    public String getQuery() {
        return queries.values().stream().findFirst().orElse(null);
    }
}

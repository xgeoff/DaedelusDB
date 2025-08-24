package biz.digitalindustry.db.server.model;

import java.util.*;

public class QueryRequest {
    private Map<String, String> query;

    public Map<String, String> getQuery() {
        return query;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }
}

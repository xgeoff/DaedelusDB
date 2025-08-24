package biz.digitalindustry.db.server.model;

public class QueryResponse {
    private String result;

    public QueryResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}

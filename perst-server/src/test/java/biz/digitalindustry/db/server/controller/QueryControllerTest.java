package biz.digitalindustry.db.server.controller;

import biz.digitalindustry.db.server.model.Node;
import biz.digitalindustry.db.server.model.QueryRequest;
import biz.digitalindustry.db.server.model.QueryResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class QueryControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testPostCypherQueryReturnsStructuredResults() {
        QueryRequest request = QueryRequest.of("cypher", "MATCH (n) RETURN n");

        HttpRequest<QueryRequest> httpRequest = HttpRequest.POST("/query", request);
        HttpResponse<QueryResponse> response = client.toBlocking().exchange(httpRequest, QueryResponse.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        QueryResponse body = response.body();
        assertNotNull(body);
        assertNotNull(body.getResults());
        assertFalse(body.getResults().isEmpty());

        Map<String, Node> row = body.getResults().get(0);
        assertTrue(row.containsKey("node"));

        Node node = row.get("node");
        assertNotNull(node.getId());
        assertEquals("Processed Cypher query: MATCH (n) RETURN n", node.getProperties().get("result"));
    }

    @Test
    void testPostSqlQueryInvokesSqlHandler() {
        QueryRequest request = QueryRequest.of("sql", "SELECT * FROM Person");

        HttpRequest<QueryRequest> httpRequest = HttpRequest.POST("/query", request);
        HttpResponse<QueryResponse> response = client.toBlocking().exchange(httpRequest, QueryResponse.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        QueryResponse body = response.body();
        assertNotNull(body);
        assertFalse(body.getResults().isEmpty());

        Map<String, Node> row = body.getResults().get(0);
        Node node = row.get("row");
        assertNotNull(node);
        assertEquals("Processed SQL query: SELECT * FROM Person", node.getProperties().get("result"));
    }

    @Test
    void testMissingQueryTypeReturnsError() {
        QueryRequest request = new QueryRequest();
        HttpRequest<QueryRequest> httpRequest = HttpRequest.POST("/query", request);

        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, QueryResponse.class));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        String body = ex.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("queryType"));
    }

    @Test
    void testEmptyQueryReturnsError() {
        QueryRequest request = QueryRequest.of("cypher", "");
        HttpRequest<QueryRequest> httpRequest = HttpRequest.POST("/query", request);

        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, QueryResponse.class));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        String body = ex.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("query"));
    }

    @Test
    void testUnsupportedQueryTypeReturnsError() {
        QueryRequest request = QueryRequest.of("foo", "bar");
        HttpRequest<QueryRequest> httpRequest = HttpRequest.POST("/query", request);

        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, QueryResponse.class));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        String body = ex.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("Unsupported query type"));
    }
}


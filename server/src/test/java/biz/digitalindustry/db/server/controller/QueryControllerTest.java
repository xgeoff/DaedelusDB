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

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class QueryControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testPostCypherQueryReturnsStructuredResults() {
        QueryRequest request = new QueryRequest();
        request.setCypher("MATCH (n) RETURN n");

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
    void testMissingCypherFieldReturnsError() {
        QueryRequest request = new QueryRequest(); // cypher remains null
        HttpRequest<QueryRequest> httpRequest = HttpRequest.POST("/query", request);

        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, QueryResponse.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        String body = ex.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("Missing query payload"));
    }

    @Test
    void testEmptyCypherReturnsError() {
        QueryRequest request = new QueryRequest();
        request.setCypher("");
        HttpRequest<QueryRequest> httpRequest = HttpRequest.POST("/query", request);

        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, QueryResponse.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        String body = ex.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("Missing query payload"));
    }
}


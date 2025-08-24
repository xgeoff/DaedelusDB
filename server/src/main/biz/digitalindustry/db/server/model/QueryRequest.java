package biz.digitalindustry.db.server.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record QueryRequest(String queryType, String query) {
}

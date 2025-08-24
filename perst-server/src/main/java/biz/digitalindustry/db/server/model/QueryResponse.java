package biz.digitalindustry.db.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Introspected
public record QueryResponse(List<Map<String, Node>> results) {
    public QueryResponse() {
        this(List.of());
    }

    @JsonCreator
    public QueryResponse(@JsonProperty("results") List<Map<String, Node>> results) {
        this.results = results == null ? List.of() :
                List.copyOf(results.stream()
                        .map(Map::copyOf)
                        .collect(Collectors.toList()));
    }
}

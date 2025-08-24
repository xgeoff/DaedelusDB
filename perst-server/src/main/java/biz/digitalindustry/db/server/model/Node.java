package biz.digitalindustry.db.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import java.util.Map;

@Introspected
public record Node(String id, Map<String, Object> properties) {
    public Node() {
        this(null, Map.of());
    }

    @JsonCreator
    public Node(@JsonProperty("id") String id,
                @JsonProperty("properties") Map<String, Object> properties) {
        this.id = id;
        this.properties = properties == null ? Map.of() : Map.copyOf(properties);
    }
}

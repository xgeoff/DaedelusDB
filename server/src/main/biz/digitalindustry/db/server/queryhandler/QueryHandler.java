package biz.digitalindustry.db.server.queryhandler;

import biz.digitalindustry.db.server.model.Node;
import java.util.List;
import java.util.Map;

public interface QueryHandler {
    List<Map<String, Node>> handle(String query);
}

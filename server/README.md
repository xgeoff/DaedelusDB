# Server Module

## Query Handlers

`QueryHandlerRegistry` routes incoming queries to a handler based on the `queryType`.
Handlers implement the `QueryHandler` interface and are registered under a key that
represents their query language.

### Adding a new handler

1. Implement `QueryHandler` for your query language:
   ```java
   public class SqlQueryHandler implements QueryHandler {
       public QueryResponse handle(String query) {
           // execute SQL query
       }
   }
   ```
2. Register the handler when constructing `QueryHandlerRegistry`:
   ```java
   Map<String, QueryHandler> handlers = Map.of(
       "cypher", new CypherQueryHandler(),
       "sql", new SqlQueryHandler()
   );
   QueryHandlerRegistry registry = new QueryHandlerRegistry(handlers);
   ```
   Additional handlers like Gremlin can be appended to the map using their
   associated query type keys.

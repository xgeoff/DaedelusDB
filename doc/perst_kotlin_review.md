# Perst package review

This document maps packages under `org.garret.perst` and highlights classes with reflection, static state, or platform-specific logic that may require attention when migrating to Kotlin.

## Package overview

| Package | Role | API vs. implementation |
| --- | --- | --- |
| `org.garret.perst` | Core database interfaces, storage abstractions, and common utilities | Core API |
| `org.garret.perst.impl` | Storage engine internals, reflection helpers, B-tree implementations, serializers | Implementation |
| `org.garret.perst.impl.sun14` | Alternative reflection provider for Sun/Oracle JDK 1.4 using `sun.misc.Unsafe` | Platform-specific implementation |
| `org.garret.perst.assoc` | Lightweight associative database layer | Core API |
| `org.garret.perst.rdf` / `org.garret.perst.rdf.xml` | RDF entities, versioned storage, and XML utilities | Core API / Implementation |
| `org.garret.perst.jassist` / `org.garret.perst.jassist.expr` | Bytecode instrumentation support used by the code generator | Implementation |
| `org.garret.perst.fulltext` | Full-text search abstractions and helpers | Core API |
| `org.garret.perst.aspectj` | Placeholder for AspectJ integrations (no sources) | Implementation |
| `org.garret.perst.continuous` | Continuous or temporal database support and versioned queries | Core API |
| `org.garret.perst.lucene` | Integration helpers for Apache Lucene | Core API |

## Reflection-heavy or static classes

- **`impl/ClassDescriptor`** – central metadata class with a global reflection provider and arrays describing field types; dynamically loads `sun.misc.Unsafe` and falls back to a standard reflection provider.
- **`impl/StorageImpl`** – custom object streams resolve classes using `Class.forName`, enabling dynamic class loading during deserialization.
- **`impl/sun14/Sun14ReflectionProvider`** – platform-specific provider relying on `sun.reflect` and `sun.misc.Unsafe` for direct field access.
- **`impl/StandardReflectionProvider`** – default reflection helper that sets field values via the Java reflection API.
- **`StorageFactory`** – exposes a singleton instance used to create storages, introducing global static state.
- **`Database`** – inspects classes via reflection to auto-create indices based on annotated fields and to look up fields on demand.
- **`Projection`** – retrieves fields by name and makes them accessible at runtime, then reads values reflectively during projection.
- **`FieldIndex`** – API exposes the underlying `java.lang.reflect.Field` objects for indexed keys.
- **`L2List`** – uses `java.lang.reflect.Array.newInstance` to allocate typed arrays in `toArray`.
- **`continuous/TableDescriptor`** – maintains transient `Field` references and extracts key values reflectively for indexing version histories.
- **`impl/ReflectionMultidimensionalComparator`** – stores reflective `Field` references to compare multidimensional objects at runtime.


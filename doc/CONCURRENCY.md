# Concurrency utilities

Two helper methods in `Storage` simplify reading objects while coordinating
with concurrent writers.

## `tryReadObject`

```java
MyClass obj = storage.tryReadObject(oid, MyClass.class);
```

`tryReadObject` immediately attempts to load the object. If another thread
holds the write lock for the same object a `ConcurrentWriteException` is
thrown.

When the type of the object is not known in advance an overload without the
class parameter can be used:

```java
Object obj = storage.tryReadObject(oid);
```

## `readObjectAsync`

```java
storage.readObjectAsync(oid, MyClass.class)
       .thenAccept(obj -> System.out.println(obj));
```

`readObjectAsync` returns a `CompletableFuture`. When the object is not
write-locked the future completes immediately. Otherwise it is enqueued and
completed once the writer releases the lock.

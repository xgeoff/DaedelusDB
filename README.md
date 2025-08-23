# Perst Central Locking

Perst coordinates concurrent access through a central lock.  All write
operations must be executed by a designated **writer thread**.  Read
transactions may be started from any thread but they block while the
writer holds the lock.

## Writer Thread Requirement

Use `setWriterThread(Thread)` to specify which thread performs updates.
Only this thread should call mutating APIs such as `store`, `modify`, or
`commit`.  Other threads that invoke these methods enqueue the
operations to the writer and wait for completion.

```java
Storage storage = StorageFactory.getInstance().createStorage();
storage.open("data.dbs");

// Designate the main thread as the writer
storage.setWriterThread(Thread.currentThread());

Thread reader = new Thread(() -> {
    storage.beginThreadTransaction(Storage.READ_ONLY_TRANSACTION);
    try {
        MyRoot root = storage.getRoot();
        System.out.println(root.value);
    } finally {
        storage.endThreadTransaction();
    }
});

reader.start();

// All updates must run on the writer thread
storage.beginThreadTransaction(Storage.EXCLUSIVE_TRANSACTION);
try {
    MyRoot root = storage.getRoot();
    root.value++;
    storage.modify(root);
    storage.commit();
} finally {
    storage.endThreadTransaction();
}

reader.join();
```

## Limitations

* **Blocking reads** – reader threads are blocked while the writer holds
  the lock or processes queued updates.
* **Keep writer operations short** – long running work in the writer
  thread prevents readers from progressing.  Offload heavy computation to
  other threads and only perform quick database updates on the writer.


package org.garret.perst;

/**
 * Transaction modes for thread-bound transactions.
 */
public enum TransactionMode {
    /** Exclusive per-thread transaction: each thread accesses database in exclusive mode. */
    EXCLUSIVE,
    /** Alias for EXCLUSIVE. Any transaction modifying database should be exclusive. */
    READ_WRITE,
    /** Cooperative mode; all threads share the same transaction. */
    COOPERATIVE,
    /** Alias for COOPERATIVE. Only read-only transactions can be executed in parallel. */
    READ_ONLY,
    /** Serializable per-thread transaction. */
    SERIALIZABLE,
    /** Read only transaction which can be started at replication slave node. */
    REPLICATION_SLAVE
}

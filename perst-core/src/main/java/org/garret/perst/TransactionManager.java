package org.garret.perst;

/**
 * Service interface for transaction management operations.
 */
public interface TransactionManager {
    /** Exclusive per-thread transaction: each thread access database in exclusive mode */
    int EXCLUSIVE_TRANSACTION = 0;
    /** Alias for EXCLUSIVE_TRANSACTION. In case of multiclient access,
     * any transaction modifying database should be exclusive. */
    int READ_WRITE_TRANSACTION = EXCLUSIVE_TRANSACTION;
    /** Cooperative mode; all threads share the same transaction. */
    int COOPERATIVE_TRANSACTION = 1;
    /** Alias for COOPERATIVE_TRANSACTION. Only read-only transactions can be executed in parallel. */
    int READ_ONLY_TRANSACTION = COOPERATIVE_TRANSACTION;
    /** Serializable per-thread transaction. */
    int SERIALIZABLE_TRANSACTION = 2;
    /** Read only transaction which can be started at replication slave node. */
    int REPLICATION_SLAVE_TRANSACTION = 3;

    /** Commit changes done by the last transaction. */
    void commit();

    /** Rollback changes made by the last transaction. */
    void rollback();

    /** Begin per-thread transaction. */
    void beginThreadTransaction(int mode);

    /** End per-thread transaction started by beginThreadTransaction method. */
    void endThreadTransaction();

    /** End per-thread cooperative transaction with specified maximal delay of transaction commit. */
    void endThreadTransaction(int maxDelay);

    /** Check if nested thread transaction is active. */
    boolean isInsideThreadTransaction();

    /** Rollback per-thread transaction. */
    void rollbackThreadTransaction();

    /** Start serializable transaction. */
    void beginSerializableTransaction();

    /** Commit serializable transaction. */
    void commitSerializableTransaction();

    /** Rollback serializable transaction. */
    void rollbackSerializableTransaction();
}

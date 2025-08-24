package org.garret.perst;

/**
 * Service interface for transaction management operations.
 */
public interface TransactionManager {

    /** Commit changes done by the last transaction. */
    void commit();

    /** Rollback changes made by the last transaction. */
    void rollback();

    /** Begin per-thread transaction. */
    void beginThreadTransaction(TransactionMode mode);

    /**
     * Convenience method returning {@link Transaction} object allowing
     * the use of try-with-resources statement for transactions.
     *
     * @param mode transaction mode
     * @return transaction handle
     */
    default Transaction beginTransaction(TransactionMode mode) {
        return new Transaction(this, mode);
    }

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

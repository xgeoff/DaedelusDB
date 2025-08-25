package org.garret.perst;

/**
 * Transaction handle that begins a transaction upon creation and commits or
 * rolls it back when closed. Commit is performed by default; calling
 * {@link #rollback()} will cause the transaction to roll back instead.
 */
public class Transaction implements AutoCloseable {
    private final Storage storage;
    private boolean rollback;

    Transaction(Storage storage, TransactionMode mode) {
        this.storage = storage;
        storage.beginThreadTransaction(mode);
    }

    /**
     * Mark this transaction to be rolled back when it is closed.
     */
    public void rollback() {
        this.rollback = true;
    }

    @Override
    public void close() {
        if (rollback) {
            storage.rollbackThreadTransaction();
        } else {
            storage.endThreadTransaction();
        }
    }
}

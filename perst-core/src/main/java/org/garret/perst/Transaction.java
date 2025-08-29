package org.garret.perst;

import org.garret.perst.impl.ThreadTransactionContext;

/**
 * Transaction handle that begins a transaction upon creation and commits or
 * rolls it back when closed. Commit is performed by default; calling
 * {@link #rollback()} will cause the transaction to roll back instead.
 */
public class Transaction implements AutoCloseable {
    private final Storage storage;
    private boolean rollback;
    private final long startTimestamp;
    private long commitTimestamp;

    Transaction(Storage storage, TransactionMode mode) {
        this.storage = storage;
        storage.beginThreadTransaction(mode);
        ThreadTransactionContext ctx = storage.getTransactionContext();
        this.startTimestamp = ctx != null ? ctx.startTimestamp : System.currentTimeMillis();
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
            ThreadTransactionContext ctx = storage.getTransactionContext();
            commitTimestamp = ctx != null ? ctx.commitTimestamp : System.currentTimeMillis();
        }
    }

    /**
     * Get transaction start timestamp.
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Get transaction commit timestamp. Returns zero if transaction was rolled back.
     */
    public long getCommitTimestamp() {
        return commitTimestamp;
    }
}

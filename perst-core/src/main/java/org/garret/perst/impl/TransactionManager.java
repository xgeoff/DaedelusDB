package org.garret.perst.impl;

/**
 * Dedicated component responsible for keeping transaction state on a
 * per-thread basis.  StorageImpl delegates transaction context related
 * operations to this manager which makes it easier to test and evolve
 * transaction handling independently from storage orchestration.
 */
public class TransactionManager {
    private final ThreadLocal<ThreadTransactionContext> context =
        new ThreadLocal<ThreadTransactionContext>() {
            @Override
            protected ThreadTransactionContext initialValue() {
                return new ThreadTransactionContext();
            }
        };

    public ThreadTransactionContext getContext() {
        return context.get();
    }

    public ThreadTransactionContext setContext(ThreadTransactionContext ctx) {
        ThreadTransactionContext old = context.get();
        context.set(ctx);
        return old;
    }
}


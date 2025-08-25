package org.garret.perst;

/**
 * Thrown when a read operation is attempted on an object that is currently
 * write locked by another thread.
 */
public class ConcurrentWriteException extends RuntimeException {
    public ConcurrentWriteException(String message) {
        super(message);
    }
}

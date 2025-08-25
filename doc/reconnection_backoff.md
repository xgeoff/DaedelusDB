# Replication reconnection backoff

`ReplicationMasterFile` retries connections to slave nodes. Each attempt is
separated by an interruptible wait implemented with `Condition.await` rather
than `Thread.sleep`. This allows the thread to be interrupted during shutdown
and avoids blocking the thread while backing off between retries. The delay
between attempts is governed by `CONNECTION_TIMEOUT` (one second by default)
and the number of attempts is controlled by `slaveConnectionTimeout` or
`MAX_CONNECT_ATTEMPTS` when no storage is supplied.

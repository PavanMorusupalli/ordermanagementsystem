package com.mdaqtest.ordermanagementsystem.domain;

/**
 * Optimistic locking to protect from race conditions during write operations.
 */
public class OptimisticLockingException extends RuntimeException
{
    public OptimisticLockingException(String message) {
        super(message);
    }
}

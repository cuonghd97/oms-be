package com.semicolon.oms.common.exception;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException(String currentStatus, String targetStatus) {
        super(String.format("Cannot transition order from '%s' to '%s'", currentStatus, targetStatus));
    }
}

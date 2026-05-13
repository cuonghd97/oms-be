package com.semicolon.oms.common.exception;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(String productName, int requested, int available) {
        super(String.format("Insufficient inventory for '%s': requested %d, available %d",
                productName, requested, available));
    }
}

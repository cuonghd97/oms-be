package com.semicolon.oms.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private Long productId;
    private int quantity;
    private int reservedQuantity;
    private int availableQuantity;
}

package com.semicolon.oms.inventory.controller;

import com.semicolon.oms.common.response.ApiResponse;
import com.semicolon.oms.common.response.PagedResponse;
import com.semicolon.oms.inventory.dto.InventoryAdjustRequest;
import com.semicolon.oms.inventory.dto.InventoryResponse;
import com.semicolon.oms.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inventories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
@Tag(name = "Admin - Inventory", description = "Inventory management APIs")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "List all inventories")
    public ResponseEntity<ApiResponse<PagedResponse<InventoryResponse>>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllInventories(pageable)));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory by product ID")
    public ResponseEntity<ApiResponse<InventoryResponse>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getByProductId(productId)));
    }

    @PostMapping("/{productId}/increase")
    @Operation(summary = "Increase product stock")
    public ResponseEntity<ApiResponse<InventoryResponse>> increase(
            @PathVariable Long productId, @Valid @RequestBody InventoryAdjustRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.increaseStock(productId, request)));
    }

    @PostMapping("/{productId}/decrease")
    @Operation(summary = "Decrease product stock")
    public ResponseEntity<ApiResponse<InventoryResponse>> decrease(
            @PathVariable Long productId, @Valid @RequestBody InventoryAdjustRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.decreaseStock(productId, request)));
    }

    @PostMapping("/{productId}/adjust")
    @Operation(summary = "Adjust product stock to specific quantity")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjust(
            @PathVariable Long productId, @Valid @RequestBody InventoryAdjustRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.adjustStock(productId, request)));
    }
}

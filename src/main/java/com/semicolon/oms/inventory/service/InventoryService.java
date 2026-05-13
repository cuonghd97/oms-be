package com.semicolon.oms.inventory.service;

import com.semicolon.oms.common.exception.BadRequestException;
import com.semicolon.oms.common.exception.ResourceNotFoundException;
import com.semicolon.oms.inventory.dto.InventoryAdjustRequest;
import com.semicolon.oms.inventory.dto.InventoryResponse;
import com.semicolon.oms.inventory.entity.Inventory;
import com.semicolon.oms.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.semicolon.oms.common.response.PagedResponse;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public PagedResponse<InventoryResponse> getAllInventories(Pageable pageable) {
        Page<Inventory> page = inventoryRepository.findAll(pageable);
        return PagedResponse.<InventoryResponse>builder()
                .items(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(Long productId) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        return toResponse(inv);
    }

    @Transactional
    public InventoryResponse increaseStock(Long productId, InventoryAdjustRequest request) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        inv.setQuantity(inv.getQuantity() + request.getQuantity());
        return toResponse(inventoryRepository.save(inv));
    }

    @Transactional
    public InventoryResponse decreaseStock(Long productId, InventoryAdjustRequest request) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        if (inv.getAvailableQuantity() < request.getQuantity()) {
            throw new BadRequestException("Cannot decrease: available stock is " + inv.getAvailableQuantity());
        }
        inv.setQuantity(inv.getQuantity() - request.getQuantity());
        return toResponse(inventoryRepository.save(inv));
    }

    @Transactional
    public InventoryResponse adjustStock(Long productId, InventoryAdjustRequest request) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        inv.setQuantity(request.getQuantity());
        return toResponse(inventoryRepository.save(inv));
    }

    private InventoryResponse toResponse(Inventory inv) {
        return InventoryResponse.builder()
                .id(inv.getId())
                .productId(inv.getProductId())
                .quantity(inv.getQuantity())
                .reservedQuantity(inv.getReservedQuantity())
                .availableQuantity(inv.getAvailableQuantity())
                .build();
    }
}

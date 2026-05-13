package com.semicolon.oms.common.config;

import com.semicolon.oms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/admin/cache")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Cache", description = "Cache management APIs")
public class CacheController {

    private final CacheManager cacheManager;

    @DeleteMapping("/products")
    public ResponseEntity<ApiResponse<Void>> evictProducts() {
        Objects.requireNonNull(cacheManager.getCache("products")).clear();
        return ResponseEntity.ok(ApiResponse.success("Product cache cleared"));
    }

    @DeleteMapping("/categories")
    public ResponseEntity<ApiResponse<Void>> evictCategories() {
        Objects.requireNonNull(cacheManager.getCache("categories")).clear();
        return ResponseEntity.ok(ApiResponse.success("Category cache cleared"));
    }

    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<Void>> evictAll() {
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());
        return ResponseEntity.ok(ApiResponse.success("All caches cleared"));
    }
}

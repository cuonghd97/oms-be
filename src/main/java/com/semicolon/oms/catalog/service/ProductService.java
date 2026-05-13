package com.semicolon.oms.catalog.service;

import com.semicolon.oms.catalog.dto.ProductRequest;
import com.semicolon.oms.catalog.dto.ProductResponse;
import com.semicolon.oms.catalog.entity.Category;
import com.semicolon.oms.catalog.entity.Product;
import com.semicolon.oms.catalog.entity.ProductStatus;
import com.semicolon.oms.catalog.mapper.ProductMapper;
import com.semicolon.oms.catalog.repository.CategoryRepository;
import com.semicolon.oms.catalog.repository.ProductRepository;
import com.semicolon.oms.common.exception.DuplicateResourceException;
import com.semicolon.oms.common.exception.ResourceNotFoundException;
import com.semicolon.oms.common.response.PagedResponse;
import com.semicolon.oms.common.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(String keyword, Long categoryId,
                                                          BigDecimal minPrice, BigDecimal maxPrice,
                                                          ProductStatus status, Pageable pageable) {
        Page<Product> page = productRepository.searchProducts(keyword, categoryId, minPrice, maxPrice, status, pageable);
        return PagedResponse.<ProductResponse>builder()
                .items(page.getContent().stream().map(productMapper::toResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product", "sku", request.getSku());
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .category(category)
                .sku(request.getSku())
                .name(request.getName())
                .slug(SlugUtil.toSlug(request.getName()))
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        return productMapper.toResponse(productRepository.save(product));
    }

    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setCategory(category);
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setSlug(SlugUtil.toSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        return productMapper.toResponse(productRepository.save(product));
    }

    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }
}

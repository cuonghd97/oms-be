package com.semicolon.oms.catalog.mapper;

import com.semicolon.oms.catalog.dto.ProductResponse;
import com.semicolon.oms.catalog.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);
}

package com.semicolon.oms.catalog.mapper;

import com.semicolon.oms.catalog.dto.CategoryResponse;
import com.semicolon.oms.catalog.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
}

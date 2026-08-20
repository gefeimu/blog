package com.blog.mapper;

import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectAll();

    Category selectById(Long id);

    int insert(Category category);

    int update(Category category);

    int deleteById(Long id);

    long countByCategoryId(@Param("categoryId") Long categoryId);
}

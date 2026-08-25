package com.blog.mapper;

import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectAll();

    Category selectById(Long id);

    /** 按名称精确查询（重名校验用，DB 层有唯一约束兜底） */
    Category selectByName(@Param("name") String name);

    int insert(Category category);

    int update(Category category);

    int deleteById(Long id);

    long countByCategoryId(@Param("categoryId") Long categoryId);
}

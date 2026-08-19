package com.blog.mapper;

import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectAll();

    Category selectById(Long id);
}

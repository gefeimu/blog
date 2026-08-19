package com.blog.controller;

import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/api/categories")
    public List<Category> list() {
        return categoryMapper.selectAll();
    }

    @GetMapping("/api/categories/{id}")
    public Category get(@PathVariable Long id) {
        return categoryMapper.selectById(id);
    }
}

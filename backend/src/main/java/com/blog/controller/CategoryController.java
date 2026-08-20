package com.blog.controller;

import com.blog.entity.Category;
import com.blog.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ---------- 公开读接口 ----------

    @GetMapping("/api/categories")
    public List<Category> list() {
        return categoryService.list();
    }

    @GetMapping("/api/categories/{id}")
    public Category get(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    // ---------- 管理接口（需登录） ----------

    @PostMapping("/api/admin/categories")
    public Category create(@RequestBody Category category) {
        return categoryService.create(category);
    }

    @PutMapping("/api/admin/categories/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.update(id, category);
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

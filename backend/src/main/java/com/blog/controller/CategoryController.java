package com.blog.controller;

import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    // ---------- 公开读接口 ----------

    @GetMapping("/api/categories")
    public List<Category> list() {
        return categoryMapper.selectAll();
    }

    @GetMapping("/api/categories/{id}")
    public Category get(@PathVariable Long id) {
        return categoryMapper.selectById(id);
    }

    // ---------- 管理接口（需登录） ----------

    @PostMapping("/api/admin/categories")
    public ResponseEntity<?> create(@RequestBody Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "分类名不能为空"));
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        categoryMapper.insert(category);
        return ResponseEntity.ok(categoryMapper.selectById(category.getId()));
    }

    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Category category) {
        if (categoryMapper.selectById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        if (category.getName() == null || category.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "分类名不能为空"));
        }
        category.setId(id);
        categoryMapper.update(category);
        return ResponseEntity.ok(categoryMapper.selectById(id));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (categoryMapper.selectById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        if (categoryMapper.countByCategoryId(id) > 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "该分类下还有文章，不能删除"));
        }
        categoryMapper.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.blog.service;

import com.blog.common.BizException;
import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> list() {
        return categoryMapper.selectAll();
    }

    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw BizException.notFound("分类不存在");
        }
        return category;
    }

    @Transactional
    public Category create(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw BizException.badRequest("分类名不能为空");
        }
        String name = category.getName().trim();
        if (categoryMapper.selectByName(name) != null) {
            throw BizException.conflict("分类已存在: " + name);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        category.setName(name);
        categoryMapper.insert(category);
        return categoryMapper.selectById(category.getId());
    }

    @Transactional
    public Category update(Long id, Category category) {
        if (categoryMapper.selectById(id) == null) {
            throw BizException.notFound("分类不存在");
        }
        if (category.getName() == null || category.getName().isBlank()) {
            throw BizException.badRequest("分类名不能为空");
        }
        String name = category.getName().trim();
        Category existing = categoryMapper.selectByName(name);
        if (existing != null && !existing.getId().equals(id)) {
            throw BizException.conflict("分类已存在: " + name);
        }
        category.setId(id);
        category.setName(name);
        categoryMapper.update(category);
        return categoryMapper.selectById(id);
    }

    @Transactional
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw BizException.notFound("分类不存在");
        }
        if (categoryMapper.countByCategoryId(id) > 0) {
            throw BizException.conflict("该分类下还有文章，不能删除");
        }
        categoryMapper.deleteById(id);
    }
}

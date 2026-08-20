package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.dto.ArticleRequest;
import com.blog.dto.ArticleVO;
import com.blog.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public PageResult<ArticleVO> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) Long categoryId) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        return articleService.page(page, size, status, categoryId);
    }

    @GetMapping("/{id}")
    public ArticleVO detail(@PathVariable Long id) {
        return articleService.getById(id);
    }

    @PostMapping
    public ArticleVO create(@RequestBody ArticleRequest request) {
        return articleService.create(request);
    }

    @PutMapping("/{id}")
    public ArticleVO update(@PathVariable Long id, @RequestBody ArticleRequest request) {
        return articleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.entity.Article;
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

import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public PageResult<Article> page(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) Integer status) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        return articleService.page(page, size, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> detail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Article article) {
        if (article.getTitle() == null || article.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "title 不能为空"));
        }
        return ResponseEntity.ok(articleService.create(article));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Article article) {
        if (article.getTitle() == null || article.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "title 不能为空"));
        }
        Article updated = articleService.update(id, article);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!articleService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

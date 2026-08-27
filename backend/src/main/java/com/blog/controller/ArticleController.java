package com.blog.controller;

import com.blog.common.BizException;
import com.blog.common.PageResult;
import com.blog.dto.ArticleRequest;
import com.blog.dto.ArticleVO;
import com.blog.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) Long tagId,
                                      @RequestParam(required = false) String keyword) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        // 安全：未登录只能看已发布文章，status 参数对游客强制为 1，防草稿泄露
        // 注意：不能写成 isAuthenticated() ? status : 1 —— Integer 与 int 混用会触发自动拆箱，
        // status 为 null 时直接 NPE（管理端列表不带 status 参数时必现 500）
        Integer effectiveStatus = 1;
        if (isAuthenticated()) {
            effectiveStatus = status;
        }
        return articleService.page(page, size, effectiveStatus, categoryId, tagId, keyword);
    }

    @GetMapping("/{id}")
    public ArticleVO detail(@PathVariable Long id) {
        ArticleVO vo = articleService.getById(id);
        // 安全：草稿只对登录态（后台）可见，游客访问返回 404 不暴露存在性
        if (!isAuthenticated() && vo.getStatus() != 1) {
            throw BizException.notFound("文章不存在");
        }
        return vo;
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

    /** 当前请求是否已登录（JwtAuthFilter 注入的认证；匿名访问者是 AnonymousAuthenticationToken） */
    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }
}

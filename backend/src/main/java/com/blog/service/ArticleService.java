package com.blog.service;

import com.blog.common.BizException;
import com.blog.common.PageResult;
import com.blog.converter.ArticleConverter;
import com.blog.dto.ArticleRequest;
import com.blog.dto.ArticleVO;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class ArticleService {

    /** 支持的展示布局，前台 ArticleView 按此动态渲染 */
    private static final Set<String> ALLOWED_LAYOUTS = Set.of("default", "minimal", "banner");

    private final ArticleMapper articleMapper;
    private final FileStorageService fileStorageService;
    private final MarkdownService markdownService;
    private final ArticleConverter articleConverter;

    public ArticleService(ArticleMapper articleMapper,
                          FileStorageService fileStorageService,
                          MarkdownService markdownService,
                          ArticleConverter articleConverter) {
        this.articleMapper = articleMapper;
        this.fileStorageService = fileStorageService;
        this.markdownService = markdownService;
        this.articleConverter = articleConverter;
    }

    public PageResult<ArticleVO> page(int page, int size, Integer status, Long categoryId) {
        int offset = (page - 1) * size;
        long total = articleMapper.count(status, categoryId);
        List<Article> list = total == 0
                ? Collections.emptyList()
                : articleMapper.selectPage(offset, size, status, categoryId);
        List<ArticleVO> voList = list.stream().map(articleConverter::toVO).toList();
        return new PageResult<>(total, page, size, voList);
    }

    public ArticleVO getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw BizException.notFound("文章不存在");
        }
        // 详情浏览量 +1（列表页访问不加）
        articleMapper.incrementViewCount(id);
        article.setViewCount(article.getViewCount() + 1);
        // 正文：原文（content）+ 渲染 HTML（contentHtml，走 MarkdownService 管线）
        article.setContent(markdownService.read(id));
        ArticleVO vo = articleConverter.toVO(article);
        vo.setContentHtml(markdownService.render(id));
        return vo;
    }

    @Transactional
    public ArticleVO create(ArticleRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw BizException.badRequest("title 不能为空");
        }
        Article article = articleConverter.toEntity(request);
        article.setLayout(normalizeLayout(article.getLayout()));
        if (article.getStatus() == null) {
            article.setStatus(0);
        }
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        articleMapper.insert(article);
        saveTags(article.getId(), article.getTagIds());
        // 保存正文 markdown 文件
        fileStorageService.saveMarkdown(article.getId(), article.getContent());
        // 回查，返回含分类名/标签/时间的完整视图（带 content）
        Article saved = articleMapper.selectById(article.getId());
        saved.setContent(markdownService.read(saved.getId()));
        return articleConverter.toVO(saved);
    }

    @Transactional
    public ArticleVO update(Long id, ArticleRequest request) {
        if (articleMapper.selectById(id) == null) {
            throw BizException.notFound("文章不存在");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw BizException.badRequest("title 不能为空");
        }
        Article article = articleConverter.toEntity(request);
        article.setId(id);
        article.setLayout(normalizeLayout(article.getLayout()));
        articleMapper.update(article);
        articleMapper.deleteArticleTags(id);
        saveTags(id, article.getTagIds());
        // 更新正文 markdown 文件（content 不为 null 时才覆盖）
        if (article.getContent() != null) {
            fileStorageService.saveMarkdown(id, article.getContent());
        }
        Article saved = articleMapper.selectById(id);
        saved.setContent(markdownService.read(id));
        return articleConverter.toVO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (articleMapper.selectById(id) == null) {
            throw BizException.notFound("文章不存在");
        }
        articleMapper.deleteArticleTags(id);
        articleMapper.deleteById(id);
        fileStorageService.deleteMarkdown(id);
    }

    private void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        articleMapper.insertArticleTags(articleId, tagIds);
    }

    /** 布局缺省为 default，非法值直接拒绝，避免脏数据进入前台渲染 */
    private String normalizeLayout(String layout) {
        if (layout == null || layout.isBlank()) {
            return "default";
        }
        if (!ALLOWED_LAYOUTS.contains(layout)) {
            throw BizException.badRequest("layout 仅支持 default/minimal/banner");
        }
        return layout;
    }
}

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

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final FileStorageService fileStorageService;
    private final ArticleConverter articleConverter;

    public ArticleService(ArticleMapper articleMapper,
                          FileStorageService fileStorageService,
                          ArticleConverter articleConverter) {
        this.articleMapper = articleMapper;
        this.fileStorageService = fileStorageService;
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
        // 读取 markdown 正文
        article.setContent(fileStorageService.readMarkdown(id));
        return articleConverter.toVO(article);
    }

    @Transactional
    public ArticleVO create(ArticleRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw BizException.badRequest("title 不能为空");
        }
        Article article = articleConverter.toEntity(request);
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
        saved.setContent(fileStorageService.readMarkdown(saved.getId()));
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
        articleMapper.update(article);
        articleMapper.deleteArticleTags(id);
        saveTags(id, article.getTagIds());
        // 更新正文 markdown 文件（content 不为 null 时才覆盖）
        if (article.getContent() != null) {
            fileStorageService.saveMarkdown(id, article.getContent());
        }
        Article saved = articleMapper.selectById(id);
        saved.setContent(fileStorageService.readMarkdown(id));
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
}

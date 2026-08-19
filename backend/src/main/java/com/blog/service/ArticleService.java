package com.blog.service;

import com.blog.common.PageResult;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final MarkdownService markdownService;

    public ArticleService(ArticleMapper articleMapper, MarkdownService markdownService) {
        this.articleMapper = articleMapper;
        this.markdownService = markdownService;
    }

    public PageResult<Article> page(int page, int size, Integer status, Long categoryId) {
        int offset = (page - 1) * size;
        long total = articleMapper.count(status, categoryId);
        List<Article> list = total == 0
                ? Collections.emptyList()
                : articleMapper.selectPage(offset, size, status, categoryId);
        return new PageResult<>(total, page, size, list);
    }

    public Article getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return null;
        }
        // 详情浏览量 +1（列表页访问不加）
        articleMapper.incrementViewCount(id);
        article.setViewCount(article.getViewCount() + 1);
        // 读取 markdown 正文
        article.setContent(markdownService.read(id));
        return article;
    }

    @Transactional
    public Article create(Article article) {
        article.setId(null);
        if (article.getStatus() == null) {
            article.setStatus(0);
        }
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        article.setContentPath(null);
        articleMapper.insert(article);
        saveTags(article.getId(), article.getTagIds());
        // 保存正文 markdown 文件
        markdownService.save(article.getId(), article.getContent());
        // 回查，返回含分类名/标签/时间的完整实体（带 content）
        Article saved = articleMapper.selectById(article.getId());
        saved.setContent(markdownService.read(saved.getId()));
        return saved;
    }

    @Transactional
    public Article update(Long id, Article article) {
        if (articleMapper.selectById(id) == null) {
            return null;
        }
        article.setId(id);
        articleMapper.update(article);
        articleMapper.deleteArticleTags(id);
        saveTags(id, article.getTagIds());
        // 更新正文 markdown 文件（content 不为 null 时才覆盖）
        if (article.getContent() != null) {
            markdownService.save(id, article.getContent());
        }
        Article saved = articleMapper.selectById(id);
        saved.setContent(markdownService.read(id));
        return saved;
    }

    @Transactional
    public boolean delete(Long id) {
        if (articleMapper.selectById(id) == null) {
            return false;
        }
        articleMapper.deleteArticleTags(id);
        articleMapper.deleteById(id);
        markdownService.delete(id);
        return true;
    }

    private void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        articleMapper.insertArticleTags(articleId, tagIds);
    }
}

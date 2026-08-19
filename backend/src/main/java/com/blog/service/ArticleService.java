package com.blog.service;

import com.blog.common.PageResult;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;

    public ArticleService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    public PageResult<Article> page(int page, int size, Integer status) {
        int offset = (page - 1) * size;
        long total = articleMapper.count(status);
        List<Article> list = total == 0
                ? Collections.emptyList()
                : articleMapper.selectPage(offset, size, status);
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
        articleMapper.insert(article);
        saveTags(article.getId(), article.getTagIds());
        // 回查，返回含分类名/标签/时间的完整实体
        return articleMapper.selectById(article.getId());
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
        return articleMapper.selectById(id);
    }

    @Transactional
    public boolean delete(Long id) {
        if (articleMapper.selectById(id) == null) {
            return false;
        }
        articleMapper.deleteArticleTags(id);
        articleMapper.deleteById(id);
        return true;
    }

    private void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        articleMapper.insertArticleTags(articleId, tagIds);
    }
}

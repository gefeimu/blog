package com.blog.converter;

import com.blog.dto.ArticleRequest;
import com.blog.dto.ArticleVO;
import com.blog.entity.Article;
import org.springframework.stereotype.Component;

/**
 * Article 各模型间的转换：Request -> DO（入参）、DO -> VO（出参）。
 * 后续 DO 字段变化时，只需在此处调整映射，不污染 Controller / Service。
 */
@Component
public class ArticleConverter {

    public Article toEntity(ArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setCategoryId(request.getCategoryId());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setLayout(request.getLayout());
        article.setExt(request.getExt());
        article.setStatus(request.getStatus());
        article.setTagIds(request.getTagIds());
        return article;
    }

    public ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setCategoryId(article.getCategoryId());
        vo.setCategoryName(article.getCategoryName());
        vo.setSummary(article.getSummary());
        vo.setContentPath(article.getContentPath());
        vo.setLayout(article.getLayout());
        vo.setExt(article.getExt());
        vo.setContent(article.getContent());
        vo.setStatus(article.getStatus());
        vo.setViewCount(article.getViewCount());
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());
        vo.setTags(article.getTags());
        return vo;
    }
}

package com.blog.mapper;

import com.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    List<Article> selectPage(@Param("offset") int offset,
                             @Param("size") int size,
                             @Param("status") Integer status);

    long count(@Param("status") Integer status);

    Article selectById(Long id);

    List<String> selectTagNames(Long articleId);

    int insert(Article article);

    int update(Article article);

    int deleteById(Long id);

    int insertArticleTags(@Param("articleId") Long articleId,
                          @Param("tagIds") List<Long> tagIds);

    int deleteArticleTags(Long articleId);

    int incrementViewCount(Long id);
}

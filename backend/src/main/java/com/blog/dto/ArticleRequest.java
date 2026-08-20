package com.blog.dto;

import java.util.List;

/**
 * 文章入参 DTO：仅包含前端可提交的字段，与 DO / VO 解耦。
 */
public class ArticleRequest {

    private String title;
    private Long categoryId;
    private String summary;
    /** markdown 正文（新建必填；更新时传 null 表示不覆盖正文文件） */
    private String content;
    /** 0草稿 1发布 */
    private Integer status;
    private List<Long> tagIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}

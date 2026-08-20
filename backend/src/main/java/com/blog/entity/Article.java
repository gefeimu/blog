package com.blog.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Article {

    private Long id;
    private String title;
    private Long categoryId;
    /** 冗余展示字段：分类名（关联查询） */
    private String categoryName;
    private String summary;
    /** markdown 文件相对路径 */
    private String contentPath;
    /** 展示布局：default/minimal/banner（前台按此动态渲染） */
    private String layout;
    /** 扩展字段（JSON）：封面图 cover / 置顶 pinned / 自定义 slug 等，避免频繁加列 */
    private java.util.Map<String, Object> ext;
    /** 非持久化字段：正文内容（入参/出参） */
    private String content;
    /** 0草稿 1发布 */
    private Integer status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 冗余展示字段：标签名列表（关联查询） */
    private List<String> tags;
    /** 入参字段：标签ID列表（非表字段） */
    private List<Long> tagIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public java.util.Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(java.util.Map<String, Object> ext) {
        this.ext = ext;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}

package com.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章出参 VO：对外返回的完整视图模型，字段与前端约定保持稳定。
 */
public class ArticleVO {

    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private String summary;
    /** markdown 文件相对路径 */
    private String contentPath;
    /** 展示布局：default/minimal/banner（前台按此动态渲染） */
    private String layout;
    /** 扩展字段（JSON 对象）：封面图 cover / 置顶 pinned 等 */
    private java.util.Map<String, Object> ext;
    /** 正文内容（仅详情/创建/更新返回，列表页为 null） */
    private String content;
    /** 渲染后的 HTML（仅详情接口返回，由 MarkdownService 管线生成） */
    private String contentHtml;
    /** 0草稿 1发布 */
    private Integer status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;

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

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
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
}

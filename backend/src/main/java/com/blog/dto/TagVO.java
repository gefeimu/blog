package com.blog.dto;

import java.time.LocalDateTime;

/**
 * 标签视图：附带已发布文章数量，供前台标签云展示与点击筛选
 */
public class TagVO {

    private Long id;
    private String name;
    /** 已发布（status=1）文章数，草稿不计 */
    private Long count;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

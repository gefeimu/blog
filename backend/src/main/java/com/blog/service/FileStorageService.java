package com.blog.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String saveImage(MultipartFile file);

    void saveMarkdown(Long articleId, String content);

    String readMarkdown(Long articleId);

    void deleteMarkdown(Long articleId);
}

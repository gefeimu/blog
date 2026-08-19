package com.blog.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MarkdownService {

    private static final Logger log = LoggerFactory.getLogger(MarkdownService.class);

    @Value("${blog.markdown.dir:markdown}")
    private String markdownDir;

    private Path dir;

    @PostConstruct
    public void init() throws IOException {
        this.dir = Paths.get(markdownDir).toAbsolutePath().normalize();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            log.info("创建 markdown 目录: {}", dir);
        }
    }

    public void save(Long articleId, String content) {
        if (articleId == null) {
            return;
        }
        try {
            Files.writeString(resolve(articleId), content == null ? "" : content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("保存 markdown 文件失败: articleId=" + articleId, e);
        }
    }

    public String read(Long articleId) {
        if (articleId == null) {
            return null;
        }
        Path path = resolve(articleId);
        try {
            if (!Files.exists(path)) {
                return null;
            }
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取 markdown 文件失败: articleId=" + articleId, e);
        }
    }

    public void delete(Long articleId) {
        if (articleId == null) {
            return;
        }
        try {
            Files.deleteIfExists(resolve(articleId));
        } catch (IOException e) {
            log.warn("删除 markdown 文件失败: articleId={}, error={}", articleId, e.getMessage());
        }
    }

    private Path resolve(Long articleId) {
        // 文件名：{articleId}.md，保持简单
        return dir.resolve(articleId + ".md");
    }
}

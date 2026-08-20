package com.blog.service;

import com.blog.config.BlogProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageService.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;

    private static final DateTimeFormatter MONTH_DIR = DateTimeFormatter.ofPattern("yyyy/MM");

    private final BlogProperties.Storage storage;

    private Path markdownPath;

    private Path uploadPath;

    public LocalFileStorageService(BlogProperties properties) {
        this.storage = properties.getStorage();
    }

    @PostConstruct
    public void init() throws IOException {
        this.markdownPath = Paths.get(storage.getMarkdownDir()).toAbsolutePath().normalize();
        this.uploadPath = Paths.get(storage.getUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(markdownPath)) {
            Files.createDirectories(markdownPath);
            log.info("创建 markdown 目录: {}", markdownPath);
        }
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("创建 uploads 目录: {}", uploadPath);
        }
    }

    @Override
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("图片不能超过 5MB");
        }
        String original = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("仅支持 jpg/jpeg/png/gif/webp 格式");
        }
        ext = ext.toLowerCase(Locale.ROOT);
        validateImageContent(file, ext);
        // 按 yyyy/MM 分目录 + UUID 文件名，避免重名与单目录文件过多
        String month = LocalDate.now().format(MONTH_DIR);
        Path dir = uploadPath.resolve(month);
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path target = dir.resolve(filename);
        try {
            Files.createDirectories(dir);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("保存图片失败: " + original, e);
        }
        String url = storage.getUrlPrefix() + "/" + month + "/" + filename;
        log.info("图片已保存: {}", url);
        return url;
    }

    /** 校验文件头（magic bytes）与扩展名一致，防止伪造扩展名的非图片文件上传 */
    private void validateImageContent(MultipartFile file, String ext) {
        byte[] header = new byte[12];
        try (InputStream in = file.getInputStream()) {
            int read = in.readNBytes(header, 0, header.length);
            boolean valid = switch (ext) {
                case "jpg", "jpeg" -> read >= 3
                        && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
                case "png" -> read >= 8
                        && (header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47
                        && header[4] == 0x0D && header[5] == 0x0A && header[6] == 0x1A && header[7] == 0x0A;
                case "gif" -> read >= 4
                        && header[0] == 0x47 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x38;
                case "webp" -> read >= 12
                        && header[0] == 0x52 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x46
                        && header[8] == 0x57 && header[9] == 0x45 && header[10] == 0x42 && header[11] == 0x50;
                default -> false;
            };
            if (!valid) {
                throw new IllegalArgumentException("文件内容与扩展名不符或非有效图片");
            }
        } catch (IOException e) {
            throw new RuntimeException("读取图片内容失败: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void saveMarkdown(Long articleId, String content) {
        if (articleId == null) {
            return;
        }
        try {
            Files.writeString(markdownPath.resolve(articleId + ".md"),
                    content == null ? "" : content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("保存 markdown 文件失败: articleId=" + articleId, e);
        }
    }

    @Override
    public String readMarkdown(Long articleId) {
        if (articleId == null) {
            return null;
        }
        Path path = markdownPath.resolve(articleId + ".md");
        try {
            if (!Files.exists(path)) {
                return null;
            }
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取 markdown 文件失败: articleId=" + articleId, e);
        }
    }

    @Override
    public void deleteMarkdown(Long articleId) {
        if (articleId == null) {
            return;
        }
        try {
            Files.deleteIfExists(markdownPath.resolve(articleId + ".md"));
        } catch (IOException e) {
            log.warn("删除 markdown 文件失败: articleId={}, error={}", articleId, e.getMessage());
        }
    }
}

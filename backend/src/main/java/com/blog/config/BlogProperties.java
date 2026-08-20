package com.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 博客业务配置类：收敛 application.yml 中 blog.* 的所有配置，
 * 提供类型安全的访问方式（IDE 可自动补全，key 写错编译期即暴露）。
 */
@ConfigurationProperties(prefix = "blog")
public class BlogProperties {

    private final Storage storage = new Storage();
    private final Jwt jwt = new Jwt();
    private final Admin admin = new Admin();

    public Storage getStorage() {
        return storage;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Admin getAdmin() {
        return admin;
    }

    public static class Storage {
        /** Markdown 文件存储目录（容器内路径，docker-compose 挂载到宿主机 ./markdown） */
        private String markdownDir = "markdown";
        /** 图片上传目录（容器内路径，docker-compose 挂载到宿主机 ./uploads） */
        private String uploadDir = "uploads";
        /** 图片 URL 前缀（与 Nginx location /uploads/ 对应） */
        private String urlPrefix = "/uploads";

        public String getMarkdownDir() {
            return markdownDir;
        }

        public void setMarkdownDir(String markdownDir) {
            this.markdownDir = markdownDir;
        }

        public String getUploadDir() {
            return uploadDir;
        }

        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }

        public String getUrlPrefix() {
            return urlPrefix;
        }

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = urlPrefix;
        }
    }

    public static class Jwt {
        /** JWT 签名密钥（生产环境用 BLOG_JWT_SECRET 覆盖，至少 32 字符） */
        private String secret;
        /** token 有效期（毫秒），默认 7 天 */
        private long expirationMs = 604800000L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationMs() {
            return expirationMs;
        }

        public void setExpirationMs(long expirationMs) {
            this.expirationMs = expirationMs;
        }
    }

    public static class Admin {
        /** 默认管理员用户名（仅首次启动自动创建时生效） */
        private String username = "admin";
        /** 默认管理员密码（仅首次启动自动创建时生效） */
        private String password = "admin123";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

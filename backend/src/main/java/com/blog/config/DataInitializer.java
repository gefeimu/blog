package com.blog.config;

import com.blog.entity.AdminUser;
import com.blog.mapper.AdminUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动初始化：
 * 1. 幂等创建 admin_user 表（兼容已初始化的旧库，init.sql 不会重复执行）
 * 2. 无任何管理员时创建默认管理员
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final AdminUserMapper adminUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BlogProperties.Admin admin;

    public DataInitializer(JdbcTemplate jdbcTemplate,
                           AdminUserMapper adminUserMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           BlogProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.adminUserMapper = adminUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.admin = properties.getAdmin();
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS admin_user (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  username VARCHAR(50) NOT NULL COMMENT '用户名',
                  password_hash VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码哈希',
                  nickname VARCHAR(50) COMMENT '昵称',
                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                  UNIQUE KEY uk_username (username)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员'
                """);

        // 幂等迁移：为已初始化的旧库补充新列（init.sql 对全新库已包含，不会重复执行）
        ensureColumn("article", "layout",
                "layout VARCHAR(20) NOT NULL DEFAULT 'default' COMMENT '布局: default/minimal/banner'");
        ensureColumn("article", "ext",
                "ext JSON COMMENT '扩展字段(JSON): 封面图/置顶/自定义slug等'");

        if (adminUserMapper.selectByUsername(admin.getUsername()) == null) {
            AdminUser user = new AdminUser();
            user.setUsername(admin.getUsername());
            user.setPasswordHash(passwordEncoder.encode(admin.getPassword()));
            user.setNickname(admin.getUsername());
            adminUserMapper.insert(user);
            log.warn("已创建默认管理员: {} / {}（请尽快登录后台修改密码）", admin.getUsername(), admin.getPassword());
        } else {
            log.info("管理员账号已存在，跳过初始化");
        }
    }

    /** 检查列是否存在，不存在则 ALTER TABLE 补充（MySQL 8 无 ADD COLUMN IF NOT EXISTS） */
    private void ensureColumn(String table, String column, String columnDdl) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?
                """, Integer.class, table, column);
        if (count == null || count == 0) {
            jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + columnDdl);
            log.info("已为表 {} 补充字段: {}", table, column);
        }
    }
}

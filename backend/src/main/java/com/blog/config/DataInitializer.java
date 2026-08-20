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
}

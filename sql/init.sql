SET NAMES utf8mb4;
USE blog;

CREATE TABLE IF NOT EXISTS category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT '分类名',
  sort INT DEFAULT 0 COMMENT '排序',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类';

CREATE TABLE IF NOT EXISTS tag (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT '标签名',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签';

CREATE TABLE IF NOT EXISTS article (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL COMMENT '标题',
  category_id BIGINT COMMENT '分类ID',
  summary VARCHAR(500) COMMENT '摘要',
  content_path VARCHAR(500) COMMENT 'markdown 文件相对路径',
  layout VARCHAR(20) NOT NULL DEFAULT 'default' COMMENT '布局: default/minimal/banner',
  ext JSON COMMENT '扩展字段(JSON): 封面图/置顶/自定义slug等',
  status TINYINT DEFAULT 0 COMMENT '0草稿 1发布',
  view_count INT DEFAULT 0 COMMENT '浏览量',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_status_created (status, created_at),
  KEY idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章';

CREATE TABLE IF NOT EXISTS article_tag (
  article_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (article_id, tag_id),
  KEY idx_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联';

CREATE TABLE IF NOT EXISTS admin_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password_hash VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码哈希',
  nickname VARCHAR(50) COMMENT '昵称',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员';

INSERT INTO category (name, sort) VALUES
  ('开发技术', 1),
  ('拼装模型', 2),
  ('骑行', 3),
  ('咖啡', 4),
  ('木工', 5),
  ('随笔', 6);

INSERT INTO tag (name) VALUES ('入门'), ('工具'), ('记录'), ('复盘');

INSERT INTO article (title, category_id, summary, status) VALUES
  ('第一篇：博客搭起来了', 1, '从零搭全栈博客的记录', 1);

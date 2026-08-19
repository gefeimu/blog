# 个人博客 - 本地脚手架

最小可跑骨架：MySQL 8 + Spring Boot 3 后端 + Nginx 反代，跑通后端链路。

## 目录结构

```
blog/
├── docker-compose.yml      # 三件套编排
├── .env                    # 数据库密码等环境变量
├── nginx/
│   ├── default.conf        # Nginx 反代配置
│   └── html/index.html     # 前端占位页（Vue 接入后替换）
├── sql/
│   └── init.sql            # 建表 + 测试数据（首次启动自动执行）
└── backend/
    ├── Dockerfile          # 多阶段构建（maven 打包 + jre 运行）
    ├── settings.xml        # maven 阿里云仓库加速
    ├── pom.xml
    └── src/main/...
```

## 跑起来

把整个 `blog/` 目录传到 Ubuntu 笔记本，然后在目录里执行：

```bash
docker compose up -d --build
```

首次会慢几分钟（拉镜像 + maven 下依赖），之后改代码 rebuild 走缓存会快。

## 验证

```bash
docker compose ps                  # 三个容器都 Up
curl localhost/api/health          # 返回 JSON = 后端起来了
curl localhost/api/categories      # 返回 6 条分类 = MySQL 连通
```

浏览器开 `http://笔记本IP/` 看到占位页 + 两个接口链接 = 全链路通。

## 常用命令

```bash
docker compose logs -f backend     # 看后端日志
docker compose restart backend     # 改后端代码后重启
docker compose down                 # 停掉全部
docker compose down -v             # 停掉并删 MySQL 数据（重来）
```

## 下一步

- [ ] 加 MyBatis（XML 映射），把 CategoryController 从 JdbcTemplate 换成 Mapper
- [ ] 加文章 CRUD 接口
- [ ] 接 Vue 3 + Vite 前端，替换 nginx/html
- [ ] 后台 Markdown 编辑器（mdEditorV3）
- [ ] 上云：compose 原样搬，补域名 + SSL + 备案

# 白工的博客

个人兴趣爱好笔记博客：开发技术、拼装模型、骑行、咖啡、木工、随笔。

自建全栈项目，主要为了练技术 —— 从零搭起一套「Spring Boot + MySQL + Vue」的完整前后端，并自己完成部署上线。更新频率约一月一篇，读者是自己和朋友。

## 技术栈

| 层 | 选型 |
|---|---|
| 前端 | Vue 3 + Vite + Element Plus（开发中） |
| 后端 | Spring Boot 3.2 · MyBatis（XML 映射）· Spring Security |
| 数据库 | MySQL 8（正文存 Markdown 文件，DB 只存元数据） |
| 部署 | Docker Compose（MySQL + 后端 + Nginx） |
| 持续集成 | GitHub Actions（规划中） |

## 功能

- 文章：分页列表（按分类/状态筛选）、详情、发布/草稿、增删改
- 内容组织：分类（主题大类，单选）+ 标签（跨分类细粒度，多选）
- 博客惯例：详情页浏览量自动 +1
- API 风格：REST + JSON

## 架构

```
浏览器
  │
  ▼
Nginx (:80) ── 静态资源 + 反代 /api
  │
  ▼
Spring Boot (:8080)
  │
  ▼
MySQL 8 (内网 mysql:3306，不暴露宿主机)
```

## 目录结构

```
blog/
├── docker-compose.yml      # 三件套编排
├── .env                    # 数据库密码等环境变量（不入库）
├── nginx/
│   ├── default.conf        # Nginx 反代配置
│   └── html/index.html     # 前端占位页（Vue 接入后替换）
├── sql/
│   └── init.sql            # 建表 + 测试数据（首次启动自动执行）
└── backend/
    ├── Dockerfile          # 多阶段构建（maven 打包 + jre 运行）
    ├── settings.xml        # maven 阿里云仓库加速
    ├── pom.xml
    └── src/main/java/com/blog/
        ├── controller/     # REST 接口
        ├── service/        # 业务逻辑（事务）
        ├── mapper/         # MyBatis 接口
        ├── entity/         # 实体
        └── common/         # 通用封装（分页等）
```

## 快速开始

需要 Docker + Docker Compose。

```bash
# 1. 准备环境变量
cp .env.example .env   # 按需修改数据库密码

# 2. 启动三件套（首次拉镜像 + 下依赖，稍慢）
docker compose up -d --build

# 3. 验证
docker compose ps                  # 三个容器都 Up
curl localhost/api/health          # 后端存活
curl localhost/api/categories      # MySQL 连通（6 条中文分类）
```

浏览器访问 `http://<服务器IP>/` 看到页面即全链路通。

## API 一览

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/health` | 健康检查 |
| GET | `/api/categories` | 分类列表 |
| GET | `/api/categories/{id}` | 分类详情 |
| GET | `/api/articles?page=1&size=10&status=1` | 文章分页列表 |
| GET | `/api/articles/{id}` | 文章详情（浏览量 +1） |
| POST | `/api/articles` | 新建文章 |
| PUT | `/api/articles/{id}` | 更新文章 |
| DELETE | `/api/articles/{id}` | 删除文章 |

## 常用命令

```bash
docker compose logs -f backend     # 看后端日志
docker compose up -d --build backend  # 改代码后重建
docker compose down                 # 停掉全部
docker compose down -v             # 停掉并删 MySQL 数据（重来）
```

## Roadmap

- [x] 脚手架：MySQL 8 + Spring Boot 3 + Nginx 反代
- [x] MyBatis（XML 映射）替换 JdbcTemplate
- [x] 文章 CRUD（分页/详情/标签关联）
- [ ] 文章正文 Markdown 文件存储 + 读取
- [ ] Vue 3 前台（列表/详情渲染）
- [ ] 后台管理（登录 + Markdown 编辑器）
- [ ] GitHub Actions CI/CD
- [ ] 上云：阿里云 ECS + 域名 + SSL + ICP 备案

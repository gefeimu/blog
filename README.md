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
| 持续集成 | GitHub Actions（CI + CD 自动部署） |

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
├── docker-compose.yml      # 三件套编排（backend 支持本地构建 + 远程镜像双模式）
├── .env                    # 数据库密码等环境变量（不入库）
├── .github/workflows/
│   ├── ci-cd.yml           # CI/CD：后端测试 → 镜像推 ghcr.io → 自托管 Runner 部署
│   └── build-frontend.yml  # 前端产物自动构建回提交
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
| POST | `/api/upload/image` | 图片上传（multipart，jpg/png/gif/webp，≤5MB），返回 `{url}` |

## 常用命令

```bash
docker compose logs -f backend     # 看后端日志
docker compose up -d --build backend  # 改代码后重建
docker compose down                 # 停掉全部
docker compose down -v             # 停掉并删 MySQL 数据（重来）
```

## CI/CD 部署

push 到 main 自动执行（`.github/workflows/ci-cd.yml`）：

```
backend-test  →  docker-image  →  deploy
mvn package    构建镜像推 ghcr.io  自托管 Runner 本机执行
（编译+测试）   latest + commit SHA   git pull + pull + up -d
```

前两段在 GitHub 云端跑；`deploy` 段跑在**部署机上的自托管 Runner**（Runner 主动连 GitHub 拉任务，只需出网，无需公网 IP、无需 SSH 密钥）。前端源码改动走 `build-frontend.yml`：先自动构建产物回提交到 `nginx/html`，再由产物提交触发上表流水线部署，前后端互不阻塞。

**首次配置（只需一次）：**

1. **部署机装 Runner**（仓库 Settings → Actions → Runners → New self-hosted runner，选 Linux x64，复制下载+注册命令，在部署机上执行）：

```bash
# 下载并解压 runner（具体 URL/命令以页面为准）
mkdir -p ~/actions-runner && cd ~/actions-runner
curl -o actions-runner-linux-x64-2.319.1.tar.gz -L <下载地址>
tar xzf actions-runner-linux-x64-2.319.1.tar.gz
./config.sh --url https://github.com/<你的用户名>/blog --token <页面给的 TOKEN>
./run.sh          # 前台跑；想开机自启见下方 systemd
```

2. **给 Runner 用户 Docker 权限**（部署脚本要用 docker）：

```bash
sudo usermod -aG docker $USER && newgrp docker
```

3. **准备部署目录**（流水线会自动 clone 到 `~/blog`，只需放 `.env`）：

```bash
cd ~ && git clone https://github.com/<你的用户名>/blog.git
cp blog/.env.example blog/.env   # 填入数据库密码/JWT密钥/管理员密码（与 .env.example 格式一致）
```

> Runner 开机自启（可选，推荐）：`sudo ./svc.sh install && sudo ./svc.sh start`

之后每次 push，流水线自动完成：测试 → 打镜像推 `ghcr.io/<仓库>:latest` → 部署机 `git pull` + `docker compose pull backend` + `up -d --no-build`。

镜像当前存 ghcr.io（公开仓库免费无限、零额外注册）；部署机在国内拉 ghcr.io 慢时，可切阿里云 ACR，只需改 `docker-compose.yml` 的 image 地址和流水线登录步骤。

## Roadmap

- [x] 脚手架：MySQL 8 + Spring Boot 3 + Nginx 反代
- [x] MyBatis（XML 映射）替换 JdbcTemplate
- [x] 文章 CRUD（分页/详情/标签关联）
- [x] 文章正文 Markdown 文件存储 + 读取
- [x] Vue 3 前台（列表/详情渲染）
- [x] 图片上传 + Nginx 静态托管（`/uploads`，存储层已抽象 FileStorageService，可切换 OSS）
- [x] 后台管理（登录 + Markdown 编辑器）
- [x] GitHub Actions CI/CD
- [ ] 上云：阿里云 ECS + 域名 + SSL + ICP 备案

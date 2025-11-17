# 个人博客系统

一个基于前后端分离架构的现代化博客系统，展示了全栈开发能力与工程实践。

## 效果展示

![blog1](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog1.png)
![blog2](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog2.png)
![blog3](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog3.png)
![blog4](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog4.png)
![blog5](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog5.png)

## 项目概述

这是一个基于Spring Boot和Vue.js的全栈博客系统，采用前后端分离架构。后端使用Spring Boot + MongoDB + Redis技术栈，前端使用Vue 3 + TypeScript构建。

### 技术栈

#### 后端 (Java)
- **核心框架**: Spring Boot 3.x (Java 21)
- **数据库**: MongoDB (主要数据存储)
- **缓存**: Redis (用于数据缓存)
- **安全认证**: Spring Security + JWT
- **API文档**: Swagger/OpenAPI 3.0
- **构建工具**: Maven
- **文件存储**: 本地存储 + 日期分目录管理

#### 前端 (TypeScript)
- **框架**: Vue 3 (Composition API)
- **语言**: TypeScript
- **构建工具**: Vite
- **路由**: Vue Router
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **样式**: CSS Modules + 响应式设计

### 核心功能

1. **文章管理**
   - 创建、编辑、删除文章
   - 文章状态管理（草稿、已发布、已下架）
   - Markdown格式内容编辑与实时预览
   - 文章分类管理
   - 文章搜索与筛选

2. **摄影记录**
   - 支持批量图片上传（最多20张）
   - 拖拽上传功能
   - 图片预览和实时进度显示
   - 按日期分目录存储管理
   - 支持JPG、PNG、GIF、WebP格式

3. **用户认证**
   - 用户注册与登录
   - JWT Token认证机制
   - 角色权限控制（管理员、普通用户）

4. **分类系统**
   - 分类创建、编辑、删除
   - 按分类筛选文章

5. **搜索功能**
   - 关键词搜索文章
   - 组合条件筛选

## 系统架构

```text
┌─────────────────┐    REST API    ┌──────────────────┐
│   Vue Frontend  │◄──────────────►│  Spring Backend  │
│   (Port:3001)   │                │   (Port:8080)    │
└─────────────────┘                └──────────────────┘
                                            │
                                    ┌───────┴───────┐
                                    │ MongoDB       │
                                    │ (Port:27017)  │
                                    └───────────────┘
                                            │
                                    ┌───────┴───────┐
                                    │ Redis         │
                                    │ (Port:6379)   │
                                    └───────────────┘
```

## 快速开始

### 环境要求
- Java 17+ (推荐 Java 21)
- Node.js 16+ (推荐 18+)
- MongoDB 5+
- Redis 6+
- Maven 3.6+

### 本地开发部署

#### 1. 克隆项目
```bash
git clone <repository-url>
cd myBlog_v2
```

#### 2. 后端配置与启动
```bash
# 进入项目目录（已在根目录）

# 配置数据库
# 修改 src/main/resources/application.yml
# 设置 MongoDB 和 Redis 连接信息

# 启动 MongoDB 和 Redis
mongod --dbpath /path/to/mongodb/data
redis-server

# 构建并运行后端
mvn clean install
mvn spring-boot:run
```

后端服务地址：`http://localhost:8080`
API文档：`http://localhost:8080/swagger-ui.html`

#### 3. 前端配置与启动
```bash
# 进入前端目录
cd blog-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端地址：`http://localhost:3001`

#### 4. 默认账户
系统初始化时会自动创建管理员账户：
- 用户名：`admin`
- 密码：`admin123`

⚠️ **首次登录后请修改默认密码！**

## 生产环境部署

### 🚀 推荐方案：Railway（最适合）

**优势**：支持Docker、内置数据库、免费额度充足、配置简单

#### 步骤：
1. 注册 [Railway](https://railway.app) 账号
2. 连接 GitHub 仓库
3. 自动检测 Java 项目并部署
4. 添加 MongoDB 和 Redis 插件
5. 配置环境变量
6. 自动获得 HTTPS 域名

#### 前端部署：
```bash
# 构建生产版本
cd blog-frontend
npm run build

# 部署到 Vercel 或 Netlify
npm i -g vercel
vercel --prod
```

### 方案二：Vercel + MongoDB Atlas

**前端部署到 Vercel：**
1. 连接 GitHub 仓库到 Vercel
2. 自动构建和部署 Vue 应用
3. 获得全球 CDN 加速

**数据库使用 MongoDB Atlas：**
1. 注册 MongoDB Atlas 免费版
2. 创建集群并配置网络访问
3. 获取连接字符串并配置到后端

**后端部署选项：**
- Railway（推荐）
- Render
- 阿里云/腾讯云学生机

### 方案三：传统云服务器

#### 1. 购买云服务器（阿里云/腾讯云/华为云）
#### 2. 环境配置
```bash
# 安装 Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# 安装 Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 安装 MongoDB
wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/5.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-5.0.list
sudo apt-get update
sudo apt-get install -y mongodb-org

# 安装 Redis
sudo apt install redis-server
```

#### 3. 应用部署
```bash
# 后端部署
mvn clean package -DskipTests
nohup java -jar target/myBlog-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# 前端构建部署
cd blog-frontend
npm run build
# 配置 nginx 指向前端构建产物
```

#### 4. Nginx 配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/blog-frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端 API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # 上传文件访问
    location /uploads/ {
        alias /path/to/uploads/;
        expires 30d;
    }
}
```

## 环境变量配置

### 生产环境变量
```bash
# 后端环境变量
SPRING_DATA_MONGODB_URI=mongodb://username:password@host:port/database
SPRING_DATA_REDIS_HOST=redis-host
SPRING_DATA_REDIS_PASSWORD=redis-password
JWT_SECRET=your-secure-jwt-secret-key

# 前端环境变量（构建时）
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## 文件上传配置

### 本地存储路径
- 上传目录：`uploads/`
- 访问路径：`/uploads/{yyyy}/{MM}/{dd}/{filename}`
- 最大文件大小：10MB
- 支持格式：jpg, jpeg, png, gif, webp

### 云存储迁移（可选）
如需使用云存储，修改 `FileController.java`：
```java
// 集成阿里云 OSS
// 集成腾讯云 COS
// 集成 AWS S3
```

## 备份与维护

### 数据备份
```bash
# MongoDB 备份
mongodump --uri="mongodb://localhost:27017/MyBlog" --out=/backup/path

# 上传文件备份
tar -czf uploads-backup.tar.gz uploads/
```

### 日志管理
- 应用日志：`logs/application.log`
- 日志级别：INFO（生产环境）
- 最大文件大小：10MB
- 保留历史：10个文件

## 性能优化建议

1. **启用 Redis 缓存**：已集成，确保 Redis 正常运行
2. **数据库索引**：MongoDB 已创建必要索引
3. **前端优化**：
   - 代码分割已配置
   - 图片懒加载
   - CDN 加速（生产环境）
4. **后端优化**：
   - 数据库连接池优化
   - 静态资源缓存
   - Gzip 压缩

## 安全建议

1. **修改默认密码**：首次登录后立即修改
2. **JWT 密钥**：生产环境使用强密钥
3. **数据库安全**：使用强密码，限制访问IP
4. **HTTPS**：生产环境必须启用
5. **文件上传安全**：限制文件类型和大小

## 常见问题

### Q: 上传的图片无法访问？
A: 检查文件权限和 nginx 配置，确保 uploads 目录可访问

### Q: 数据库连接失败？
A: 检查 MongoDB 和 Redis 服务状态，确认连接配置

### Q: 构建失败？
A: 确保使用正确的 Node.js 版本（查看 package.json engines）

### Q: 内存不足？
A: 调整 JVM 参数：`-Xms512m -Xmx1024m`

## 技术支持

- 项目基于 Spring Boot 3.x 和 Vue 3 最新稳定版
- 如有问题可查看日志文件或在 GitHub 提交 Issue
- API 文档访问：`/swagger-ui.html`

## 许可证

本项目仅供学习和参考使用。
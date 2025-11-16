# 个人博客系统

一个基于前后端分离架构的现代化博客系统，展示了全栈开发能力与工程实践。

## 效果展示

![blog1](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog1.png)

![blog2](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog2.png)

![blog3](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog3.png)

![blog4](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog4.png)

![blog5](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog5.png)

我来为您生成一个完整的README文件，基于对项目的分析：

## 项目概述

这是一个基于Spring Boot和Vue.js的全栈博客系统，采用前后端分离架构。后端使用Spring Boot + MongoDB + Redis技术栈，前端使用Vue 3 + TypeScript构建。

### 技术栈

#### 后端 (Java)
- **核心框架**: Spring Boot 3.x
- **数据库**: MongoDB (主要数据存储)
- **缓存**: Redis (用于数据缓存)
- **安全认证**: Spring Security + JWT
- **API文档**: Swagger/OpenAPI 3.0
- **构建工具**: Maven

#### 前端 (TypeScript)
- **框架**: Vue 3 (Composition API)
- **语言**: TypeScript
- **构建工具**: Vite
- **路由**: Vue Router
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **样式**: CSS Modules

### 核心功能

1. **文章管理**
   - 创建、编辑、删除文章
   - 文章状态管理（草稿、已发布、已下架）
   - Markdown格式内容编辑与实时预览
   - 文章分类管理

2. **用户认证**
   - 用户注册与登录
   - JWT Token认证机制
   - 角色权限控制（管理员、普通用户）

3. **分类系统**
   - 分类创建、编辑、删除
   - 按分类筛选文章

4. **搜索功能**
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


## API接口设计

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/user/register` - 用户注册

### 文章相关
- `GET /api/article/list` - 获取所有文章
- `GET /api/article/published` - 获取已发布文章
- `GET /api/article/drafts` - 获取草稿文章（管理员）
- `GET /api/article/{id}` - 获取文章详情
- `POST /api/article/new` - 创建文章（管理员）
- `POST /api/article/edit/{id}` - 更新文章（管理员）
- `POST /api/article/delete/{id}` - 删除文章（管理员）
- `POST /api/article/{id}/publish` - 发布文章（管理员）

### 分类相关
- `GET /api/category` - 获取所有分类
- `POST /api/category` - 创建分类（管理员）
- `PUT /api/category/{id}` - 更新分类（管理员）
- `DELETE /api/category/{id}` - 删除分类（管理员）

## 部署要求

### 环境依赖
- Java 17+
- Node.js 16+
- MongoDB 5+
- Redis 6+

### 后端配置
1. 安装MongoDB并创建数据库`MyBlog`
2. 安装Redis并设置密码`root`
3. 修改`application.yml`中的数据库连接配置
4. 运行`mvn spring-boot:run`启动后端服务

### 前端配置
1. 安装依赖：`npm install`
2. 开发模式启动：`npm run dev`
3. 构建生产版本：`npm run build`

### 默认账户
系统初始化时会自动创建管理员账户：
- 用户名：`admin`
- 密码：`admin123`

## 项目特点

1. **安全性**
   - JWT Token认证机制
   - 基于角色的访问控制
   - 密码加密存储

2. **性能优化**
   - Redis缓存支持
   - 数据库索引优化
   - 前端代码分割

3. **可维护性**
   - 前后端分离架构
   - 清晰的代码结构
   - 完整的异常处理机制

4. **用户体验**
   - 响应式设计
   - Markdown实时预览
   - 丰富的文章编辑功能

## 开发指南

### 后端开发
```bash
# 克隆项目
git clone <repository-url>

# 进入后端目录
cd myBlog_v2/src

# 运行项目
mvn spring-boot:run
```


### 前端开发
```bash
# 进入前端目录
cd myBlog_v2/blog-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```


## 未来规划

1. 增加文章评论功能
2. 实现文章点赞和收藏
3. 添加文章浏览统计
4. 支持文章标签系统
5. 增加文件上传管理
6. 实现邮件通知功能

## 贡献指南

欢迎提交Issue和Pull Request来改进这个项目。在提交代码前，请确保：

1. 代码符合项目编码规范
2. 添加必要的测试用例
3. 更新相关文档

## 许可证

本项目仅供学习和参考使用。


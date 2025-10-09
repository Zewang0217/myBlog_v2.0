# 个人博客系统

一个基于前后端分离架构的现代化博客系统，展示了全栈开发能力与工程实践。

## 效果展示

![blog1](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog1.png)

![blog2](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog2.png)

![blog3](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog3.png)

![blog4](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog4.png)

![blog5](https://github.com/Zewang0217/myBlog_v2.0/blob/master/photos/blog5.png)

## 🎯 项目概述

这是一个功能完整的个人博客平台，支持文章的创建、编辑、发布和管理。项目采用现代化的技术栈，遵循良好的软件工程实践，旨在展示我在全栈开发中的技术能力和架构思维。

## 🛠 技术栈

### 前端技术栈
- **Vue 3** + **TypeScript** - 渐进式JavaScript框架，提供类型安全和现代化开发体验
- **Vue Router** - 单页面应用路由管理
- **Pinia** - Vue 3官方推荐的状态管理库
- **Vite** - 极速的构建工具，提供热模块替换(HMR)
- **Axios** - 基于Promise的HTTP客户端
- **Marked** + **DOMPurify** - Markdown解析与XSS防护
- **ESLint** + **Prettier** - 代码质量与格式化工具

### 后端技术栈
- **Spring Boot 3** - 简化Spring应用的快速开发框架
- **MyBatis** - 持久层框架，提供SQL映射
- **MySQL** - 关系型数据库
- **Lombok** - 简化Java代码的工具库
- **Jakarta Validation** - Java Bean验证框架
- **JUnit 5** + **Mockito** - 单元测试框架

### 开发实践

- 前后端分离架构
- RESTful API设计
- 统一响应格式和异常处理
- 数据传输对象(DTO)模式
- 日志记录和监控
- CORS跨域支持
- 输入验证和安全防护

## 🔧 核心功能

- ✅ 文章创建与实时Markdown预览
- ✅ 文章发布与状态管理(草稿/已发布)
- ✅ 文章列表展示与详情查看
- ✅ 文章编辑与删除
- ✅ 响应式UI设计
- ✅ 草稿箱管理
- ✅ 代码质量保证(ESLint/Prettier/单元测试)

## 🏗 项目架构

```text
blog-frontend/ # 前端项目 
├── src/
│ ├── api/ # API客户端封装
│ ├── components/ # 可复用组件 
│ ├── composables/ # Vue组合式函数 
│ ├── router/ # 路由配置 
│ ├── views/ # 页面组件 
│ └── types/ # TypeScript类型定义 
└── ...
myBlog-backend/ # 后端项目 
├── src/main/java/ 
│ ├── controller/ # 控制器层 
│ ├── service/ # 业务逻辑层 
│ ├── dao/ # 数据访问层 
│ ├── model/ # 实体模型 
│ ├── dto/ # 数据传输对象 
│ └── common/ # 通用工具类 
└── ...
```

## 💡 技术亮点

### 前端亮点
1. **组合式API架构** - 使用Vue 3 Composition API实现可复用逻辑
2. **类型安全** - 完整的TypeScript类型定义，提升代码可靠性
3. **实时预览功能** - Markdown编辑器支持实时渲染预览
4. **响应式设计** - 适配不同屏幕尺寸的现代化UI
5. **状态管理** - 使用Pinia进行全局状态管理

### 后端亮点
1. **分层架构** - Controller-Service-DAO三层架构，职责清晰
2. **统一异常处理** - 全局异常处理器，统一错误响应格式
3. **数据验证** - 使用Jakarta Validation进行输入验证
4. **枚举模式** - 文章状态使用枚举类型，提高代码可读性
5. **事务管理** - 使用Spring声明式事务保证数据一致性
6. **日志记录** - 完整的日志体系，便于问题排查

## 📈 性能优化

- 前端使用Vite构建，提供极速的开发体验
- 后端使用MyBatis优化SQL查询
- 前端实现组件懒加载，减少初始包体积
- 后端接口使用分页查询(可扩展)
- 前端实现防抖处理，优化用户输入体验

## 🔮 未来改进计划

### 短期目标
1. **用户认证系统** - 添加JWT认证和权限控制  OK
2. **文章分类和标签** - 支持文章分类管理和标签系统 OK
3. **搜索功能** - 实现全文搜索和过滤功能 OK
4. **图片上传** - 集成文件上传服务，支持文章插图
5. **评论系统** - 添加文章评论功能
6. **添加护眼模式**

### 中期目标
1. **服务端渲染(SSR)** - 使用Nuxt.js提升SEO和首屏加载性能
2. **微服务架构** - 将单体应用拆分为微服务
3. **缓存优化** - 集成Redis缓存热点数据 OK
4. **消息队列** - 使用RabbitMQ/Kafka处理异步任务
5. **容器化部署** - 使用Docker和Kubernetes进行容器编排

### 长期目标
1. **多语言支持** - 国际化(i18n)支持
2. **数据分析** - 集成Google Analytics等分析工具
3. **PWA支持** - 渐进式Web应用，提供原生应用体验
4. **API文档** - 集成Swagger/OpenAPI自动生成接口文档
5. **微前端架构** - 采用微前端方案支持团队协作开发

## 🧪 测试策略(还未执行)

- 前端单元测试：使用Vitest和Vue Test Utils
- 端到端测试：使用Cypress进行E2E测试
- 后端单元测试：使用JUnit 5和Mockito
- API测试：使用Postman集合进行接口测试 ok

## 🚀 部署方案

可以使用以下技术栈进行生产环境部署：
- **Nginx** - 反向代理和静态资源服务
- **Docker** - 容器化部署
- **Jenkins/GitHub Actions** - CI/CD自动化部署
- **云服务** - 阿里云/AWS等云平台部署

## 📊 项目亮点总结

| 方面     | 技术实现              | 优势               |
| -------- | --------------------- | ------------------ |
| 架构设计 | 前后端分离            | 易于维护和扩展     |
| 开发效率 | Vite + Spring Boot    | 快速开发和热更新   |
| 代码质量 | TypeScript + ESLint   | 类型安全和代码规范 |
| 测试覆盖 | 单元测试 + E2E测试    | 保证代码质量       |
| 用户体验 | 响应式设计 + 实时预览 | 现代化交互体验     |

## 📞 联系方式

- 邮箱: Zewang0217@outlook.com
- GitHub: [Zewang0217](https://github.com/Zewang0217)

---
*该项目为个人技术展示项目，旨在体现全栈开发能力和工程实践经验*



待学习知识点：

+ stream 流  collector




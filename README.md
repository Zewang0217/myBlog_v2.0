# MyBLog

> 这是我的博客2.0版本。采用java21，结合新的java技术完成

[TOC]



# MyBlog 2.0

## 结构：

---

> Record
>
> Record 是 Java 14 引入的一个新特性（在 Java 16 中成为正式特性），它是一种更简洁的方式来创建不可变的数据载体类。
> Record 简介
> 1. 什么是 Record？
> Record 是一种特殊的类，主要用于存储不可变的数据。它自动为你生成：
> + 构造器
> + getter 方法
> + equals() 方法
> + hashCode() 方法
> + toString() 方法



## 前端知识简介

### 1. 主入口文件
- [main.ts](cci:7://file:///D:/Zewang/PROJECTS/myBlog_v2/blog-frontend/src/main.ts:0:0-0:0) - 这是Vue应用的入口文件，负责初始化Vue应用、注册全局组件和插件。
- [App.vue](cci:7://file:///D:/Zewang/PROJECTS/myBlog_v2/blog-frontend/src/App.vue:0:0-0:0) - 根组件，所有其他组件的容器，通常包含应用的布局结构。

### 2. API 相关
- `api/` 目录包含与后端通信的代码：
  - `apiClient.ts` - 配置了HTTP客户端（如axios）的实例，设置基础URL、请求/响应拦截器等。
  - 其他API模块文件 - 按功能模块组织的API请求函数。

### 3. 资源文件
- `assets/` - 存放静态资源文件，如图片、字体、全局CSS等。

### 4. 组件 (Components)
- `components/` 目录包含可复用的UI组件：
  - `blog/` - 博客相关组件
    - `ArticleDetail.vue` - 显示单篇文章详情的组件
    - 可能还有其他如文章列表、评论等组件

### 5. 组合式函数 (Composables)
- `composables/` - 存放Vue 3的组合式API函数，用于封装和复用状态逻辑。
  - 例如：`usePosts.ts` 可能包含获取和管理文章数据的逻辑

### 6. 路由 (Router)
- `router/` - 包含路由配置文件，定义URL路径与Vue组件的映射关系。
  - `index.ts` - 定义所有路由规则

### 7. 状态管理 (Stores)
- `stores/` - 使用Pinia进行状态管理，存放全局状态和业务逻辑。
  - 例如：`userStore.ts` 管理用户登录状态

### 8. 类型定义
- `types/` - 包含TypeScript类型定义文件
  - 定义接口和类型，如文章、用户等数据结构

### 9. 视图 (Views)
- `views/` - 包含页面级组件
  - `BlogArticleCreate.vue` - 创建博客文章的页面
  - 其他页面如首页、登录页等

### 10. 配置文件
- 根目录下的配置文件：
  - `package.json` - 项目依赖和脚本配置
  - `tsconfig.json` - TypeScript配置
  - `vite.config.ts` - Vite构建工具配置

### 技术栈说明：
- **Vue 3** - 前端框架
- **TypeScript** - 类型安全的JavaScript超集
- **Pinia** - 状态管理
- **Vite** - 构建工具
- **Vue Router** - 路由管理

---

## 新增功能：草稿箱

---

## 新增功能：日志管理

+ 工作原理：

+ Spring Boot 使用了 Logback 作为默认的日志框架（通过 spring-boot-starter-web 依赖引入）。其工作原理如下：

+ 自动配置：Spring Boot 启动时会自动读取 application.yml 中的 logging 配置

+ 日志收集：应用程序中的各种组件（Spring、MyBatis、自定义代码等）产生日志

+ 过滤处理：根据配置的日志级别进行过滤，只输出符合级别的日志

+ 格式化输出：按照指定的格式输出到控制台和文件

+ 文件管理：当日志文件达到 10MB 时自动创建新文件，并保留最近的 10 个文件

+ 日志系统的作用

  + 日志级别控制：
    + 根日志级别设置为 INFO
    + 您的自定义包 org.Zewang.myBlog 设置为 DEBUG 级别，便于开发调试
    + Spring 框架日志级别设置为 WARN，减少不必要的信息输出
    + MyBatis 日志设置为 DEBUG，方便查看 SQL 执行情况
  + 日志输出：
    + 控制台输出：显示实时日志信息
    + 文件输出：将日志写入 logs/application.log 文件，便于后续分析
  + 日志格式：
    + 时间戳：精确到毫秒
    + 线程信息：便于排查并发问题
    + 日志级别：区分信息重要程度
    + 日志来源：显示哪个类输出的日志
    + 日志内容：具体的日志信息
  + 日志文件管理：
    + 单个日志文件最大为 10MB
    + 最多保留 10 个历史日志文件
    + 自动滚动，防止日志文件过大

  

  

  Spring Boot 使用了 Logback 作为默认的日志框架（通过 spring-boot-starter-web 依赖引入）。其工作原理如下：
  自动配置：Spring Boot 启动时会自动读取 application.yml 中的 logging 配置
  日志收集：应用程序中的各种组件（Spring、MyBatis、自定义代码等）产生日志
  过滤处理：根据配置的日志级别进行过滤，只输出符合级别的日志
  格式化输出：按照指定的格式输出到控制台和文件
  文件管理：当日志文件达到 10MB 时自动创建新文件，并保留最近的 10 个文件



> 可优化方向
>
> ### 功能增强
>
> 1. **内容管理**
>    - [x] 添加文章草稿功能 
>    - [x] 支持 Markdown 实时预览
>    - [ ] 添加标签和分类管理
> 2. **社交功能**
>    - 评论系统
>    - 文章分享功能
>    - 用户收藏和点赞
> 3. **搜索功能**
>    - 实现全文搜索
>    - 添加搜索建议
>    - 支持按标签/分类筛选

# MyBLog

> 这是我的博客2.0版本。采用java21，结合新的java技术完成

[TOC]



# MyBlog 1.0

## 技术栈及解释（初期规划）：

#### 后端篇：

- **Spring Boot**
  相当于造房子的地基 + 框架。它帮你快速搭建起一个项目，不用每次都费劲配置一大堆繁琐的东西。Kyrie Blog里它就是启动核心，提供了简化的配置和内嵌的服务器（比如Tomcat）。
- **MyBatis**
  数据库“翻译官”。数据库说“SQL语言”，Java说“Java语言”，它帮两边对接。你写 SQL，它安全地把结果变成 Java 对象。
- **Thymeleaf**
  网页模板引擎。它就像一个印刷模具，把后端输出的数据“嵌入”到 HTML 页面里，最后生成用户看到的网页。
- **PageHelper**
  数据分页助手。比如博客文章一口气有 100 篇，不可能一次性全扔出来。这时 PageHelper就出场，自动帮你分页显示。
- **Ehcache**
  缓存工具。常用的数据（比如首页推荐文章）放内存里，下次直接拿，速度飞快，不用每次都去翻数据库。
- **Commonmark**
  Markdown 解析器。写作者用 Markdown 格式写文章，它会负责把这些标记符转成最终的 HTML 页面

#### 前端篇：

- **Query**
  辅助 JavaScript 库，解决了浏览器兼容性，同时让常见操作（比如按钮点击、显示隐藏）写起来简洁。
- **Bootstrap**
  前端样式框架，带来好看的按钮、排版、表格，还能让网页自动适配电脑和手机。
- **editor.md**
  一个强大的 Markdown 富文本编辑器，后台写博客时用它能直接所见即所得预览。
- **dropzone**
  拖拽上传工具。有了它，作者上传图片就不用点“找文件”，直接拖过来就行。
- **sweetalert**
  弹框美化库。后台操作比如“确定删除吗？”有时候自带弹窗很丑，这个库专门让弹框变漂亮、带特效。

#### 第三方服务：

- **七牛云**
  存储和 CDN 服务。用户上传的文件（比如文章插图）可以放到七牛云，访问速度快，还能减轻服务器压力。
- **百度统计**
  网站分析工具，显示访客人数、访问来源、热门页面等信息，帮站长了解运营情况。

### AI建议路线升级

- **后端**：Spring Boot + MyBatis-Plus + Redis + flexmark-java
- **前端**：Vue 3 + Tailwind CSS + Vditor + sweetalert2
- **存储**：先 MinIO 自建，后可迁移 OSS
- **统计**：Matomo 自建 or Google Analytics

#### 思路：

+ 先跟着搭建，熟悉框架模式
+ 完成之后，生成2.0版本，替换新的技术栈

---

## 结构分析

![image-20250921213526632](C:\Users\zewan\AppData\Roaming\Typora\typora-user-images\image-20250921213526632.png)

### 1. 根目录文件

- `pom.xml` - Maven项目配置文件，定义了项目依赖、构建配置和插件
- `README.md` - 项目说明文档
- `.gitignore` - Git版本控制忽略文件配置
- `.gitattributes` - Git属性配置

### 2. 源代码结构 (`src/main/java/com/wip/`)

#### 核心包结构：

- **`api`** - 可能包含API相关的配置或统一响应格式
- **`constant`** - 存放常量定义（如错误码、配置常量等）
- **`controller`** - MVC控制器层，处理HTTP请求和响应
- **`dao`** - 数据访问层（Data Access Object），直接与数据库交互
- **`dto`** - 数据传输对象（Data Transfer Object），用于层间数据传输
- **`exception`** - 自定义异常和全局异常处理
- **`interceptor`** - Spring拦截器，用于权限验证、日志记录等
- **`model`** - 数据模型/实体类（通常对应数据库表）
- **`service`** - 业务逻辑层，包含接口和实现
- **`utils`** - 工具类集合
- **`MyBlogApplication`** - Spring Boot主启动类

### 3. 资源文件 (`src/main/resources/`)

- **`mapper/`** - MyBatis的XML映射文件（如果使用MyBatis）
- **`static/`** - 静态资源（CSS, JS, 图片等）
- **`templates/`** - 模板文件（Thymeleaf, FreeMarker等）
- **`application.yml`** - 主配置文件
- **`application-dev.yml`** - 开发环境配置
- **`application-prod.yml`** - 生产环境配置
- **`lu_tale.sql`** - 数据库初始化脚本

---

## 架构特点：

1. **分层清晰**：严格遵循Controller-Service-Dao分层架构
2. **环境隔离**：通过不同的配置文件支持多环境部署
3. **异常处理**：有专门的异常处理机制
4. **拦截器支持**：提供了统一的请求拦截处理
5. **工具类集中管理**：utils包提供通用功能

---

## Stage1 start



### 第一步：搭建spring boot 框架

#### 使用 [Spring Initializr](https://start.spring.io/)

1. 打开 https://start.spring.io/
2. 选择：
   - Project: Maven
   - Language: Java
   - Spring Boot: 最新稳定版（如 3.2.x）
   - Group: `com.wip`
   - Artifact: `my-blog`
   - Packaging: Jar
   - Java: 11 或 17
3. 添加依赖：
   - Spring Web
   - Thymeleaf （用于模板渲染）
   - MySQL Driver（后面用到数据库）
4. 点击 "Generate" 下载 ZIP 包
5. 解压后导入到 IntelliJ IDEA 中

#### 修改主类 `MyBlogApplication.java`

---

### 第二步：创建控制器 Controller （核心）

+ 关键点：

- `@Controller`：表示这是一个控制器。
- `@RequestMapping("/")`：所有以 `/` 开头的请求都由它处理。
- `@GetMapping`：只处理 GET 请求。
- 返回 `"index"`：Spring 会自动去找 `templates/index.html` 模板。

---

### 第三步：创建前端页面 Thymeleaf模板

简略创建一个前端页面，运行启动类，访问 [http://localhost:8080](http://localhost:8080/)，就能看到欢迎界面！初步成功。

---

#### 接下来：

| 阶段 | 学习内容                 | 实践目标                |
| ---- | ------------------------ | ----------------------- |
| 1️⃣    | MVC 分层 + 控制器 + 模板 | 读取文章列表            |
| 2️⃣    | 数据库连接 + MyBatis     | 把文章存入 MySQL        |
| 3️⃣    | Service 层封装业务逻辑   | 发布文章、查询文章      |
| 4️⃣    | 用户系统（注册/登录）    | 使用 JWT 或 Session     |
| 5️⃣    | 权限拦截器               | 登录后才能发文章        |
| 6️⃣    | Markdown 编辑器          | 写文章更方便            |
| 7️⃣    | 分页功能                 | 文章太多时分页展示      |
| 8️⃣    | 部署上线                 | 打成 jar 包部署到服务器 |

+ 关键点：
+ 先搞懂 **Spring Boot 启动流程**
+ 理解 **MVC 架构**（Controller → Service → DAO）
+ 学会 **用 Thymeleaf 渲染页面**
+ 学会 **配置数据库连接**（MySQL + JDBC）
+ 学会 **用 MyBatis 写 SQL 查询**
+ 最后加功能：登录、权限、分页等

---

## Stage2 MVC

### 一、数据库准备

```sql
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARSET utf8mb4;
USE blog;
CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT COMMENT '内容',
    author VARCHAR(50) DEFAULT 'Zewang' COMMENT '作者',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' # 自动更新
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO article (title, content) VALUES
                                                 ('我的第一篇博客', 'test');

SELECT * FROM article;
```

### 二、配置数据库连接

+ 修改 `src/main/resources/application.yml`

### 三、添加依赖

+ 修改pom.xml，将依赖加入

### 四、创建Model（实体类）

+ 在 `src/main/java/org/Zewang/model/` 下创建：`Article.java`

### 五、创建DAO层 （数据访问）

在 `src/main/java/org/Zewang/dao/` 下创建：`ArticleMapper.java`

### 六、创建Service层（业务逻辑）

在 `src/main/java/org/Zewang/service/` 下创建：`ArticleService.java`（接口）和`ArticleServiceImpl.java`（实现）

### 七、创建Controller（控制层）

在 `src/main/java/org/Zewang/controller/` 下创建： `ArticleController.java`

### 八、创建前端页面（Thymeleaf模板）

在 `src/main/resources/templates/` 下新建文件夹 `article`，然后创建： `list.html`

### 九、测试运行

启动项目，打开浏览器访问

### 遇到问题：

+ 版本兼容  jdk8和mybatis，mysql的兼容性问题

+ 路径问题：controller文件在admin文件夹下，那么list也应该在admin下

+ > Moder的补充：
  >
  > odel是什么？
  > Model 是Spring MVC中的一个接口，用于在控制器(Controller)和视图(View)之间传递数据。它是MVC架构模式中Model部分的具体实现。
  > 为什么要使用Model？
  >
  > 1. 数据传递
  >    控制器到视图：将后端处理的数据传递给前端模板
  >    解耦合：分离业务逻辑和视图展示
  > 2. 工作原理
  > 3. Model的特点
  >
  >   + 键值对存储：使用 addAttribute(key, value) 方法存储数据
  >   + 自动绑定：Spring自动将Model数据绑定到视图中
  >   + 类型安全：可以在模板中直接访问Java对象的属性

  

---

## Stage 3：文章详情页

### 一、修改dao层，加功能

```java
@Select("SELECT id, title, content, author, created_at, updated_at FROM article WHERE id = #{id}")
    Article findById(Long id);
```

### 二、修改Service层

```java
public interface ArticleService {
    List<Article> getAllArticles();
    Article getById(Long id); // ✅ 新增
}
```

### 三、修改Controller （添加详情页接口）

```java
@GetMapping("/{id}")  // ✅ 匹配 /article/1, /article/2 等
public String viewArticle(@PathVariable("id") Long id, Model model) {
    Article article = articleService.getById(id);
    if (article == null) {
        // 文章不存在，跳转到列表页或显示错误页
        return "redirect:/article/list";
    }
    model.addAttribute("article", article);
    return "article/detail"; // 返回详情页模板
}
```

### 四、修改列表页，添加链接

提供文章总览页面

### 五、创建详情页

提供文章详情界面

### 六、创建错误页：文章不存在

- [x] ### 文章详情页 GET！

#### 小结

| 技术                                 | 用途                                     |
| ------------------------------------ | ---------------------------------------- |
| `@PathVariable`                      | 从 URL 中获取动态参数（如 `/article/1`） |
| `th:href="@{/path/{id}(id=${...})}"` | 生成带参数的链接                         |
| `findById(Long id)`                  | 查询单条记录                             |
| 模板 `detail.html`                   | 展示详细内容                             |
| 异常处理                             | 判断 `article == null`，避免 NPE         |

---

## Stage4: 文章发布

### 一、创建DTO层 （数据传输对象）

> DTO：避免直接暴露`Article`实体，控制字段，提高安全性

### 二、修改DAO层 （添加 insert 方法）

+ 在 `ArticleMapper.java` 中添加：

### 三、修改Service层   （接口和impl）

### 四、添加Controller方法 （表单+提交）

> 1.重定向 Redirect
>
> + 重定向是一种 HTTP 响应机制，服务器通过返回一个特殊的响应状态码（通常是 302），告诉浏览器访问另一个 URL。
> + 在 Spring MVC 中，可以通过返回 "redirect:/path" 来实现页面跳转。
> + 为什么要使用重定向？
>   + 防止重复提交：当用户刷新页面时，不会再次提交表单。
>   + 符合 Post/Redirect/Get 模式：这是一种常见的 Web 表单处理模式，用于避免重复提交。
>
> 2.model的作用 
>
> + Model 是什么？
>   + Model 是 Spring MVC 提供的一个接口，用于向视图（HTML 页面）传递数据。
>   + 它本质上是一个 Map 结构，可以将键值对数据添加进去，并在模板引擎（如 Thymeleaf）中读取。
>
> 3.RedirectAttributes
>
> + RedirectAttributes 是什么？
>   + RedirectAttributes 是 Spring MVC 提供的一种特殊对象，用于在重定向时传递数据。
>   + 与普通的 Model 不同，它支持在重定向过程中临时存储数据，这些数据在下一次请求中仍然可用。
>
> 4.为什么使用 addFlashAttribute？
>
> + Flash Attribute 的特点：
>   + 一次性使用：只在下一次请求中有效，之后自动删除。
>   + 适合传递提示信息：例如操作成功或失败的消息。

### 五、创建表单页面、新增按钮（提交文章）

### 小结：

| 技术                           | 用途                           |
| ------------------------------ | ------------------------------ |
| DTO（`CreateArticleDTO`）      | 控制输入字段，解耦前端和数据库 |
| `@GetMapping` + `@PostMapping` | 分离显示表单和提交处理         |
| `@ModelAttribute`              | 自动绑定表单数据               |
| `RedirectAttributes`           | 重定向时传消息（闪现）         |
| `th:object` / `th:field`       | Thymeleaf 表单绑定             |
| `th:action`                    | 设置提交地址                   |
| 错误提示                       | 提升用户体验                   |

### 问题：路径，什么时候加admin，什么时候不加？

---

## Stage5：修改、删除文章

### 一、修改dao层

在ArticleMapper中添加更新与删除方法

### 二、修改Service层，添加方法

### 三、修改Controller层

---

##  完成：version-1

---

## 总结

+ 已实现的功能
  + 文章列表展示
  + 文章详情查看
  + 创建新文章
  + 编辑现有文章
  + 删除文章
  + 表单验证和错误处理
  + 友好的用户界面
+ 技术要点回顾
  + Spring Boot MVC：控制器路由设计和数据传输
  + Thymeleaf 模板引擎：动态页面渲染
  + MyBatis：数据库操作
  + RESTful 设计：合理的URL路径规划
  + 前后端分离：通过模板引擎实现数据交互

## 可能的后续优化方向：



> 很棒！你已经完成了博客的核心功能。在此基础上，可以从 用户体验、技术深度、功能扩展 和 性能/安全 四个维度进行优化和扩展。
>
> 这是一个详细的优化和扩展清单，你可以根据项目时间和兴趣选择性地实现：
>
> ---
>
> 一、 用户体验优化
>
> 这部分能让你的博客用起来更舒服、更现代。
>
> 1. 前端重构与响应式设计
>    · 现状分析： 如果你的前端还是简单的JSP/Thymeleaf + Bootstrap，可以考虑现代化。
>    · 优化方案：
>      · 前后端分离： 使用 Vue.js、React 或 Angular 等前端框架重写前端。后端只提供 RESTful API。这会使项目结构更清晰，前后端开发可以并行。
>      · 响应式设计： 确保网站在手机、平板、电脑上都有良好的显示效果。Bootstrap 5 可以很好地实现这一点。
> 2. 富文本编辑器升级
>    · 现状分析： 你可能使用的是简单的 textarea。
>    · 优化方案： 集成一个功能强大的富文本编辑器，如：
>      · Markdown编辑器： 如 Editor.md、Vditor。非常适合技术博客，代码高亮和排版很优雅。
>      · 所见即所得编辑器： 如 TinyMCE、WangEditor。更适合普通用户。
> 3. 文章目录导航
>    · 功能描述： 在文章详情页，根据标题（h1, h2, h3...）自动生成一个浮动目录，方便用户快速定位和浏览长文章。
> 4. 暗黑模式
>    · 功能描述： 增加一个开关，允许用户在亮色和暗色主题之间切换。可以通过 CSS Variables 和 JavaScript 轻松实现。
> 5. 站内搜索
>    · 功能描述： 在首页或导航栏增加搜索框，可以根据标题、内容、标签搜索文章。
>    · 技术实现：
>      · 简单方案： 使用数据库的 LIKE 语句（性能较差，不推荐用于大量数据）。
>      · 推荐方案： 集成专业的搜索引擎，如 Elasticsearch 或 Solr。它们能提供高性能、高亮、分词等强大功能。
>
> ---
>
> 二、 功能扩展
>
> 这部分为博客增加新的、实用的功能。
>
> 1. 评论系统
>    · 核心功能： 用户可以对文章发表评论。
>    · 扩展功能：
>      · 回复功能： 支持对评论进行回复，形成讨论区。
>      · 审核机制： 评论需要管理员审核后才能显示。
>      · 第三方集成： 集成 Gitalk、Utterances（基于 GitHub Issues）或 Valine（基于 LeanCloud）等第三方无后端评论系统。
> 2. 用户与权限管理
>    · 功能描述：
>      · 用户注册/登录： 支持用户注册账号，并通过邮箱验证。
>      · 角色划分： 例如：管理员（所有权限）、普通用户（只能评论）。
>      · 社交登录： 集成 GitHub、QQ、微博等第三方登录。
> 3. 分类与标签
>    · 功能描述：
>      · 分类： 文章属于一个分类（一对多），如“Java”、“数据库”。
>      · 标签： 文章可以拥有多个标签（多对多），如“Spring Boot”、“Hibernate”、“优化”。在文章详情页显示标签，点击标签可以列出所有相关文章。
> 4. 文章浏览量统计与热门文章
>    · 功能描述： 记录每篇文章的阅读次数，并在侧边栏展示“热门文章排行榜”。
> 5. 文件上传与管理
>    · 功能描述： 在富文本编辑器中支持图片/文件上传。
>    · 技术实现：
>      · 将文件上传到本地服务器指定目录。
>      · 更好方案： 使用对象存储服务，如 阿里云 OSS、腾讯云 COS 或 七牛云，减轻服务器压力，提高访问速度。
> 6. 博客数据统计
>    · 功能描述： 为管理员提供一个后台仪表盘，显示总文章数、总评论数、近期访问量等统计信息。
> 7. RSS 订阅
>    · 功能描述： 提供 RSS 源，方便用户通过 Feedly 等订阅工具跟踪你的博客更新。
>
> ---
>
> 三、 技术深度与架构优化
>
> 这部分能提升代码质量、可维护性和性能。
>
> 1. 缓存集成
>    · 目的： 减少数据库压力，大幅提高响应速度。
>    · 应用场景：
>      · 首页文章列表、热门文章： 使用 Redis 进行缓存，设置合理的过期时间。
>      · 文章详情页： 对于不常变动的文章，也可以进行缓存。
> 2. 异步任务
>    · 目的： 将耗时操作异步化，快速响应用户请求。
>    · 应用场景：
>      · 发送邮件（如注册验证码、评论通知）。
>      · 文章索引更新（如果用了 Elasticsearch）。
>    · 技术实现： 使用 Spring Boot 的 @Async 注解，或更强大的消息队列，如 RabbitMQ、Apache Kafka。
> 3. API 设计与文档
>    · 现状分析： 如果你做了前后端分离，后端API的设计至关重要。
>    · 优化方案：
>      · RESTful 风格： 遵循 RESTful 设计规范。
>      · API 文档： 使用 Swagger/OpenAPI 自动生成 API 文档，方便前端开发者查阅和测试。
> 4. 项目分层与设计模式
>    · 检查点： 你的代码结构是否是标准的 Controller-Service-Dao 分层？是否使用了接口编程？
>    · 优化方案：
>      · 引入 DTO 进行前后端数据传输，与 Entity 实体类分离。
>      · 使用 MapStruct 等工具简化 DTO 和 Entity 之间的转换。
>      · 考虑使用 设计模式 如工厂模式、策略模式来优化复杂的业务逻辑。
>
> ---
>
> 四、 性能、安全与部署
>
> 这是项目的最后一道关卡，决定了项目的稳定性和专业性。
>
> 1. 数据库优化
>    · 为常用查询字段建立索引，如文章标题、创建时间、分类ID等。
>    · 分析慢查询日志，对复杂SQL进行优化。
> 2. 安全加固
>    · SQL 注入： 确保使用 MyBatis 等框架的参数绑定功能，杜绝拼接SQL。
>    · XSS 攻击： 对用户输入（如评论内容）进行转义或过滤。
>    · CSRF 攻击： 如果是前后端不分离，在表单中加入 CSRF Token。
>    · 密码安全： 使用 BCrypt 等强哈希算法加密用户密码，切勿明文存储。
> 3. 日志管理
>    · 使用 SLF4J + Logback 记录详细的日志。
>    · 对关键业务操作（如登录、发布文章）、异常错误进行记录，便于排查问题。
> 4. 容器化与部署
>    · Docker 化： 将你的博客应用、Redis、MySQL 等分别制作成 Docker 镜像，使用 docker-compose.yml 文件统一管理。这极大地简化了部署和环境配置。
>    · CI/CD： 集成 Jenkins 或 GitHub Actions，实现代码推送后自动构建、测试和部署。
>
> 推荐实施路线图
>
> 建议分阶段进行：
>
> 1. 第一阶段（快速提升）： 分类标签、评论系统、富文本编辑器、站内搜索（数据库Like方案）。这些功能能立刻让博客丰满起来。
> 2. 第二阶段（体验升级）： 用户权限、缓存(Redis)、前后端分离。提升性能和架构现代性。
> 3. 第三阶段（高阶扩展）： 集成Elasticsearch、消息队列、文件对象存储、Docker化。向一个成熟、可扩展的生产级项目迈进。
>
> 希望这些建议能帮到你！祝你项目顺利！



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

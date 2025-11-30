# 博客项目超低成本部署方案

本文档提供了针对小流量博客的超低成本部署方案，重点关注预算控制（20元/月以内），移除不必要的Redis缓存，并提供替代MongoDB的经济数据库选项。

## 项目技术栈概览

### 后端
- Spring Boot 3.2.5
- Java 21
- MongoDB (当前主要数据库，但可替换为更经济的替代方案)
- Spring Security (认证和授权)
- JWT (令牌管理)
- SpringDoc OpenAPI (API文档)

> 注意：项目配置中包含Redis依赖，但实际业务代码中并未使用Redis缓存功能，可安全移除以降低部署成本。

### 前端
- Vue 3
- TypeScript
- Vite (构建工具)
- Pinia (状态管理)
- Vue Router (路由)
- Axios (HTTP客户端)

## 部署方案一：超低成本静态托管 + 轻量级云服务（预算20元/月以内）

### 方案概述
针对小流量博客，将前端部署到免费的静态网站托管服务，后端使用按需计费的轻量级函数计算服务，同时将MongoDB替换为更经济的SQLite或H2数据库，完全移除Redis以节省成本。

### 优点
- 总成本控制在20元/月以内
- 按需付费，访问量低时成本更低
- 部署简单，易于维护
- 适合不需要24小时持续访问的场景

### 缺点
- 按需函数计算有冷启动延迟
- 数据库容量和性能有限
- 不适合高并发访问

### 详细实施步骤

#### 1. 移除Redis依赖

1. 修改`pom.xml`文件，移除Redis相关依赖：

```xml
<!-- 删除以下依赖 -->
<!-- <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency> -->
```

2. 修改配置文件，删除Redis相关配置

#### 2. 替换MongoDB为SQLite（最经济选择）

1. 修改`pom.xml`文件，添加SQLite依赖并移除MongoDB依赖：

```xml
<!-- 删除MongoDB依赖 -->
<!-- <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency> -->

<!-- 添加SQLite依赖 -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.44.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

2. 修改数据模型类，将MongoDB注解替换为JPA注解：

```java
// 将@Document(collection = "articles")改为
@Entity
@Table(name = "articles")

// 为每个模型添加@Id注解
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private String id;
```

3. 创建`application-prod.properties`文件：

```properties
# SQLite配置
spring.datasource.url=jdbc:sqlite:myblog.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.datasource.username=
spring.datasource.password=

# JPA配置
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# 服务器配置
server.port=${PORT:8080}

# 文件上传配置
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# JWT密钥（生产环境使用安全的随机值）
app.jwt.secret=your-secure-jwt-secret
app.jwt.expiration=86400000
```

#### 3. 构建后端

```bash
mvn clean package -DskipTests
```

#### 4. 部署后端到云函数（按需付费，成本极低）

**选项A: 阿里云函数计算**
1. 注册[阿里云](https://www.aliyun.com/)账户
2. 进入函数计算服务
3. 创建新函数，选择Java运行环境
4. 配置函数内存为128MB（最低配置以节省成本）
5. 上传打包好的jar文件
6. 配置环境变量，设置数据库路径
7. 配置HTTP触发器
8. 获取访问地址

**选项B: 腾讯云云函数**
1. 注册[腾讯云](https://cloud.tencent.com/)账户
2. 进入云函数服务
3. 创建新函数，选择Java运行环境
4. 配置函数内存为128MB
5. 上传jar文件
6. 配置环境变量和触发方式
7. 获取访问地址

#### 5. 部署前端到免费静态托管服务

**选项A: GitHub Pages**
1. 将前端代码推送到GitHub仓库
2. 进入仓库设置，启用GitHub Pages
3. 选择从`gh-pages`分支或`main`分支的`/docs`目录部署
4. 等待自动部署完成

**选项B: Gitee Pages（国内访问更快）**
1. 将前端代码推送到Gitee仓库
2. 进入仓库服务，选择Gitee Pages
3. 选择分支并部署
4. 注意：Gitee Pages需要实名认证

**选项C: 阿里云OSS静态网站托管**
1. 创建OSS存储桶
2. 开启静态网站托管功能
3. 上传前端构建文件
4. 配置CORS规则
5. 使用免费额度，超出部分按量付费

#### 6. 配置前端API地址

修改前端项目中的API基础URL为函数计算的访问地址：

```typescript
// 在apiClient.ts中
const apiClient = axios.create({
  baseURL: 'https://your-function-compute-url.com',
  timeout: 10000,
});
```

### 成本分析

**阿里云方案**
| 服务 | 价格 |
|------|------|
| 函数计算 | 0元（每月100万次调用内免费） |
| OSS存储 | 0元（5GB内免费） |
| 自定义域名（可选） | 约20元/年 |

**腾讯云方案**
| 服务 | 价格 |
|------|------|
| 云函数 | 0元（每月100万次调用内免费） |
| COS存储 | 0元（50GB内免费） |
| 自定义域名（可选） | 约20元/年 |

**Gitee Pages + 云函数方案**
| 服务 | 价格 |
|------|------|
| Gitee Pages | 免费 |
| 云函数 | 0元（低调用量下免费） |
| 自定义域名（可选） | 约20元/年 |

**估算月度总成本**：约0-5元/月（主要是自定义域名费用，按年分摊）

## 部署方案二：本地数据库 + 轻量级VPS（预算20元/月以内）

### 方案概述
使用极小型VPS（如1核1G内存），将所有服务（后端、SQLite数据库、静态文件）部署在同一台服务器上，使用Nginx作为反向代理，完全移除Redis。

### 优点
- 总成本控制在20元/月以内
- 无需云数据库费用
- 一次性部署，管理简单
- 适合个人小博客长期运行

### 缺点
- 服务器性能有限
- 需要自行维护服务器
- 数据备份需要手动管理

### 详细实施步骤

#### 1. 准备轻量级VPS

**选项A: 国内低价VPS**
- 阿里云突发性能型实例 t6（1核1G）：新用户首年约20元/月
- 腾讯云轻量应用服务器（1核1G）：新用户首年约18元/月
- 华为云耀云服务器（1核1G）：新用户首年约22元/月

**选项B: 海外低价VPS**
- 各种小型VPS提供商，最低约15-20元/月
- 无需备案，但国内访问速度较慢

#### 2. 服务器配置

1. 安装必要软件：

```bash
# 更新系统
apt update && apt upgrade -y

# 安装Java运行环境
apt install openjdk-21-jre-headless -y

# 安装Nginx
apt install nginx -y

# 安装Git（如需从仓库拉取代码）
apt install git -y
```

#### 3. 数据库替换（同方案一）

将MongoDB替换为SQLite，修改依赖和配置文件。

#### 4. 部署后端

1. 上传打包好的jar文件到服务器：

```bash
scp target/myBlog-0.0.1-SNAPSHOT.jar user@your-server-ip:/home/user/
```

2. 创建启动脚本：

```bash
#!/bin/bash
nohup java -jar /home/user/myBlog-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > /home/user/app.log 2>&1 &
```

3. 启动应用：

```bash
chmod +x start.sh
./start.sh
```

#### 5. 部署前端

1. 构建前端：

```bash
cd blog-frontend
npm install
npm run build
```

2. 上传构建文件到服务器：

```bash
scp -r dist/* user@your-server-ip:/var/www/html/
```

#### 6. 配置Nginx

1. 创建或修改Nginx配置文件：

```bash
nano /etc/nginx/sites-available/default
```

2. 添加以下配置：

```nginx
server {
    listen 80;
    server_name yourdomain.com; # 可选，如使用域名

    root /var/www/html;
    index index.html;

    # 静态文件路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API路由
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 文件上传路由
    location /uploads {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }
}
```

3. 重启Nginx：

```bash
systemctl restart nginx
```

### 成本分析

| 服务 | 价格 |
|------|------|
| 轻量级VPS（1核1G） | 约15-20元/月（新用户优惠） |
| 域名（可选） | 约20元/年 |
| 数据存储 | 包含在VPS中（无需额外费用） |

**估算月度总成本**：约15-20元/月（包含所有服务）

## 部署方案三：Docker + 国内云服务器部署（最灵活）

### 方案概述
使用Docker容器化应用，部署在国内云服务器上（如阿里云ECS、腾讯云CVM或华为云ECS）。

### 优点
- 完全控制基础设施
- 无应用休眠限制
- 长期成本更可预测
- 国内服务器访问速度快
- 支持国内支付方式
- 适合有一定技术经验的用户

### 缺点
- 需要自己管理服务器
- 需要设置安全措施
- 部署过程较复杂

### 详细实施步骤

#### 1. 创建Docker配置文件

1. 为后端创建`Dockerfile`：

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/myBlog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

2. 为前端创建`Dockerfile`：

```dockerfile
# 构建阶段
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 生产阶段
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

3. 创建`nginx.conf`文件：

```nginx
server {
    listen 80;
    server_name _;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /uploads {
        proxy_pass http://backend:8080/uploads;
        proxy_set_header Host $host;
    }
}
```

4. 创建`docker-compose.yml`文件：

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:7.0
    volumes:
      - mongo-data:/data/db
    restart: always

  redis:
    image: redis:alpine
    volumes:
      - redis-data:/data
    restart: always

  backend:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    depends_on:
      - mongodb
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/myBlog
      - SPRING_DATA_REDIS_HOST=redis
    restart: always

  frontend:
    build:
      context: ./blog-frontend
      dockerfile: Dockerfile
    ports:
      - "80:80"
    depends_on:
      - backend
    restart: always

volumes:
  mongo-data:
  redis-data:
```

#### 2. 准备国内云服务器

1. 选择并购买云服务器：
   - 阿里云ECS (共享型n4，1核2G)
   - 腾讯云CVM (S4，1核2G)
   - 华为云ECS (通用型S6，1核2G)

2. 配置云服务器：
   - 选择CentOS 7或Ubuntu 20.04镜像
   - 配置安全组，开放80、443和22端口
   - 登录服务器并更新系统
   - 安装Docker和Docker Compose
   - 创建非root用户并设置权限

#### 3. 部署应用

1. 将项目文件上传到云服务器：

```bash
scp -r . username@your-server-ip:/path/to/project
```

2. 连接到服务器：

```bash
ssh username@your-server-ip
```

3. 构建后端：

```bash
cd /path/to/project
mvn clean package -DskipTests
```

4. 使用Docker Compose启动所有服务：

```bash
docker-compose up -d
```

#### 4. 配置域名和SSL（可选）

1. 在国内域名注册商（如阿里云万网、腾讯云域名服务）购买域名
2. 配置DNS指向服务器IP
3. 申请SSL证书：
   - 阿里云SSL证书服务提供免费证书
   - 腾讯云SSL证书服务提供免费证书
   - 华为云SSL证书管理提供免费证书

4. 配置Nginx使用SSL证书（修改nginx.conf）：

```nginx
server {
    listen 443 ssl;
    server_name yourdomain.com;
    
    ssl_certificate /etc/nginx/ssl/yourdomain.com.crt;
    ssl_certificate_key /etc/nginx/ssl/yourdomain.com.key;
    
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

# HTTP重定向到HTTPS
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$host$request_uri;
}
```

### 成本分析

**阿里云方案**
| 服务 | 新用户价格 | 标准价格 |
|------|------------|----------|
| ECS共享型n4 | 约70元/月（1核2G） | 约120元/月（1核2G） |
| 域名 | 约50元/年起 | - |
| SSL证书 | 免费（单域名） | - |

**腾讯云方案**
| 服务 | 新用户价格 | 标准价格 |
|------|------------|----------|
| CVM S4 | 约60元/月（1核2G） | 约110元/月（1核2G） |
| 域名 | 约50元/年起 | - |
| SSL证书 | 免费（单域名） | - |

**华为云方案**
| 服务 | 新用户价格 | 标准价格 |
|------|------------|----------|
| ECS S6 | 约65元/月（1核2G） | 约115元/月（1核2G） |
| 域名 | 约50元/年起 | - |
| SSL证书 | 免费（单域名） | - |

**估算月度总成本（新用户）**：约60-80元/月 + 一次性域名费用
**估算月度总成本（标准用户）**：约110-130元/月 + 域名续费

## 部署方案四：国内混合云方案（最佳性价比）

### 方案概述
结合国内多个云服务，后端使用阿里云函数计算或腾讯云云函数，数据库使用国内云数据库服务，前端使用阿里云OSS或腾讯云COS托管。

### 优点
- 各个组件可以独立扩展
- 减少单点故障风险
- 国内服务访问速度快
- 支持国内支付方式
- 便于管理和监控

### 缺点
- 配置较复杂
- 服务间集成需要额外工作
- 可能产生跨服务数据传输费用

### 详细实施步骤

#### 1. 配置国内MongoDB服务

参考方案一中的步骤1（阿里云MongoDB或腾讯云MongoDB）。

#### 2. 配置国内Redis服务

参考方案一中的步骤2（阿里云Redis或腾讯云Redis）。

#### 3. 部署后端到国内云函数服务

1. 阿里云函数计算或腾讯云云函数
2. 参考方案一中的后端部署步骤
3. 配置环境变量连接MongoDB和Redis服务

#### 4. 部署前端到国内对象存储服务

1. 使用阿里云OSS或腾讯云COS
2. 参考方案一中的前端部署步骤
3. 配置CORS规则，允许跨域访问API
4. 使用CDN加速访问（可选）

#### 5. 配置CORS

确保后端应用配置了正确的CORS设置，允许前端域名访问：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://your-domain.oss-cn-region.aliyuncs.com", "https://your-domain.cos.ap-region.myqcloud.com", "http://localhost:3001")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 成本分析

**阿里云混合方案**
| 服务 | 新用户价格 | 标准价格 |
|------|------------|----------|
| 函数计算 | 40万次调用/月免费 | 0.4元/万次调用起 |
| MongoDB云数据库 | 新用户套餐约40元/月起 | 按量付费约0.36元/小时起 |
| Redis缓存 | 新用户套餐约30元/月起 | 按量付费约0.27元/小时起 |
| OSS存储 | 5GB存储免费 | 0.12元/GB/月起 |

**腾讯云混合方案**
| 服务 | 新用户价格 | 标准价格 |
|------|------------|----------|
| 云函数 | 100万次调用/月免费 | 0.2元/万次调用起 |
| MongoDB数据库 | 新用户套餐约35元/月起 | 按量付费约0.32元/小时起 |
| Redis | 新用户套餐约28元/月起 | 按量付费约0.25元/小时起 |
| COS存储 | 50GB存储免费 | 0.11元/GB/月起 |

**估算月度总成本（新用户）**：约70-100元/月
**估算月度总成本（标准用户）**：约150-200元/月（根据实际使用量）

## 部署方案比较

| 特性 | 国内PaaS方案 | Docker + 国内云服务器方案 | 国内混合云方案 |
|------|------------|------------------------|--------------|
| 初始设置复杂度 | 低 | 中高 | 中 |
| 维护难度 | 低 | 高 | 中 |
| 成本控制 | 中等 | 高 | 高 |
| 可扩展性 | 中等 | 高 | 高 |
| 性能控制 | 低 | 高 | 中高 |
| 国内访问速度 | 高 | 高 | 高 |
| 支付方式便利性 | 高（支持国内支付） | 高（支持国内支付） | 高（支持国内支付） |
| 适合人群 | 初学者，快速部署 | 有经验的开发者，需要完全控制 | 寻求最佳性价比的用户 |

## 国际与国内部署方案对比

| 因素 | 国际平台方案 | 国内平台方案 |
|------|------------|------------|
| 访问速度（国内用户） | 较慢 | 快速 |
| 支付便利性 | 需国际信用卡 | 支持支付宝、微信支付等 |
| 备案要求 | 无需备案（海外服务器） | 需ICP备案（使用国内服务器时） |
| 服务稳定性 | 全球服务稳定 | 国内访问稳定 |
| 价格水平 | 美元计价，可能有汇率波动 | 人民币计价，价格相对稳定 |
| 技术支持 | 英文为主，响应时间可能较长 | 中文支持，响应时间较快 |

## ICP备案说明

如果选择国内服务器部署方案，请注意：

1. 使用中国大陆服务器且绑定域名时，需要进行ICP备案
2. 备案流程通常需要7-20个工作日
3. 需要准备以下材料：
   - 企业营业执照（企业网站）或个人身份证（个人网站）
   - 网站负责人身份证明
   - 网站备案信息真实性核验单
4. 各云服务商提供备案服务和指导
5. 备案前建议先使用IP地址进行测试，确认网站可正常运行

## 迁移策略

如果需要从国际平台迁移到国内平台，或在国内平台间迁移，遵循以下步骤：

1. **数据备份**：导出MongoDB数据
2. **ICP备案**：如使用国内服务器绑定域名，提前进行ICP备案
3. **代码准备**：确保应用支持环境变量配置，调整CORS设置
4. **数据库迁移**：将数据导入到新的MongoDB实例
5. **部署新环境**：按照目标方案的步骤部署应用
6. **验证功能**：确保所有功能正常工作
7. **DNS切换**：更新DNS指向新的部署

## 安全最佳实践

1. 使用HTTPS加密传输
2. 定期更新依赖包
3. 配置强密码策略
4. 使用环境变量存储敏感信息
5. 实施API速率限制
6. 配置防火墙规则
7. 定期备份数据
8. 遵循国内网络安全相关法规要求

## 性能优化建议

1. 使用国内CDN分发静态资源
2. 实施Redis缓存策略
3. 优化MongoDB索引
4. 前端资源压缩和代码分割
5. 配置适当的HTTP缓存头
6. 选择靠近目标用户的服务器地域

## 监控和日志

1. 使用应用程序内置的日志记录系统
2. 对于云服务器部署，使用云服务商提供的监控服务：
   - 阿里云云监控
   - 腾讯云监控
   - 华为云云监控服务
3. 对于PaaS部署，使用平台提供的监控工具
4. 设置关键指标的告警
5. 配置日志收集和分析

## 国内部署注意事项

1. **ICP备案**：使用国内服务器绑定域名必须进行ICP备案
2. **网络安全**：遵循《网络安全法》等相关法规要求
3. **数据存储**：敏感数据考虑存储在国内服务器上
4. **带宽选择**：根据访问量选择合适的带宽配置
5. **服务商选择**：可根据价格、服务质量和技术支持选择合适的云服务商
6. **优惠政策**：关注各云服务商的新用户优惠和促销活动

---

根据您的具体需求、技术经验和预算，选择最适合您的部署方案。由于支付方式限制，推荐优先考虑国内平台的部署方案，它们都支持国内主流支付方式，同时提供良好的性能和稳定性。

---

根据您的具体需求、技术经验和预算，选择最适合您的部署方案。如果刚开始，建议从PaaS方案开始；如果需要更多控制和长期成本效益，考虑Docker + VPS或混合云方案。
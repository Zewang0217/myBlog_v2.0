# 博客项目轻量级VPS部署方案

本文档提供了博客项目的轻量级VPS部署方案，专注于预算控制（20元/月以内），包含从数据库替换到最终部署的完整步骤。

## 项目技术栈概览

### 后端
- Spring Boot 3.2.5
- Java 21
- MongoDB (将被替换为SQLite以降低成本)
- Spring Security (认证和授权)
- JWT (令牌管理)
- SpringDoc OpenAPI (API文档)

> 注意：项目配置中包含Redis依赖，但实际业务代码中并未使用Redis缓存功能，将被移除以降低部署成本。

### 前端
- Vue 3
- TypeScript
- Vite (构建工具)
- Pinia (状态管理)
- Vue Router (路由)
- Axios (HTTP客户端)

## 部署方案：本地数据库 + 轻量级VPS（预算20元/月以内）

### 方案概述
使用极小型VPS（1核1G内存），将所有服务（后端、SQLite数据库、静态文件）部署在同一台服务器上，使用Nginx作为反向代理，完全移除Redis。

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

#### 2. 代码准备：移除Redis依赖

1. 修改`pom.xml`文件，移除Redis相关依赖：

```xml
<!-- 删除以下依赖 -->
<!-- <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency> -->
```

2. 修改配置文件，删除Redis相关配置

#### 3. 代码准备：替换MongoDB为SQLite

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
server.port=8080

# 文件上传配置
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# JWT密钥（生产环境使用安全的随机值）
app.jwt.secret=your-secure-jwt-secret
app.jwt.expiration=86400000
```

#### 4. 服务器配置

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

#### 5. 构建和部署后端

1. 在本地构建后端应用：

```bash
mvn clean package -DskipTests
```

2. 上传打包好的jar文件到服务器：

```bash
scp target/myBlog-0.0.1-SNAPSHOT.jar user@your-server-ip:/home/user/
```

3. 上传配置文件到服务器：

```bash
scp src/main/resources/application-prod.properties user@your-server-ip:/home/user/
```

4. 在服务器上创建启动脚本：

```bash
nano /home/user/start.sh
```

5. 添加启动脚本内容：

```bash
#!/bin/bash
nohup java -jar /home/user/myBlog-0.0.1-SNAPSHOT.jar --spring.config.location=/home/user/application-prod.properties > /home/user/app.log 2>&1 &
echo "应用已启动，进程ID: $!"
```

6. 设置脚本执行权限并启动应用：

```bash
chmod +x /home/user/start.sh
cd /home/user
./start.sh
```

#### 6. 构建和部署前端

1. 在本地构建前端：

```bash
cd blog-frontend
npm install
npm run build
```

2. 上传构建文件到服务器：

```bash
scp -r dist/* user@your-server-ip:/var/www/html/
```

3. 设置正确的文件权限：

```bash
ssh user@your-server-ip
chown -R www-data:www-data /var/www/html/
```

#### 7. 配置Nginx

1. 创建或修改Nginx配置文件：

```bash
ssh user@your-server-ip
sudo nano /etc/nginx/sites-available/default
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

3. 测试Nginx配置并重启：

```bash
sudo nginx -t
sudo systemctl restart nginx
```

#### 8. 设置防火墙（可选）

```bash
# 允许HTTP流量
sudo ufw allow 80/tcp
# 允许SSH连接
sudo ufw allow 22/tcp
# 启用防火墙
sudo ufw enable
```

### 部署后管理

#### 应用日志查看
```bash
# 实时查看日志
tail -f /home/user/app.log

# 查看完整日志
cat /home/user/app.log
```

#### 重启后端应用
```bash
# 查找应用进程ID
ps aux | grep java

# 终止进程
kill [进程ID]

# 重启应用
cd /home/user
./start.sh
```

#### 数据备份
```bash
# 创建备份目录
mkdir -p /home/user/backup

# 备份SQLite数据库
cp /home/user/myblog.db /home/user/backup/myblog_$(date +%Y%m%d).db

# 备份上传文件
tar -czf /home/user/backup/uploads_backup_$(date +%Y%m%d).tar.gz /home/user/uploads
```

#### 定期备份脚本
创建一个定时备份脚本，可通过crontab设置定期执行：

```bash
nano /home/user/backup.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/home/user/backup"
mkdir -p $BACKUP_DIR

# 备份数据库
cp /home/user/myblog.db $BACKUP_DIR/myblog_$(date +%Y%m%d).db

# 备份上传文件
if [ -d "/home/user/uploads" ]; then
    tar -czf $BACKUP_DIR/uploads_backup_$(date +%Y%m%d).tar.gz /home/user/uploads
fi

# 清理7天前的备份
find $BACKUP_DIR -name "*.db" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "备份完成：$(date)"
```

设置执行权限并添加到crontab：
```bash
chmod +x /home/user/backup.sh
echo "0 3 * * * /home/user/backup.sh >> /home/user/backup.log 2>&1" | crontab -
```

### 成本分析

| 服务 | 价格 |
|------|------|
| 轻量级VPS（1核1G） | 约15-20元/月（新用户优惠） |
| 域名（可选） | 约20元/年 |
| 数据存储 | 包含在VPS中（无需额外费用） |

**估算月度总成本**：约15-20元/月（包含所有服务）

### 注意事项

1. **国内VPS备案**：使用国内VPS时，需要进行ICP备案才能绑定域名访问
2. **服务器安全**：建议修改SSH默认端口，禁用密码登录，使用密钥认证
3. **定期更新**：定期更新系统和应用以获取安全补丁
4. **资源监控**：密切关注服务器资源使用情况，避免内存不足导致服务崩溃
5. **数据安全**：养成定期备份的习惯，可以将备份文件下载到本地保存
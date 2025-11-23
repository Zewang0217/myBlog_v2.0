# 博客项目部署指南

## 一、项目概述
该项目是一个基于 **Spring Boot + Vue 3** 的前后端分离博客系统，后端使用 MongoDB 作为数据库，无需 Redis 即可运行。

## 二、技术栈
- 后端：Spring Boot 3.x
- 前端：Vue 3 + TypeScript
- 数据库：MongoDB 5.x+
- 构建工具：Maven + npm

## 三、部署架构
```
用户 → 域名/CDN → Nginx反向代理 → 前端静态资源 + 后端API服务
                                        ↓
                                    MongoDB (云数据库)
```

## 四、部署准备
### 1. 服务器配置
- 推荐配置：2核2G 内存 / 40G SSD 硬盘 / 3M 带宽
- 操作系统：Ubuntu 22.04 LTS
- 端口开放：80 (HTTP)、443 (HTTPS)、22 (SSH)

### 2. 购买服务
- 云服务器：阿里云/腾讯云/火山引擎 轻量应用服务器
- 数据库：MongoDB Atlas (海外) 或 阿里云MongoDB 云数据库 (国内)
- 域名：.com 或 .cn 域名 (需备案，国内)

## 五、部署步骤

### 1. 清理Redis配置
由于无需Redis，需先移除相关配置：
```bash
# 后端移除Redis依赖
sed -i '/redis/d' pom.xml
# 前端无需修改
```

### 2. 后端部署
```bash
# 1. 安装Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# 2. 构建后端项目
./mvnw clean package -DskipTests

# 3. 上传JAR包到服务器
scp target/myBlog-0.0.1-SNAPSHOT.jar root@your-server-ip:/opt

# 4. 创建系统服务
cat > /etc/systemd/system/myblog.service <<EOF
[Unit]
Description=MyBlog Backend Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt
ExecStart=/usr/bin/java -jar myBlog-0.0.1-SNAPSHOT.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target

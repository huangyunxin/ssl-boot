# HTTPS证书管理、证书部署

## 使用场景

* 需要管理很多域名证书，并且证书部署在不同位置，比如远程主机的Nginx、阿里云CDN、阿里云OSS、七牛云CDN、阿里云k8s的保密字典等
* 现在免费域名有效期缩短至3个月，手动更新非常繁琐
* 所以需要一个自动化工具来管理证书，到期自动申请新证书，自动部署到各个服务中

## 项目说明

* 项目基于Java SpringBoot开发，页面使用Thymeleaf模板引擎、LayUI前端框架
* 证书服务通过acme4j客户端，申请Let's Encrypt免费证书，并通过各家云服务的API或SDK添加DNS解析和部署证书
* <font color='red'>本项目接口未设置鉴权机制，不要部署在公网上，以免敏感信息泄露</font>

## 主要功能

* 证书管理：证书到期自动申请新证书（当前支持在阿里云DNS、腾讯云DNS解析的域名）
![img.png](doc/image/certList1.png)
![img.png](doc/image/certList2.png)
![img.png](doc/image/certList3.png)

* 证书部署：将证书部署到服务器或云服务中
![img.png](doc/image/certDeploy1.jpg)
![img.png](doc/image/certDeploy2.jpg)
![img.png](doc/image/certDeploy3.jpg)

* 证书监控：自动获取域名到期时间，即将到期发送提醒消息（当前支持飞书机器人消息）
![img.png](doc/image/certCheck1.jpg)

## 开发环境

* 开发工具：IntelliJ IDEA
* JDK:OpenJDK 17.0.7
* Maven:3.8.6
* MySQL:5.7

## 快速开始

### 启动项目

* 创建MySQL数据库，名称`ssl`，并执行`doc/mysql/mysql.sql`初始化数据库表
* 修改配置文件`src/main/resources/application.yml`中的`spring.datasource`，配置数据库连接信息
* 运行`Application.java`，浏览器访问`http://localhost:8080`

### 使用Docker部署

* [Docker部署](doc%2FdockerDeploy)

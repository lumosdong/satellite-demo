Satellite Demo
=

一个简易的卫星图像管理系统，支持图片上传、存储、缩略图生成与下载。

下载与运行
1. 克隆项目
```
git clone https://github.com/lumosdong/satellite-demo.git
cd satellite-demo
```
2. 数据库准备

确保你已经安装并运行了 MySQL（版本 ≥ 8.0）。

创建数据库：
```
mysql -u root -p -e "CREATE DATABASE satellite DEFAULT CHARACTER SET utf8mb4;"
```

导入初始化数据：
```
mysql -u root -p satellite < db/satellite.sql
```
3. 配置

在 application.properties 或 application.yml 中修改数据库连接信息，例如：
```
spring.datasource.url=jdbc:mysql://localhost:3306/satellite-demo?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=你的密码
```
4. 运行应用

你可以直接运行 jar 包（推荐在 Releases 页面下载）：
```
java -jar satellite-demo-0.0.1-SNAPSHOT.jar
```

默认端口是 http://localhost:8090。

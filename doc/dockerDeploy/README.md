# 使用Docker部署

* 服务器先安装`Docker`和`MySQL`
* 将当前目录的文件`deploy.sh`和`application.yml`上传到服务器目录
* 将Maven打包后的`jar`包`ssl-1.0.0.jar`拷贝到服务器目录
* 修改`application.yml`文件中的数据库连接信息
* 执行命令`sh deploy.sh`

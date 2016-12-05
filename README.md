# aliyun 
本项目的功能
1.将阿里云ECS服务器上的所有的tomcat服务器，tomcat项目，tomcat日志，nginx项目，nginx配置，nginx项目，mysql数据库备份到阿里云oss上。可以手动点击页面上的“开始备份按钮”，或者每天00:10进行自动备份。
2.每天当自动备份完成后，自动重启所有的已经开启的tomcat，未开启的tomcat将不会启动。
3.获取所有oss的信息和根节点（功能未完善）。

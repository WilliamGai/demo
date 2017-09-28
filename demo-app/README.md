# 提供RestFul风格的后台服务
- (embed tomcat without jsp)

## 1 启动命令示例，注意参数位置
### 1.1
java -jar(推荐)
``` xml
java -jar target/XXX.jar --spring.profiles.active=dev --server.tomcat.basedir=./tmp
```
java -classpath(不建议)
``` xml
java -classpath XXX.jar org.springframework.boot.loader.JarLauncher  --spring.profiles.active=dev
```
如果要调整日志级别
``` xml
java -jar target/XXX.jar --spring.profiles.active=dev --logging.level.org.springframework.web=debug --logging.level.com.sincetimes=debug
```
如果调整jvm参数和启动jconsole
``` xml
java -jar  -Xms8000m -Xmx8000m -Xmn6000m -Xss256k -XX:PermSize=64m -Dcom.sun.management.jmxremote.port=8999 -Djava.rmi.server.hostname=123.56.78.9 -Dcom.sun.managent.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false   XXX.jar --spring.profiles.active=dev
```
在某些服务器会清除/tmp目录的情况下添加系统参数`java.io.tmpdir`或者application参数`server.tomcat.basedir`(推荐g)
``` xml
java -jar -Djava.io.tmpdir=/data/temp app-52.jar --spring.profiles.active=demo  
java -jar  app-55.jar  --spring.profiles.active=demo --server.tomcat.basedir=./tmp2
```
### 1.2
mvn 启动命令：
``` xml
mvn spring-boot:run -D spring.profiles.active=guanwang
```
或
``` xml
mvn spring-boot:run -D spring.profiles.active=jztpy -D ip=119.29.52.126
``` 
或者
``` xml
mvn spring-boot:run -D spring.profiles.active=jztpy -D ip=119.29.52.126 -D server.port=8060
```
或
```
mvn spring-boot:run -Drun.jvmArguments="-XX:+PerfBypassFileSystemCheck" -D spring.profiles.active=demo
```
## 2 服务器部署
### 2.1 使用Apache:
``` xml
<VirtualHost *:80>
        ServerName app.sincetimes.com
        DocumentRoot /data/web/guanwang_app/static_src
        ErrorLog         /data/web/guanwang_app/apache_error.log
        Header set Access-Control-Allow-Origin *
        ProxyRequests Off
        ProxyPreserveHost On
        ProxyPassMatch  /mg/  http://127.0.0.1:8123  
        # 线上多个项目验证写为  ProxyPassMatch  /mg/  http://127.0.0.1:8123/ 的时候 mg代理后的URI为//mg/article/articles 多了个反斜杠
        ProxyPassMatch /*.jpg !
        ProxyPassMatch /*.mp4 !
        ProxyPassMatch /*.css !
        ProxyPassMatch /*.gif !
        ProxyPassMatch /*.png !
        ProxyPassMatch /*.js !
        ProxyPassMatch /*.html$ !
        ProxyPass /test.html !
        ProxyPass /index.html !
        ProxyPassMatch  ^/$ !
        ProxyPass       /   http://127.0.0.1:8123/
        ProxyPassReverse /  http://127.0.0.1:8123/
        <Directory "/">
                Options -Indexes
                AllowOverride None
                Order allow,deny
                Allow from all
                Header set Access-Control-Allow-Origin *
                DirectoryIndex index.html
        </Directory>
</VirtualHost>
``` 
### 2.2 使用Nginx:
``` xml
upstream demo.williamy.xin{
        server 127.0.0.1:8060 weight=1;
    }
    server {
        listen       8090;
        server_name  upstream demo.williamy.xin;
        location / {
            proxy_http_version 1.1;
            proxy_pass http://demo.williamy.xin;
            proxy_redirect default;
            proxy_connect_timeout 1s;
            proxy_read_timeout 5s;
            proxy_send_timeout 2s;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            add_header 'Access-Control-Allow-Headers' 'Content-Type';
            add_header 'Access-Control-Allow-Origin' '*';
            add_header 'Access-Control-Allow-Methods' 'GET';
        }
        location ~ /mg/+ {
            proxy_pass http://demo.williamy.xin;
        }
		location ~ ^/$
        {
             root /data/web/demo/static_src;
        }
        location ~ .*\.(html|htm|gif|jpg|jpeg|bmp|png|ico|txt|js|css|mp3|mp4)$
        {    #index index2.html;
             root /data/web/demo/static_src;
             expires      14d;
        }
        error_page  404   /404.html;
         location = /404.html{
            root   html;
        }
        error_page   500 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
         error_page   502  /502.html;
        location = /502.html{
            root  html;
        }
}
```
### 3 客户端访问API目录
- 地址(https://github.com/WilliamGai/demo/blob/master/demo-app/doc/README.md)

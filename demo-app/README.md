#app
#embed tomcat without jsp

启动命令示例
java -jar target/XXX.jar --spring.profiles.active=dev --logging.level.org.springframework.web=debug --logging.level.com.sincetimes=debug
或
java -classpath XXX.jar org.springframework.boot.loader.JarLauncher  --spring.profiles.active=dev
或
java -jar target/XXX.jar --spring.profiles.active=dev
或
java -jar  -Xms8000m -Xmx8000m -Xmn6000m -Xss256k -XX:PermSize=64m -Dcom.sun.management.jmxremote.port=8999 -Djava.rmi.server.hostname=123.56.78.9 -Dcom.sun.managent.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false   XXX.jar --spring.profiles.active=dev

mvn 启动命令：
mvn spring-boot:run -D spring.profiles.active=guanwang
或
mvn spring-boot:run -D spring.profiles.active=jztpy -D ip=119.29.52.126
或者
mvn spring-boot:run -D spring.profiles.active=jztpy -D ip=119.29.52.126 -D server.port=8060

服务器部署
使用Apache:
<VirtualHost *:80>
        ServerName app.sincetimes.com
        DocumentRoot /data/web/guanwang_app/static_src
        ErrorLog         /data/web/guanwang_app/apache_error.log
        Header set Access-Control-Allow-Origin *
        ProxyRequests Off
        ProxyPreserveHost On
        ProxyPassMatch  /mg/  http://127.0.0.1:8123/
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

使用Nginx:
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


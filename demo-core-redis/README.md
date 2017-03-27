# 提供redis的使用
## 项目的依赖关系
``` xml
demo-redis-->demo-parent
    |
    |--demo-core-common
    |
    |--spring-boot-starter-redis

```
## 写法

原则上只有com.sincetimes.website.redis.jedis.spring的类才可以使用spring相关的依赖
``` java
public class JedisServiceDemo implements JedisWrapper{

	@Autowired
	public JedisPoolTemplate jedisTemplate;

	@Override
	public JedisPoolTemplate template() {
		
		return jedisTemplate;
	}
}

```



### 备注
``` xml
 com.sincetimes.website:demo-redis:jar:0.0.1
 +- com.sincetimes.website:demo-core-common:jar:0.0.1:compile
 |  +- org.apache.commons:commons-lang3:jar:3.4:compile
 |  +- com.google.guava:guava:jar:19.0:compile
 |  +- org.quartz-scheduler:quartz:jar:2.2.3:compile
 |  |  +- c3p0:c3p0:jar:0.9.1.1:compile
 |  |  \- org.slf4j:slf4j-api:jar:1.7.21:compile
 |  \- ch.qos.logback:logback-classic:jar:1.1.7:compile
 |     \- ch.qos.logback:logback-core:jar:1.1.7:compile
 +- org.springframework.boot:spring-boot-starter-redis:jar:1.3.5.RELEASE:compile
 |  +- org.springframework.boot:spring-boot-starter:jar:1.3.5.RELEASE:compile
 |  |  +- org.springframework.boot:spring-boot:jar:1.3.5.RELEASE:compile
 |  |  |  \- org.springframework:spring-context:jar:4.2.6.RELEASE:compile
 |  |  |     \- org.springframework:spring-expression:jar:4.2.6.RELEASE:compile
 |  |  +- org.springframework.boot:spring-boot-autoconfigure:jar:1.3.5.RELEASE:compile
 |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:1.3.5.RELEASE:compile
 |  |  |  +- org.slf4j:jul-to-slf4j:jar:1.7.21:compile
 |  |  |  \- org.slf4j:log4j-over-slf4j:jar:1.7.21:compile
 |  |  +- org.springframework:spring-core:jar:4.2.6.RELEASE:compile
 |  |  \- org.yaml:snakeyaml:jar:1.16:runtime
 |  +- org.springframework.data:spring-data-redis:jar:1.6.4.RELEASE:compile
 |  |  +- org.springframework:spring-tx:jar:4.2.6.RELEASE:compile
 |  |  |  \- org.springframework:spring-beans:jar:4.2.6.RELEASE:compile
 |  |  +- org.springframework:spring-oxm:jar:4.2.6.RELEASE:compile
 |  |  +- org.springframework:spring-aop:jar:4.2.6.RELEASE:compile
 |  |  |  \- aopalliance:aopalliance:jar:1.0:compile
 |  |  +- org.springframework:spring-context-support:jar:4.2.6.RELEASE:compile
 |  |  \- org.slf4j:jcl-over-slf4j:jar:1.7.21:compile
 |  \- redis.clients:jedis:jar:2.7.3:compile
 |     \- org.apache.commons:commons-pool2:jar:2.4.2:compile
 +- com.alibaba:fastjson:jar:1.2.8:compile
 \- junit:junit:jar:4.12:compile
    \- org.hamcrest:hamcrest-core:jar:1.3:compile
```

# 注意事项
## 1 本模块原则上只依赖jedis.client包，与spring解耦。
下面是本模块所依赖的配置,一定要配置。

##jedis
``` xml
jedis.database=2
jedis.host=${ip}
jedis.auth=foobared
jedis.port=6379
jedis.timeout=0
``` 
## 2 因为模块使用了spring-boot-starter-redis还间接提供了注解驱动的redis缓存机制！
实际上不仅仅依赖了jedis
而是
``` xml
 spring-boot-starter-redis
      |
      |----spring-boot-starter
      |----spring-data-redis
      |----jedis
```
spring会试图扫描配置文件中的redis配置
#reids
``` xml
spring.redis.database=1
spring.redis.host=123.56.13.70
spring.redis.password=foobared
spring.redis.pool.max-active=8
spring.redis.pool.max-idle=8
spring.redis.pool.max-wait=-1
spring.redis.pool.min-idle=0
spring.redis.port=6379
``` 
如果不配置或者连接不可用,对程序运行没影响。
不过如果作为spring boot admin客户端连接admin server的时候,health检测是redis状态会以这个配置来显示DOWN状态。

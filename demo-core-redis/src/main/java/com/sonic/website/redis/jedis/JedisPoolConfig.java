package com.sonic.website.redis.jedis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sonic.website.redis.jedis.proxy.JedisHandle;
import com.sonic.website.redis.jedis.proxy.JedisProxy;

@Configuration
//@PropertySource("classpath:jedis.properties")
public class JedisPoolConfig {
    @Bean
    JedisPoolTemplate getJedisPoolTemplate(
            @Value("${jedis.host}") final String host,
            @Value("${jedis.port}") final int port,
            @Value("${jedis.auth}") final String auth,
            @Value("${jedis.timeout}") final int timeout,
            @Value("${jedis.database}") final int database
            ) {
        JedisPoolTemplate template = new JedisPoolTemplate(host, port, timeout, auth, database);
        return template;
    }
    @Bean
    JedisHandle getJedisJedisProxy(
            @Value("${jedis.host}") final String host,
            @Value("${jedis.port}") final int port,
            @Value("${jedis.auth}") final String auth,
            @Value("${jedis.timeout}") final int timeout,
            @Value("${jedis.database}") final int database
            ) {
        JedisHandle template = JedisProxy.createProxy(host, port, timeout, auth, database);
        return template;
    }
}
package com.sincetimes.website.redis.jedis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@PropertySource("classpath:jedis.properties")
public class JedisPoolConfig {
	@Bean
	JedisPoolTemplate getJedisPoolTemplate(
			@Value("${jedis.host}") String host,
			@Value("${jedis.port}") int port,
			@Value("${jedis.auth}") String auth,
			@Value("${jedis.timeout}") int timeout,
			@Value("${jedis.database}") int	database
			) {
		JedisPoolTemplate template = new JedisPoolTemplate(host, port, timeout, auth, database);
		return template;
	}
}
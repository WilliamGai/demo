package com.sonic.website.app.redis;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.website.core.common.support.LogCore;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
		RedisCacheManager manager = new RedisCacheManager(redisTemplate);
		// manager.setDefaultExpiration(180);//设置默认过期时间，过这个时间redis会删除掉
		return manager;
	}
	/**
	 * redisTemplate()方法名不可以修改，修改了后，springcache会找到不到template,自己引用的RedisTemplate会是默认的序列化都是jdk序列化的RedisTemplate
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connectionFactory);
		setMySerializer(template);
		template.afterPropertiesSet();
		LogCore.BASE.info("template{}" ,ReflectionToStringBuilder.toString(template, ToStringStyle.SHORT_PREFIX_STYLE));
		return template;
	}

	/**
	 * 设置序列化方法
	 */
	private void setMySerializer(RedisTemplate<?,?> template) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setKeySerializer(template.getStringSerializer());
		template.setValueSerializer(jackson2JsonRedisSerializer);
	}

	@Bean
	public KeyGenerator smpkeyGenerator() {
		return (target, method, params) -> {
			StringBuilder sb = new StringBuilder();
			sb.append(target.getClass().getSimpleName()).append(":");//执行方法所在的类
			sb.append(Stream.of(params).map(String::valueOf).collect(Collectors.joining("_")));
//			sb.append(String.join("_", Arrays.asList(params));

			return sb.toString();
		};
	}
}

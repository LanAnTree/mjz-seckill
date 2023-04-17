package com.lanan.mjzseckill.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.lanan.mjzseckill.utils.BloomFilterHelper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/9-20:32
 */
@Configuration
@EnableCaching
public class RedisConfig {

	@Value("${spring.redis.database}")
	private int database;

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.timeout}")
	private long timeout;

	@Value("${spring.redis.lettuce.shutdown-timeout}")
	private long shutDownTimeout;

	@Value("${spring.redis.lettuce.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.lettuce.pool.min-idle}")
	private int minIdle;

	@Value("${spring.redis.lettuce.pool.max-active}")
	private int maxActive;

	@Value("${spring.redis.lettuce.pool.max-wait}")
	private long maxWait;

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		GenericObjectPoolConfig<Object> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
		genericObjectPoolConfig.setMaxIdle(maxIdle);
		genericObjectPoolConfig.setMinIdle(minIdle);
		genericObjectPoolConfig.setMaxTotal(maxActive);
		genericObjectPoolConfig.setMaxWait(Duration.ofSeconds(maxWait));
		genericObjectPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(100));
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setDatabase(database);
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
				.commandTimeout(Duration.ofSeconds(timeout))
				.shutdownTimeout(Duration.ofSeconds(shutDownTimeout))
				.poolConfig(genericObjectPoolConfig)
				.build();

		return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
	}

	@Bean("redisCacheManager")
	@Primary
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		RedisCacheConfiguration config = RedisCacheConfiguration
				.defaultCacheConfig()
				// 默认缓存时间
				.entryTtl(Duration.ofSeconds(600))
				// 设置key的序列化方式
				.serializeKeysWith(RedisSerializationContext.SerializationPair
						.fromSerializer(stringRedisSerializer))
				// 设置value的序列化方式
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(fastJsonRedisSerializer))
				//不允许缓存null值
				.disableCachingNullValues();
		return RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(config)
				.transactionAware()
				.build();
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		// 使用fastJson序列化
		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
		// value值的序列化采用fastJsonRedisSerializer
		redisTemplate.setValueSerializer(fastJsonRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
		// 建议使用这种方式，小范围指定白名单，需要序列化的类
		ParserConfig.getGlobalInstance().addAccept("com.lanan.mjzseckill");
		// key的序列化采用StringRedisSerializer
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

	@Bean
	public BloomFilterHelper<String> bloomFilterHelper() {
		return new BloomFilterHelper<>((Funnel<String>) (from, into) ->
				into
						.putString(from, Charsets.UTF_8)
						.putString(from, Charsets.UTF_8), 1000000, 0.01);
	}

	@Bean("seckillStockScript")
	public DefaultRedisScript<Long> seckillStockScript() {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		//放在和application.yml 同层目录下
		redisScript.setLocation(new ClassPathResource("lua/seckillStock.lua"));
		redisScript.setResultType(Long.class);
		return redisScript;
	}
}

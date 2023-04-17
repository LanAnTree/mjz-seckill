package com.lanan.mjzseckill.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Lucky LanAn 设置redis使用fastJson序列化
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-14:22
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	private final Class<T> clazz;

	public FastJsonRedisSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return new byte[0];
		}
		return JSON.toJSONString(t, SerializerFeature.WriteClassName,
				// 是否输出值为null的字段,默认为false
				SerializerFeature.WriteMapNullValue,
				// List字段如果为null,输出为[],而非null
				SerializerFeature.WriteNullListAsEmpty).getBytes(DEFAULT_CHARSET);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		String str = new String(bytes, DEFAULT_CHARSET);
		return JSON.parseObject(str, clazz);
	}
}

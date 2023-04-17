package com.lanan.mjzseckill.service.impl;

import com.google.common.base.Preconditions;
import com.lanan.mjzseckill.service.RedisService;
import com.lanan.mjzseckill.utils.BloomFilterHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/17-14:49
 */
@Service
public class RedisServiceImpl implements RedisService {
	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	/**
	 * @Description {根据给定的布隆过滤器添加值}
	 * @Date 2023/4/17 14:53
	 * @param bloomFilterHelper
	 * @param key
	 * @param value
	 */
	@Override
	public <T> void addByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
		Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			redisTemplate.opsForValue().setBit(key, i, true);
		}
	}

	/**
	 * @Description {根据给定的布隆过滤器判断值是否存在}
	 * @Date 2023/4/17 14:53
	 * @param bloomFilterHelper
	 * @param key
	 * @param value
	 * @Return {@link boolean}
	 */
	@Override
	public <T> boolean includeByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
		Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			if (Boolean.FALSE.equals(redisTemplate.opsForValue().getBit(key, i))) {
				return false;
			}
		}

		return true;
	}
}

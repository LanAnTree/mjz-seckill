package com.lanan.mjzseckill.service;

import com.lanan.mjzseckill.utils.BloomFilterHelper;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/17-14:51
 */
public interface RedisService {

	<T> void addByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value);

	<T> boolean includeByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value);
}

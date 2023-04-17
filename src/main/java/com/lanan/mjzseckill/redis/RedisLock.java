package com.lanan.mjzseckill.redis;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/12-16:06
 */
@Slf4j
@Component
@NoArgsConstructor
public class RedisLock {

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	/**
	 * 要休眠的时间ms
	 */
	private long sleepMils;

	public boolean tryLock(String lockKey) {
		DefaultRedisScript<Long> lockScript = new DefaultRedisScript<>();
		lockScript.setLocation(new ClassPathResource("lua/REDIS_LOCK.lua"));
		lockScript.setResultType(Long.class);
		// TODO 加锁 这里 Thread.currentThread().getName() 用 分布式id 替换
		Long result = redisTemplate.execute(lockScript, Arrays.asList(new String[]{lockKey}),
				500, Thread.currentThread().getName());
		if (result != 1) {
			sleepMils = result;
			return false;
		}
		// 线程激烈竞争锁 可以调waitLock
		return true;
	}

	public void waitLock() {
		try {
			Thread.sleep(sleepMils);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void unlock(String lockKey) {
		DefaultRedisScript<Long> unlockScript = new DefaultRedisScript<>();
		unlockScript.setLocation(new ClassPathResource("lua/REDIS_UNLOCK.lua"));
		unlockScript.setResultType(Long.class);
		redisTemplate.execute(unlockScript, Arrays.asList(new String[]{lockKey}),
				500, Thread.currentThread().getName());
	}

}

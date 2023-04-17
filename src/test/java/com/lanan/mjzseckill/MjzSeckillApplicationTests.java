package com.lanan.mjzseckill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.mapper.OrderMapper;
import com.lanan.mjzseckill.rabbitMq.MqSender;
import com.lanan.mjzseckill.redis.RedisLock;
import com.lanan.mjzseckill.service.IMenuService;
import com.lanan.mjzseckill.service.TreeService;
import com.lanan.mjzseckill.utils.BloomFilterHelper;
import com.lanan.mjzseckill.vo.ResponseResultVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
class MjzSeckillApplicationTests {

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	@Resource
	private OrderMapper orderMapper;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private MqSender mqSender;

	@Resource
	private TreeService treeService;

	@Resource
	private IMenuService iMenuService;

	@Resource
	private RedisLock redisLock;

	@Resource
	private BloomFilterHelper<String> bloomFilterHelper;

	@Test
	void contextLoads() {
	}

	@Test
	void testRedisSetString() {
		redisTemplate.opsForValue().set("avatar:message-service:token", "token");
	}

	@Test
	void testRedisSetEntity() {
		User info = new User();
		info.setId(18373918176L);
		info.setNickname("user:default");
		info.setPassword("test");
		info.setSalt("randomSalt");
		info.setHead("/default");
		info.setLoginCount(1);
		redisTemplate.opsForValue().set("avatar:message-service:user", info);
		System.out.println("保存成功");
		User printUser = (User) redisTemplate.opsForValue().get("avatar:message-service:user");
		System.out.println("从redis中获取值：" + printUser);
	}

	@Test
	void testMapper() {
		System.out.println(orderMapper.selectList(null));
	}

	@Test
	void testEncoding() {
		System.out.println(passwordEncoder.encode("1234"));
		System.out.println(passwordEncoder.matches("1234",
				"$2a$10$/Vf55PNGxNzZrLx4yiP5kOUZ4BanNAQ53aIbuIKqKXgDxqBvoLJ0O"));
	}

	@Test
	void testLock() {
		Service service = new Service();
		for (int i = 0; i < 50; i++) {
			new Thread(new AddNumRunnable(service)).start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	CountDownLatch countDownLatch = new CountDownLatch(50);

	class Service {
		private int num;

		public void addNum() {
			System.out.println(Thread.currentThread().getName() + "---num:" + ++num);
		}

		public Service() {
			this.num = 0;
		}
	}

	class AddNumRunnable implements Runnable {
		private Service service;

		public AddNumRunnable(Service service) {
			this.service = service;
		}

		@Override
		public void run() {
			try {
				redisLock.tryLock("lock");
				service.addNum();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				redisLock.unlock("lock");
			}
			countDownLatch.countDown();
		}
	}


	@Test
	void testMq() {
		mqSender.sendTest("hello");
	}

	@Test
	void testEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(passwordEncoder.encode("password"));
		System.out.println(encoder.encode("password"));
		System.out.println(passwordEncoder.matches("password",
				"$2a$10$Z98Nr2BWW34apDjy0qm0feN2hUEZ1tI26tMJu4rdjoVhF7F0oYS3e"));
		System.out.println(encoder.matches("1234",
				"$2a$10$/Vf55PNGxNzZrLx4yiP5kOUZ4BanNAQ53aIbuIKqKXgDxqBvoLJ0O"));
	}

	@Test
	void testJson2Object() {
		String json = "{\"code\":200,\"message\":\"鐧诲綍鎴愬姛\",\"data\":{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzNDI5MDg3YzlhOTk0MWQyOGUyYjczMWExMzdiM2VjMCIsInN1YiI6Im1qei1zZWNraWxsIiwiaWF0IjoxNjgxMzAyNjE1LCJleHAiOjE2ODEzODkwMTUsIm5pY2tuYW1lIjoidXNlcjowIiwidXNlcklkIjoxMDAwMH0.BDd0ecFfOomegjbfSARA8lEmNbjerc14QT_0d077gvs\"}}";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ResponseResultVo vo = objectMapper.readValue(json, ResponseResultVo.class);
			System.out.println(vo);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test
	void testBloom() {
		int[] offset = bloomFilterHelper.murmurHashOffset("seckill-server:good:1");
		for (int i : offset) {
			System.out.println(i + ":" + Boolean.TRUE.equals(redisTemplate.opsForValue().getBit("bloom", i)));
		}
	}
}

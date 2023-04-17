package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.entity.UserDetail;
import com.lanan.mjzseckill.exception.ApiException;
import com.lanan.mjzseckill.mapper.UserMapper;
import com.lanan.mjzseckill.service.IUserService;
import com.lanan.mjzseckill.utils.JwtUtil;
import com.lanan.mjzseckill.utils.ResponseResult;
import com.lanan.mjzseckill.utils.UUIDUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * @Description UserServiceImpl 用户表 服务实现类
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @Author Lucky LanAn
 * @Date 2023-04-08 18:37:43
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

	@Resource
	private AuthenticationManager authenticationManager;

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	/**
	 * @param user
	 * @Description {login}
	 * @Date 2023/4/10 17:42
	 * @Return {@link ResponseResult <Map<String, Object>>}
	 */
	@Override
	public ResponseResult<Map<String, Object>> login(User user) {
		// AuthenticationManager进行用户认证
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(user.getNickname(), user.getPassword());
		Authentication authenticate = authenticationManager.authenticate(authenticationToken);
		if (ObjectUtils.isEmpty(authenticate)) {
			throw new ApiException("登录失败");
		}

		// 返回jwt
		UserDetail userDetail = (UserDetail) authenticate.getPrincipal();
		User info = userDetail.getUser();
		String key = "seckill-server:user:" + info.getId();
		String jwtToken = JwtUtil.getJwtToken(UUIDUtil.getUUID(), null, info);
		String hashKey = "token";
		if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, hashKey))) {
			redisTemplate.opsForHash().delete(key, hashKey);
		}
		redisTemplate.opsForHash().put(key, hashKey, jwtToken);


		// 用户信息存入redis key -> seckill-server:user:userId
		String str = "info";
		if (!Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, str))) {
			redisTemplate.opsForHash().put(key, str, userDetail);
		}

		Map<String, Object> map = new HashMap<>(2);
		map.put("token", jwtToken);
		return new ResponseResult<>(200, "登录成功", map);
	}

	/**
	 * @Description {logout}
	 * @Date 2023/4/10 20:06
	 * @Return {@link ResponseResult<String>}
	 */
	@Override
	public ResponseResult<String> logout() {
		// 获取Authentication 中用户id
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetail detail = (UserDetail) authentication.getPrincipal();
		Long id = detail.getUser().getId();

		// 删除redis中用户信息
		String key = "seckill-server:user:" + id;
		redisTemplate.delete(key);
		return new ResponseResult<>(200, "登出成功");
	}
}

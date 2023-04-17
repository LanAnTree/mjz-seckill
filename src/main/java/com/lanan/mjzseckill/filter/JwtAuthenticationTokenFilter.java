package com.lanan.mjzseckill.filter;

import com.lanan.mjzseckill.entity.UserDetail;
import com.lanan.mjzseckill.exception.ApiException;
import com.lanan.mjzseckill.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-19:09
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Resource
	RedisTemplate<Object, Object> redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, ApiException {
		// 获取token
		String token = request.getHeader("token");
		if (StringUtils.isEmpty(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		// 获取用户信息
		String userId;
		String key;
		String hashKeyVal1 = "token";
		String hashKeyVal2 = "info";
		try {
			Claims claims = JwtUtil.parseJwt(token);
			userId = String.valueOf(claims.get("userId"));
			if (StringUtils.isEmpty(userId)) {
				log.debug("无效token");
				throw new RuntimeException("无效token");
			}
			key = "seckill-server:user:" + userId;
			String redisToken =
					String.valueOf(redisTemplate.opsForHash().get(key, hashKeyVal1));
			if (!token.equals(redisToken)) {
				log.debug("token 失效");
				throw new RuntimeException("token 失效");
			}
		} catch (Exception e) {
			log.error("token:" + token + "<<<非法", e);
			throw new RuntimeException(e.getMessage());
		}

		// 从redis中获取完整的用户信息
		UserDetail userDetail = (UserDetail) redisTemplate.opsForHash().get(key, hashKeyVal2);
		if (ObjectUtils.isEmpty(userDetail)) {
			log.error("用户未登录");
			throw new RuntimeException("用户未登录");
		}

		// 存入
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

		// 放行
		filterChain.doFilter(request, response);
	}
}

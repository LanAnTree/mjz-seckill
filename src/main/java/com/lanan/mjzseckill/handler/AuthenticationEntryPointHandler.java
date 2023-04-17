package com.lanan.mjzseckill.handler;

import com.alibaba.fastjson.JSON;
import com.lanan.mjzseckill.utils.ResponseResult;
import com.lanan.mjzseckill.utils.WebUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-10:36
 */
@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		ResponseResult<String> responseResult =
				new ResponseResult<>(HttpStatus.UNAUTHORIZED.value(), "用户认证失败，请查询登录");
		WebUtil.renderString(response, JSON.toJSONString(responseResult));
	}
}

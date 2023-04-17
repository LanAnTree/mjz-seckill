package com.lanan.mjzseckill.handler;

import com.alibaba.fastjson.JSON;
import com.lanan.mjzseckill.utils.ResponseResult;
import com.lanan.mjzseckill.utils.WebUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-10:40
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		ResponseResult<String> responseResult =
				new ResponseResult<>(HttpStatus.FORBIDDEN.value(), "权限不足");
		WebUtil.renderString(response, JSON.toJSONString(responseResult));
	}
}

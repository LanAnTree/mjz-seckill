package com.lanan.mjzseckill.exception;

import com.lanan.mjzseckill.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/8-22:42
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

	@ExceptionHandler(ApiException.class)
	public ResponseResult<String> setApiExceptionHandler(ApiException e) {
		log.error(e.getMessage(), e);
		return new ResponseResult<>(5000, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseResult<String> setBindExceptionHandler(Exception e) {
		log.error(e.getMessage(), e);
		return new ResponseResult<>(50001, e.getMessage());
	}
}

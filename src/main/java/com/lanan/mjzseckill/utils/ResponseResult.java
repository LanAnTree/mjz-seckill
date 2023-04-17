package com.lanan.mjzseckill.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-15:54
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {

	/**
	 * 状态码
	 **/
	private Integer code;

	/**
	 * 提示信息
	 **/
	private String message;

	/**
	 * 数据
	 **/
	private T data;

	public ResponseResult(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResponseResult(Integer code, T data) {
		this.code = code;
		this.data = data;
	}

	public ResponseResult(ResponseEnum responseEnum, T data) {
		this.code = responseEnum.getCode();
		this.message = responseEnum.getMessage();
		this.data = data;
	}

	public ResponseResult(ResponseEnum responseEnum) {
		this.code = responseEnum.getCode();
		this.message = responseEnum.getMessage();
	}
}

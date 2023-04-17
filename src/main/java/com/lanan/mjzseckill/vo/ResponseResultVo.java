package com.lanan.mjzseckill.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/12-20:25
 */
@JsonIgnoreProperties
@Data
public class ResponseResultVo {
	/**
	 * 状态码
	 **/
	@JsonProperty(value = "code")
	private Integer code;

	/**
	 * 提示信息
	 **/
	@JsonProperty(value = "message")
	private String message;

	/**
	 * 数据
	 **/
	@JsonProperty(value = "data")
	private Map<String, String> data;
}

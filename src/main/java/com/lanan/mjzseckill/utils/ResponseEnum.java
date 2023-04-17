package com.lanan.mjzseckill.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-17:50
 */
@ToString
@Getter
@AllArgsConstructor
public enum ResponseEnum {

	//通用状态码
	SUCCESS(200,"success"),
	ERROR(500,"服务端异常"),

	//登录模块5002xx
	SESSION_ERROR(500210,"session不存在或者已经失效"),
	LOGIN_ERROR(500211,"用户名或者密码错误"),
	MOBILE_ERROR(500212,"手机号码格式错误"),
	REGISTER_ERROR(500213,"注册失败"),
	REGISTER_SUCCESS(500214,"注册成功"),
	LOGIN_SUCCESS(500215,"登录成功"),
	BINDING_ERROR(500216,"参数校验失败"),
	GOOD_DETAIL_LOW(500217, "商品详情异常"),
	EMPTY_STOCK(500218, "库存不足"),
	REPEATE_ERROR(500219, "重复抢购"),
	REPEATE_LOGIN(500220,"重复登录"),
	MOBILE_NOT_EXIST(500221, "手机号码不存在"),
	PASSWORD_UPDATE_FAIL(500222, "密码更新失败"),
	SECKILL_FAIL(500223, "商品已抢购完毕或者不在抢购时间段"),
	SECKILL_SUCCESS(500224, "商品抢购成功"),
	ORDER_SUCCESS(500225, "下单成功"),
	ORDER_FAIL(500226, "下单失败");

	private final Integer code;
	private final String message;
}

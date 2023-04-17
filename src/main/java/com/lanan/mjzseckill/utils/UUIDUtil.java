package com.lanan.mjzseckill.utils;

import java.util.UUID;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-18:05
 */
public class UUIDUtil {

	public static String getUUID() {
		return (UUID.randomUUID()).toString().replaceAll("-", "");
	}
}

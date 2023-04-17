package com.lanan.mjzseckill.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-10:33
 */
public class WebUtil {

	public static String renderString(HttpServletResponse response, String string) {
		try {
			response.setStatus(200);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

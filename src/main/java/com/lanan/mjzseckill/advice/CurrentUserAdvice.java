package com.lanan.mjzseckill.advice;

import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.entity.UserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-15:10
 */
@RestControllerAdvice
public class CurrentUserAdvice {

	@ModelAttribute()
	public User currentUser(Authentication authentication) {
		User user = null;
		if(authentication!=null) {
			UserDetail detail = (UserDetail) authentication.getPrincipal();
			user = detail.getUser();
		}
		return user;
	}
}

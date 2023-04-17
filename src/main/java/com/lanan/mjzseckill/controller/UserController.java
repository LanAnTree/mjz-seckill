package com.lanan.mjzseckill.controller;

import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.rabbitMq.MqSender;
import com.lanan.mjzseckill.service.IUserService;
import com.lanan.mjzseckill.utils.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-17:38
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Resource
	private IUserService userService;

	@Resource
	private MqSender mqSender;

	/**
	 * @Description {login}
	 * @Date 2023/4/10 20:06
	 * @param user
	 * @Return {@link ResponseResult<Map<String,Object>>}
	 */
	@PostMapping("/login")
	public ResponseResult<Map<String, Object>> login(@RequestBody User user) {
		return userService.login(user);
	}

	/**
	 * @Description {logout}
	 * @Date 2023/4/10 20:06
	 * @Return {@link ResponseResult<String>}
	 */
	@PostMapping("/logout")
	public ResponseResult<String> logout() {
		return userService.logout();
	}


	/**
	 * @Description {test mq server}
	 * @Date 2023/4/12 9:46
	 * @param message
	 */
	@GetMapping("/mq")
	public void testMq(@RequestParam("message") String message) {
		mqSender.sendTest(message);
	}
}

package com.lanan.mjzseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.utils.ResponseResult;

import java.util.Map;

/**
 * @Description IUserService 用户表 服务类
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @Author Lucky LanAn
 * @Date 2023-04-10 17:01:44
 **/
public interface IUserService extends IService<User> {

	/**
	 * @Description {login}
	 * @Date 2023/4/10 17:42
	 * @param user
	 * @Return {@link ResponseResult<Map<String,Object>>}
	 */
	ResponseResult<Map<String, Object>> login(User user);

	/**
	 * @Description {logout}
	 * @Date 2023/4/10 20:05
	 * @Return {@link ResponseResult<String>}
	 */
	ResponseResult<String> logout();
}

package com.lanan.mjzseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanan.mjzseckill.entity.Menu;

import java.util.List;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-10:17
 */
public interface IMenuService extends IService<Menu> {

	/**
	 * @Description {getPermissionsByUserId}
	 * @Date 2023/4/11 10:21
	 * @param userId
	 * @Return {@link List< String>}
	 */
	List<String> getPermissionsByUserId(Long userId);
}

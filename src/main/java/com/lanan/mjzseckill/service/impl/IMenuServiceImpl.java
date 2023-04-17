package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanan.mjzseckill.entity.Menu;
import com.lanan.mjzseckill.mapper.MenuMapper;
import com.lanan.mjzseckill.service.IMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-10:17
 */
@Service
public class IMenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

	@Resource
	private MenuMapper menuMapper;

	/**
	 * @param userId
	 * @Description {getPermissionsByUserId}
	 * @Date 2023/4/11 10:21
	 * @Return {@link List < String>}
	 */
	@Override
	public List<String> getPermissionsByUserId(Long userId) {
		return menuMapper.getPermissionsByUserId(userId);
	}
}

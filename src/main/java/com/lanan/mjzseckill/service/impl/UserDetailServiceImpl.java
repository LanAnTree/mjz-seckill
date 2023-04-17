package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.entity.UserDetail;
import com.lanan.mjzseckill.mapper.MenuMapper;
import com.lanan.mjzseckill.mapper.UserMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-17:02
 */
@Service("userDetailServiceImpl")
public class UserDetailServiceImpl implements UserDetailsService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private MenuMapper menuMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// 查询用户是否存在
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(User::getNickname, username);
		User user = userMapper.selectOne(queryWrapper);
		if (ObjectUtils.isEmpty(user)) {
			throw new UsernameNotFoundException("用户不存在");
		}

		// 查询用户对应权限
		List<String> permissions = menuMapper.getPermissionsByUserId(user.getId());

		// 封装UserDetail
		return new UserDetail(user, permissions);
	}
}

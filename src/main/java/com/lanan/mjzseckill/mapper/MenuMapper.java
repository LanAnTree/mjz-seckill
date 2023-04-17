package com.lanan.mjzseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lanan.mjzseckill.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-10:16
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

	/**
	 * @Description {getPermissionsByUserId}
	 * @Date 2023/4/11 10:21
	 * @param userId
	 * @Return {@link List < String>}
	 */
	List<String> getPermissionsByUserId(Long userId);
}

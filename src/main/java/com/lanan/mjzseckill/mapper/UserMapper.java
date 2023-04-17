package com.lanan.mjzseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lanan.mjzseckill.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description UserMapper 用户表 Mapper 接口
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @Author Lucky LanAn
 * @Date 2023-04-08 17:25:45
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

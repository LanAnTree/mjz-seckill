package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanan.mjzseckill.entity.Order;
import com.lanan.mjzseckill.mapper.OrderMapper;
import com.lanan.mjzseckill.service.IOrderService;
import org.springframework.stereotype.Service;

/**
* @Description OrderController 订单表 服务实现类
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}

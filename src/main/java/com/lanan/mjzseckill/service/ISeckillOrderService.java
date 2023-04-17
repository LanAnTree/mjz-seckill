package com.lanan.mjzseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanan.mjzseckill.entity.Order;
import com.lanan.mjzseckill.entity.SeckillOrder;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.utils.ResponseResult;
import com.lanan.mjzseckill.vo.GoodsVo;

/**
* @Description SeckillOrderController 秒杀订单表 服务类
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
public interface ISeckillOrderService extends IService<SeckillOrder> {

	/**
	 * @Description {seckill}
	 * @Date 2023/4/11 22:33
	 * @param user
	 * @param goodId
	 * @Return {@link ResponseResult<String>}
	 */
	ResponseResult<String> seckill(User user, Long goodId);

	/**
	 * @Description {redis预存订单}
	 * @Date 2023/4/12 11:24
	 * @param user
	 * @param goodId
	 * @Return {@link boolean}
	 */
	boolean preOrder(User user, Long goodId);

	/**
	 * @Description {校验redis中是否预存了订单信息 同时判断 整理中 订单和 处理完成 订单}
	 * @Date 2023/4/12 11:23
	 * @param userId
	 * @param goodId
	 * @Return {@link boolean}
	 */
	boolean checkOrderProcessingAndProcessed(Long userId, Long goodId);


	/**
	 * @Description {校验redis中是否预存了订单信息 只判断处理完成 订单}
	 * @Date 2023/4/12 11:23
	 * @param userId
	 * @param goodId
	 * @Return {@link boolean}
	 */
	boolean checkOrderOnlyProcessed(Long userId, Long goodId);

	/**
	 * @Description {秒杀下单}
	 * @Date 2023/4/12 11:39
	 * @param user
	 * @param goodsVo
	 * @Return {@link Order}
	 */
	Order seckillOrder(User user, GoodsVo goodsVo);
}

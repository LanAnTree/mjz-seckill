package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanan.mjzseckill.entity.Order;
import com.lanan.mjzseckill.entity.SeckillGoods;
import com.lanan.mjzseckill.entity.SeckillOrder;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.exception.ApiException;
import com.lanan.mjzseckill.mapper.OrderMapper;
import com.lanan.mjzseckill.mapper.SeckillOrderMapper;
import com.lanan.mjzseckill.rabbitMq.MqSender;
import com.lanan.mjzseckill.redis.RedisLock;
import com.lanan.mjzseckill.service.IGoodsService;
import com.lanan.mjzseckill.service.ISeckillGoodsService;
import com.lanan.mjzseckill.service.ISeckillOrderService;
import com.lanan.mjzseckill.service.RedisService;
import com.lanan.mjzseckill.utils.BloomFilterHelper;
import com.lanan.mjzseckill.utils.JsonUtil;
import com.lanan.mjzseckill.utils.ResponseEnum;
import com.lanan.mjzseckill.utils.ResponseResult;
import com.lanan.mjzseckill.vo.GoodsVo;
import com.lanan.mjzseckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
* @Description SeckillOrderController 秒杀订单表 服务实现类
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
@Slf4j
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

	private final Map<Long, Boolean> EmptyStockMap = new ConcurrentHashMap<>(16);

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	@Resource
	private ISeckillGoodsService seckillGoodsService;

	@Resource
	private OrderMapper orderMapper;

	@Resource
	private MqSender mqSender;

	@Resource
	private DefaultRedisScript<Long> seckillStockScript;

	@Resource
	private RedisLock redisLock;

	@Resource
	private RedisService redisService;

	@Resource
	private BloomFilterHelper<String> bloomFilterHelper;

	/**
	 * @param user
	 * @param goodId
	 * @Description  {
	 * 				重复下单: 1、购买过一次，一段时间后再次购买 ==》 通过redis进行预检，判别是否重复购买
	 * 	    				2、快速发起购买请求，且请求都通过了redis预检（redis来不及建立缓存） ==》 限流、sso
	 * 	    					本系统有两处地方进行 【重复下单判断】，分别是异步下单前进行一次和MQ下单时进行，由于两者之间是非强一致性
	 * 	    					导致同一用户快速发起购买请求，出现【重复下单问题】
	 * 	 							这里将订单分为两个状态，异步下单前【orderProcessing】和MQ下单时【orderProcessed】
	 * 	 							在redis中分别设置值，【重复下单判断】时，异步下单前【orderProcessing & orderProcessed】有一个
	 * 	 							key在redis中存在，即为【重复下单】；MQ下单时只需要判断【orderProcessed】
	 * 				内存标记（client 缓存）: 减轻前往redis的压力
	 * 				redis 库存预减 redis + lua
	 * 				缓存重建
	 * 				MQ异步下单
	 *				lua脚本加锁 redis + lua 实现锁，需要注意锁需要被原持有者释放，保证锁可重入
	 *
	 *
	 * }
	 * @Date 2023/4/11 22:33
	 * @Return {@link ResponseResult<String>}
	 */
	@Override
	public ResponseResult<String> seckill(User user, Long goodId) {
		Long userId = user.getId();
		// 内存标记 此处用于减少 【判断商品】 对redis查询
		if (memoryMarkers(goodId)) {
			return new ResponseResult<>(ResponseEnum.SECKILL_FAIL);
		}
		ValueOperations<Object, Object>  operations = redisTemplate.opsForValue();
		String key = "seckill-server:user:" + userId + ":good:"  + goodId + ":RedissonLock";
		String goodKey = "seckill-server:good:" + goodId;
		String bloomStr = "bloom";
		boolean result;
		// bloom过滤器 判断商品是否存在
		if (redisService.includeByBloomFilter(bloomFilterHelper, bloomStr, goodKey)) {
			try {
				// 加锁
				boolean cacheRes = redisLock.tryLock(key);
				if (cacheRes && !memoryMarkers(goodId)) {
					// 判断是否重复购买
					if (checkOrderProcessingAndProcessed(userId, goodId)) {

						// 判断商品库存  查 判断 减 lua保证原子性
//					Long stock = redisTemplate.execute(seckillStockScript, Collections.singletonList(goodKey),
//							Collections.EMPTY_LIST);
						// 减 operations.decrement(goodKey)
						// 判断缓存是否重建
						Long stock;
						if (Boolean.TRUE.equals(redisTemplate.hasKey(goodKey))) {
							stock = operations.decrement(goodKey);
						} else {
							SeckillGoods goodsServiceById = seckillGoodsService.getById(goodId);
							stock = Long.valueOf(goodsServiceById.getStockCount());
							// 缓存重建 解决缓存击穿
							seckillGoodsService.reCreateGoodCache(goodId);
						}
						// 维护 EmptyStockMap 为负次数 >> 为正次数
						if (stock != null && stock < 0) {
							EmptyStockMap.put(goodId, true);
							operations.set(goodKey, 0);
							return new ResponseResult<>(ResponseEnum.EMPTY_STOCK);
						} else if (stock != null && stock == 0){
							EmptyStockMap.put(goodId, true);
						}
						// 异步下单
						result = preOrder(user, goodId);
					} else {
						return new ResponseResult<>(ResponseEnum.REPEATE_ERROR);
					}
				} else {
					return new ResponseResult<>(ResponseEnum.SECKILL_FAIL);
				}
			} finally {
				redisLock.unlock(key);
			}
		} else {
			return new ResponseResult<>(ResponseEnum.SECKILL_FAIL);
		}
		return result ? new ResponseResult<>(ResponseEnum.ORDER_SUCCESS) :
				new ResponseResult<>(ResponseEnum.ORDER_FAIL);
	}

	/**
	 * @Description {内存标记}
	 * @Date 2023/4/15 15:11
	 * @param goodId
	 * @Return {@link Boolean}
	 */
	private Boolean memoryMarkers(Long goodId) {
		return EmptyStockMap.containsKey(goodId) && EmptyStockMap.get(goodId);
	}

	/**
	 * @param user
	 * @param goodId
	 * @Description {redis预存订单}
	 * @Date 2023/4/11 17:31
	 * @Return {@link boolean}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean preOrder(User user, Long goodId) {
		SeckillMessage seckillMessage = new SeckillMessage(user, goodId);
		// mq进行异步入单
		mqSender.sendSeckillInform(JsonUtil.object2JsonStr(seckillMessage), "seckill.msg");
		// TODO 防止消息丢失 写消息发送表 避免消息发送失败，使用任务队列进行失败重试
		// TODO 任务队列进行失败重试可能存在垃圾消息问题，消息发送表增加重试次数字段
		// TODO 设置ttl 到商品秒杀结束 timeOut
		redisTemplate.opsForValue()
				.set("seckill-server:order_processing:user:" + user.getId() + ":seckill-good:" + goodId, "订单下单中");
		return true;
	}

	/**
	 * @param userId
	 * @param goodId
	 * @Description {
	 * 					校验redis中是否预存了订单信息
	 * 					orderProcessing && orderProcessed 都不存在，返回true
	 * 			}
	 * @Date 2023/4/11 17:32
	 * @Return {@link boolean}
	 */
	@Override
	public boolean checkOrderProcessingAndProcessed(Long userId, Long goodId) {
		String orderProcessing = "seckill-server:order_processing:user:" + userId + ":seckill-good:" + goodId;
		String orderProcessed = "seckill-server:order:user:" + userId + ":seckill-good:" + goodId;
		return Boolean.FALSE.equals(redisTemplate.hasKey(orderProcessing))
				&& Boolean.FALSE.equals(redisTemplate.hasKey(orderProcessed));
	}

	/**
	 * @param userId
	 * @param goodId
	 * @Description {校验redis中是否预存了订单信息 只判断处理完成 订单}
	 * @Date 2023/4/12 11:23
	 * @Return {@link boolean}
	 */
	@Override
	public boolean checkOrderOnlyProcessed(Long userId, Long goodId) {
		String orderProcessed = "seckill-server:order:user:" + userId + ":seckill-good:" + goodId;
		return Boolean.FALSE.equals(redisTemplate.hasKey(orderProcessed));
	}

	/**
	 * @Description {秒杀下单}
	 * @Date 2023/4/12 11:39
	 * @param user
	 * @param goodsVo
	 * @Return {@link Order}
	 */
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public Order seckillOrder(User user, GoodsVo goodsVo) {
		// 秒杀商品表减库存
		LambdaQueryWrapper<SeckillGoods> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SeckillGoods::getGoodsId, goodsVo.getId());
		SeckillGoods seckillGoods = seckillGoodsService.getOne(queryWrapper);

		if (seckillGoods.getStockCount() < 1) {
			redisTemplate.opsForSet().add("isStockEmpty" + goodsVo.getId(), "0");
			return null;
		}

		// 更新库存
		UpdateWrapper<SeckillGoods> updateWrapper = new UpdateWrapper<>();
		updateWrapper.setSql("stock_count = stock_count - 1")
				.eq("goods_id", goodsVo.getId()).gt("stock_count", 0);
		seckillGoodsService.update(updateWrapper);

		// 生成订单
		Order order = new Order();
		order.setUserId(user.getId());
		order.setGoodsId(goodsVo.getId());
		order.setDeliveryAddrId(0L);
		order.setGoodsName(goodsVo.getGoodsName());
		order.setGoodsCount(1);
		order.setGoodsPrice(seckillGoods.getSeckillPrice());
		order.setOrderChannel(1);
		// 0新建未支付，1已支付，2已发货，3已收货，4已退货，5已完成
		order.setStatus(0);
		order.setCreateDate(new Date());
		orderMapper.insert(order);
		// 生成秒杀订单
		SeckillOrder seckillOrder = new SeckillOrder();
		seckillOrder.setOrderId(order.getId());
		seckillOrder.setUserId(user.getId());
		seckillOrder.setGoodsId(goodsVo.getId());
		this.save(seckillOrder);
		// redis 订单生成，下单成功
		redisTemplate.opsForValue().set(strKey("order", user.getId(), goodsVo.getId()),
				Objects.requireNonNull(JsonUtil.object2JsonStr(seckillOrder)));
		// 删除 处理中订单
		redisTemplate.delete(strKey("order_processing", user.getId(), goodsVo.getId()));
		// TODO 保证消息幂等性 写消息处理表
		// TODO 发送消息给MQ延迟队列
		return order;
	}

	private String strKey(String key, Long userId, Long goodId) {
		return "seckill-server:" + key + ":user:" + userId + ":seckill-good:" + goodId;
	}
}

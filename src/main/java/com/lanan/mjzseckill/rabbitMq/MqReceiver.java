package com.lanan.mjzseckill.rabbitMq;

import com.lanan.mjzseckill.config.RabbitMqConfig;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.service.IGoodsService;
import com.lanan.mjzseckill.service.ISeckillOrderService;
import com.lanan.mjzseckill.utils.JsonUtil;
import com.lanan.mjzseckill.vo.GoodsVo;
import com.lanan.mjzseckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/12-9:41
 */
@Component
@Slf4j
public class MqReceiver {

	@Resource
	private MqSender mqSender;

	@Resource
	private IGoodsService goodsService;

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	@Resource
	private ISeckillOrderService seckillOrderService;

	@RabbitListener(queues = {"queue"})
	public void receiveTest(Object message) {
		log.info("Test: QUEUE接受消息：" + message);
	}

	/**
	 * @Description {mq进行下单}
	 * @Date 2023/4/12 10:42
	 */
	@RabbitListener(queues = {RabbitMqConfig.QUEUE_SECKILL})
	public void seckillReceive(String message) {
//		log.info("Seckill: QUEUE接受消息" + message);
		SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
		if (ObjectUtils.isEmpty(seckillMessage)) {
//			log.info("seckill message cannot be empty");
			mqSender.sendEmailInform("seckill message cannot be empty",
					"inform.seckillMessage.email.error");
			return;
		}

		// TODO 防止消息重复消费 查询消息处理表

		Long goodsId = seckillMessage.getGoodsId();
		if (ObjectUtils.isEmpty(goodsId)) {
//			log.info("seckill good id cannot be empty");
			mqSender.sendEmailInform("seckill good id cannot be empty",
					"inform.goodsId.email.error");
			return;
		}

		User user = seckillMessage.getUser();
		if (ObjectUtils.isEmpty(user)) {
//			log.info("seckill user info cannot be empty");
			mqSender.sendEmailInform("seckill user info cannot be empty",
					"inform.userInfo.email.error");
			return;
		}

		// 库存判断
		GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
		if (goodsVo.getStockCount() < 1) {
			mqSender.sendSmsInform("商品抢完了，下次得再快点", "inform.stockLow.sms.info");
			return;
		}

		// 重复下单
		if (!seckillOrderService.checkOrderOnlyProcessed(user.getId(), goodsId)) {
			log.info("重复下单");
			// TODO 回刷redis
			// TODO 延迟队列
			return;
		}

		// 下单
		seckillOrderService.seckillOrder(user, goodsVo);
		log.info("用户：" + user.getId() + "抢购商品" + goodsId + "成功");
	}

	/**
	 * @Description {消息提醒}
	 * @Date 2023/4/12 13:14
	 * @param message
	 */
	@RabbitListener(queues = {RabbitMqConfig.QUEUE_INFORM_SMS, RabbitMqConfig.QUEUE_INFORM_EMAIL})
	public void smsReceiver(String message) {
		log.info("SMS : " + message);
	}
}

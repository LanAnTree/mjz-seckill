package com.lanan.mjzseckill.rabbitMq;

import com.lanan.mjzseckill.config.RabbitMqConfig;
import com.lanan.mjzseckill.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/12-9:37
 */
@Component
@Slf4j
public class MqSender {

	@Resource
	private RabbitTemplate rabbitTemplate;

	public void sendTest(Object message) {
		log.info("发送test消息：" + message);
		rabbitTemplate.convertAndSend("queue", message);
	}

	public void sendSeckillInform(String message, String routingKey) {
		log.info("发送seckill消息：" + message);
		rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_TOPICS_SECKILL, routingKey, message);
	}

	public void sendEmailInform(String message, String routingKey) {
		log.info("发送email消息：" + message);
		rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_TOPICS_EMAIL, routingKey, message);
	}

	public void sendSmsInform(String message, String routingKey) {
		log.info("发送sms消息：" + message);
		rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_TOPICS_SMS, routingKey, message);
	}
}

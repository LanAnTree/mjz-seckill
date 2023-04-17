package com.lanan.mjzseckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/12-9:35
 */
@Configuration
public class RabbitMqConfig {

	public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
	public static final String QUEUE_SECKILL = "queue_seckill";
	public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
	public static final String EXCHANGE_TOPICS_EMAIL = "exchange_topics_inform";
	public static final String EXCHANGE_TOPICS_SECKILL = "seckill_topics_seckill";
	public static final String EXCHANGE_TOPICS_SMS = "exchange_topics_sms";
	public static final String ROUTING_KEY_EMAIL = "inform.#.email.#";
	public static final String ROUTING_KEY_SECKILL = "seckill.#";
	public static final String ROUTING_KEY_SMS = "inform.#.sms.#";

	// *************
	// email
	// *************


	@Bean(QUEUE_INFORM_EMAIL)
	public Queue queueInformEmail() {
		return new Queue(QUEUE_INFORM_EMAIL, true);
	}

	@Bean(EXCHANGE_TOPICS_EMAIL)
	public Exchange emailTopicExchange() {
		return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_EMAIL).durable(true).build();
	}

	@Bean
	public Binding emailBinding(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
								@Qualifier(EXCHANGE_TOPICS_EMAIL) Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_EMAIL).noargs();
	}

	// *************
	// seckill
	// *************


	@Bean(QUEUE_SECKILL)
	public Queue queueSeckill() {
		return new Queue(QUEUE_SECKILL, true);
	}

	@Bean(EXCHANGE_TOPICS_SECKILL)
	public Exchange seckillTopicExchange() {
		return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_SECKILL).durable(true).build();
	}

	@Bean
	public Binding seckillBinding(@Qualifier(QUEUE_SECKILL) Queue queue,
								  @Qualifier(EXCHANGE_TOPICS_SECKILL) Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_SECKILL).noargs();
	}


	// *************
	// SMS
	// *************


	@Bean(QUEUE_INFORM_SMS)
	public Queue queueInformSms() {
		return new Queue(QUEUE_INFORM_SMS, true);
	}

	@Bean(EXCHANGE_TOPICS_SMS)
	public Exchange smsTopicExchange() {
		return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_SMS).durable(true).build();
	}

	@Bean
	public Binding smsBinding(@Qualifier(QUEUE_INFORM_SMS) Queue queue,
								  @Qualifier(EXCHANGE_TOPICS_SMS) Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_SMS).noargs();
	}
}

package com.lanan.mjzseckill.vo;

import com.lanan.mjzseckill.entity.Goods;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lucky LanAn 商品返回对象
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/11-23:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsVo extends Goods {

	/**
	 * 秒杀价格
	 **/
	private BigDecimal seckillPrice;

	/**
	 * 剩余数量
	 **/
	private Integer stockCount;

	/**
	 * 开始时间
	 **/
	private Date startDate;

	/**
	 * 结束时间
	 **/
	private Date endDate;

}

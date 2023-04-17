package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanan.mjzseckill.entity.Goods;
import com.lanan.mjzseckill.entity.SeckillGoods;
import com.lanan.mjzseckill.mapper.SeckillGoodsMapper;
import com.lanan.mjzseckill.service.ISeckillGoodsService;
import com.lanan.mjzseckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description SeckillGoodsController 秒杀商品表 服务实现类
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

	@Resource
	private SeckillGoodsMapper seckillGoodsMapper;

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;


	@Override
	public void reCreateGoodCache(Long goodsId) {
		SeckillGoods good = seckillGoodsMapper.selectById(goodsId);
		if (ObjectUtils.isEmpty(good)) {
			return;
		}
		String key = "seckill-server:good:";
		redisTemplate.opsForValue().set(key + goodsId, good.getStockCount());
	}
}

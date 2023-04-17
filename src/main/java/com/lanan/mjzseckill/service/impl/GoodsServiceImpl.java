package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanan.mjzseckill.entity.Goods;
import com.lanan.mjzseckill.mapper.GoodsMapper;
import com.lanan.mjzseckill.service.IGoodsService;
import com.lanan.mjzseckill.service.RedisService;
import com.lanan.mjzseckill.utils.BloomFilterHelper;
import com.lanan.mjzseckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description GoodsController 商品表 服务实现类
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

	@Resource
	private GoodsMapper goodsMapper;

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	@Resource
	private RedisService redisService;

	@Resource
	private BloomFilterHelper<String> bloomFilterHelper;

	@Override
	public List<GoodsVo> findGoodsVo() {
		return goodsMapper.findGoodsVo();
	}

	@Override
	public void reCreateGoodCache() {
		List<GoodsVo> goodsVo = goodsMapper.findGoodsVo();
		if (CollectionUtils.isEmpty(goodsVo)) {
			return;
		}
		String key = "seckill-server:good:";
		goodsVo.forEach(good -> {
			redisTemplate.opsForValue().set(key + good.getId(), good.getStockCount());
			// bloom过滤器
			redisService.addByBloomFilter(bloomFilterHelper, "bloom", 	key + good.getId());
		});
	}

	/**
	 * @param goodsId
	 * @Description {根据商品id获取商品详情}
	 * @Date 2023/4/9 10:02
	 * @Return {@link GoodsVo}
	 */
	@Override
	public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
		return goodsMapper.findGoodsVoByGoodsId(goodsId);
	}
}

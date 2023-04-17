package com.lanan.mjzseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanan.mjzseckill.entity.SeckillGoods;

/**
* @Description SeckillGoodsController 秒杀商品表 服务类
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
public interface ISeckillGoodsService extends IService<SeckillGoods> {

	void reCreateGoodCache(Long goodsId);
}

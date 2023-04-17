package com.lanan.mjzseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanan.mjzseckill.entity.Goods;
import com.lanan.mjzseckill.vo.GoodsVo;

import java.util.List;


/**
* @Description GoodsController 商品表 服务类
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
public interface IGoodsService extends IService<Goods> {

	List<GoodsVo> findGoodsVo();

	void reCreateGoodCache();

	/**
	 * @Description {根据商品id获取商品详情}
	 * @Date 2023/4/9 10:02
	 * @param goodsId
	 * @Return {@link GoodsVo}
	 */
	GoodsVo findGoodsVoByGoodsId(Long goodsId);
}

package com.lanan.mjzseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lanan.mjzseckill.entity.Goods;
import com.lanan.mjzseckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @Description GoodsController 商品表 Mapper 接口
* @Created with IntelliJ IDEA 2021.3.1 .
* @Author Lucky LanAn
* @Date 2023-04-08
**/
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

	/**
	 * @Description {返回商品列表}
	 * @Date 2023/4/11 23:29
	 * @Return {@link List< GoodsVo>}
	 */
	List<GoodsVo> findGoodsVo();

	/**
	 * @Description {根据商品id获取商品详情}
	 * @Date 2023/4/12 11:20
	 * @param goodsId
	 * @Return {@link GoodsVo}
	 */
	GoodsVo findGoodsVoByGoodsId(Long goodsId);
}

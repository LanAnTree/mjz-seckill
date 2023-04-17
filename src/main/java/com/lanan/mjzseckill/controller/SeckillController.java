package com.lanan.mjzseckill.controller;

import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.service.IGoodsService;
import com.lanan.mjzseckill.service.ISeckillOrderService;
import com.lanan.mjzseckill.utils.ResponseEnum;
import com.lanan.mjzseckill.utils.ResponseResult;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-11:40
 */
@RestController
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

	@Resource
	private ISeckillOrderService seckillOrderService;

	@Resource
	private IGoodsService goodsService;

	/**
	 * @Description {test web application start and SpringSecurity}
	 * @Date 2023/4/10 11:41
	 * @Return {@link String}
	 */
	@GetMapping("/test")
	@PreAuthorize("hasAuthority('test')")
	public String test() {
		return "hello";
	}


	/**
	 * @Description seckill
	 * @Date 2023/4/11 19:23
	 * @param map
	 * @param user
	 * @Return {@link ResponseResult<String>}
	 */
	@PostMapping("/doSeckill")
	public ResponseResult<String> seckill(@RequestBody Map<String, String> map, @ModelAttribute() User user) {
		if (ObjectUtils.isEmpty(map)) {
			return new ResponseResult<>(ResponseEnum.GOOD_DETAIL_LOW);
		}
		// TODO 秒杀路径判断  获取path接口进行限流
		Long goodIdVal = Long.valueOf(map.get("goodId"));
		// 返回友好提示
		return seckillOrderService.seckill(user, goodIdVal);
	}

	/**
	 * @Description {hot key load into redis}
	 * @Date 2023/4/11 23:31
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		goodsService.reCreateGoodCache();
	}
}

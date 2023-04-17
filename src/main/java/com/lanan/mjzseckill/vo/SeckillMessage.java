package com.lanan.mjzseckill.vo;

import com.lanan.mjzseckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/12-10:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
	private User user;
	private Long goodsId;
}

package com.lanan.mjzseckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-21:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_tree")
public class Tree {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	private String name;

	private Long parentId;
}

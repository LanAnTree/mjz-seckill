package com.lanan.mjzseckill.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-21:41
 */
@Data
public class TreeNode {
	private Long id;
	private String name;
	private List<TreeNode> children;
	private Long parentId;

	public TreeNode(Long id, String name, Long parentId) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.children = new ArrayList<>();
	}

	public void addChild(TreeNode child) {
		children.add(child);
	}
}


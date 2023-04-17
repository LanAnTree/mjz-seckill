package com.lanan.mjzseckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanan.mjzseckill.entity.Tree;
import com.lanan.mjzseckill.entity.TreeNode;
import com.lanan.mjzseckill.mapper.TreeMapper;
import com.lanan.mjzseckill.service.TreeService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-21:46
 */
@Service
public class TreeServiceImpl extends ServiceImpl<TreeMapper, Tree> implements TreeService {

	@Override
	public void getTree() {
		List<Tree> trees = this.baseMapper.selectList(null);
		System.out.println(buildTree(trees));
		System.out.println(build(trees));
	}

	/**
	 * @Description {方法一}
	 * @Date 2023/4/10 22:37
	 * @param dataList
	 * @Return {@link List<TreeNode>}
	 */
	public List<TreeNode> build(List<Tree> dataList) {
		List<TreeNode> nodeList = new ArrayList<>();
		for (Tree data : dataList) {
			nodeList.add(new TreeNode(data.getId(), data.getName(), data.getParentId()));
		}
		List<TreeNode> res = new ArrayList<>();
		for (TreeNode node : nodeList) {
			// 如果是父节点
			if (node.getParentId() == 0L) {
				buildChildren(node, nodeList);
				res.add(node);
			}
		}
		return res;
	}

	private void buildChildren(TreeNode node, final List<TreeNode> list) {
		// 从list中找到node的孩子
		List<TreeNode> children = list.stream()
				.filter(p->p.getParentId().equals(node.getId())).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(children)) {
			for (TreeNode child : children) {
				// 递归调用
				buildChildren(child, list);
			}
			node.setChildren(children);
		}
	}

	/**
	 * @Description {方法二}
	 * @Date 2023/4/10 22:38
	 * @param dataList
	 * @Return {@link TreeNode}
	 */
	public TreeNode buildTree(List<Tree> dataList) {
		Map<Long, TreeNode> idToNodeMap = new HashMap<>(dataList.size());
		for (Tree data : dataList) {
			Long id = data.getId();
			String name = data.getName();
			Long parentId = data.getParentId();

			TreeNode node = idToNodeMap.get(id);
			if (node == null) {
				node = new TreeNode(id, name, parentId);
				idToNodeMap.put(id, node);
			} else {
				node.setName(name);
			}

			if (parentId != 0) {
				TreeNode parentNode = idToNodeMap.get(parentId);
				if (parentNode == null) {
					parentNode = new TreeNode(parentId, null, null);
					idToNodeMap.put(parentId, parentNode);
				}
				parentNode.addChild(node);
			}
		}

		TreeNode root = null;
		for (TreeNode node : idToNodeMap.values()) {
			if (node.getParentId() == 0) {
				root = node;
				break;
			}
		}

		return root;
	}

}

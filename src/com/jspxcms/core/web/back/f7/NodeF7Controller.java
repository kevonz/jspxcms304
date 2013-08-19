package com.jspxcms.core.web.back.f7;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jspxcms.core.domain.Node;
import com.jspxcms.core.service.NodeQueryService;
import com.jspxcms.core.support.Context;

/**
 * NodeF7Controller
 * 
 * @author liufang
 * 
 */
@Controller
@RequestMapping("/core/node")
public class NodeF7Controller {
	/**
	 * 节点单选。信息选择主节点。
	 * 
	 * @param id
	 * @param excludeChildrenId
	 * @param isRealNode
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("f7_node_tree.do")
	public String f7NodeTree(Integer id, Integer excludeChildrenId,
			Boolean isRealNode, HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		Integer siteId = Context.getCurrentSiteId(request);
		List<Node> list = query.findList(siteId, null, isRealNode, null);
		Node bean = null, excludeChildrenBean = null;
		if (id != null) {
			bean = query.get(id);
		}
		if (excludeChildrenId != null) {
			excludeChildrenBean = query.get(excludeChildrenId);
		}

		modelMap.addAttribute("id", id);
		modelMap.addAttribute("excludeChildrenId", excludeChildrenId);
		modelMap.addAttribute("bean", bean);
		modelMap.addAttribute("excludeChildrenBean", excludeChildrenBean);
		modelMap.addAttribute("list", list);
		return "core/node/f7_node_tree";
	}

	/**
	 * 节点多选。信息选择父栏目。
	 * 
	 * @param ids
	 * @param isRealNode
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("f7_node_tree_multi.do")
	public String f7NodeTreeMulti(Integer[] ids, Boolean isRealNode,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		Integer siteId = Context.getCurrentSiteId(request);
		List<Node> list = query.findList(siteId, null, isRealNode, null);
		List<Node> beans = new ArrayList<Node>();
		if (ids != null) {
			for (Integer id : ids) {
				beans.add(query.get(id));
			}
		}

		modelMap.addAttribute("ids", ids);
		modelMap.addAttribute("beans", beans);
		modelMap.addAttribute("list", list);
		return "core/node/f7_node_tree_multi";
	}

	/**
	 * 节点多选。信息权限、节点权限
	 * 
	 * @param ids
	 * @param isRealNode
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("f7_node_tree_rights.do")
	public String f7NodeTreeRights(Integer[] ids, Boolean isRealNode,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		Integer siteId = Context.getCurrentSiteId(request);
		List<Node> list = query.findList(siteId, null, isRealNode, null);
		List<Node> beans = new ArrayList<Node>();
		if (ids != null) {
			for (Integer id : ids) {
				beans.add(query.get(id));
			}
		}
		modelMap.addAttribute("ids", ids);
		modelMap.addAttribute("beans", beans);
		modelMap.addAttribute("list", list);
		return "core/node/f7_node_tree_rights";
	}

	@Autowired
	private NodeQueryService query;
}

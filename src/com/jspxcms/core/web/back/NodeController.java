package com.jspxcms.core.web.back;

import static com.jspxcms.core.support.Constants.CREATE;
import static com.jspxcms.core.support.Constants.DELETE_SUCCESS;
import static com.jspxcms.core.support.Constants.EDIT;
import static com.jspxcms.core.support.Constants.MESSAGE;
import static com.jspxcms.core.support.Constants.OPERATION_SUCCESS;
import static com.jspxcms.core.support.Constants.OPRT;
import static com.jspxcms.core.support.Constants.SAVE_SUCCESS;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jspxcms.common.util.RowSide;
import com.jspxcms.common.web.Servlets;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Model;
import com.jspxcms.core.domain.Node;
import com.jspxcms.core.domain.NodeDetail;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.fulltext.InfoFulltextService;
import com.jspxcms.core.generatepage.GeneratePageService;
import com.jspxcms.core.service.ModelService;
import com.jspxcms.core.service.NodeQueryService;
import com.jspxcms.core.service.NodeService;
import com.jspxcms.core.service.WorkflowService;
import com.jspxcms.core.support.Constants;
import com.jspxcms.core.support.Context;

import freemarker.template.TemplateException;

/**
 * NodeController
 * 
 * @author liufang
 * 
 */
@Controller
@RequestMapping("/core/node")
public class NodeController {
	private static final Logger logger = LoggerFactory
			.getLogger(NodeController.class);

	@RequiresPermissions("core:node:left")
	@RequestMapping("left.do")
	public String left(HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		User user = Context.getCurrentUser(request);
		Integer siteId = Context.getCurrentSiteId(request);
		List<Node> list = query.findList(siteId, null);
		Boolean allNode = user.getAllNode(siteId);
		modelMap.addAttribute("allNode", allNode);
		if (!allNode) {
			modelMap.addAttribute("nodeRights", user.getNodeRights(siteId));
		}
		modelMap.addAttribute("list", list);
		return "core/node/node_left";
	}

	@RequiresPermissions("core:node:list")
	@RequestMapping("list.do")
	public String list(
			Integer queryParentId,
			Boolean showDescendants,
			@PageableDefaults(sort = { "treeNumber", "id" }, sortDir = Direction.ASC) Pageable pageable,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		User user = Context.getCurrentUser(request);
		Integer siteId = Context.getCurrentSiteId(request);
		Node parent = null;
		if (queryParentId != null) {
			parent = query.get(queryParentId);
		}
		if (parent == null) {
			parent = query.findRoot(siteId);
			if (parent != null) {
				queryParentId = parent.getId();
			}
		}
		Map<String, String[]> params = Servlets.getParameterValuesMap(request,
				Constants.SEARCH_PREFIX);
		List<Node> list = query.findList(queryParentId, showDescendants,
				user.getId(), user.getAllNode(siteId), params,
				pageable.getSort());
		List<Model> nodeModelList = modelService.findList(siteId,
				Node.NODE_MODEL_TYPE);
		modelMap.addAttribute("parent", parent);
		modelMap.addAttribute("list", list);
		modelMap.addAttribute("nodeModelList", nodeModelList);
		modelMap.addAttribute("queryParentId", queryParentId);
		modelMap.addAttribute("showDescendants", showDescendants);
		return "core/node/node_list";
	}

	@RequiresPermissions("core:node:create")
	@RequestMapping("create.do")
	public String create(Integer cid, Integer parentId, Integer modelId,
			Integer queryParentId, Boolean showDescendants,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		Integer siteId = Context.getCurrentSiteId(request);
		Node bean = null;
		if (cid != null) {
			bean = query.get(cid);
		}
		Node parent = null;
		if (bean != null) {
			if (bean.getParent() != null) {
				parent = bean.getParent();
			} else {
				// 首页不能复制
				cid = null;
				bean = null;
				parent = query.findRoot(siteId);
			}
		} else if (parentId != null) {
			parent = query.get(parentId);
		} else {
			parent = query.findRoot(siteId);
		}
		Model model = null;
		if (modelId != null) {
			model = modelService.get(modelId);
		} else if (parent != null && parent.getParent() != null) {
			// 指定了父节点，且父节点不是根节点。
			model = parent.getNodeModel();
		} else if (query.findRoot(siteId) != null) {
			// 根节点已经存在，获取默认节点模型
			model = modelService.findDefault(siteId, Node.NODE_MODEL_TYPE);
			// 没有定义节点模型
			if (model == null) {
				// TODO
				throw new IllegalStateException("Node model not found!");
			}
		} else {
			// 根节点不存在，获取默认首页模型
			model = modelService.findDefault(siteId, Node.HOME_MODEL_TYPE);
			// 没有定义首页模型
			if (model == null) {
				// TODO
				throw new IllegalStateException("NodeHome model not found!");
			}
		}
		String modelType = model.getType();
		List<Model> nodeModelList = modelService.findList(siteId, modelType);
		List<Model> infoModelList = modelService.findList(siteId,
				Info.MODEL_TYPE);
		Model infoModelDef = null;
		if (!infoModelList.isEmpty()) {
			infoModelDef = infoModelList.get(0);
		}
		List<Workflow> workflowList = workflowService.findList(siteId);
		modelMap.addAttribute("workflowList", workflowList);

		modelMap.addAttribute("cid", cid);
		modelMap.addAttribute("bean", bean);
		modelMap.addAttribute("parent", parent);
		modelMap.addAttribute("model", model);
		modelMap.addAttribute("nodeModelList", nodeModelList);
		modelMap.addAttribute("infoModelDef", infoModelDef);
		modelMap.addAttribute("infoModelList", infoModelList);
		modelMap.addAttribute("queryParentId", queryParentId);
		modelMap.addAttribute("showDescendants", showDescendants);
		modelMap.addAttribute(OPRT, CREATE);
		return "core/node/node_form";
	}

	@RequiresPermissions("core:node:edit")
	@RequestMapping("edit.do")
	public String edit(
			Integer id,
			Integer modelId,
			Integer queryParentId,
			Boolean showDescendants,
			Integer position,
			@PageableDefaults(sort = { "treeNumber", "id" }, sortDir = Direction.ASC) Pageable pageable,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		User user = Context.getCurrentUser(request);
		Integer siteId = Context.getCurrentSiteId(request);
		Node bean = query.get(id);
		Node parent = bean.getParent();
		Map<String, String[]> params = Servlets.getParameterValuesMap(request,
				Constants.SEARCH_PREFIX);
		RowSide<Node> side = query.findSide(queryParentId, showDescendants,
				user.getId(), user.getAllNode(siteId), params, bean, position,
				pageable.getSort());
		modelMap.addAttribute("bean", bean);
		modelMap.addAttribute("side", side);
		modelMap.addAttribute("position", position);
		List<Model> infoModelList = modelService.findList(siteId,
				Info.MODEL_TYPE);
		List<Model> modelList = modelService.findList(siteId,
				Node.NODE_MODEL_TYPE);
		List<Model> nodeModelList;
		if (bean.getParent() == null) {
			// 首页模型
			nodeModelList = modelService.findList(siteId, Node.HOME_MODEL_TYPE);
		} else {
			nodeModelList = modelList;
		}
		Model model;
		// 允许修改栏目模型
		if (modelId != null) {
			model = modelService.get(modelId);
		} else {
			model = bean.getNodeModel();
		}
		List<Workflow> workflowList = workflowService.findList(siteId);
		modelMap.addAttribute("workflowList", workflowList);

		modelMap.addAttribute("model", model);
		modelMap.addAttribute("parent", parent);
		modelMap.addAttribute("bean", bean);
		modelMap.addAttribute("modelList", modelList);
		modelMap.addAttribute("nodeModelList", nodeModelList);
		modelMap.addAttribute("infoModelList", infoModelList);
		modelMap.addAttribute("queryParentId", queryParentId);
		modelMap.addAttribute("showDescendants", showDescendants);
		modelMap.addAttribute(OPRT, EDIT);
		return "core/node/node_form";
	}

	@RequiresPermissions("core:node:save")
	@RequestMapping("save.do")
	public String save(Integer cid, Node bean, NodeDetail detail,
			Integer parentId, Integer nodeModelId, Integer infoModelId,
			Integer workflowId, Integer queryParentId, Boolean showDescendants,
			String redirect, HttpServletRequest request, RedirectAttributes ra) {
		Integer siteId = Context.getCurrentSiteId(request);
		Integer userId = Context.getCurrentUserId(request);
		Map<String, String> customs = Servlets.getParameterMap(request,
				"customs_");
		Map<String, String> clobs = Servlets.getParameterMap(request, "clobs_");
		service.save(bean, detail, customs, clobs, parentId, nodeModelId,
				infoModelId, workflowId, userId, siteId);
		logger.info("save Node, name={}.", bean.getName());
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
		ra.addFlashAttribute("refreshLeft", true);
		if (Constants.REDIRECT_LIST.equals(redirect)) {
			return "redirect:list.do";
		} else if (Constants.REDIRECT_CREATE.equals(redirect)) {
			ra.addAttribute("parentId", parentId);
			return "redirect:create.do";
		} else {
			ra.addAttribute("id", bean.getId());
			return "redirect:edit.do";
		}
	}

	@RequiresPermissions("core:node:update")
	@RequestMapping("update.do")
	public String update(@ModelAttribute("bean") Node bean,
			@ModelAttribute("detail") NodeDetail detail, Integer parentId,
			Integer nodeModelId, Integer infoModelId, Integer workflowId,
			Integer position, Integer queryParentId, Boolean showDescendants,
			String redirect, HttpServletRequest request, RedirectAttributes ra) {
		Integer siteId = Context.getCurrentSiteId(request);
		Map<String, String> customs = Servlets.getParameterMap(request,
				"customs_");
		Map<String, String> clobs = Servlets.getParameterMap(request, "clobs_");
		service.update(bean, detail, customs, clobs, nodeModelId, infoModelId,
				workflowId);
		// 移动节点
		if (parentId != null && bean.getParent() != null
				&& !parentId.equals(bean.getParent().getId())) {
			service.move(new Integer[] { bean.getId() }, parentId, siteId);
		}
		logger.info("update Node, name={}.", bean.getName());
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
		ra.addFlashAttribute("refreshLeft", true);
		if (Constants.REDIRECT_LIST.equals(redirect)) {
			return "redirect:list.do";
		} else {
			ra.addAttribute("id", bean.getId());
			ra.addAttribute("position", position);
			return "redirect:edit.do";
		}
	}

	@RequiresPermissions("core:node:batch_update")
	@RequestMapping("batch_update.do")
	public String batchUpdate(Integer[] id, String[] name, String[] number,
			Integer[] views, Boolean[] hidden, Integer queryParentId,
			Boolean showDescendants, Pageable pageable,
			HttpServletRequest request, RedirectAttributes ra) {
		Integer siteId = Context.getCurrentSiteId(request);
		if (ArrayUtils.isNotEmpty(id)) {
			// 有排序的情况下不更新树结构，以免引误操作。
			boolean isUpdateTree = pageable.getSort() == null;
			Node[] beans = service.batchUpdate(id, name, number, views, hidden,
					siteId, isUpdateTree);
			for (Node bean : beans) {
				logger.info("update Node, name={}.", bean.getName());
			}
		}
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
		ra.addFlashAttribute("refreshLeft", true);
		return "redirect:list.do";
	}

	@RequiresPermissions("core:node:delete")
	@RequestMapping("delete.do")
	public String delete(Integer[] ids, Integer queryParentId,
			Boolean showDescendants, RedirectAttributes ra) {
		Node[] beans = service.delete(ids);
		for (Node bean : beans) {
			logger.info("delete Node, name={}.", bean.getName());
		}
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
		ra.addFlashAttribute("refreshLeft", true);
		return "redirect:list.do";
	}

	@RequiresPermissions("core:node:move:form")
	@RequestMapping("move_input.do")
	public String moveInput(Integer[] ids, Boolean noChecked,
			Integer queryParentId, Boolean showDescendants,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		User user = Context.getCurrentUser(request);
		Integer siteId = Context.getCurrentSiteId(request);
		List<Node> selectedList = query.findByIds(ids);
		// 获得已选择节点的所有父节点ID，用于展开树。
		Set<Integer> selectedPids = new HashSet<Integer>();
		// 获得选中节点的ID
		Set<Integer> selectedIds = new HashSet<Integer>();
		for (Node node : selectedList) {
			selectedIds.add(node.getId());
			Node parent = node.getParent();
			while (parent != null) {
				selectedPids.add(parent.getId());
				parent = parent.getParent();
			}
		}
		List<Node> list = query.findList(siteId, null);
		Boolean allNode = user.getAllNode(siteId);
		modelMap.addAttribute("allNode", allNode);
		if (!allNode) {
			modelMap.addAttribute("nodeRights", user.getNodeRights(siteId));
		}
		modelMap.addAttribute("list", list);
		modelMap.addAttribute("noChecked", noChecked);
		modelMap.addAttribute("selectedList", selectedList);
		modelMap.addAttribute("selectedIds", selectedIds.toArray());
		modelMap.addAttribute("selectedPids", selectedPids.toArray());
		modelMap.addAttribute("queryParentId", queryParentId);
		modelMap.addAttribute("showDescendants", showDescendants);
		return "core/node/node_move";
	}

	@RequiresPermissions("core:node:move:submit")
	@RequestMapping("move_submit.do")
	public String moveSubmit(Integer[] ids, Integer id, Integer queryParentId,
			Boolean showDescendants, HttpServletRequest request,
			RedirectAttributes ra) {
		Integer siteId = Context.getCurrentSiteId(request);
		service.move(ids, id, siteId);
		// bug??
		// ra.addAttribute("ids", ids);
		ra.addAttribute("noChecked", true);
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
		ra.addFlashAttribute("refreshLeft", true);
		return "redirect:move_input.do?ids=" + StringUtils.join(ids, "&ids=");
	}

	@RequiresPermissions("core:node:generate_page")
	@RequestMapping("generate_page.do")
	public String generatePage(Integer[] ids, Integer queryParentId,
			Boolean showDescendants, HttpServletRequest request,
			RedirectAttributes ra) throws IOException, TemplateException {
		List<Node> nodes = query.findByIds(ids);
		if (CollectionUtils.isNotEmpty(nodes)) {
			Node[] nodeArr = nodes.toArray(new Node[nodes.size()]);
			generatePageService.generateNodeWhole(nodeArr);
		}
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, OPERATION_SUCCESS);
		return "redirect:list.do";
	}

	@RequiresPermissions("core:node:delete_page")
	@RequestMapping("delete_page.do")
	public String deletePage(Integer id, Integer queryParentId,
			Boolean showDescendants, HttpServletRequest request,
			RedirectAttributes ra) throws IOException, TemplateException {
		Node node = query.get(id);
		if (node != null) {
			generatePageService.deletePage(node);
		}
		ra.addAttribute("id", id);
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, OPERATION_SUCCESS);
		return "redirect:edit.do";
	}

	@RequiresPermissions("core:node:add_fulltext")
	@RequestMapping("add_fulltext.do")
	public String addFulltext(Integer[] ids, Integer queryParentId,
			Boolean showDescendants, HttpServletRequest request,
			RedirectAttributes ra) throws IOException, TemplateException {
		List<Node> nodes = query.findByIds(ids);
		if (CollectionUtils.isNotEmpty(nodes)) {
			infoFulltextService.addDocumentWhole(nodes);
		}
		ra.addAttribute("queryParentId", queryParentId);
		ra.addAttribute("showDescendants", showDescendants);
		ra.addFlashAttribute(MESSAGE, OPERATION_SUCCESS);
		return "redirect:list.do";
	}

	@ModelAttribute
	public void preloadBean(@RequestParam(required = false) Integer oid,
			org.springframework.ui.Model modelMap) {
		if (oid != null) {
			Node bean = query.get(oid);
			if (bean != null) {
				modelMap.addAttribute("bean", bean);
				NodeDetail detail = bean.getDetail();
				if (detail == null) {
					detail = new NodeDetail();
					detail.setId(oid);
					detail.setNode(bean);
				}
				modelMap.addAttribute("detail", detail);
			}
		}
	}

	@Autowired
	private InfoFulltextService infoFulltextService;
	@Autowired
	private GeneratePageService generatePageService;
	@Autowired
	private WorkflowService workflowService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private NodeService service;
	@Autowired
	private NodeQueryService query;
}

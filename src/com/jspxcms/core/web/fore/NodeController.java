package com.jspxcms.core.web.fore;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspxcms.common.web.Servlets;
import com.jspxcms.core.domain.Node;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.service.NodeBufferService;
import com.jspxcms.core.service.NodeQueryService;
import com.jspxcms.core.support.Context;
import com.jspxcms.core.support.ForeContext;

/**
 * NodeController
 * 
 * @author liufang
 * 
 */
@Controller
public class NodeController {
	@RequestMapping(value = { "/", "/index.jspx" })
	public String index(HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		Site site = Context.getCurrentSite(request);
		Node node = query.findRoot(site.getId());
		modelMap.addAttribute("node", node);

		ForeContext.setData(modelMap.asMap(), request);
		String template = Servlets.getParameter(request, "template");
		if (StringUtils.isNotBlank(template)) {
			return template;
		} else {
			return node.getTemplate();
		}
	}

	@RequestMapping(value = "/node/{nodeId:[0-9]+}_{page:[0-9]+}.jspx")
	public String nodeByPagePath(@PathVariable Integer nodeId,
			@PathVariable Integer page, HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		// Site site = Context.getCurrentSite(request);
		Node node = query.get(nodeId);
		String linkUrl = node.getLinkUrl();
		if (StringUtils.isNotBlank(linkUrl)) {
			return "redirect:" + linkUrl;
		}
		// TODO if(node==null) {...}
		// Site site = node.getSite();
		modelMap.addAttribute("node", node);
		modelMap.addAttribute("text", node.getText());

		Map<String, Object> data = modelMap.asMap();
		ForeContext.setData(data, request);
		ForeContext.setPage(data, page, node);
		String template = Servlets.getParameter(request, "template");
		if (StringUtils.isNotBlank(template)) {
			return template;
		} else {
			return node.getTemplate();
		}
	}

	@RequestMapping(value = "/node/{nodeId:[0-9]+}.jspx")
	public String node(@PathVariable Integer nodeId, Integer page,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		return nodeByPagePath(nodeId, null, request, modelMap);
	}

	@ResponseBody
	@RequestMapping(value = "/node_views/{nodeId:[0-9]+}.jspx")
	public String views(@PathVariable Integer nodeId) {
		return Integer.toString(bufferService.updateViews(nodeId));
	}

	// @Autowired
	// private SiteService siteService;
	@Autowired
	private NodeBufferService bufferService;
	@Autowired
	private NodeQueryService query;
}

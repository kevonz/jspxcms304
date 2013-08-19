package com.jspxcms.core.web.back.f7;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jspxcms.core.domain.Org;
import com.jspxcms.core.service.OrgService;

/**
 * NodeF7Controller
 * 
 * @author liufang
 * 
 */
@Controller
@RequestMapping("/core/org")
public class OrgF7Controller {
	/**
	 * 组织单选。
	 * 
	 * @param id
	 * @param excludeChildrenId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("f7_org_tree.do")
	public String f7OrgTree(Integer id,
			@RequestParam(defaultValue = "true") Boolean allowRoot,
			Integer excludeChildrenId, HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		List<Org> list = service.findList();
		Org bean = null, excludeChildrenBean = null;
		if (id != null) {
			bean = service.get(id);
		}
		if (excludeChildrenId != null) {
			excludeChildrenBean = service.get(excludeChildrenId);
		}

		modelMap.addAttribute("id", id);
		modelMap.addAttribute("allowRoot", allowRoot);
		modelMap.addAttribute("excludeChildrenId", excludeChildrenId);
		modelMap.addAttribute("bean", bean);
		modelMap.addAttribute("excludeChildrenBean", excludeChildrenBean);
		modelMap.addAttribute("list", list);
		return "core/org/f7_org_tree";
	}

	@Autowired
	private OrgService service;
}

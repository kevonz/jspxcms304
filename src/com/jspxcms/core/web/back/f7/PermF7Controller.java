package com.jspxcms.core.web.back.f7;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jspxcms.core.support.MenuHolder;

/**
 * PermF7Controller
 * 
 * @author liufang
 * 
 */
@Controller
@RequestMapping("/core/role")
public class PermF7Controller {

	@RequestMapping("f7_perm_tree.do")
	public String f7NodeTree(HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		modelMap.addAttribute("menus", menuHolder.getMenus());
		return "core/role/f7_perm_tree";
	}

	@Autowired
	private MenuHolder menuHolder;
}

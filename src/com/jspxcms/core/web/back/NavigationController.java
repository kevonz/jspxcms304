package com.jspxcms.core.web.back;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jspxcms.core.support.Context;
import com.jspxcms.core.support.Menu;
import com.jspxcms.core.support.MenuHolder;

/**
 * NavigationController
 * 
 * @author liufang
 * 
 */
@Controller
public class NavigationController {
	@RequestMapping({ "/", "/index.do" })
	public String index(HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			modelMap.addAttribute("menus", menuHolder.getMenus());
			modelMap.addAttribute("user", Context.getCurrentUser(request));
			return "index";
		} else {
			return "login";
		}
	}

	@RequiresPermissions("core:nav:container")
	@RequestMapping("/container.do")
	public String container() {
		return "container";
	}

	@RequiresPermissions("core:nav:nav")
	@RequestMapping("/nav.do")
	public String nav(String menuId, String subId,
			org.springframework.ui.Model modelMap) {
		Set<Menu> menus = menuHolder.getMenus();
		Menu menu = null;
		for (Menu m : menus) {
			if (m.getId().equals(menuId)) {
				if (StringUtils.isNotBlank(m.getPerm())) {
					Subject subject = SecurityUtils.getSubject();
					subject.checkPermission(m.getPerm());
				}
				menu = m;
				break;
			}
		}
		modelMap.addAttribute("menu", menu);
		modelMap.addAttribute("subId", subId);
		return "nav";
	}

	@Autowired
	private MenuHolder menuHolder;

}

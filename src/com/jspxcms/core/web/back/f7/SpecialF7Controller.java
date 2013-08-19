package com.jspxcms.core.web.back.f7;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jspxcms.core.domain.Special;
import com.jspxcms.core.service.SpecialService;

/**
 * SpecialF7Controller
 * 
 * @author liufang
 * 
 */
@Controller
@RequestMapping("/core/special")
public class SpecialF7Controller {
	@RequestMapping("f7_special_multi.do")
	public String f7NodeTreeMulti(Integer[] ids, HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		// Integer siteId = Context.getCurrentSiteId(request);
		List<Special> list = service.findAll(null,
				new PageRequest(0, 2000, Direction.ASC, "id")).getContent();
		List<Special> beans = new ArrayList<Special>();
		if (ids != null) {
			for (Integer id : ids) {
				beans.add(service.get(id));
			}
		}

		modelMap.addAttribute("ids", ids);
		modelMap.addAttribute("beans", beans);
		modelMap.addAttribute("list", list);
		return "core/special/f7_special_multi";
	}

	@Autowired
	private SpecialService service;
}

package com.jspxcms.core.web.back;

import static com.jspxcms.core.support.Constants.CREATE;
import static com.jspxcms.core.support.Constants.EDIT;
import static com.jspxcms.core.support.Constants.OPRT;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jspxcms.core.domain.Site;
import com.jspxcms.core.service.SiteService;

/**
 * SiteController
 * 
 * @author liufang
 * 
 */
@Controller
@RequestMapping("/core/site")
public class SiteController {
	private static final Logger logger = LoggerFactory
			.getLogger(SiteController.class);

	// 列表
	@RequestMapping("list.do")
	public String list(HttpServletRequest request, Pageable pageable,
			org.springframework.ui.Model modelMap) {
		List<Site> list = service.findList();
		modelMap.addAttribute("list", list);
		return "core/site/site_list";
	}

	// 新增
	@RequestMapping(value = "create.do")
	public String create(org.springframework.ui.Model modelMap) {
		modelMap.addAttribute(OPRT, CREATE);
		return "core/site/site_form";
	}

	// 修改
	@RequestMapping(value = "edit.do")
	public String edit(Integer id, HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		Site bean = service.get(id);
		modelMap.addAttribute("bean", bean);
		modelMap.addAttribute(OPRT, EDIT);
		return "core/site/site_form";
	}

	// 保存
	@RequestMapping(value = "save.do", method = RequestMethod.POST)
	public String save(Site bean, HttpServletRequest request,
			RedirectAttributes redirect) {
		service.save(bean);
		logger.info("save Site, name={}.", bean.getName());
		return "redirect:list.do";
	}

	// 更新
	@RequestMapping(value = "update.do", method = RequestMethod.POST)
	public String update(@ModelAttribute("preloadBean") Site bean,
			HttpServletRequest request, RedirectAttributes redirect) {
		service.update(bean);
		logger.info("update Site, name={}.", bean.getName());
		return "redirect:list.do";
	}

	// 删除
	@RequestMapping("/delete.do")
	public String delete(Integer[] ids, HttpServletRequest request,
			RedirectAttributes redirect) {
		Site[] beans = service.delete(ids);
		for (Site bean : beans) {
			logger.info("delete Attribute, name={}.", bean.getName());
		}
		return "redirect:list.do";
	}

	@ModelAttribute("preloadBean")
	public Site preloadBean(@RequestParam(required = false) Integer oid) {
		return oid != null ? service.get(oid) : null;
	}

	@Autowired
	private SiteService service;
}

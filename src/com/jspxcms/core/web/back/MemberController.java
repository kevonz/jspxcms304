package com.jspxcms.core.web.back;

import static com.jspxcms.core.support.Constants.CREATE;
import static com.jspxcms.core.support.Constants.DELETE_SUCCESS;
import static com.jspxcms.core.support.Constants.EDIT;
import static com.jspxcms.core.support.Constants.MESSAGE;
import static com.jspxcms.core.support.Constants.OPERATION_SUCCESS;
import static com.jspxcms.core.support.Constants.OPRT;
import static com.jspxcms.core.support.Constants.SAVE_SUCCESS;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.jspxcms.core.domain.MemberGroup;
import com.jspxcms.core.domain.Role;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.UserDetail;
import com.jspxcms.core.service.MemberGroupService;
import com.jspxcms.core.service.RoleService;
import com.jspxcms.core.service.UserService;
import com.jspxcms.core.support.Constants;
import com.jspxcms.core.support.Context;

/**
 * MemberController
 * 
 * @author liufang
 * 
 */
@Controller
@RequestMapping("/core/member")
public class MemberController {
	private static final Logger logger = LoggerFactory
			.getLogger(MemberController.class);

	@RequiresPermissions("core:member:list")
	@RequestMapping("list.do")
	public String list(
			@PageableDefaults(sort = "id", sortDir = Direction.DESC) Pageable pageable,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		User currUser = Context.getCurrentUser(request);
		Map<String, String[]> params = Servlets.getParameterValuesMap(request,
				Constants.SEARCH_PREFIX);
		Page<User> pagedList = service.findPage(currUser.getRank(), null,
				params, pageable);
		modelMap.addAttribute("pagedList", pagedList);
		return "core/member/member_list";
	}

	@RequiresPermissions("core:member:create")
	@RequestMapping("create.do")
	public String create(Integer id, HttpServletRequest request,
			org.springframework.ui.Model modelMap) {
		Site site = Context.getCurrentSite(request);
		User currUser = Context.getCurrentUser(request);
		if (id != null) {
			User bean = service.get(id);
			modelMap.addAttribute("bean", bean);
		}
		List<Role> roleList = roleService.findList(site.getId());
		modelMap.addAttribute("roleList", roleList);
		List<MemberGroup> groupList = groupService.findList();
		modelMap.addAttribute("groupList", groupList);
		modelMap.addAttribute("currRank", currUser.getRank());
		modelMap.addAttribute(OPRT, CREATE);
		return "core/member/member_form";
	}

	@RequiresPermissions("core:member:edit")
	@RequestMapping("edit.do")
	public String edit(
			Integer id,
			Integer position,
			@PageableDefaults(sort = "id", sortDir = Direction.DESC) Pageable pageable,
			HttpServletRequest request, org.springframework.ui.Model modelMap) {
		Site site = Context.getCurrentSite(request);
		User currUser = Context.getCurrentUser(request);
		User bean = service.get(id);
		Map<String, String[]> params = Servlets.getParameterValuesMap(request,
				Constants.SEARCH_PREFIX);
		RowSide<User> side = service.findSide(currUser.getRank(),
				new Integer[] { User.ADMIN }, params, bean, position,
				pageable.getSort());
		List<Role> roleList = roleService.findList(site.getId());
		modelMap.addAttribute("roleList", roleList);
		List<MemberGroup> groupList = groupService.findList();
		modelMap.addAttribute("currRank", currUser.getRank());
		modelMap.addAttribute("groupList", groupList);
		modelMap.addAttribute("bean", bean);
		modelMap.addAttribute("side", side);
		modelMap.addAttribute("position", position);
		modelMap.addAttribute(OPRT, EDIT);
		return "core/member/member_form";
	}

	@RequiresPermissions("core:member:save")
	@RequestMapping("save.do")
	public String save(User bean, UserDetail detail, Integer[] roleIds,
			Integer orgId, Integer groupId, String redirect,
			HttpServletRequest request, RedirectAttributes ra) {
		User currUser = Context.getCurrentUser(request);
		Integer currRank = currUser.getRank();
		String ip = Servlets.getRemoteAddr(request);
		service.save(bean, detail, roleIds, orgId, groupId, currRank, ip, null);
		logger.info("save Member, username={}.", bean.getUsername());
		ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
		if (Constants.REDIRECT_LIST.equals(redirect)) {
			return "redirect:list.do";
		} else if (Constants.REDIRECT_CREATE.equals(redirect)) {
			return "redirect:create.do";
		} else {
			ra.addAttribute("id", bean.getId());
			return "redirect:edit.do";
		}
	}

	@RequiresPermissions("core:member:update")
	@RequestMapping("update.do")
	public String update(@ModelAttribute("bean") User bean,
			@ModelAttribute("detail") UserDetail detail, Integer[] roleIds,
			Integer orgId, Integer groupId, Integer position, String redirect,
			HttpServletRequest request, RedirectAttributes ra) {
		User currUser = Context.getCurrentUser(request);
		if (ArrayUtils.isEmpty(roleIds)) {
			bean.setType(User.MEMBER);
		}
		service.update(bean, detail, roleIds, orgId, groupId,
				currUser.getRank(), null);
		logger.info("update Member, username={}.", bean.getUsername());
		ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
		if (Constants.REDIRECT_LIST.equals(redirect)) {
			return "redirect:list.do";
		} else {
			ra.addAttribute("id", bean.getId());
			ra.addAttribute("position", position);
			return "redirect:edit.do";
		}
	}

	@RequiresPermissions("core:member:delete")
	@RequestMapping("delete.do")
	public String delete(Integer[] ids, RedirectAttributes ra) {
		User[] beans = service.delete(ids);
		for (User bean : beans) {
			logger.info("delete Member, username={}.", bean.getUsername());
		}
		ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
		return "redirect:list.do";
	}

	// 删除密码
	@RequiresPermissions("core:member:delete_password")
	@RequestMapping("delete_password.do")
	public String deletePassword(Integer[] ids, RedirectAttributes ra) {
		User[] beans = service.deletePassword(ids);
		for (User bean : beans) {
			logger.info("delete Member password, username={}.",
					bean.getUsername());
		}
		ra.addFlashAttribute(MESSAGE, OPERATION_SUCCESS);
		return "redirect:list.do";
	}

	// 审核账户
	@RequiresPermissions("core:member:check")
	@RequestMapping("check.do")
	public String check(Integer[] ids, RedirectAttributes ra) {
		User[] beans = service.check(ids);
		for (User bean : beans) {
			logger.info("check Member, username={}.", bean.getUsername());
		}
		ra.addFlashAttribute(MESSAGE, OPERATION_SUCCESS);
		return "redirect:list.do";
	}

	// 锁定账户
	@RequiresPermissions("core:member:lock")
	@RequestMapping("lock.do")
	public String lock(Integer[] ids, RedirectAttributes ra) {
		User[] beans = service.lock(ids);
		for (User bean : beans) {
			logger.info("disable Member, username={}.", bean.getUsername());
		}
		ra.addFlashAttribute(MESSAGE, OPERATION_SUCCESS);
		return "redirect:list.do";
	}

	// 解锁账户
	@RequiresPermissions("core:member:unlock")
	@RequestMapping("unlock.do")
	public String unlock(Integer[] ids, RedirectAttributes ra) {
		User[] beans = service.unlock(ids);
		for (User bean : beans) {
			logger.info("undisable Member, username={}.", bean.getUsername());
		}
		ra.addFlashAttribute(MESSAGE, OPERATION_SUCCESS);
		return "redirect:list.do";
	}

	/**
	 * 检查用户名是否存在
	 * 
	 * @return
	 */
	@RequestMapping("check_username.do")
	public void checkUsername(String username, String original,
			HttpServletResponse response) {
		if (StringUtils.isBlank(username)) {
			Servlets.writeHtml(response, "false");
			return;
		}
		if (StringUtils.equals(username, original)) {
			Servlets.writeHtml(response, "true");
			return;
		}
		// 检查数据库是否重名
		User user = service.findByUsername(username);
		if (user == null) {
			Servlets.writeHtml(response, "true");
		} else {
			Servlets.writeHtml(response, "false");
		}
	}

	@ModelAttribute
	public void preloadBean(@RequestParam(required = false) Integer oid,
			org.springframework.ui.Model modelMap) {
		if (oid != null) {
			User bean = service.get(oid);
			if (bean != null) {
				modelMap.addAttribute("bean", bean);
				modelMap.addAttribute("detail", bean.getDetail());
			}
		}
	}

	@Autowired
	private MemberGroupService groupService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService service;
}

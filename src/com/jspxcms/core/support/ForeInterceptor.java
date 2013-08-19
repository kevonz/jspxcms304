package com.jspxcms.core.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.security.ShiroUser;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.core.service.UserService;

/**
 * ForeInterceptor
 * 
 * @author liufang
 * 
 */
public class ForeInterceptor implements HandlerInterceptor {
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 站点信息
		Site site = siteService.findUniqueSite();
		if (site == null) {
			throw new IllegalStateException("no site found!");
		}
		Context.setCurrentSite(request, site);

		// 用户登录信息，允许记住用户。
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated() || subject.isRemembered()) {
			ShiroUser shiroUser = (ShiroUser) subject.getPrincipal();
			User user = userService.get(shiroUser.id);
			Context.setCurrentUser(request, user);
		}
		return true;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// if (modelAndView != null) {
		// // 重定向不需要加base参数
		// String viewName = modelAndView.getViewName();
		// if (viewName != null && viewName.startsWith("redirect:")) {
		// return;
		// }
		// modelAndView.getModelMap().addAttribute(Constants.BASE,
		// request.getContextPath());
		// }
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	private UserService userService;
	private SiteService siteService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

}

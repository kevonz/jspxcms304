package com.jspxcms.core.support;

import javax.servlet.ServletRequest;

import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;

/**
 * CMS上下文
 * 
 * @author liufang
 * 
 */
public abstract class Context {
	public static Integer getCurrentSiteId(ServletRequest request) {
		Site site = getCurrentSite(request);
		return site != null ? site.getId() : null;
	}

	public static Site getCurrentSite(ServletRequest request) {
		Site site = (Site) request.getAttribute(SITE_REQUEST_NAME);
		return site;
	}

	public static void setCurrentSite(ServletRequest request, Site site) {
		request.setAttribute(SITE_REQUEST_NAME, site);
	}

	public static void resetCurrentSite(ServletRequest request) {
		request.removeAttribute(SITE_REQUEST_NAME);
	}

	public static Integer getCurrentUserId(ServletRequest request) {
		User user = getCurrentUser(request);
		return user != null ? user.getId() : null;
	}

	public static User getCurrentUser(ServletRequest request) {
		User user = (User) request.getAttribute(USER_REQUEST_NAME);
		return user;
	}

	public static void setCurrentUser(ServletRequest request, User user) {
		request.setAttribute(USER_REQUEST_NAME, user);
	}

	public static void resetCurrentUser(ServletRequest request) {
		request.removeAttribute(USER_REQUEST_NAME);
	}

	/**
	 * 页数线程变量
	 */
	private static ThreadLocal<Site> siteHolder = new ThreadLocal<Site>();

	public static void setCurrentSite(Site site) {
		siteHolder.set(site);
	}

	public static Site getCurrentSite() {
		return siteHolder.get();
	}

	public static void resetCurrentSite() {
		siteHolder.remove();
	}

	private static final String SITE_REQUEST_NAME = "_CMS_SITE_REQUEST";
	private static final String USER_REQUEST_NAME = "_CMS_USER_REQUEST";
}

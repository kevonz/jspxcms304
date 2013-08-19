package com.jspxcms.core.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import com.jspxcms.common.freemarker.Freemarkers;
import com.jspxcms.common.web.PageUrlResolver;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;

import freemarker.core.Environment;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ForeContext
 * 
 * @author liufang
 * 
 */
public abstract class ForeContext {
	/**
	 * 用户资源路径名
	 */
	public static final String FILES = "_files";
	/**
	 * 用户资源路径
	 */
	public static final String FILES_PATH = "/files";
	/**
	 * 前台资源路径名
	 */
	public static final String FORE = "fore";
	/**
	 * 前台资源路径
	 */
	public static final String FORE_PATH = "/fore";
	/**
	 * 站点
	 */
	public static final String SITE = "site";
	/**
	 * 全局
	 */
	public static final String GLOBAL = "global";
	/**
	 * 用户
	 */
	public static final String USER = "user";
	/**
	 * 分页对象
	 */
	public static final String PAGED_LIST = "pagedList";

	public static void setData(Map<String, Object> data, User user, Site site,
			String url) {
		String ctx = site.getContextPath();
		data.put(Constants.CTX, ctx != null ? ctx : "");
		data.put(FILES, site.getFilesPath());
		data.put(FORE, ctx != null ? ctx + FORE_PATH : FORE_PATH);
		data.put(USER, user);
		data.put(SITE, site);
		data.put(GLOBAL, site.getGlobal());
		data.put(Freemarkers.URL, url);
	}

	public static void setData(Map<String, Object> data,
			HttpServletRequest request) {
		String url = getCurrentUrl(request);
		User user = Context.getCurrentUser(request);
		Site site = Context.getCurrentSite(request);
		setData(data, user, site, url);
	}

	public static void setPage(Map<String, Object> data, Integer page,
			PageUrlResolver pageUrlResolver, Page<?> pagedList) {
		if (page == null || page < 1) {
			page = 1;
		}
		data.put(Freemarkers.PAGE, page);
		if (pageUrlResolver != null) {
			data.put(Freemarkers.PAGE_URL_RESOLVER, pageUrlResolver);
		}
		if (pagedList != null) {
			data.put(PAGED_LIST, pagedList);
		}
	}

	public static void setPage(Map<String, Object> data, Integer page,
			PageUrlResolver pageUrlResolver) {
		setPage(data, page, pageUrlResolver, null);
	}

	public static void setPage(Map<String, Object> data, Integer page) {
		setPage(data, page, null, null);
	}

	public static String getCurrentUrl(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		if (StringUtils.isNotBlank(queryString)) {
			uri += "?" + queryString;
		}
		return uri;
	}

	public static Site getSite(Environment env) throws TemplateModelException {
		TemplateModel model = env.getDataModel().get(SITE);
		if (model instanceof AdapterTemplateModel) {
			return (Site) ((AdapterTemplateModel) model)
					.getAdaptedObject(Site.class);
		} else {
			throw new TemplateModelException("\"" + SITE
					+ "\" not found in DataModel");
		}
	}

	public static Integer getSiteId(Environment env)
			throws TemplateModelException {
		return getSite(env).getId();
	}

	/**
	 * 页数线程变量
	 */
	private static ThreadLocal<Integer> totalPagesHolder = new ThreadLocal<Integer>();

	public static void setTotalPages(Integer totalPages) {
		totalPagesHolder.set(totalPages);
	}

	public static Integer getTotalPages() {
		return totalPagesHolder.get();
	}

	public static void resetTotalPages() {
		totalPagesHolder.remove();
	}

}

package com.jspxcms.core.support;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.jspxcms.core.domain.Site;
import com.jspxcms.core.service.SiteShiroService;

/**
 * SiteFilter
 * 
 * @author liufang
 * 
 */
public class SiteFilter implements Filter {
	private SiteShiroService siteShiroService;

	public void setSiteShiroService(SiteShiroService siteShiroService) {
		this.siteShiroService = siteShiroService;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Site site = siteShiroService.findUniqueSite();
		// 站点必须存在
		if (site == null) {
			throw new IllegalStateException("no site found!");
		}
		Context.setCurrentSite(request, site);
		Context.setCurrentSite(site);
		chain.doFilter(request, response);
		Context.resetCurrentSite();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
}

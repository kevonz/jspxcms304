package com.jspxcms.core.service;

import java.util.List;
import java.util.Map;

import com.jspxcms.core.domain.Site;
import com.jspxcms.core.support.Configurable;

/**
 * SiteService
 * 
 * @author liufang
 * 
 */
public interface SiteService {
	public List<Site> findList();

	public Site get(Integer id);

	public Site findUniqueSite();

	public Site save(Site bean);

	public Site update(Site bean);

	public void updateConf(Site site, Configurable conf);

	public void updateCustoms(Site site, String prefix, Map<String, String> map);

	public Site delete(Integer id);

	public Site[] delete(Integer[] ids);
}

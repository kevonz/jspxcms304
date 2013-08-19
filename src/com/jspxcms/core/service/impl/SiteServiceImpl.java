package com.jspxcms.core.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.Global;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.listener.OrgDeleteListener;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.repository.SiteDao;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.core.support.Configurable;
import com.jspxcms.core.support.DeleteException;

/**
 * SiteServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class SiteServiceImpl implements SiteService, OrgDeleteListener {
	public List<Site> findList() {
		List<Site> list = dao.findAll();
		return list;
	}

	public Site findUniqueSite() {
		// Site site = dao.findUniqueSite();
		Site site = dao.findOne(1);
		return site;
	}

	public Site get(Integer id) {
		Site entity = dao.findOne(id);
		return entity;
	}

	@Transactional
	public Site save(Site bean) {
		bean.applyDefaultValue();
		bean = dao.save(bean);
		return bean;
	}

	@Transactional
	public Site update(Site bean) {
		bean.applyDefaultValue();
		bean = dao.save(bean);
		return bean;
	}

	@Transactional
	public void updateConf(Site site, Configurable conf) {
		Map<String, String> customs = site.getCustoms();
		Global.removeAttr(customs, conf.getPrefix());
		customs.putAll(conf.getCustoms());
	}

	@Transactional
	public void updateCustoms(Site site, String prefix, Map<String, String> map) {
		Map<String, String> customs = site.getCustoms();
		Global.removeAttr(customs, prefix);
		customs.putAll(map);
	}

	private Site doDelete(Integer id) {
		Site entity = dao.findOne(id);
		if (entity != null) {
			dao.delete(entity);
		}
		return entity;
	}

	@Transactional
	public Site delete(Integer id) {
		firePreDelete(new Integer[] { id });
		return doDelete(id);
	}

	@Transactional
	public Site[] delete(Integer[] ids) {
		firePreDelete(ids);
		Site[] beans = new Site[ids.length];
		for (int i = 0, len = beans.length; i < len; i++) {
			beans[i] = doDelete(ids[i]);
		}
		return beans;
	}

	public void preOrgDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countByOrgId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("site.management");
			}
		}
	}

	private void firePreDelete(Integer[] ids) {
		if (!CollectionUtils.isEmpty(deleteListeners)) {
			for (SiteDeleteListener listener : deleteListeners) {
				listener.preSiteDelete(ids);
			}
		}
	}

	private List<SiteDeleteListener> deleteListeners;

	@Autowired(required = false)
	public void setDeleteListeners(List<SiteDeleteListener> deleteListeners) {
		this.deleteListeners = deleteListeners;
	}

	private SiteDao dao;

	@Autowired
	public void setSiteDao(SiteDao dao) {
		this.dao = dao;
	}
}

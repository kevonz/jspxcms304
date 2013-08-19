package com.jspxcms.core.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.jspxcms.core.domain.Site;
import com.jspxcms.core.repository.SiteDaoPlus;

/**
 * SiteDaoImpl
 * 
 * @author liufang
 * 
 */
public class SiteDaoImpl implements SiteDaoPlus {
	public Site findUniqueSite() {
		String hql = "from Site bean order by bean.id asc";
		List<Site> list = em.createQuery(hql, Site.class).setMaxResults(1)
				.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	private EntityManager em;

	@javax.persistence.PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}
}

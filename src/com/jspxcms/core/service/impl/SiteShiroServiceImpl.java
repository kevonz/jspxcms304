package com.jspxcms.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.Site;
import com.jspxcms.core.repository.SiteDao;
import com.jspxcms.core.service.SiteShiroService;

/**
 * SiteServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class SiteShiroServiceImpl implements SiteShiroService {
	public Site findUniqueSite() {
		// Site site = dao.findUniqueSite();
		Site site = dao.findOne(1);
		return site;
	}

	private SiteDao dao;

	@Autowired
	public void setSiteDao(SiteDao dao) {
		this.dao = dao;
	}
}

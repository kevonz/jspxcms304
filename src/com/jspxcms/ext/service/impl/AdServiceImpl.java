package com.jspxcms.ext.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.common.orm.SearchFilter;
import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.core.support.DeleteException;
import com.jspxcms.ext.domain.Ad;
import com.jspxcms.ext.domain.AdSlot;
import com.jspxcms.ext.listener.AdSlotDeleteListener;
import com.jspxcms.ext.repository.AdDao;
import com.jspxcms.ext.service.AdService;
import com.jspxcms.ext.service.AdSlotService;

@Service
@Transactional(readOnly = true)
public class AdServiceImpl implements AdService, SiteDeleteListener,
		AdSlotDeleteListener {
	public List<Ad> findList(Map<String, String[]> params, Sort sort) {
		return dao.findAll(spec(params), sort);
	}

	public RowSide<Ad> findSide(Map<String, String[]> params, Ad bean,
			Integer position, Sort sort) {
		if (position == null) {
			return new RowSide<Ad>();
		}
		Limitable limit = RowSide.limitable(position, sort);
		List<Ad> list = dao.findAll(spec(params), limit);
		return RowSide.create(list, bean);
	}

	private Specification<Ad> spec(Map<String, String[]> params) {
		Collection<SearchFilter> filters = SearchFilter.parse(params).values();
		Specification<Ad> sp = SearchFilter.spec(filters, Ad.class);
		return sp;
	}

	public Ad get(Integer id) {
		return dao.findOne(id);
	}

	@Transactional
	public Ad save(Ad bean, Integer slotId, Integer siteId) {
		Site site = siteService.get(siteId);
		bean.setSite(site);
		AdSlot slot = slotService.get(slotId);
		bean.setSlot(slot);
		bean.applyDefaultValue();
		bean = dao.save(bean);
		return bean;
	}

	@Transactional
	public Ad update(Ad bean, Integer slotId) {
		AdSlot slot = slotService.get(slotId);
		bean.setSlot(slot);
		bean.applyDefaultValue();
		bean = dao.save(bean);
		return bean;
	}

	@Transactional
	public Ad delete(Integer id) {
		Ad entity = dao.findOne(id);
		dao.delete(entity);
		return entity;
	}

	@Transactional
	public Ad[] delete(Integer[] ids) {
		Ad[] beans = new Ad[ids.length];
		for (int i = 0; i < ids.length; i++) {
			beans[i] = delete(ids[i]);
		}
		return beans;
	}

	public void preSiteDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countBySiteId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("ad.management");
			}
		}
	}

	public void preAdSlotDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countBySlotId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("ad.management");
			}
		}
	}

	private AdSlotService slotService;
	private SiteService siteService;

	@Autowired
	public void setSlotService(AdSlotService slotService) {
		this.slotService = slotService;
	}

	@Autowired
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private AdDao dao;

	@Autowired
	public void setDao(AdDao dao) {
		this.dao = dao;
	}
}

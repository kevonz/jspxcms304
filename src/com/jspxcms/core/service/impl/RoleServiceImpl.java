package com.jspxcms.core.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.common.orm.SearchFilter;
import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Role;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.listener.RoleDeleteListener;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.repository.RoleDao;
import com.jspxcms.core.service.RoleService;
import com.jspxcms.core.service.RoleSiteService;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.core.support.DeleteException;

/**
 * RoleServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService, SiteDeleteListener {
	public List<Role> findList(Map<String, String[]> params, Sort sort) {
		return dao.findAll(spec(params), sort);
	}

	public RowSide<Role> findSide(Map<String, String[]> params, Role bean,
			Integer position, Sort sort) {
		if (position == null) {
			return new RowSide<Role>();
		}
		Limitable limit = RowSide.limitable(position, sort);
		List<Role> list = dao.findAll(spec(params), limit);
		return RowSide.create(list, bean);
	}

	private Specification<Role> spec(Map<String, String[]> params) {
		Collection<SearchFilter> filters = SearchFilter.parse(params).values();
		Specification<Role> sp = SearchFilter.spec(filters, Role.class);
		return sp;
	}

	public List<Role> findList(Integer siteId) {
		return dao.findBySiteId(siteId);
	}

	public Role get(Integer id) {
		return dao.findOne(id);
	}

	@Transactional
	public Role save(Role bean, Boolean allPerm, String perms, Boolean allInfo,
			Integer[] infoRightIds, Boolean allNode, Integer[] nodeRightIds,
			Integer infoRightType, Integer siteId) {
		Site site = siteService.get(siteId);
		bean.setSite(site);
		bean.applyDefaultValue();
		bean = dao.save(bean);
		roleSiteService.save(bean, site, allPerm, perms, allInfo, infoRightIds,
				allNode, nodeRightIds, infoRightType);
		return bean;
	}

	@Transactional
	public Role update(Role bean, Boolean allPerm, String perms,
			Boolean allInfo, Integer[] infoRightIds, Boolean allNode,
			Integer[] nodeRightIds, Integer infoRightType) {
		Site site = bean.getSite();
		bean.applyDefaultValue();
		bean = dao.save(bean);
		roleSiteService.update(bean, site, allPerm, perms, allInfo,
				infoRightIds, allNode, nodeRightIds, infoRightType);
		return bean;
	}

	private Role doDelete(Integer id) {
		Role entity = dao.findOne(id);
		if (entity != null) {
			dao.delete(entity);
		}
		return entity;
	}

	@Transactional
	public Role delete(Integer id) {
		firePreDelete(new Integer[] { id });
		return doDelete(id);
	}

	@Transactional
	public Role[] delete(Integer[] ids) {
		firePreDelete(ids);
		Role[] beans = new Role[ids.length];
		for (int i = 0; i < ids.length; i++) {
			beans[i] = doDelete(ids[i]);
		}
		return beans;
	}

	public void preSiteDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countBySiteId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("role.management");
			}
		}
	}

	private void firePreDelete(Integer[] ids) {
		if (!CollectionUtils.isEmpty(deleteListeners)) {
			for (RoleDeleteListener listener : deleteListeners) {
				listener.preRoleDelete(ids);
			}
		}
	}

	private List<RoleDeleteListener> deleteListeners;

	@Autowired(required = false)
	public void setDeleteListeners(List<RoleDeleteListener> deleteListeners) {
		this.deleteListeners = deleteListeners;
	}

	private RoleSiteService roleSiteService;
	private SiteService siteService;

	@Autowired
	public void setRoleSiteService(RoleSiteService roleSiteService) {
		this.roleSiteService = roleSiteService;
	}

	@Autowired
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private RoleDao dao;

	@Autowired
	public void setDao(RoleDao dao) {
		this.dao = dao;
	}
}

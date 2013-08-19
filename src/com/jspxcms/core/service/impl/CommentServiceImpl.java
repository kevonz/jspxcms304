package com.jspxcms.core.service.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.common.orm.SearchFilter;
import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Comment;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.listener.UserDeleteListener;
import com.jspxcms.core.repository.CommentDao;
import com.jspxcms.core.service.CommentService;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.core.service.UserService;
import com.jspxcms.core.support.Commentable;

/**
 * CommentServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService, SiteDeleteListener,
		UserDeleteListener {
	public Page<Comment> findAll(Map<String, String[]> params, Pageable pageable) {
		return dao.findAll(spec(params), pageable);
	}

	public RowSide<Comment> findSide(Map<String, String[]> params,
			Comment bean, Integer position, Sort sort) {
		if (position == null) {
			return new RowSide<Comment>();
		}
		Limitable limit = RowSide.limitable(position, sort);
		List<Comment> list = dao.findAll(spec(params), limit);
		return RowSide.create(list, bean);
	}

	private Specification<Comment> spec(Map<String, String[]> params) {
		Collection<SearchFilter> filters = SearchFilter.parse(params).values();
		Specification<Comment> sp = SearchFilter.spec(filters, Comment.class);
		return sp;
	}

	public List<Comment> findList(String ftype, Integer fid, Integer[] status,
			Integer[] siteId, Limitable limitable) {
		return dao.findList(ftype, fid, status, siteId, limitable);
	}

	public Page<Comment> findPage(String ftype, Integer fid, Integer[] status,
			Integer[] siteId, Pageable pageable) {
		return dao.findPage(ftype, fid, status, siteId, pageable);
	}

	public Object getEntity(String entityName, Serializable id) {
		return dao.getEntity(entityName, id);
	}

	public Comment get(Integer id) {
		return dao.findOne(id);
	}

	@Transactional
	public Comment save(Comment bean, Integer userId, Integer siteId) {
		Site site = siteService.get(siteId);
		bean.setSite(site);
		User user = userService.get(userId);
		bean.setCreator(user);
		bean.applyDefaultValue();
		bean = dao.save(bean);
		dao.flushAndRefresh(bean);
		if (bean.getStatus() == Comment.AUDITED) {
			Object anchor = bean.getAnchor();
			if (anchor instanceof Commentable) {
				((Commentable) anchor).addComments(1);
			}
		}
		return bean;
	}

	@Transactional
	public Comment update(Comment bean) {
		bean.applyDefaultValue();
		bean = dao.save(bean);
		return bean;
	}

	@Transactional
	public Comment delete(Integer id) {
		Comment entity = dao.findOne(id);
		dao.delete(entity);
		return entity;
	}

	@Transactional
	public Comment[] delete(Integer[] ids) {
		Comment[] beans = new Comment[ids.length];
		for (int i = 0; i < ids.length; i++) {
			beans[i] = delete(ids[i]);
		}
		return beans;
	}

	@Transactional
	public Comment[] audit(Integer[] ids) {
		Comment[] beans = new Comment[ids.length];
		Comment bean;
		for (int i = 0; i < ids.length; i++) {
			bean = get(ids[i]);
			beans[i] = bean;
			if (bean.getStatus() == Comment.SAVED) {
				bean.setStatus(Comment.AUDITED);
				Object anchor = bean.getAnchor();
				if (anchor instanceof Commentable) {
					((Commentable) anchor).addComments(1);
				}
			}
		}
		return beans;
	}

	@Transactional
	public Comment[] unaudit(Integer[] ids) {
		Comment[] beans = new Comment[ids.length];
		Comment bean;
		for (int i = 0; i < ids.length; i++) {
			bean = get(ids[i]);
			beans[i] = get(ids[i]);
			if (bean.getStatus() == Comment.AUDITED) {
				bean.setStatus(Comment.SAVED);
				Object anchor = bean.getAnchor();
				if (anchor instanceof Commentable) {
					((Commentable) anchor).addComments(-1);
				}
			}
		}
		return beans;
	}

	public void preSiteDelete(Integer[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}
		dao.deleteBySiteId(Arrays.asList(ids));
	}

	public void preUserDelete(Integer[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}
		dao.deleteByCreatorId(Arrays.asList(ids));
		dao.deleteByAuditorId(Arrays.asList(ids));
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

	private CommentDao dao;

	@Autowired
	public void setDao(CommentDao dao) {
		this.dao = dao;
	}
}

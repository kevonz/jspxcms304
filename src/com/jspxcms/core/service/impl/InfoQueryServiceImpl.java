package com.jspxcms.core.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
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
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.RoleSite;
import com.jspxcms.core.domain.WorkflowProcess;
import com.jspxcms.core.repository.InfoDao;
import com.jspxcms.core.service.InfoQueryService;

/**
 * 信息Service实现
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class InfoQueryServiceImpl implements InfoQueryService {
	public Page<Info> findAll(Integer siteId, Integer mainNodeId,
			Integer nodeId, String treeNumber, Integer userId, boolean allInfo,
			int infoRightType, String status, Map<String, String[]> params,
			Pageable pageable) {
		return dao.findAll(
				spec(siteId, mainNodeId, nodeId, treeNumber, userId, allInfo,
						infoRightType, status, params), pageable);
	}

	public RowSide<Info> findSide(Integer siteId, Integer mainNodeId,
			Integer nodeId, String treeNumber, Integer userId, boolean allInfo,
			int infoRightType, String status, Map<String, String[]> params,
			Info bean, Integer position, Sort sort) {
		if (position == null) {
			return new RowSide<Info>();
		}
		Limitable limit = RowSide.limitable(position, sort);
		List<Info> list = dao.findAll(
				spec(siteId, mainNodeId, nodeId, treeNumber, userId, allInfo,
						infoRightType, status, params), limit);
		return RowSide.create(list, bean);
	}

	private Specification<Info> spec(final Integer siteId,
			final Integer mainNodeId, final Integer nodeId,
			final String treeNumber, final Integer userId,
			final boolean allInfo, final int infoRightType,
			final String status, Map<String, String[]> params) {
		Collection<SearchFilter> filters = SearchFilter.parse(params).values();
		final Specification<Info> fs = SearchFilter.spec(filters, Info.class);
		Specification<Info> sp = new Specification<Info>() {
			public Predicate toPredicate(Root<Info> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate pred = fs.toPredicate(root, query, cb);
				if (mainNodeId != null) {
					pred = cb.and(pred,
							cb.equal(root.get("node").get("id"), mainNodeId));
				} else if (nodeId != null) {
					pred = cb.and(
							pred,
							cb.equal(
									root.join("infoNodes").join("node")
											.get("id"), nodeId));
				} else if (StringUtils.isNotBlank(treeNumber)) {
					pred = cb.and(pred, cb.like(
							root.get("node").<String> get("treeNumber"),
							treeNumber + "%"));
				} else if (siteId != null) {
					pred = cb.and(pred,
							cb.equal(root.get("site").get("id"), siteId));
				}
				if (!allInfo) {
					Path<Integer> userPath = root.join("node")
							.join("infoRoleSites").join("role").join("users")
							.<Integer> get("id");
					pred = cb.and(pred, cb.equal(userPath, userId));
					query.distinct(true);
				}
				if (infoRightType == RoleSite.INFO_RIGHT_SELF) {
					pred = cb.and(pred, cb.equal(root.get("creator")
							.<Integer> get("id"), userId));
				}
				if (StringUtils.isNotBlank(status)) {
					if (status.length() == 1) {
						pred = cb.and(pred,
								cb.equal(root.get("status"), status));
					} else if (status.equals("auditing")) {
						pred = cb.and(pred, cb.between(
								root.<String> get("status"), "1", "9"));
					} else if (status.equals("pending")
							|| status.equals("notpassed")) {
						boolean rejection = false;
						if (status.equals("notpassed")) {
							rejection = true;
						}
						Subquery<Integer> sq = query.subquery(Integer.class);
						Root<WorkflowProcess> root2 = sq
								.from(WorkflowProcess.class);
						sq.select(root2.<Integer> get("dataId"));
						sq.where(
								cb.equal(root2.get("rejection"), rejection),
								cb.equal(root2.get("type"), 1),
								cb.equal(root2.get("end"), false),
								cb.equal(root2.join("processUsers")
										.join("user").get("id"), userId));
						pred = cb.and(pred, cb.in(root.<Integer> get("id"))
								.value(sq));
					}
				}
				return pred;
			}
		};
		return sp;
	}

	public List<Info> findAll(Iterable<Integer> ids) {
		return dao.findAll(ids);
	}

	public List<Info> findList(Integer[] nodeId, Integer[] attrId,
			Integer[] specialId, Integer[] tagId, Integer[] siteId,
			Integer[] mainNodeId, Integer[] userId, String[] treeNumber,
			String[] specialTitle, String[] tagName, Integer[] priority,
			Date startDate, Date endDate, String[] title, Integer[] includeId,
			Integer[] excludeId, Integer[] excludeMainNodeId,
			String[] excludeTreeNumber, Boolean isWithImage, String[] status,
			Limitable limitable) {
		return dao.findList(nodeId, attrId, specialId, tagId, siteId,
				mainNodeId, userId, treeNumber, specialTitle, tagName,
				priority, startDate, endDate, title, includeId, excludeId,
				excludeMainNodeId, excludeTreeNumber, isWithImage, status,
				limitable);
	}

	public Page<Info> findPage(Integer[] nodeId, Integer[] attrId,
			Integer[] specialId, Integer[] tagId, Integer[] siteId,
			Integer[] mainNodeId, Integer[] userId, String[] treeNumber,
			String[] specialTitle, String[] tagName, Integer[] priority,
			Date startDate, Date endDate, String[] title, Integer[] includeId,
			Integer[] excludeId, Integer[] excludeMainNodeId,
			String[] excludeTreeNumber, Boolean isWithImage, String[] status,
			Pageable pageable) {
		return dao.findPage(nodeId, attrId, specialId, tagId, siteId,
				mainNodeId, userId, treeNumber, specialTitle, tagName,
				priority, startDate, endDate, title, includeId, excludeId,
				excludeMainNodeId, excludeTreeNumber, isWithImage, status,
				pageable);
	}

	public Info findNext(Integer id, boolean bySite) {
		Info info = get(id);
		if (info != null) {
			Integer siteId = bySite ? info.getSite().getId() : null;
			Integer nodeId = bySite ? null : info.getNode().getId();
			return dao.findNext(siteId, nodeId, id, info.getPublishDate());
		} else {
			return null;
		}
	}

	public Info findPrev(Integer id, boolean bySite) {
		Info info = get(id);
		if (info != null) {
			Integer siteId = bySite ? info.getSite().getId() : null;
			Integer nodeId = bySite ? null : info.getNode().getId();
			return dao.findPrev(siteId, nodeId, id, info.getPublishDate());
		} else {
			return null;
		}
	}

	public Info get(Integer id) {
		return dao.findOne(id);
	}

	private InfoDao dao;

	@Autowired
	public void setDao(InfoDao dao) {
		this.dao = dao;
	}
}

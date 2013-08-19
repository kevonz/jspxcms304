package com.jspxcms.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.common.orm.LimitRequest;
import com.jspxcms.common.orm.Limitable;
import com.jspxcms.common.orm.SearchFilter;
import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Node;
import com.jspxcms.core.repository.NodeDao;
import com.jspxcms.core.service.NodeQueryService;

/**
 * NodeQueryServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class NodeQueryServiceImpl implements NodeQueryService {
	public List<Node> findList(Integer parentId, Boolean showDescendants,
			Integer userId, boolean allNode, Map<String, String[]> params,
			Sort sort) {
		List<Node> list;
		if (showDescendants != null && showDescendants) {
			Node parent = get(parentId);
			String treeNumber = parent.getTreeNumber();
			list = dao.findAll(spec(null, treeNumber, userId, allNode, params),
					sort);
		} else {
			list = dao.findAll(spec(parentId, null, userId, allNode, params),
					sort);
		}
		return list;
	}

	public RowSide<Node> findSide(Integer parentId, Boolean showDescendants,
			Integer userId, boolean allNode, Map<String, String[]> params,
			Node bean, Integer position, Sort sort) {
		if (position == null) {
			return new RowSide<Node>();
		}
		Limitable limit = RowSide.limitable(position, sort);
		List<Node> list;
		if (showDescendants != null && showDescendants) {
			String treeNumber = null;
			if (parentId != null) {
				Node parent = get(parentId);
				treeNumber = parent.getTreeNumber();
			}
			list = dao.findAll(spec(null, treeNumber, userId, allNode, params),
					limit);
		} else {
			if (parentId != null) {
				list = dao.findAll(
						spec(parentId, null, userId, allNode, params), limit);
			} else {
				list = Collections.emptyList();
			}
		}
		return RowSide.create(list, bean);
	}

	private Specification<Node> spec(final Integer parentId,
			final String treeNumber, final Integer userId,
			final boolean allNode, Map<String, String[]> params) {
		Collection<SearchFilter> filters = SearchFilter.parse(params).values();
		final Specification<Node> fs = SearchFilter.spec(filters, Node.class);
		Specification<Node> sp = new Specification<Node>() {
			public Predicate toPredicate(Root<Node> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate pred = fs.toPredicate(root, query, cb);
				if (parentId != null) {
					pred = cb.and(pred, cb.equal(root.get("parent")
							.<Integer> get("id"), parentId));
				}
				if (StringUtils.isNotBlank(treeNumber)) {
					Path<String> tnPath = root.<String> get("treeNumber");
					pred = cb.and(pred, cb.like(tnPath, treeNumber + "%"));
				}
				if (!allNode) {
					Path<Integer> userPath = root.join("nodeRoleSites")
							.join("role").join("users").<Integer> get("id");
					pred = cb.and(pred, cb.equal(userPath, userId));
					query.distinct(true);
				}
				return pred;
			}
		};
		return sp;
	}

	public List<Node> findList(Integer[] siteId, Integer parentId,
			String treeNumber, Boolean isRealNode, Boolean isHidden,
			Limitable limitable) {
		return dao.findList(siteId, parentId, treeNumber, isRealNode, isHidden,
				limitable);
	}

	public Page<Node> findPage(Integer[] siteId, Integer parentId,
			String treeNumber, Boolean isRealNode, Boolean isHidden,
			Pageable pageable) {
		return dao.findPage(siteId, parentId, treeNumber, isRealNode, isHidden,
				pageable);
	}

	public List<Node> findByIds(Integer... ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return dao.findAll(Arrays.asList(ids));
	}

	public List<Node> findByIds(Integer[] ids, Integer selfId) {
		List<Node> list = new ArrayList<Node>();
		if (!ArrayUtils.isEmpty(ids)) {
			Set<Integer> idSet = new HashSet<Integer>();
			for (Integer id : ids) {
				if (!idSet.contains(id) && !id.equals(selfId)) {
					idSet.add(id);
					list.add(get(id));
				}
			}
		}
		list.add(get(selfId));
		return list;
	}

	public List<Node> findList(Integer siteId, Integer parentId) {
		return findList(siteId, parentId, null, null);
	}

	public List<Node> findList(Integer siteId, Integer parentId,
			Boolean isRealNode, Boolean isHidden) {
		String treeNumber = null;
		if (parentId != null) {
			Node node = get(parentId);
			if (node != null) {
				treeNumber = node.getTreeNumber();
			}
		}
		Sort sort = new Sort("treeNumber");
		Limitable limitable = new LimitRequest(null, null, sort);
		return dao.findList(new Integer[] { siteId }, null, treeNumber,
				isRealNode, isHidden, limitable);
	}

	public List<Node> findChildren(Integer parentId) {
		return findChildren(parentId, null, null, null, null);
	}

	public List<Node> findChildren(Integer parentId, Boolean isRealNode,
			Boolean isHidden, Integer offset, Integer limit) {
		if (parentId == null) {
			return Collections.emptyList();
		}
		Sort sort = new Sort("treeNumber");
		Limitable limitable = new LimitRequest(offset, limit, sort);
		return dao.findList(null, parentId, null, isRealNode, isHidden,
				limitable);
	}

	public Node findRoot(Integer siteId) {
		List<Node> list = dao.findBySiteIdAndParentIdIsNull(siteId);
		return !list.isEmpty() ? list.get(0) : null;
	}

	public Node findByNumber(Integer siteId, String number) {
		List<Node> list = dao.findBySiteIdAndNumber(siteId, number);
		return !list.isEmpty() ? list.get(0) : null;
	}

	public List<Node> findByNumber(String[] numbers) {
		List<Node> list = dao.findByNumbers(numbers);
		return list;
	}

	public List<Node> findByNumberLike(String[] numbers) {
		List<Node> list = dao.findByNumbersLike(numbers);
		return list;
	}

	public Node get(Integer id) {
		return dao.findOne(id);
	}

	private NodeDao dao;

	@Autowired
	public void setDao(NodeDao dao) {
		this.dao = dao;
	}
}

package com.jspxcms.core.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.Node;
import com.jspxcms.core.domain.RoleNodeNode;
import com.jspxcms.core.domain.RoleSite;
import com.jspxcms.core.repository.RoleNodeNodeDao;
import com.jspxcms.core.service.NodeQueryService;
import com.jspxcms.core.service.RoleNodeNodeService;

@Service
@Transactional(readOnly = true)
public class RoleNodeNodeServiceImpl implements RoleNodeNodeService {
	@Transactional
	public Set<RoleNodeNode> save(RoleSite roleSite, Integer[] nodeIds) {
		int len = ArrayUtils.getLength(nodeIds);
		Set<RoleNodeNode> roleNodeNodes = new HashSet<RoleNodeNode>(len);
		roleSite.setRoleNodeNodes(roleNodeNodes);
		if (len > 0) {
			Node node;
			RoleNodeNode rnn;
			for (Integer nodeId : nodeIds) {
				node = nodeQueryService.get(nodeId);
				rnn = new RoleNodeNode(roleSite, node);
				dao.save(rnn);
				// 需要往Node里面add对象吗？node.addRoleNodeNode()??
				roleNodeNodes.add(rnn);
			}
		}
		return roleNodeNodes;
	}

	@Transactional
	public Set<RoleNodeNode> update(RoleSite roleSite, Integer[] nodeIds) {
		dao.deleteByRoleSiteId(roleSite.getId());
		Set<RoleNodeNode> roleNodeNodes = save(roleSite, nodeIds);
		return roleNodeNodes;
	}

	private NodeQueryService nodeQueryService;

	@Autowired
	public void setNodeQueryService(NodeQueryService nodeQueryService) {
		this.nodeQueryService = nodeQueryService;
	}

	private RoleNodeNodeDao dao;

	@Autowired
	public void setDao(RoleNodeNodeDao dao) {
		this.dao = dao;
	}
}

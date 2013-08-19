package com.jspxcms.core.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.Node;
import com.jspxcms.core.domain.RoleNodeInfo;
import com.jspxcms.core.domain.RoleSite;
import com.jspxcms.core.repository.RoleNodeInfoDao;
import com.jspxcms.core.service.NodeQueryService;
import com.jspxcms.core.service.RoleNodeInfoService;

@Service
@Transactional(readOnly = true)
public class RoleNodeInfoServiceImpl implements RoleNodeInfoService {
	@Transactional
	public Set<RoleNodeInfo> save(RoleSite roleSite, Integer[] nodeIds) {
		int len = ArrayUtils.getLength(nodeIds);
		Set<RoleNodeInfo> roleNodeInfos = new HashSet<RoleNodeInfo>(len);
		roleSite.setRoleNodeInfos(roleNodeInfos);
		if (len > 0) {
			Node node;
			RoleNodeInfo rni;
			for (Integer nodeId : nodeIds) {
				node = nodeQueryService.get(nodeId);
				rni = new RoleNodeInfo(roleSite, node);
				dao.save(rni);
				// 需要往Node里面add对象吗？node.addRoleNodeInfo()??
				roleNodeInfos.add(rni);
			}
		}
		return roleNodeInfos;
	}

	@Transactional
	public Set<RoleNodeInfo> update(RoleSite roleSite, Integer[] nodeIds) {
		dao.deleteByRoleSiteId(roleSite.getId());
		Set<RoleNodeInfo> roleNodeInfos = save(roleSite, nodeIds);
		return roleNodeInfos;
	}

	private NodeQueryService nodeQueryService;

	@Autowired
	public void setNodeQueryService(NodeQueryService nodeQueryService) {
		this.nodeQueryService = nodeQueryService;
	}

	private RoleNodeInfoDao dao;

	@Autowired
	public void setDao(RoleNodeInfoDao dao) {
		this.dao = dao;
	}
}

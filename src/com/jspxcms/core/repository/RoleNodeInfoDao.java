package com.jspxcms.core.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.core.domain.RoleNodeInfo;

public interface RoleNodeInfoDao extends Repository<RoleNodeInfo, Integer> {
	public RoleNodeInfo findOne(Integer id);

	public RoleNodeInfo save(RoleNodeInfo bean);

	// --------------------

	@Modifying
	@Query("delete from RoleNodeInfo bean where bean.roleSite.id=?1")
	public int deleteByRoleSiteId(Integer roleSiteId);

	@Modifying
	@Query("delete from RoleNodeInfo bean where bean.node.id=?1")
	public int deleteByNodeId(Integer nodeId);
}

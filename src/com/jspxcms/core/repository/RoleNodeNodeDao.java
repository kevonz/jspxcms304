package com.jspxcms.core.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.core.domain.RoleNodeNode;

public interface RoleNodeNodeDao extends Repository<RoleNodeNode, Integer> {

	public RoleNodeNode findOne(Integer id);

	public RoleNodeNode save(RoleNodeNode bean);

	// --------------------

	@Modifying
	@Query("delete from RoleNodeNode bean where bean.roleSite.id=?1")
	public int deleteByRoleSiteId(Integer roleSiteId);

	@Modifying
	@Query("delete from RoleNodeNode bean where bean.node.id=?1")
	public int deleteByNodeId(Integer nodeId);
}

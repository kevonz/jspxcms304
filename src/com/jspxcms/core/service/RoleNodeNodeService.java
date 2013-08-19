package com.jspxcms.core.service;

import java.util.Set;

import com.jspxcms.core.domain.RoleNodeNode;
import com.jspxcms.core.domain.RoleSite;

public interface RoleNodeNodeService {
	public Set<RoleNodeNode> save(RoleSite roleSite, Integer[] nodeIds);

	public Set<RoleNodeNode> update(RoleSite roleSite, Integer[] nodeIds);
}

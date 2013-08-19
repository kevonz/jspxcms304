package com.jspxcms.core.service;

import java.util.Set;

import com.jspxcms.core.domain.RoleNodeInfo;
import com.jspxcms.core.domain.RoleSite;

public interface RoleNodeInfoService {
	public Set<RoleNodeInfo> save(RoleSite roleSite, Integer[] nodeIds);

	public Set<RoleNodeInfo> update(RoleSite roleSite, Integer[] nodeIds);
}

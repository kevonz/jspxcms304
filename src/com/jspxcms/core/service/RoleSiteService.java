package com.jspxcms.core.service;

import com.jspxcms.core.domain.Role;
import com.jspxcms.core.domain.RoleSite;
import com.jspxcms.core.domain.Site;

/**
 * RoleSiteService
 * 
 * @author liufang
 * 
 */
public interface RoleSiteService {
	public RoleSite get(Integer id);

	public RoleSite save(Role role, Site site, Boolean allPerm, String perms,
			Boolean allInfo, Integer[] infoRightIds, Boolean allNode,
			Integer[] nodeRightIds, Integer infoRightType);

	public RoleSite update(Role role, Site site, Boolean allPerm, String perms,
			Boolean allInfo, Integer[] infoRightIds, Boolean allNode,
			Integer[] nodeRightIds, Integer infoRightType);

	public RoleSite delete(Integer id);

	public RoleSite[] delete(Integer[] ids);
}

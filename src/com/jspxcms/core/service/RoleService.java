package com.jspxcms.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;

import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Role;

/**
 * RoleService
 * 
 * @author liufang
 * 
 */
public interface RoleService {
	public List<Role> findList(Map<String, String[]> params, Sort sort);

	public RowSide<Role> findSide(Map<String, String[]> params, Role bean,
			Integer position, Sort sort);

	public List<Role> findList(Integer siteId);

	public Role get(Integer id);

	public Role save(Role bean, Boolean allPerm, String perms, Boolean allInfo,
			Integer[] infoRightIds, Boolean allNode, Integer[] nodeRightIds,
			Integer infoRightType, Integer siteId);

	public Role update(Role bean, Boolean allPerm, String perms,
			Boolean allInfo, Integer[] infoRightIds, Boolean allNode,
			Integer[] nodeRightIds, Integer infoRightType);

	public Role delete(Integer id);

	public Role[] delete(Integer[] ids);
}

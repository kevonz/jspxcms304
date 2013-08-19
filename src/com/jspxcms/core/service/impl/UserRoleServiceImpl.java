package com.jspxcms.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.Role;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.UserRole;
import com.jspxcms.core.repository.UserRoleDao;
import com.jspxcms.core.service.RoleService;
import com.jspxcms.core.service.UserRoleService;

@Service
@Transactional(readOnly = true)
public class UserRoleServiceImpl implements UserRoleService {
	@Transactional
	public List<UserRole> save(User user, Integer[] roleIds) {
		int len = ArrayUtils.getLength(roleIds);
		List<UserRole> userRoles = new ArrayList<UserRole>(len);
		user.setUserRoles(userRoles);
		if (len > 0) {
			UserRole userRole;
			Role role;
			for (Integer roleId : roleIds) {
				role = roleService.get(roleId);
				userRole = new UserRole(user, role);
				dao.save(userRole);
				role.addUserRole(userRole);
				userRoles.add(userRole);
			}
		}
		return userRoles;
	}

	@Transactional
	public List<UserRole> update(User user, Integer[] roleIds) {
		dao.deleteByUserId(user.getId());
		List<UserRole> userRole = save(user, roleIds);
		return userRole;
	}

	@Transactional
	public int deleteByUserId(Integer userId) {
		return dao.deleteByUserId(userId);
	}

	@Transactional
	public int deleteByRoleId(Integer roleId) {
		return dao.deleteByRoleId(roleId);
	}

	private RoleService roleService;

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	private UserRoleDao dao;

	@Autowired
	public void setDao(UserRoleDao dao) {
		this.dao = dao;
	}
}

package com.jspxcms.core.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.WorkflowStep;
import com.jspxcms.core.domain.WorkflowStepRole;
import com.jspxcms.core.repository.WorkflowStepRoleDao;
import com.jspxcms.core.service.RoleService;
import com.jspxcms.core.service.WorkflowStepRoleService;

@Service
@Transactional(readOnly = true)
public class WorkflowStepRoleServiceImpl implements WorkflowStepRoleService {
	@Transactional
	public Set<WorkflowStepRole> save(WorkflowStep step, Integer[] roleIds) {
		Set<WorkflowStepRole> stepRoles = new HashSet<WorkflowStepRole>();
		if (roleIds != null) {
			WorkflowStepRole stepRole;
			for (Integer roleId : roleIds) {
				stepRole = new WorkflowStepRole(step, roleService.get(roleId));
				dao.save(stepRole);
				stepRoles.add(stepRole);
			}
		}
		return stepRoles;
	}

	public long countByRoleId(Collection<Integer> roleIds) {
		return dao.countByRoleId(roleIds);
	}

	private RoleService roleService;

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	private WorkflowStepRoleDao dao;

	@Autowired
	public void setDao(WorkflowStepRoleDao dao) {
		this.dao = dao;
	}
}

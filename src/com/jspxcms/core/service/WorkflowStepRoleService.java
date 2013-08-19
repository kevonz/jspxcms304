package com.jspxcms.core.service;

import java.util.Collection;
import java.util.Set;

import com.jspxcms.core.domain.WorkflowStep;
import com.jspxcms.core.domain.WorkflowStepRole;

public interface WorkflowStepRoleService {
	public Set<WorkflowStepRole> save(WorkflowStep step, Integer[] roleIds);

	public long countByRoleId(Collection<Integer> roleIds);
}

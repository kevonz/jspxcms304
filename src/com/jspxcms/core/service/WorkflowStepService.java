package com.jspxcms.core.service;

import java.util.Collection;

import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.domain.WorkflowStep;

public interface WorkflowStepService {
	public void save(String[] stepName, Integer[] stepRoleIds, Workflow workflow);

	public void delete(Collection<WorkflowStep> steps);
}

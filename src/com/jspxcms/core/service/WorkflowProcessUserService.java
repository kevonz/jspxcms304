package com.jspxcms.core.service;

import java.util.Collection;
import java.util.Set;

import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.WorkflowProcess;
import com.jspxcms.core.domain.WorkflowProcessUser;

public interface WorkflowProcessUserService {
	public Set<WorkflowProcessUser> save(WorkflowProcess process,
			Set<User> users);

	public Set<WorkflowProcessUser> update(WorkflowProcess process,
			Set<User> users);

	public int deleteByProcessId(Collection<Integer> processIds);

	public int deleteByUserId(Collection<Integer> userIds);

	public int deleteByPorcessUserId(Collection<Integer> userIds);

	public int deleteByPorcessWorkflowId(Collection<Integer> workflowIds);

	public int deleteByPorcessSiteId(Collection<Integer> siteIds);
}

package com.jspxcms.core.service;

import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.domain.WorkflowProcess;

public interface WorkflowProcessService {
	public Page<WorkflowProcess> findAll(Map<String, String[]> params,
			Pageable pageable);

	public RowSide<WorkflowProcess> findSide(Map<String, String[]> params,
			WorkflowProcess bean, Integer position, Sort sort);

	public WorkflowProcess findOne(Integer type, Integer dataId);

	public WorkflowProcess get(Integer id);

	public WorkflowProcess save(Site site, Workflow workflow, User operator,
			Set<User> users, Integer dataId, int step, Integer type,
			Boolean isReject, Boolean isEnd);

	public WorkflowProcess delete(Integer id);

	public WorkflowProcess[] delete(Integer[] ids);
}

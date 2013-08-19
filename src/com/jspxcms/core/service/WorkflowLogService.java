package com.jspxcms.core.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.WorkflowLog;

public interface WorkflowLogService {
	public Page<WorkflowLog> findAll(Map<String, String[]> params, Pageable pageable);

	public RowSide<WorkflowLog> findSide(Map<String, String[]> params,
			WorkflowLog bean, Integer position, Sort sort);

	public WorkflowLog get(Integer id);

	public WorkflowLog save(WorkflowLog bean, Integer siteId);

	public WorkflowLog update(WorkflowLog bean);

	public WorkflowLog delete(Integer id);

	public WorkflowLog[] delete(Integer[] ids);
}

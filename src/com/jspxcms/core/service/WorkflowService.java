package com.jspxcms.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;

import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.Workflow;

/**
 * WorkflowService
 * 
 * @author liufang
 * 
 */
public interface WorkflowService {
	public List<Workflow> findList(Map<String, String[]> params, Sort sort);

	public RowSide<Workflow> findSide(Map<String, String[]> params,
			Workflow bean, Integer position, Sort sort);

	public List<Workflow> findList(Integer siteId);

	public Workflow get(Integer id);

	public int auditPass(Workflow workflow, User owner, User operator,
			Integer type, Integer dataId, String opinion);

	public int auditReject(Workflow workflow, User owner, User operator,
			Integer type, Integer dataId, String opinion);

	public Workflow save(Workflow bean, Integer groupId, String[] stepName,
			Integer[] stepRoleId, Integer siteId);

	public Workflow update(Workflow bean, Integer groupId, String[] stepName,
			Integer[] stepRoleId);

	public Workflow delete(Integer id);

	public Workflow[] delete(Integer[] ids);
}

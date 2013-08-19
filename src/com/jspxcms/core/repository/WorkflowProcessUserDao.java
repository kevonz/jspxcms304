package com.jspxcms.core.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.core.domain.WorkflowProcessUser;

public interface WorkflowProcessUserDao extends
		Repository<WorkflowProcessUser, Integer> {

	public WorkflowProcessUser findOne(Integer id);

	public WorkflowProcessUser save(WorkflowProcessUser bean);

	// --------------------

	@Modifying
	@Query("delete from WorkflowProcessUser bean where bean.process.id in ?1")
	public int deleteByPorcessId(Collection<Integer> processId);

	@Modifying
	@Query("delete from WorkflowProcessUser bean where bean.user.id in ?1")
	public int deleteByUserId(Collection<Integer> userId);

	@Modifying
	@Query("delete from WorkflowProcessUser bean where bean.process.id in (select process.id from WorkflowProcess process where process.user.id in (?1))")
	public int deleteByPorcessUserId(Collection<Integer> userIds);

	@Modifying
	@Query("delete from WorkflowProcessUser bean where bean.process.id in (select process.id from WorkflowProcess process where process.workflow.id in (?1))")
	public int deleteByPorcessWorkflowId(Collection<Integer> workflowIds);

	@Modifying
	@Query("delete from WorkflowProcessUser bean where bean.process.id in (select process.id from WorkflowProcess process where process.site.id in (?1))")
	public int deleteByPorcessSiteId(Collection<Integer> siteIds);
}

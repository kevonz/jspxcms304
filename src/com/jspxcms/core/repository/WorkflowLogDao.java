package com.jspxcms.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.Repository;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.core.domain.WorkflowLog;

public interface WorkflowLogDao extends Repository<WorkflowLog, Integer> {
	public Page<WorkflowLog> findAll(Specification<WorkflowLog> spec,
			Pageable pageable);

	public List<WorkflowLog> findAll(Specification<WorkflowLog> spec,
			Limitable limitable);

	public WorkflowLog findOne(Integer id);

	public WorkflowLog save(WorkflowLog bean);

	public void delete(WorkflowLog bean);

	// --------------------

}

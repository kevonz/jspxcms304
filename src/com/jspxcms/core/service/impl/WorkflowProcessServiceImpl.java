package com.jspxcms.core.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.common.orm.SearchFilter;
import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.domain.WorkflowProcess;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.listener.UserDeleteListener;
import com.jspxcms.core.listener.WorkflowDeleteListener;
import com.jspxcms.core.repository.WorkflowProcessDao;
import com.jspxcms.core.service.WorkflowProcessService;
import com.jspxcms.core.service.WorkflowProcessUserService;

@Service
@Transactional(readOnly = true)
public class WorkflowProcessServiceImpl implements WorkflowProcessService,
		SiteDeleteListener, WorkflowDeleteListener, UserDeleteListener {
	public Page<WorkflowProcess> findAll(Map<String, String[]> params,
			Pageable pageable) {
		return dao.findAll(spec(params), pageable);
	}

	public RowSide<WorkflowProcess> findSide(Map<String, String[]> params,
			WorkflowProcess bean, Integer position, Sort sort) {
		if (position == null) {
			return new RowSide<WorkflowProcess>();
		}
		Limitable limit = RowSide.limitable(position, sort);
		List<WorkflowProcess> list = dao.findAll(spec(params), limit);
		return RowSide.create(list, bean);
	}

	private Specification<WorkflowProcess> spec(Map<String, String[]> params) {
		Collection<SearchFilter> filters = SearchFilter.parse(params).values();
		Specification<WorkflowProcess> sp = SearchFilter.spec(filters,
				WorkflowProcess.class);
		return sp;
	}

	public WorkflowProcess findOne(Integer type, Integer dataId) {
		return dao.findByTypeAndDataId(type, dataId);
	}

	public WorkflowProcess get(Integer id) {
		return dao.findOne(id);
	}

	@Transactional
	public WorkflowProcess save(Site site, Workflow workflow, User operator,
			Set<User> users, Integer dataId, int step, Integer type,
			Boolean isReject, Boolean isEnd) {
		WorkflowProcess process = new WorkflowProcess();
		process.setSite(site);
		process.setWorkflow(workflow);
		process.setUser(operator);
		process.setDataId(dataId);
		process.setStep(step);
		process.setType(type);
		process.setRejection(isReject);
		process.setEnd(isEnd);

		process.applyDefaultValue();
		process = dao.save(process);

		processUserService.save(process, users);
		return process;
	}

	@Transactional
	public WorkflowProcess update(WorkflowProcess bean) {
		bean.applyDefaultValue();
		bean = dao.save(bean);
		return bean;
	}

	@Transactional
	public WorkflowProcess delete(Integer id) {
		WorkflowProcess entity = dao.findOne(id);
		dao.delete(entity);
		return entity;
	}

	@Transactional
	public WorkflowProcess[] delete(Integer[] ids) {
		WorkflowProcess[] beans = new WorkflowProcess[ids.length];
		for (int i = 0; i < ids.length; i++) {
			beans[i] = delete(ids[i]);
		}
		return beans;
	}

	public void preSiteDelete(Integer[] ids) {
		if (ids == null) {
			return;
		}
		processUserService.deleteByPorcessSiteId(Arrays.asList(ids));
		dao.deleteBySiteId(Arrays.asList(ids));
	}

	public void preWorkflowDelete(Integer[] ids) {
		if (ids == null) {
			return;
		}
		processUserService.deleteByPorcessWorkflowId(Arrays.asList(ids));
		dao.deleteByWorkflowId(Arrays.asList(ids));
	}

	public void preUserDelete(Integer[] ids) {
		if (ids == null) {
			return;
		}
		processUserService.deleteByPorcessUserId(Arrays.asList(ids));
		processUserService.deleteByUserId(Arrays.asList(ids));
		dao.deleteByUserId(Arrays.asList(ids));
	}

	private WorkflowProcessUserService processUserService;

	@Autowired
	public void setProcessUserService(
			WorkflowProcessUserService processUserService) {
		this.processUserService = processUserService;
	}

	private WorkflowProcessDao dao;

	@Autowired
	public void setDao(WorkflowProcessDao dao) {
		this.dao = dao;
	}
}

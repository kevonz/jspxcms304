package com.jspxcms.core.service.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.jspxcms.core.domain.WorkflowGroup;
import com.jspxcms.core.domain.WorkflowProcess;
import com.jspxcms.core.domain.WorkflowStep;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.listener.WorkflowDeleteListener;
import com.jspxcms.core.listener.WorkflowGroupDeleteListener;
import com.jspxcms.core.repository.WorkflowDao;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.core.service.WorkflowGroupService;
import com.jspxcms.core.service.WorkflowProcessService;
import com.jspxcms.core.service.WorkflowProcessUserService;
import com.jspxcms.core.service.WorkflowService;
import com.jspxcms.core.service.WorkflowStepService;
import com.jspxcms.core.support.DeleteException;

/**
 * WorkflowServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class WorkflowServiceImpl implements WorkflowService,
		WorkflowGroupDeleteListener, SiteDeleteListener {
	public List<Workflow> findList(Map<String, String[]> params, Sort sort) {
		return dao.findAll(spec(params), sort);
	}

	public RowSide<Workflow> findSide(Map<String, String[]> params,
			Workflow bean, Integer position, Sort sort) {
		if (position == null) {
			return new RowSide<Workflow>();
		}
		Limitable limit = RowSide.limitable(position, sort);
		List<Workflow> list = dao.findAll(spec(params), limit);
		return RowSide.create(list, bean);
	}

	private Specification<Workflow> spec(Map<String, String[]> params) {
		Collection<SearchFilter> filters = SearchFilter.parse(params).values();
		Specification<Workflow> sp = SearchFilter.spec(filters, Workflow.class);
		return sp;
	}

	public List<Workflow> findList(Integer siteId) {
		Sort sort = new Sort("group.seq", "group.id", "seq", "id");
		return dao.findBySiteId(siteId, sort);
	}

	public Workflow get(Integer id) {
		return dao.findOne(id);
	}

	/**
	 * 新发表、审核。 无工作流或无步骤，结束。 判断操作人在步骤位置。 新流程（无流程、无步骤）
	 * 
	 * @param workflow
	 * @param operator
	 * @param type
	 * @param dataId
	 * @return -1：终审通过；0：无审核权限；其他代表审核步骤
	 */
	@Transactional
	public int auditPass(Workflow workflow, User owner, User operator,
			Integer type, Integer dataId, String opinion) {
		WorkflowProcess process = processService.findOne(type, dataId);
		if (workflow == null) {
			// 没有工作流，但有流程代表：原有流程，现在没有。审核通过。
			if (process != null) {
				// TODO 写流程日志
				process.passEnd();
			}
			return -1;
		}
		// 流程存在，且为结束，使用原流程工作流
		if (process != null && !process.getEnd()) {
			workflow = process.getWorkflow();
		}
		List<WorkflowStep> steps = workflow.getSteps();
		int size = steps.size();
		if (size <= 0) {
			// 无审核流程，审核通过。
			if (process != null) {
				// TODO 写流程日志
				process.passEnd();
			}
			return -1;
		}
		// 提交人在工作流中的最后步骤
		int step;
		for (step = size; step > 0; step--) {
			WorkflowStep workflowStep = steps.get(step - 1);
			if (CollectionUtils.containsAny(workflowStep.getRoles(),
					operator.getRoles())) {
				break;
			}
		}
		// step==0代表没有在工作流中。
		// 不在工作流中，可以提交新流程，但不能审核已终审流程。
		// 在工作流中，可以审核流程，但不能审核超过自己步骤的流程。

		// 流程存在&&未结束&&操作者步骤小于流程步骤，则不能操作
		// 已终审，则不能操作（本条由调用方控制）
		if (process != null && !process.getEnd() && step < process.getStep()) {
			return 0;
		}

		// TODO 写审核log

		int nextStep;
		Set<User> users;
		Date endDate;
		Boolean isEnd;
		if (step == size) {
			// 终审人员，审核结束。
			users = null;
			nextStep = -1;
			endDate = new Timestamp(System.currentTimeMillis());
			isEnd = true;
		} else {
			// 获得下一步审核人员
			users = steps.get(step).getUsers();
			// -1代表不是审核人员，则应为1，所以加2。
			nextStep = step + 1;
			endDate = null;
			isEnd = false;
		}
		Boolean isRejection = false;
		if (process != null) {
			process.setWorkflow(workflow);
			process.setStep(nextStep);
			process.setEndDate(endDate);
			process.setRejection(isRejection);
			process.setEnd(isEnd);
			processUserService.update(process, users);
		} else {
			Site site = workflow.getSite();
			processService.save(site, workflow, owner, users, dataId, nextStep,
					type, isRejection, isEnd);
		}
		return nextStep;
	}

	/**
	 * 
	 * @param workflow
	 * @param operator
	 * @param type
	 * @param dataId
	 * @param opinion
	 * @return -1:退稿；
	 */
	// TODO 退到哪一步？直接退稿。
	public int auditReject(Workflow workflow, User owner, User operator,
			Integer type, Integer dataId, String opinion) {
		WorkflowProcess process = processService.findOne(type, dataId);
		if (workflow == null) {
			// 没有工作流，退稿。
			if (process != null) {
				// TODO 写流程日志
				process.rejectEnd();
			}
			return -1;
		}
		// 流程存在，且为结束，使用原流程工作流
		if (process != null && !process.getEnd()) {
			workflow = process.getWorkflow();
		}
		List<WorkflowStep> steps = workflow.getSteps();
		int size = steps.size();
		if (size <= 0) {
			// 无审核流程，退稿。
			if (process != null) {
				// TODO 写流程日志
				process.rejectEnd();
			}
			return -1;
		}
		// 提交人在工作流中的步骤
		int step;
		for (step = 0; step < size; step++) {
			WorkflowStep workfolwStep = steps.get(step);
			if (CollectionUtils.containsAny(workfolwStep.getRoles(),
					operator.getRoles())) {
				break;
			}
		}
		if (step == size) {
			// 不在审核流程中
			step = 0;
		} else {
			// 在审核流程中的步骤
			step++;
		}
		// step==0代表没有在工作流程中
		// 小于流程步骤不能退；已终审的，非终审人员不能退。

		// 已退回，不能再操作（由调用方控制）。
		// 流程存在&&未结束&&非当前步骤人员（直接退稿且大于等于当前步骤除外），不做任何处理。
		// (流程不存在||(流程存在&&已结束))&&非终审人员，不能操作。
		if (process != null && !process.getEnd()) {
			if (step != process.getStep()) {
				return 0;
			}
		} else if ((process == null || process.getEnd()) && step != size) {
			return 0;
		}

		// TODO 写审核log

		// 已经退回到发稿人
		boolean ownerRejection = false;
		if (step > 1) {
			for (int i = step - 2; i < size; i++) {
				WorkflowStep workfolwStep = steps.get(i);
				if (workfolwStep.getUsers().contains(owner)) {
					ownerRejection = true;
					break;
				}
			}
		}

		int nextStep;
		Set<User> users;
		Date endDate;
		Boolean isEnd;
		if (step == 1 || ownerRejection) {
			// 第一个审核人员，退稿。
			users = null;
			nextStep = -1;
			endDate = new Timestamp(System.currentTimeMillis());
			isEnd = true;
		} else {
			// 获得上一步审核人员。下标从0开始，需-1，上一步审核人员再-1，共-2。
			users = steps.get(step - 2).getUsers();
			nextStep = step - 1;
			endDate = null;
			isEnd = false;
		}
		Boolean isRejection = true;
		if (process != null) {
			process.setWorkflow(workflow);
			process.setStep(nextStep);
			process.setEndDate(endDate);
			process.setEnd(isEnd);
			process.setRejection(isRejection);
			processUserService.update(process, users);
		} else {
			Site site = workflow.getSite();
			processService.save(site, workflow, owner, users, dataId, nextStep,
					type, isRejection, isEnd);
		}
		return nextStep;
	}

	@Transactional
	public Workflow save(Workflow bean, Integer groupId, String[] stepName,
			Integer[] stepRoleId, Integer siteId) {
		Site site = siteService.get(siteId);
		bean.setSite(site);
		WorkflowGroup group = groupService.get(groupId);
		bean.setGroup(group);
		bean.applyDefaultValue();
		bean = dao.save(bean);
		stepService.save(stepName, stepRoleId, bean);
		return bean;
	}

	@Transactional
	public Workflow update(Workflow bean, Integer groupId, String[] stepName,
			Integer[] stepRoleId) {
		WorkflowGroup group = groupService.get(groupId);
		bean.setGroup(group);

		List<WorkflowStep> steps = bean.getSteps();
		stepService.delete(steps);
		bean.getSteps().clear();
		stepService.save(stepName, stepRoleId, bean);

		bean.applyDefaultValue();
		bean = dao.save(bean);
		return bean;
	}

	private Workflow doDelete(Integer id) {
		Workflow entity = dao.findOne(id);
		if (entity != null) {
			dao.delete(entity);
		}
		return entity;
	}

	@Transactional
	public Workflow delete(Integer id) {
		firePreDelete(new Integer[] { id });
		return doDelete(id);
	}

	@Transactional
	public Workflow[] delete(Integer[] ids) {
		firePreDelete(ids);
		Workflow[] beans = new Workflow[ids.length];
		for (int i = 0; i < ids.length; i++) {
			beans[i] = doDelete(ids[i]);
		}
		return beans;
	}

	public void preSiteDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countBySiteId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("workflow.management");
			}
		}
	}

	public void preWorkflowGroupDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countByGroupId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("workflow.management");
			}
		}
	}

	private void firePreDelete(Integer[] ids) {
		if (!CollectionUtils.isEmpty(deleteListeners)) {
			for (WorkflowDeleteListener listener : deleteListeners) {
				listener.preWorkflowDelete(ids);
			}
		}
	}

	private List<WorkflowDeleteListener> deleteListeners;

	@Autowired(required = false)
	public void setDeleteListeners(List<WorkflowDeleteListener> deleteListeners) {
		this.deleteListeners = deleteListeners;
	}

	private WorkflowProcessUserService processUserService;
	private WorkflowProcessService processService;
	private WorkflowStepService stepService;
	private WorkflowGroupService groupService;
	private SiteService siteService;

	@Autowired
	public void setProcessUserService(
			WorkflowProcessUserService processUserService) {
		this.processUserService = processUserService;
	}

	@Autowired
	public void setProcessService(WorkflowProcessService processService) {
		this.processService = processService;
	}

	@Autowired
	public void setStepService(WorkflowStepService stepService) {
		this.stepService = stepService;
	}

	@Autowired
	public void setGroupService(WorkflowGroupService groupService) {
		this.groupService = groupService;
	}

	@Autowired
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private WorkflowDao dao;

	@Autowired
	public void setDao(WorkflowDao dao) {
		this.dao = dao;
	}
}

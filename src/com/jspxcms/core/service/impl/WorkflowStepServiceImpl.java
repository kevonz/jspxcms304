package com.jspxcms.core.service.impl;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.domain.WorkflowStep;
import com.jspxcms.core.listener.RoleDeleteListener;
import com.jspxcms.core.repository.WorkflowStepDao;
import com.jspxcms.core.service.WorkflowStepRoleService;
import com.jspxcms.core.service.WorkflowStepService;
import com.jspxcms.core.support.DeleteException;

@Service
@Transactional(readOnly = true)
public class WorkflowStepServiceImpl implements WorkflowStepService,
		RoleDeleteListener {
	public void save(String[] stepName, Integer[] stepRoleId, Workflow workflow) {
		if (ArrayUtils.isNotEmpty(stepName)) {
			WorkflowStep step;
			for (int i = 0, len = stepName.length; i < len; i++) {
				if (StringUtils.isNotBlank(stepName[i])) {
					step = new WorkflowStep();
					step.setWorkflow(workflow);
					step.setName(stepName[i]);
					step.setSeq(i);
					step.applyDefaultValue();
					step = dao.save(step);
					stepRoleService.save(step, new Integer[] { stepRoleId[i] });
					workflow.addStep(step);
				}
			}
		}
	}

	@Transactional
	public void delete(Collection<WorkflowStep> steps) {
		if (steps != null) {
			dao.delete(steps);
		}
	}

	public void preRoleDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (stepRoleService.countByRoleId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("workflow.management");
			}
		}
	}

	private WorkflowStepRoleService stepRoleService;

	@Autowired
	public void setStepRoleService(WorkflowStepRoleService stepRoleService) {
		this.stepRoleService = stepRoleService;
	}

	private WorkflowStepDao dao;

	@Autowired
	public void setDao(WorkflowStepDao dao) {
		this.dao = dao;
	}
}

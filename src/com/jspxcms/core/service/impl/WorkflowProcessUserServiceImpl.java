package com.jspxcms.core.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.WorkflowProcess;
import com.jspxcms.core.domain.WorkflowProcessUser;
import com.jspxcms.core.repository.WorkflowProcessUserDao;
import com.jspxcms.core.service.WorkflowProcessUserService;

@Service
@Transactional(readOnly = true)
public class WorkflowProcessUserServiceImpl implements
		WorkflowProcessUserService {
	@Transactional
	public Set<WorkflowProcessUser> save(WorkflowProcess process,
			Set<User> users) {
		Set<WorkflowProcessUser> processUsers = new HashSet<WorkflowProcessUser>();
		process.setProcessUsers(processUsers);
		if (users != null) {
			WorkflowProcessUser processUser;
			for (User user : users) {
				processUser = new WorkflowProcessUser(process, user);
				dao.save(processUser);
				processUsers.add(processUser);
			}
		}
		return processUsers;
	}

	public Set<WorkflowProcessUser> update(WorkflowProcess process,
			Set<User> users) {
		dao.deleteByPorcessId(Arrays.asList(new Integer[] { process.getId() }));
		return save(process, users);
	}

	@Transactional
	public int deleteByProcessId(Collection<Integer> processIds) {
		return dao.deleteByPorcessId(processIds);
	}

	@Transactional
	public int deleteByUserId(Collection<Integer> userIds) {
		return dao.deleteByUserId(userIds);
	}

	@Transactional
	public int deleteByPorcessUserId(Collection<Integer> userIds) {
		return dao.deleteByPorcessUserId(userIds);
	}

	@Transactional
	public int deleteByPorcessWorkflowId(Collection<Integer> workflowIds) {
		return dao.deleteByPorcessWorkflowId(workflowIds);
	}

	@Transactional
	public int deleteByPorcessSiteId(Collection<Integer> siteIds) {
		return dao.deleteByPorcessSiteId(siteIds);
	}

	private WorkflowProcessUserDao dao;

	@Autowired
	public void setDao(WorkflowProcessUserDao dao) {
		this.dao = dao;
	}
}

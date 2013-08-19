package com.jspxcms.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "cms_workflowprocess_user")
public class WorkflowProcessUser implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private User user;
	private WorkflowProcess process;

	public WorkflowProcessUser() {
	}

	public WorkflowProcessUser(WorkflowProcess process, User user) {
		this.process = process;
		this.user = user;
	}

	@Id
	@Column(name = "f_wfprocuser_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_workflowprocess_user", pkColumnValue = "cms_workflowprocess_user", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_workflowprocess_user")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_user_id", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_workflowprocess_id", nullable = false)
	public WorkflowProcess getProcess() {
		return process;
	}

	public void setProcess(WorkflowProcess process) {
		this.process = process;
	}
}

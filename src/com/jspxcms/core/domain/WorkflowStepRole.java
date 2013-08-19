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
@Table(name = "cms_workflowstep_role")
public class WorkflowStepRole implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Role role;
	private WorkflowStep step;

	public WorkflowStepRole() {
	}

	public WorkflowStepRole(WorkflowStep step, Role role) {
		this.step = step;
		this.role = role;
	}

	@Id
	@Column(name = "f_wfsteprole_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_workflowstep_role", pkColumnValue = "cms_workflowstep_role", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_workflowstep_role")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_role_id", nullable = false)
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_workflowstep_id", nullable = false)
	public WorkflowStep getStep() {
		return step;
	}

	public void setStep(WorkflowStep step) {
		this.step = step;
	}
}

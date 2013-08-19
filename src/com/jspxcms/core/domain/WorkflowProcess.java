package com.jspxcms.core.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * WorkflowProcess
 * 
 * @author liufang
 * 
 */
@Entity
@Table(name = "cms_workflow_process")
public class WorkflowProcess implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_TYPE = 0;

	@Transient
	public void passEnd() {
		setEndDate(new Timestamp(System.currentTimeMillis()));
		setRejection(false);
		setStep(-1);
		setProcessUsers(null);
		setEnd(true);
	}

	@Transient
	public void rejectEnd() {
		setEndDate(new Timestamp(System.currentTimeMillis()));
		setRejection(true);
		setStep(-1);
		setProcessUsers(null);
		setEnd(true);
	}

	@Transient
	public Set<User> getUsers() {
		Set<WorkflowProcessUser> processUsers = getProcessUsers();
		if (processUsers == null) {
			return null;
		}
		Set<User> users = new HashSet<User>();
		for (WorkflowProcessUser processUser : processUsers) {
			users.add(processUser.getUser());
		}
		return users;
	}

	@Transient
	public void applyDefaultValue() {
		if (getBeginDate() == null) {
			setBeginDate(new Timestamp(System.currentTimeMillis()));
		}
		if (getType() == null) {
			setType(DEFAULT_TYPE);
		}
		if (getRejection() == null) {
			setRejection(false);
		}
		if (getEnd() == null) {
			setEnd(false);
		}
	}

	private Integer id;
	private Set<WorkflowLog> logs = new HashSet<WorkflowLog>();
	private Set<WorkflowProcessUser> processUsers = new HashSet<WorkflowProcessUser>();

	private Workflow workflow;
	private User user;
	private Site site;

	private Integer dataId;
	private Date beginDate;
	private Date endDate;
	private Integer step;
	private Integer type;
	private Boolean rejection;
	private Boolean end;

	@Id
	@Column(name = "f_workflowprocess_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_workflow_process", pkColumnValue = "cms_workflow_process", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_workflow_process")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "process")
	public Set<WorkflowLog> getLogs() {
		return this.logs;
	}

	public void setLogs(Set<WorkflowLog> logs) {
		this.logs = logs;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "process")
	public Set<WorkflowProcessUser> getProcessUsers() {
		return processUsers;
	}

	public void setProcessUsers(Set<WorkflowProcessUser> processUsers) {
		this.processUsers = processUsers;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_workflow_id", nullable = false)
	public Workflow getWorkflow() {
		return this.workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
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
	@JoinColumn(name = "f_site_id", nullable = false)
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@Column(name = "f_data_id", nullable = false)
	public Integer getDataId() {
		return this.dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "f_begin_date", nullable = false, length = 19)
	public Date getBeginDate() {
		return this.beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "f_end_date", length = 19)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "f_step", nullable = false)
	public Integer getStep() {
		return this.step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	@Column(name = "f_type", nullable = false)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "f_is_rejection", nullable = false)
	public Boolean getRejection() {
		return rejection;
	}

	public void setRejection(Boolean rejection) {
		this.rejection = rejection;
	}

	@Column(name = "f_is_end", nullable = false)
	public Boolean getEnd() {
		return this.end;
	}

	public void setEnd(Boolean end) {
		this.end = end;
	}
}

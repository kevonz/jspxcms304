package com.jspxcms.core.domain;

// Generated 2013-8-18 10:20:21 by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CmsRolenodeInfo generated by hbm2java
 */
@Entity
@Table(name = "cms_rolenode_info", catalog = "jspxcms2")
public class CmsRolenodeInfo implements java.io.Serializable {

	private Integer roleninfoId;
	private Integer rolesiteId;
	private Integer nodeId;

	public CmsRolenodeInfo() {
	}

	public CmsRolenodeInfo(Integer roleninfoId, Integer rolesiteId,
			Integer nodeId) {
		this.roleninfoId = roleninfoId;
		this.rolesiteId = rolesiteId;
		this.nodeId = nodeId;
	}

	@Id
	@Column(name = "f_roleninfo_id", unique = true, nullable = false)
	public Integer getRoleninfoId() {
		return this.roleninfoId;
	}

	public void setRoleninfoId(Integer roleninfoId) {
		this.roleninfoId = roleninfoId;
	}

	@Column(name = "f_rolesite_id", nullable = false)
	public Integer getRolesiteId() {
		return this.rolesiteId;
	}

	public void setRolesiteId(Integer rolesiteId) {
		this.rolesiteId = rolesiteId;
	}

	@Column(name = "f_node_id", nullable = false)
	public Integer getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

}

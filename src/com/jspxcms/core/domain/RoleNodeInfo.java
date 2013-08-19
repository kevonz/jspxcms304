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
@Table(name = "cms_rolenode_info")
public class RoleNodeInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private RoleSite roleSite;
	private Node node;

	public RoleNodeInfo() {
	}

	public RoleNodeInfo(RoleSite roleSite, Node node) {
		this.roleSite = roleSite;
		this.node = node;
	}

	@Id
	@Column(name = "f_roleninfo_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_rolenode_info", pkColumnValue = "cms_rolenode_info", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_rolenode_info")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_rolesite_id", nullable = false)
	public RoleSite getRoleSite() {
		return roleSite;
	}

	public void setRoleSite(RoleSite roleSite) {
		this.roleSite = roleSite;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_node_id", nullable = false)
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}

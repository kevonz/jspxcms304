package com.jspxcms.core.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * MemberGroup
 * 
 * @author liufang
 * 
 */
@Entity
@Table(name = "cms_member_group")
public class MemberGroup implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public void applyDefaultValue() {
		if (getSeq() == null) {
			setSeq(Integer.MAX_VALUE);
		}
	}

	private Integer id;
	private Set<MemberGroupSite> groupSites = new HashSet<MemberGroupSite>();

	private String name;
	private String description;
	private Integer seq;

	@Id
	@Column(name = "f_membergroup_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_member_group", pkColumnValue = "cms_member_group", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_member_group")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "group")
	public Set<MemberGroupSite> getGroupSites() {
		return groupSites;
	}

	public void setGroupSites(Set<MemberGroupSite> groupSites) {
		this.groupSites = groupSites;
	}

	@Column(name = "f_name", nullable = false, length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "f_description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "f_seq", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

}

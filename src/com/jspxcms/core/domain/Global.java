package com.jspxcms.core.domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;

/**
 * Global
 * 
 * @author liufang
 * 
 */
@Entity
@Table(name = "cms_global")
public class Global implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public static void removeAttr(Map<String, String> map, String prefix) {
		Set<String> keysToRemove = new HashSet<String>();
		for (String key : map.keySet()) {
			if (key.startsWith(prefix)) {
				keysToRemove.add(key);
			}
		}
		for (String key : keysToRemove) {
			map.remove(key);
		}
	}

	@Transient
	public GlobalMail getMail() {
		return new GlobalMail(getCustoms());
	}

	@Transient
	public GlobalRegister getRegister() {
		return new GlobalRegister(getCustoms());
	}

	@Transient
	public GlobalUpload getUpload() {
		return new GlobalUpload(getCustoms());
	}

	@Transient
	public Object getConf(String className) {
		try {
			return Class.forName(className).getConstructor(Map.class)
					.newInstance(getCustoms());
		} catch (Exception e) {
			throw new IllegalArgumentException("Class '" + className
					+ "' is not Conf Class", e);
		}
	}

	private Integer id;
	private String protocol;
	private Integer port;
	private String contextPath;
	private String version;

	private Map<String, String> customs;

	@Id
	@Column(name = "f_global_id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ElementCollection
	@CollectionTable(name = "cms_global_custom", joinColumns = @JoinColumn(name = "f_global_id"))
	@MapKeyColumn(name = "f_key", length = 50)
	@Column(name = "f_value", length = 2000)
	public Map<String, String> getCustoms() {
		return this.customs;
	}

	public void setCustoms(Map<String, String> customs) {
		this.customs = customs;
	}

	@Length(max = 50)
	@Column(name = "f_protocol", length = 50)
	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Column(name = "f_port")
	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Length(max = 255)
	@Column(name = "f_context_path", length = 255)
	public String getContextPath() {
		return this.contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Length(max = 50)
	@Column(name = "f_version", nullable = false, length = 50)
	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}

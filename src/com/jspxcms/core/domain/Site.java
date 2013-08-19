package com.jspxcms.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.jspxcms.core.support.ForeContext;

/**
 * Site
 * 
 * @author liufang
 * 
 */
@Entity
@Table(name = "cms_site")
public class Site implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	public String getUrl() {
		return getUrl(getWithDomain());
	}

	@Transient
	public String getUrlFull() {
		return getUrl(true);
	}

	@Transient
	public String getUrl(boolean isFull) {
		StringBuilder sb = new StringBuilder();
		if (isFull) {
			sb.append("//").append(getDomain());
			if (getPort() != null) {
				sb.append(":").append(getPort());
			}
		}
		if (StringUtils.isNotBlank(getContextPath())) {
			sb.append(getContextPath());
		}
		return sb.toString();
	}

	/**
	 * 获得当前模板主题的资源文件路径
	 * 
	 * 例如：/jspxcms/files/1/bluewise/_files
	 * 
	 * @param path
	 * @return
	 */
	@Transient
	public String getFilesPath() {
		return getFilesPath(null);
	}

	/**
	 * 获得当前模板主题的资源文件路径
	 * 
	 * 例如：/jspxcms/files/1/bluewise/_files
	 * 
	 * @param path
	 * @return
	 */
	@Transient
	public String getFilesPath(String path) {
		return getFilesPath(path, true);
	}

	/**
	 * 获得当前模板主题的资源文件路径
	 * 
	 * 例如：/jspxcms/files/1/bluewise/_files 或 /files/1/bluewise/_files
	 * 
	 * @param path
	 * @return
	 */
	@Transient
	public String getFilesPath(String path, boolean withContextPath) {
		StringBuilder sb = new StringBuilder();
		if (withContextPath && StringUtils.isNotBlank(getContextPath())) {
			sb.append(getContextPath());
		}
		sb.append(ForeContext.FILES_PATH);
		sb.append("/").append(getId());
		sb.append("/").append(getTemplateTheme());
		sb.append("/").append(ForeContext.FILES);
		if (StringUtils.isNotBlank(path)) {
			sb.append(path);
		}
		return sb.toString();
	}

	/**
	 * 获得模板路径
	 * 
	 * 例如：/1/bluewise/index.html
	 * 
	 * @param tpl
	 * @return
	 */
	@Transient
	public String getTemplate(String tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("/").append(getId());
		sb.append("/").append(getTemplateTheme());
		if (!tpl.startsWith("/")) {
			sb.append("/");
		}
		sb.append(tpl);
		return sb.toString();
	}

	/**
	 * 获得模板的基础路径
	 * 
	 * 例如：/files/1
	 * 
	 * @param path
	 * @return
	 */
	@Transient
	public String getFilesBasePath(String path) {
		StringBuilder sb = new StringBuilder();
		sb.append(ForeContext.FILES_PATH);
		sb.append("/").append(getId());
		if (StringUtils.isNotBlank(path)) {
			if (!path.startsWith("/")) {
				sb.append("/");
			}
			sb.append(path);
		}
		return sb.toString();
	}

	@Transient
	public String getProtocol() {
		return getGlobal() != null ? getGlobal().getProtocol() : null;
	}

	@Transient
	public Integer getPort() {
		return getGlobal() != null ? getGlobal().getPort() : null;
	}

	@Transient
	public String getContextPath() {
		return getGlobal() != null ? getGlobal().getContextPath() : null;
	}

	@Transient
	public String getNoPictureUrl() {
		return getFilesPath(getNoPicture());
	}

	@Transient
	public String getFullNameOrName() {
		String fullName = getFullName();
		return StringUtils.isBlank(fullName) ? getName() : fullName;
	}

	@Transient
	public SiteWatermark getWatermark() {
		return new SiteWatermark(this);
	}

	@Transient
	public Object getConf(String className) {
		try {
			return Class.forName(className).getConstructor(Site.class)
					.newInstance(this);
		} catch (Exception e) {
			throw new IllegalArgumentException("Class '" + className
					+ "' is not Conf Class", e);
		}
	}

	@Transient
	public void applyDefaultValue() {
	}

	private Integer id;
	private List<Site> children = new ArrayList<Site>();
	private Map<String, String> customs = new HashMap<String, String>();

	private Global global;
	private Org org;
	private Site parent;

	private String name;
	private String number;
	private String fullName;
	private String htmlPath;
	private String domain;
	private Boolean withDomain;
	private String templateTheme;
	private String noPicture;
	private String treeNumber;
	private Integer treeLevel;
	private String treeMax;

	@Id
	@Column(name = "f_site_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_site", pkColumnValue = "cms_site", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_site")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy(value = "treeNumber asc, id asc")
	public List<Site> getChildren() {
		return children;
	}

	public void setChildren(List<Site> children) {
		this.children = children;
	}

	@ElementCollection
	@CollectionTable(name = "cms_site_custom", joinColumns = @JoinColumn(name = "f_site_id"))
	@MapKeyColumn(name = "f_key", length = 50)
	@Column(name = "f_value", length = 2000)
	public Map<String, String> getCustoms() {
		return this.customs;
	}

	public void setCustoms(Map<String, String> customs) {
		this.customs = customs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_parent_id")
	public Site getParent() {
		return parent;
	}

	public void setParent(Site parent) {
		this.parent = parent;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_global_id", nullable = false)
	public Global getGlobal() {
		return this.global;
	}

	public void setGlobal(Global global) {
		this.global = global;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_org_id", nullable = false)
	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}

	@Column(name = "f_name", nullable = false, length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "f_number", length = 100)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "f_full_name", length = 100)
	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Column(name = "f_html_path", length = 100)
	public String getHtmlPath() {
		return this.htmlPath;
	}

	public void setHtmlPath(String htmlPath) {
		this.htmlPath = htmlPath;
	}

	@Column(name = "f_domain", nullable = false, length = 100)
	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Column(name = "f_is_with_domain", nullable = false, length = 1)
	public Boolean getWithDomain() {
		return this.withDomain;
	}

	public void setWithDomain(Boolean withDomain) {
		this.withDomain = withDomain;
	}

	@Column(name = "f_template_theme", nullable = false, length = 100)
	public String getTemplateTheme() {
		return this.templateTheme;
	}

	public void setTemplateTheme(String templateTheme) {
		this.templateTheme = templateTheme;
	}

	@Column(name = "f_no_picture", nullable = true)
	public String getNoPicture() {
		return noPicture;
	}

	public void setNoPicture(String noPicture) {
		this.noPicture = noPicture;
	}

	@Column(name = "f_tree_number", nullable = false, length = 100)
	public String getTreeNumber() {
		return this.treeNumber;
	}

	public void setTreeNumber(String treeNumber) {
		this.treeNumber = treeNumber;
	}

	@Column(name = "f_tree_level", nullable = false)
	public Integer getTreeLevel() {
		return this.treeLevel;
	}

	public void setTreeLevel(Integer treeLevel) {
		this.treeLevel = treeLevel;
	}

	@Column(name = "f_tree_max", nullable = false, length = 20)
	public String getTreeMax() {
		return this.treeMax;
	}

	public void setTreeMax(String treeMax) {
		this.treeMax = treeMax;
	}

}

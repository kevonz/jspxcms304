package com.jspxcms.core.domain;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.Type;
import org.springframework.util.NumberUtils;

import com.jspxcms.common.util.Files;
import com.jspxcms.common.util.Strings;
import com.jspxcms.common.web.Anchor;
import com.jspxcms.common.web.ImageAnchor;
import com.jspxcms.common.web.ImageAnchorBean;
import com.jspxcms.common.web.PageUrlResolver;
import com.jspxcms.core.support.Commentable;
import com.jspxcms.core.support.Constants;
import com.jspxcms.core.support.Siteable;
import com.jspxcms.core.support.TitleText;

/**
 * Info
 * 
 * @author liufang
 * 
 */
@Entity
@Table(name = "cms_info")
public class Info implements java.io.Serializable, Anchor, Siteable,
		Commentable, PageUrlResolver {
	private static final long serialVersionUID = 1L;
	/**
	 * 评分标记
	 */
	public static final String SCORE_MARK = "InfoScore";
	/**
	 * Digg标记
	 */
	public static final String DIGG_MARK = "InfoDigg";
	/**
	 * 模型类型
	 */
	public static final String MODEL_TYPE = "info";
	/**
	 * 工作流类型
	 */
	public static final int WORKFLOW_TYPE = 1;
	/**
	 * 审核中
	 */
	public static final String AUDITING = "1";
	/**
	 * 正常
	 */
	public static final String NORMAL = "A";
	/**
	 * 草稿
	 */
	public static final String DRAFT = "B";
	/**
	 * 投稿
	 */
	public static final String CONTRIBUTE = "C";
	/**
	 * 退稿
	 */
	public static final String REJECTION = "D";
	/**
	 * 采集
	 */
	public static final String COLLECTED = "E";
	/**
	 * 删除
	 */
	public static final String DELETED = "X";
	/**
	 * 归档
	 */
	public static final String ARCHIVE = "Z";
	/**
	 * 替换标识:栏目ID
	 */
	public static String PATH_NODE_ID = "{node_id}";
	/**
	 * 替换标识:栏目编码
	 */
	public static String PATH_NODE_NUMBER = "{node_number}";
	/**
	 * 替换标识:内容ID
	 */
	public static String PATH_INFO_ID = "{info_id}";
	/**
	 * 替换标识:年
	 */
	public static String PATH_YEAR = "{year}";
	/**
	 * 替换标识:月
	 */
	public static String PATH_MONTH = "{month}";
	/**
	 * 替换标识:日
	 */
	public static String PATH_DAY = "{day}";

	/**
	 * 信息正文KEY
	 */
	public static String INFO_TEXT = "text";
	/**
	 * 分页标签打开
	 */
	public static String PAGEBREAK_OPEN = "<div>[PageBreak]";
	/**
	 * 分页标签关闭
	 */
	public static String PAGEBREAK_CLOSE = "[/PageBreak]</div>";

	/**
	 * 获得分页的InfoText(标题、正文)列表
	 * 
	 * @return
	 */
	@Transient
	public List<TitleText> getTextList() {
		List<TitleText> list = new ArrayList<TitleText>();
		String t = getText();
		String ftt = getFullTitleOrTitle();
		String title = ftt, text;
		if (t != null) {
			int start = 0, end;
			while (true) {
				end = t.indexOf(PAGEBREAK_OPEN, start);
				if (end != -1) {
					text = t.substring(start, end);
					start = end + PAGEBREAK_OPEN.length();
				} else {
					text = t.substring(start);
				}
				list.add(new TitleText(title, text));
				if (end == -1) {
					break;
				}
				end = t.indexOf(PAGEBREAK_CLOSE, start);
				if (end != -1) {
					title = t.substring(start, end);
					if (StringUtils.isBlank(title)) {
						title = ftt;
					}
					start = end + PAGEBREAK_CLOSE.length();
				} else {
					title = t.substring(start);
					if (StringUtils.isBlank(title)) {
						title = ftt;
					}
					break;
				}
			}
		} else {
			list.add(new TitleText(title, ""));
		}
		return list;
	}

	/**
	 * 获取没有分页信息的正文
	 * 
	 * @return
	 */
	@Transient
	public String getTextWithoutPageBreak() {
		String t = getText();
		if (StringUtils.isBlank(t)) {
			return t;
		}
		StringBuilder sb = new StringBuilder();
		int start = 0, end;
		while (true) {
			end = t.indexOf(PAGEBREAK_OPEN, start);
			if (end != -1) {
				sb.append(t, start, end);
				start = end + PAGEBREAK_OPEN.length();
			} else {
				sb.append(t, start, t.length());
				break;
			}
			end = t.indexOf(PAGEBREAK_CLOSE, start);
			if (end != -1) {
				start = end + PAGEBREAK_CLOSE.length();
			} else {
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 获取没有html的正文
	 * 
	 * @return
	 */
	@Transient
	public String getPlainText() {
		String text = getTextWithoutPageBreak();
		return Strings.getTextFromHtml(text);
	}

	@Transient
	public String getDescription() {
		String desciption = getMetaDescription();
		if (StringUtils.isBlank(desciption)) {
			String text = getTextWithoutPageBreak();
			desciption = Strings.getTextFromHtml(text, 255);
			return desciption != null ? desciption : getTitle();
		} else {
			return desciption;
		}
	}

	@Transient
	public boolean isDraft() {
		return getStatus() != null ? getStatus().equals(DRAFT) : false;
	}

	@Transient
	public boolean isAuditing() {
		return getStatus() != null ? (getStatusChar() >= '0' && getStatusChar() <= '9')
				: false;
	}

	@Transient
	public char getStatusChar() {
		return getStatus() != null ? getStatus().charAt(0) : NORMAL.charAt(0);
	}

	/**
	 * 获得标题列表
	 * 
	 * @return
	 */
	@Transient
	public List<String> getTitleList() {
		List<TitleText> textList = getTextList();
		List<String> titleList = new ArrayList<String>(textList.size());
		for (TitleText it : textList) {
			titleList.add(it.getTitle());
		}
		return titleList;
	}

	@Transient
	public String getUrl() {
		return getUrl(1);
	}

	@Transient
	public String getUrl(Integer page) {
		return getGenerate() ? getUrlStatic(page) : getUrlDynamic(page);
	}

	@Transient
	public String getUrlDynamic() {
		return getUrlDynamic(1);
	}

	@Transient
	public String getUrlDynamic(Integer page) {
		boolean isFull = getSite().getWithDomain();
		return getUrlDynamic(page, isFull);
	}

	@Transient
	public String getUrlDynamicFull() {
		return getUrlDynamic(1, true);
	}

	@Transient
	public String getUrlDynamic(Integer page, boolean isFull) {
		if (isLinked()) {
			return getLinkUrl();
		}
		Site site = getSite();
		StringBuilder sb = new StringBuilder();
		if (isFull) {
			sb.append("//").append(getNode().getDomainOrSite());
			if (site.getPort() != null) {
				sb.append(":").append(site.getPort());
			}
		}
		String ctx = site.getContextPath();
		if (StringUtils.isNotBlank(ctx)) {
			sb.append(ctx);
		}
		sb.append("/").append(Constants.INFO_PATH);
		sb.append("/").append(getId());
		if (page != null && page > 1) {
			sb.append("_").append(page);
		}
		sb.append(Constants.DYNAMIC_SUFFIX);
		return sb.toString();
	}

	@Transient
	public String getUrlStatic() {
		return getUrlStatic(1);
	}

	@Transient
	public String getUrlStatic(Integer page) {
		boolean isFull = getSite().getWithDomain();
		return getUrlStatic(page, isFull, false);
	}

	@Transient
	public String getUrlStaticFull() {
		return getUrlStaticFull(1);
	}

	@Transient
	public String getUrlStaticFull(Integer page) {
		return getUrlStatic(page, true, false);
	}

	@Transient
	public String getUrlStatic(Integer page, boolean isFull, boolean forRealPath) {
		if (isLinked()) {
			return getLinkUrl();
		}
		Node node = getNode();
		String path = node.getInfoPathOrDef();
		Calendar now = Calendar.getInstance();
		now.setTime(getPublishDate());
		String year = String.valueOf(now.get(Calendar.YEAR));
		int m = now.get(Calendar.MONTH) + 1;
		int d = now.get(Calendar.DAY_OF_MONTH);
		String month = String.valueOf(m);
		String day = String.valueOf(d);
		if (m < 10) {
			month = "0" + month;
		}
		if (d < 10) {
			day = "0" + day;
		}
		path = StringUtils.replace(path, PATH_NODE_ID, node.getId().toString());
		String nodeNumber = node.getNumber();
		if (StringUtils.isBlank(nodeNumber)) {
			nodeNumber = node.getId().toString();
		}
		path = StringUtils.replace(path, PATH_NODE_NUMBER, nodeNumber);
		path = StringUtils.replace(path, PATH_INFO_ID, getId().toString());
		path = StringUtils.replace(path, PATH_YEAR, year);
		path = StringUtils.replace(path, PATH_MONTH, month);
		path = StringUtils.replace(path, PATH_DAY, day);
		if (page != null && page > 1) {
			path += "_" + page;
		}
		String extension = node.getInfoExtensionOrDef();
		if (StringUtils.isNotBlank(extension)) {
			path += extension;
		}
		Site site = getSite();
		String ctx = site.getContextPath();
		if (forRealPath && StringUtils.isNotBlank(node.getDomainPathOrParent())) {
			path = node.getDomainPathOrParent() + path;
		} else if (!forRealPath && StringUtils.isNotBlank(ctx)) {
			path = ctx + path;
		}
		if (isFull) {
			if (site.getPort() != null) {
				path = ":" + site.getPort() + path;
			}
			path = "//" + node.getDomainOrSite() + path;
		}
		return path;
	}

	@Transient
	public String getPageUrl(Integer page) {
		return getUrl(page);
	}

	@Transient
	public void addComments(int comments) {
		Set<InfoBuffer> buffers = getBuffers();
		if (buffers == null) {
			buffers = new HashSet<InfoBuffer>(1);
			setBuffers(buffers);
		}
		InfoBuffer buffer;
		if (buffers.isEmpty()) {
			buffer = new InfoBuffer();
			buffer.applyDefaultValue();
			buffer.setInfo(this);
			buffers.add(buffer);
		} else {
			buffer = buffers.iterator().next();
		}
		// TODO 根据设置处理缓冲。一般网站评论数量不会很多，暂时不设置缓冲。
		// buffer.setComments(buffer.getComments() + comments);
		setComments(getComments() + comments);

	}

	@Transient
	public Boolean getGenerate() {
		if (isLinked()) {
			return false;
		}
		Node node = getNode();
		return node != null ? node.getGenerateInfoOrDef() : null;
	}

	@Transient
	public String getTemplate() {
		String infoTemplate = getInfoTemplate();
		if (infoTemplate != null) {
			infoTemplate = getSite().getTemplate(infoTemplate);
		} else {
			Node node = getNode();
			if (node != null) {
				infoTemplate = node.getInfoTemplateOrDef();
			}
		}
		return infoTemplate;
	}

	@Transient
	public String getTagKeywords() {
		List<InfoTag> infoTags = getInfoTags();
		if (infoTags != null) {
			StringBuilder keywordsBuff = new StringBuilder();
			for (InfoTag infoTag : infoTags) {
				keywordsBuff.append(infoTag.getTag().getName()).append(',');
			}
			if (keywordsBuff.length() > 0) {
				keywordsBuff.setLength(keywordsBuff.length() - 1);
			}
			return keywordsBuff.toString();
		} else {
			return null;
		}
	}

	@Transient
	public String getKeywords() {
		String keywords = getTagKeywords();
		if (StringUtils.isBlank(keywords)) {
			return getTitle();
		} else {
			return keywords;
		}
	}

	@Transient
	public List<Node> getNodes() {
		List<InfoNode> infoNodes = getInfoNodes();
		int len = CollectionUtils.size(infoNodes);
		List<Node> nodes = new ArrayList<Node>(len);
		if (len > 0) {
			for (InfoNode infoNode : infoNodes) {
				nodes.add(infoNode.getNode());
			}
		}
		return nodes;
	}

	@Transient
	public List<Tag> getTags() {
		List<InfoTag> infoTags = getInfoTags();
		if (infoTags == null) {
			return null;
		}
		List<Tag> tags = new ArrayList<Tag>(infoTags.size());
		for (InfoTag infoTag : infoTags) {
			tags.add(infoTag.getTag());
		}
		return tags;
	}

	@Transient
	public List<Special> getSpecials() {
		List<InfoSpecial> infoSpecials = getInfoSpecials();
		if (infoSpecials == null) {
			return null;
		}
		List<Special> specials = new ArrayList<Special>(infoSpecials.size());
		for (InfoSpecial infoSpecial : infoSpecials) {
			specials.add(infoSpecial.getSpecial());
		}
		return specials;
	}

	@Transient
	public List<Attribute> getAttrs() {
		List<InfoAttribute> infoAttrs = getInfoAttrs();
		if (infoAttrs == null) {
			return null;
		}
		List<Attribute> attrs = new ArrayList<Attribute>(infoAttrs.size());
		for (InfoAttribute infoAttr : infoAttrs) {
			attrs.add(infoAttr.getAttribute());
		}
		return attrs;
	}

	@Transient
	public InfoAttribute getInfoAttr(Attribute attr) {
		Collection<InfoAttribute> infoAttrs = getInfoAttrs();
		if (!CollectionUtils.isEmpty(infoAttrs)) {
			for (InfoAttribute infoAttr : infoAttrs) {
				if (infoAttr.getAttribute().equals(attr)) {
					return infoAttr;
				}
			}
		}
		return null;
	}

	@Transient
	public InfoAttribute getInfoAttr(Integer attrId) {
		Collection<InfoAttribute> infoAttrs = getInfoAttrs();
		if (!CollectionUtils.isEmpty(infoAttrs)) {
			for (InfoAttribute infoAttr : infoAttrs) {
				if (infoAttr.getAttribute().getId().equals(attrId)) {
					return infoAttr;
				}
			}
		}
		return null;
	}

	@Transient
	public String getAttrImage(String attr) {
		Collection<InfoAttribute> infoAttrs = getInfoAttrs();
		for (InfoAttribute infoAttr : infoAttrs) {
			if (infoAttr.getAttribute().getNumber().equals(attr)) {
				return infoAttr.getImage();
			}
		}
		return null;
	}

	@Transient
	public String getAttrImageUrl(String attr) {
		String url = getAttrImage(attr);
		return StringUtils.isBlank(url) ? getSite().getNoPictureUrl() : url;
	}

	@Transient
	public String getAttrImage(Integer attrId) {
		Collection<InfoAttribute> infoAttrs = getInfoAttrs();
		Attribute attr;
		for (InfoAttribute infoAttr : infoAttrs) {
			attr = infoAttr.getAttribute();
			if ((attrId == null && attr.getWithImage())
					|| attr.getId().equals(attrId)) {
				return infoAttr.getImage();
			}
		}
		return null;
	}

	@Transient
	public String getAttrImageUrl(Integer attrId) {
		String url = getAttrImage(attrId);
		return StringUtils.isBlank(url) ? getSite().getNoPictureUrl() : url;
	}

	@Transient
	public String getAttrImage() {
		return getAttrImage(getAttrId());
	}

	@Transient
	public String getAttrImageUrl() {
		String url = getAttrImage();
		return StringUtils.isBlank(url) ? getSite().getNoPictureUrl() : url;
	}

	@Transient
	public ImageAnchor getAttrImageBean() {
		Integer attrId = getAttrId();
		InfoAttribute infoAttr = getInfoAttr(attrId);
		ImageAnchorBean bean = new ImageAnchorBean();
		bean.setTitle(getTitle());
		bean.setUrl(getUrl());
		bean.setSrc(getAttrImageUrl(attrId));
		if (infoAttr != null) {
			Attribute attr = infoAttr.getAttribute();
			bean.setWidth(attr.getImageWidth());
			bean.setHeight(attr.getImageHeight());
		}
		return bean;
	}

	@Transient
	public String getText() {
		Map<String, String> clobs = getClobs();
		return clobs != null ? clobs.get(INFO_TEXT) : null;
	}

	@Transient
	public void setText(String text) {
		Map<String, String> clobs = getClobs();
		if (clobs == null) {
			clobs = new HashMap<String, String>();
			setClobs(clobs);
		}
		clobs.put(INFO_TEXT, text);
	}

	@Transient
	public Model getModel() {
		return getNode() != null ? getNode().getInfoModel() : null;
	}

	@Transient
	public Workflow getWorkflow() {
		return getNode() != null ? getNode().getWorkflow() : null;
	}

	@Transient
	public String getTitle() {
		return getDetail() != null ? getDetail().getTitle() : null;
	}

	@Transient
	public String getSubtitle() {
		return getDetail() != null ? getDetail().getSubtitle() : null;
	}

	@Transient
	public String getFullTitle() {
		return getDetail() != null ? getDetail().getFullTitle() : null;
	}

	@Transient
	public String getFullTitleOrTitle() {
		String fullTitle = getFullTitle();
		return StringUtils.isNotBlank(fullTitle) ? fullTitle : getTitle();
	}

	@Transient
	public String getLink() {
		return getDetail() != null ? getDetail().getLink() : null;
	}

	@Transient
	public String getLinkUrl() {
		String link = getLink();
		if (StringUtils.isBlank(link)) {
			return link;
		}
		if (link.startsWith("/") && !link.startsWith("//")) {
			StringBuilder sb = new StringBuilder();
			Site site = getSite();
			if (site.getWithDomain()) {
				sb.append("//").append(site.getDomain());
				if (site.getPort() != null) {
					sb.append(":").append(site.getPort());
				}
			}
			String ctx = site.getContextPath();
			if (StringUtils.isNotBlank(ctx)) {
				sb.append(ctx);
			}
			sb.append(link);
			link = sb.toString();
		}
		return link;
	}

	@Transient
	public boolean isLinked() {
		return StringUtils.isNotBlank(getLink());
	}

	@Transient
	public Boolean getNewWindow() {
		return getDetail() != null ? getDetail().getNewWindow() : null;
	}

	@Transient
	public String getColor() {
		return getDetail() != null ? getDetail().getColor() : null;
	}

	@Transient
	public Boolean getStrong() {
		return getDetail() != null ? getDetail().getStrong() : null;
	}

	@Transient
	public Boolean getEm() {
		return getDetail() != null ? getDetail().getEm() : null;
	}

	@Transient
	public String getMetaDescription() {
		return getDetail() != null ? getDetail().getMetaDescription() : null;
	}

	@Transient
	public String getInfoPath() {
		return getDetail() != null ? getDetail().getInfoPath() : null;
	}

	@Transient
	public String getInfoTemplate() {
		return getDetail() != null ? getDetail().getInfoTemplate() : null;
	}

	/**
	 * 来源
	 * 
	 * @return
	 */
	@Transient
	public String getSource() {
		return getDetail() != null ? getDetail().getSource() : null;
	}

	/**
	 * 来源url
	 * 
	 * @return
	 */
	@Transient
	public String getSourceUrl() {
		return getDetail() != null ? getDetail().getSourceUrl() : null;
	}

	/**
	 * 作者
	 * 
	 * @return
	 */
	@Transient
	public String getAuthor() {
		return getDetail() != null ? getDetail().getAuthor() : null;
	}

	@Transient
	public String getSmallImage() {
		return getDetail() != null ? getDetail().getSmallImage() : null;
	}

	@Transient
	public String getSmallImageUrl() {
		String url = getSmallImage();
		return StringUtils.isBlank(url) ? getSite().getNoPictureUrl() : url;
	}

	@Transient
	public ImageAnchor getSmallImageBean() {
		ImageAnchorBean bean = new ImageAnchorBean();
		bean.setTitle(getTitle());
		bean.setUrl(getUrl());
		bean.setSrc(getSmallImageUrl());
		Model model = getModel();
		if (model == null) {
			return bean;
		}
		ModelField mf = model.getField("smallImage");
		if (mf == null) {
			return bean;
		}
		Map<String, String> map = mf.getCustoms();
		String width = map.get(ModelField.IMAGE_WIDTH);
		if (StringUtils.isNotBlank(width)) {
			bean.setWidth(NumberUtils.parseNumber(width, Integer.class, null));
		}
		String height = map.get(ModelField.IMAGE_HEIGHT);
		if (StringUtils.isNotBlank(height)) {
			bean.setHeight(NumberUtils.parseNumber(height, Integer.class, null));
		}
		return bean;
	}

	@Transient
	public String getLargeImage() {
		return getDetail() != null ? getDetail().getLargeImage() : null;
	}

	@Transient
	public String getLargeImageUrl() {
		String url = getLargeImage();
		return StringUtils.isBlank(url) ? getSite().getNoPictureUrl() : url;
	}

	@Transient
	public ImageAnchor getLargeImageBean() {
		ImageAnchorBean bean = new ImageAnchorBean();
		bean.setTitle(getTitle());
		bean.setUrl(getUrl());
		bean.setSrc(getLargeImageUrl());
		Model model = getModel();
		if (model == null) {
			return bean;
		}
		ModelField mf = model.getField("largeImage");
		if (mf == null) {
			return bean;
		}
		Map<String, String> map = mf.getCustoms();
		String width = map.get(ModelField.IMAGE_WIDTH);
		if (StringUtils.isNotBlank(width)) {
			bean.setWidth(NumberUtils.parseNumber(width, Integer.class, null));
		}
		String height = map.get(ModelField.IMAGE_HEIGHT);
		if (StringUtils.isNotBlank(height)) {
			bean.setHeight(NumberUtils.parseNumber(height, Integer.class, null));
		}
		return bean;
	}

	@Transient
	public String getVideo() {
		return getDetail() != null ? getDetail().getVideo() : null;
	}

	@Transient
	public String getVideoName() {
		return getDetail() != null ? getDetail().getVideoName() : null;
	}

	@Transient
	public String getFile() {
		return getDetail() != null ? getDetail().getFile() : null;
	}

	@Transient
	public String getFileName() {
		return getDetail() != null ? getDetail().getFileName() : null;
	}

	@Transient
	public Long getFileLength() {
		return getDetail() != null ? getDetail().getFileLength() : null;
	}

	@Transient
	public String getFileExtension() {
		return FilenameUtils.getExtension(getFile());
	}

	@Transient
	public String getFileSize() {
		Long length = getFileLength();
		return Files.getSize(length);
	}

	@Transient
	public List<Node> getNodesExcludeSelf() {
		List<Node> nodes = getNodes();
		List<Node> list = new ArrayList<Node>(nodes.size() - 1);
		for (int i = 0, len = nodes.size() - 1; i < len; i++) {
			list.add(nodes.get(i));
		}
		return list;
	}

	@Transient
	public Integer getBufferViews() {
		InfoBuffer buffer = getBuffer();
		if (buffer != null) {
			return getViews() + buffer.getViews();
		} else {
			return getViews();
		}
	}

	@Transient
	public Integer getBufferDownloads() {
		InfoBuffer buffer = getBuffer();
		if (buffer != null) {
			return getDownloads() + buffer.getDownloads();
		} else {
			return getDownloads();
		}
	}

	@Transient
	public Integer getBufferComments() {
		InfoBuffer buffer = getBuffer();
		if (buffer != null) {
			return getComments() + buffer.getComments();
		} else {
			return getComments();
		}
	}

	@Transient
	public Integer getBufferInvolveds() {
		InfoBuffer buffer = getBuffer();
		if (buffer != null) {
			return buffer.getInvolveds();
		} else {
			return 0;
		}
	}

	@Transient
	public Integer getBufferDiggs() {
		InfoBuffer buffer = getBuffer();
		if (buffer != null) {
			return getDiggs() + buffer.getDiggs();
		} else {
			return getDiggs();
		}
	}

	@Transient
	public Integer getBufferBurys() {
		InfoBuffer buffer = getBuffer();
		if (buffer != null) {
			return buffer.getBurys();
		} else {
			return 0;
		}
	}

	@Transient
	public InfoBuffer getBuffer() {
		Set<InfoBuffer> set = getBuffers();
		if (!CollectionUtils.isEmpty(set)) {
			return set.iterator().next();
		} else {
			return null;
		}
	}

	@Transient
	public void setBuffer(InfoBuffer buffer) {
		Set<InfoBuffer> set = getBuffers();
		if (set == null) {
			set = new HashSet<InfoBuffer>(1);
			setBuffers(set);
		} else {
			set.clear();
		}
		set.add(buffer);
	}

	/**
	 * 页数线程变量
	 */
	private static ThreadLocal<Integer> attrIdHolder = new ThreadLocal<Integer>();

	@Transient
	public static void setAttrId(Integer attrId) {
		attrIdHolder.set(attrId);
	}

	@Transient
	public static Integer getAttrId() {
		return attrIdHolder.get();
	}

	@Transient
	public static void resetAttrId() {
		attrIdHolder.remove();
	}

	@Transient
	public void applyDefaultValue() {
		if (getOrg() == null) {
			setOrg(getCreator().getOrg());
		}
		if (getViews() == null) {
			setViews(0);
		}
		if (getDownloads() == null) {
			setDownloads(0);
		}
		if (getComments() == null) {
			setComments(0);
		}
		if (getDiggs() == null) {
			setDiggs(0);
		}
		if (getScore() == null) {
			setScore(0);
		}
		if (getWithImage() == null) {
			setWithImage(false);
		}
		if (getPriority() == null) {
			setPriority(0);
		}
		if (getPublishDate() == null) {
			setPublishDate(new Timestamp(System.currentTimeMillis()));
		}
		if (getStatus() == null) {
			setStatus(NORMAL);
		}
	}

	private Integer id;
	private List<InfoNode> infoNodes = new ArrayList<InfoNode>();
	private List<InfoTag> infoTags = new ArrayList<InfoTag>();
	private List<InfoSpecial> infoSpecials = new ArrayList<InfoSpecial>();
	private List<InfoAttribute> infoAttrs = new ArrayList<InfoAttribute>();
	private List<InfoImage> images = new ArrayList<InfoImage>();
	private List<InfoFile> files = new ArrayList<InfoFile>();
	private Map<String, String> customs = new HashMap<String, String>();
	private Map<String, String> clobs = new HashMap<String, String>();
	private Set<InfoBuffer> buffers = new HashSet<InfoBuffer>();

	private Node node;
	private Org org;
	private User creator;
	private Site site;
	private InfoDetail detail;

	private Date publishDate;
	private Integer priority;
	private Boolean withImage;
	private Integer views;
	private Integer downloads;
	private Integer comments;
	private Integer diggs;
	private Integer score;
	private String status;
	private Byte p1;
	private Byte p2;
	private Byte p3;
	private Byte p4;
	private Byte p5;
	private Byte p6;

	public Info() {
	}

	public Info(Node node, User creator, Site site) {
		this.node = node;
		this.creator = creator;
		this.site = site;
	}

	@Id
	@Column(name = "f_info_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_info", pkColumnValue = "cms_info", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_info")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "info")
	@OrderBy("nodeIndex")
	public List<InfoNode> getInfoNodes() {
		return infoNodes;
	}

	public void setInfoNodes(List<InfoNode> infoNodes) {
		this.infoNodes = infoNodes;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "info")
	@OrderBy("tagIndex")
	public List<InfoTag> getInfoTags() {
		return infoTags;
	}

	public void setInfoTags(List<InfoTag> infoTags) {
		this.infoTags = infoTags;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "info")
	@OrderBy("specialIndex")
	public List<InfoSpecial> getInfoSpecials() {
		return infoSpecials;
	}

	public void setInfoSpecials(List<InfoSpecial> infoSpecials) {
		this.infoSpecials = infoSpecials;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "info")
	@OrderBy("attribute asc")
	public List<InfoAttribute> getInfoAttrs() {
		return infoAttrs;
	}

	public void setInfoAttrs(List<InfoAttribute> infoAttrs) {
		this.infoAttrs = infoAttrs;
	}

	@ElementCollection
	@CollectionTable(name = "cms_info_image", joinColumns = @JoinColumn(name = "f_info_id"))
	@OrderColumn(name = "f_index")
	public List<InfoImage> getImages() {
		return images;
	}

	public void setImages(List<InfoImage> images) {
		this.images = images;
	}

	@ElementCollection
	@CollectionTable(name = "cms_info_file", joinColumns = @JoinColumn(name = "f_info_id"))
	@OrderColumn(name = "f_index")
	public List<InfoFile> getFiles() {
		return files;
	}

	public void setFiles(List<InfoFile> files) {
		this.files = files;
	}

	@ElementCollection
	@CollectionTable(name = "cms_info_custom", joinColumns = @JoinColumn(name = "f_info_id"))
	@MapKeyColumn(name = "f_key", length = 50)
	@Column(name = "f_value", length = 2000)
	public Map<String, String> getCustoms() {
		return this.customs;
	}

	public void setCustoms(Map<String, String> customs) {
		this.customs = customs;
	}

	@ElementCollection
	@CollectionTable(name = "cms_info_clob", joinColumns = @JoinColumn(name = "f_info_id"))
	@MapKeyColumn(name = "f_key", length = 50)
	@MapKeyType(value = @Type(type = "string"))
	@Lob
	@Column(name = "f_value", nullable = false)
	public Map<String, String> getClobs() {
		return this.clobs;
	}

	public void setClobs(Map<String, String> clobs) {
		this.clobs = clobs;
	}

	// 为了addComments的处理，级联加上PERSIST。
	@OneToMany(fetch = FetchType.LAZY, cascade = { PERSIST, REMOVE }, mappedBy = "info")
	public Set<InfoBuffer> getBuffers() {
		return buffers;
	}

	public void setBuffers(Set<InfoBuffer> buffers) {
		this.buffers = buffers;
	}

	@OneToOne(cascade = { REMOVE }, mappedBy = "info")
	public InfoDetail getDetail() {
		return detail;
	}

	public void setDetail(InfoDetail detail) {
		this.detail = detail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_node_id", nullable = false)
	public Node getNode() {
		return this.node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_org_id", nullable = false)
	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_creator_id", nullable = false)
	public User getCreator() {
		return this.creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_site_id", nullable = false)
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "f_publish_date", nullable = false, length = 19)
	public Date getPublishDate() {
		return this.publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	@Column(name = "f_priority", nullable = false)
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Column(name = "f_is_with_image", nullable = false, length = 1)
	public Boolean getWithImage() {
		return this.withImage;
	}

	public void setWithImage(Boolean withImage) {
		this.withImage = withImage;
	}

	@Column(name = "f_views", nullable = false)
	public Integer getViews() {
		return this.views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@Column(name = "f_downloads", nullable = false)
	public Integer getDownloads() {
		return this.downloads;
	}

	public void setDownloads(Integer downloads) {
		this.downloads = downloads;
	}

	@Column(name = "f_comments", nullable = false)
	public Integer getComments() {
		return this.comments;
	}

	public void setComments(Integer comments) {
		this.comments = comments;
	}

	@Column(name = "f_diggs", nullable = false)
	public Integer getDiggs() {
		return diggs;
	}

	public void setDiggs(Integer diggs) {
		this.diggs = diggs;
	}

	@Column(name = "f_score", nullable = false)
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Column(name = "f_status", nullable = false, length = 1)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "f_p1")
	public Byte getP1() {
		return this.p1;
	}

	public void setP1(Byte p1) {
		this.p1 = p1;
	}

	@Column(name = "f_p2")
	public Byte getP2() {
		return this.p2;
	}

	public void setP2(Byte p2) {
		this.p2 = p2;
	}

	@Column(name = "f_p3")
	public Byte getP3() {
		return this.p3;
	}

	public void setP3(Byte p3) {
		this.p3 = p3;
	}

	@Column(name = "f_p4")
	public Byte getP4() {
		return this.p4;
	}

	public void setP4(Byte p4) {
		this.p4 = p4;
	}

	@Column(name = "f_p5")
	public Byte getP5() {
		return this.p5;
	}

	public void setP5(Byte p5) {
		this.p5 = p5;
	}

	@Column(name = "f_p6")
	public Byte getP6() {
		return this.p6;
	}

	public void setP6(Byte p6) {
		this.p6 = p6;
	}
}

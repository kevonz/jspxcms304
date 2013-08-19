package com.jspxcms.core.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * VoteMark
 * 
 * 投票标记实体类
 * 
 * @author liufang
 * 
 */
@Entity
@Table(name = "cms_vote_mark")
public class VoteMark implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public static final String COOKIE_NAME = "jspxcms";

	private static String randomCookie() {
		String cookie = UUID.randomUUID().toString();
		cookie = StringUtils.remove(cookie, '-');
		return cookie;
	}

	public static String getCookie(HttpServletRequest request,
			HttpServletResponse response) {
		String value;
		Cookie cookie = WebUtils.getCookie(request, VoteMark.COOKIE_NAME);
		if (cookie != null && StringUtils.isNotBlank(cookie.getValue())) {
			value = cookie.getValue();
		} else {
			value = randomCookie();
			cookie = new Cookie(VoteMark.COOKIE_NAME, value);
			String ctx = request.getContextPath();
			if (StringUtils.isBlank(ctx)) {
				ctx = "/";
			}
			cookie.setPath(ctx);
			cookie.setMaxAge(Integer.MAX_VALUE);
			response.addCookie(cookie);
		}
		return value;
	}

	/**
	 * 用户ID
	 */
	public static final int USER_ID = 1;
	/**
	 * IP
	 */
	public static final int IP = 2;
	/**
	 * Cookie
	 */
	public static final int COOKIE = 3;

	public void applyDefaultValue() {
		if (getDate() == null) {
			setDate(new Timestamp(System.currentTimeMillis()));
		}
	}

	private Integer id;
	private User user;
	private String ftype;
	private Integer fid;
	private Date date;
	private String ip;
	private String cookie;

	@Id
	@Column(name = "f_votemark_id", unique = true, nullable = false)
	@TableGenerator(name = "tg_cms_vote_mark", pkColumnValue = "cms_vote_mark", table = "t_id_table", pkColumnName = "f_table", valueColumnName = "f_id_value", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_vote_mark")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_user_id", nullable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "f_ftype", nullable = false, length = 50)
	public String getFtype() {
		return this.ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}

	@Column(name = "f_fid", nullable = false)
	public Integer getFid() {
		return this.fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "f_date", nullable = false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "f_ip", nullable = false, length = 100)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "f_cookie", nullable = false, length = 100)
	public String getCookie() {
		return this.cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

}

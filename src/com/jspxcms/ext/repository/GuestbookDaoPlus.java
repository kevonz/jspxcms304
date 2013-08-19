package com.jspxcms.ext.repository;

import java.util.List;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.ext.domain.Guestbook;

/**
 * GuestbookDaoPlus
 * 
 * @author yangxing
 * 
 */
public interface GuestbookDaoPlus {
	public List<Guestbook> findList(Integer[] siteId, String[] type, Integer[] typeId,
			Boolean isRecommend, Integer[] status, Limitable limitable);
}

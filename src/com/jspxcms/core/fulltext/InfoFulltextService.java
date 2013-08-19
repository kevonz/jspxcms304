package com.jspxcms.core.fulltext;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Node;

/**
 * InfoFulltextService
 * 
 * @author liufang
 * 
 */
public interface InfoFulltextService {
	public List<Info> list(Integer[] siteIds, Integer[] nodeIds,
			Integer[] attrIds, Date startDate, Date endDate, String[] status,
			Integer[] excludeId, String q, String title, String keywords,
			String description, String text, Limitable limitable);

	public Page<Info> page(Integer[] siteIds, Integer[] nodeIds,
			Integer[] attrIds, Date startDate, Date endDate, String[] status,
			Integer[] excludeId, String q, String title, String keywords,
			String description, String text, Pageable pageable);

	public int addDocumentWhole(Collection<Node> nodes);
}

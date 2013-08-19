package com.jspxcms.core.fulltext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.common.fulltext.LuceneIndexTemplate;
import com.jspxcms.common.orm.Limitable;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Node;
import com.jspxcms.core.service.InfoQueryService;

/**
 * InfoFulltextServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional(readOnly = true)
public class InfoFulltextServiceImpl implements InfoFulltextService {
	public List<Info> list(Integer[] siteIds, Integer[] nodeIds,
			Integer[] attrIds, Date startDate, Date endDate, String[] status,
			Integer[] excludeId, String q, String title, String keywords,
			String description, String text, Limitable limitable) {
		Query query = FInfo.query(analyzer, siteIds, nodeIds, attrIds,
				startDate, endDate, status, excludeId, q, title, keywords,
				description, text);
		List<String> idList = template.list(query, FInfo.ID, limitable);
		List<Integer> ids = FInfo.idsFromString(idList);
		List<Info> list;
		if (!ids.isEmpty()) {
			List<Info> temp = service.findAll(ids);
			list = new ArrayList<Info>(temp.size());
			for (int i = ids.size() - 1; i >= 0; i--) {
				for (Info info : temp) {
					if (info.getId().equals(ids.get(i))) {
						list.add(info);
						break;
					}
				}
			}
		} else {
			list = Collections.emptyList();
		}
		return list;
		// Predicate predicate = FInfo.query(siteIds, nodeIds, attrIds,
		// startDate,
		// endDate, status, excludeId, q, title, keywords, description,
		// text);
		// List<Document> docList = template.list(predicate, limitable);
		// List<Integer> ids = FInfo.idsFromDoc(docList);
		// List<Info> list;
		// if (!ids.isEmpty()) {
		// list = service.findAll(ids);
		// } else {
		// list = Collections.emptyList();
		// }
		// return list;
	}

	public Page<Info> page(Integer[] siteIds, Integer[] nodeIds,
			Integer[] attrIds, Date startDate, Date endDate, String[] status,
			Integer[] excludeId, String q, String title, String keywords,
			String description, String text, Pageable pageable) {
		Query query = FInfo.query(analyzer, siteIds, nodeIds, attrIds,
				startDate, endDate, status, excludeId, q, title, keywords,
				description, text);
		Page<String> idPage = template.page(query, FInfo.ID, pageable);
		List<Integer> ids = FInfo.idsFromString(idPage.getContent());
		List<Info> content;
		if (!ids.isEmpty()) {
			List<Info> temp = service.findAll(ids);
			content = new ArrayList<Info>(temp.size());
			for (int i = ids.size() - 1; i >= 0; i--) {
				for (Info info : temp) {
					if (info.getId().equals(ids.get(i))) {
						content.add(info);
						break;
					}
				}
			}
		} else {
			content = Collections.emptyList();
		}
		return new PageImpl<Info>(content, pageable, idPage.getTotalElements());
	}

	public int addDocumentWhole(Collection<Node> nodes) {
		int size = nodes.size();
		Set<Integer> ids = new HashSet<Integer>(size);
		Set<String> tns = new HashSet<String>(size);
		for (Node node : nodes) {
			ids.add(node.getId());
			tns.add(node.getTreeNumber());
		}
		Integer[] nodeIds = ids.toArray(new Integer[size]);
		String[] treeNumber = tns.toArray(new String[size]);
		Query query = FInfo.query(analyzer, null, nodeIds, null, null, null,
				null, null, null, null, null, null, null);
		template.deleteDocuments(query);
		return dao.addDocumentWhole(treeNumber);
	}

	private Analyzer analyzer;
	private LuceneIndexTemplate template;
	private InfoQueryService service;
	private InfoFulltextDao dao;

	@Autowired
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	@Autowired
	public void setTemplate(LuceneIndexTemplate template) {
		this.template = template;
	}

	@Autowired
	public void setInfoQueryService(InfoQueryService service) {
		this.service = service;
	}

	@Autowired
	public void setDao(InfoFulltextDao dao) {
		this.dao = dao;
	}
}

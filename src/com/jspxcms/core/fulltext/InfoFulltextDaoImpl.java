package com.jspxcms.core.fulltext;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jspxcms.common.fulltext.LuceneIndexTemplate;
import com.jspxcms.core.domain.Info;

/**
 * InfoFulltextDaoImpl
 * 
 * @author liufang
 * 
 */
@Repository
public class InfoFulltextDaoImpl implements InfoFulltextDao {
	public int addDocumentWhole(String[] treeNumber) {
		Session session = (Session) em.getDelegate();
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder(
				"from Info bean where bean.status='A' and (1=2");
		for (int i = 0, len = treeNumber.length; i < len; i++) {
			hql.append(" or bean.node.treeNumber like :treeNumber").append(i);
			params.put("treeNumber" + i, treeNumber[i] + "%");
		}
		hql.append(")");
		Query query = session.createQuery(hql.toString());
		query.setProperties(params);
		query.setCacheMode(CacheMode.IGNORE);
		ScrollableResults infos = query.scroll(ScrollMode.FORWARD_ONLY);
		Info info;
		int count = 0;
		while (infos.next()) {
			info = (Info) infos.get(0);
			template.addDocument(FInfo.doc(info));
			if (++count % 20 == 0) {
				session.clear();
			}
		}
		return count;
	}

	private LuceneIndexTemplate template;

	@Autowired
	public void setLuceneIndexTemplate(LuceneIndexTemplate template) {
		this.template = template;
	}

	private EntityManager em;

	@javax.persistence.PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}
}

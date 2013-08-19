package com.jspxcms.core.generatepage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.jspxcms.common.web.PathResolver;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Node;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * GeneratePageDaoImpl
 * 
 * @author liufang
 * 
 */
@Repository
public class GeneratePageDaoImpl implements GeneratePageDao {
	public int generateNodeWhole(String[] treeNumber, Configuration config,
			PathResolver resolver) throws IOException, TemplateException {
		Session session = em.unwrap(Session.class);
//		Session session = (Session) em.getDelegate();
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder("from Node bean where (1=2");
		for (int i = 0, len = treeNumber.length; i < len; i++) {
			hql.append(" or bean.treeNumber like :treeNumber").append(i);
			params.put("treeNumber" + i, treeNumber[i] + "%");
		}
		hql.append(")");
		Query query = session.createQuery(hql.toString());
		query.setProperties(params);
		query.setCacheMode(CacheMode.IGNORE);
		ScrollableResults nodes = query.scroll(ScrollMode.FORWARD_ONLY);
		Node node;
		int count = 0;
		while (nodes.next()) {
			node = (Node) nodes.get(0);
			PNode.generate(node, config, resolver);
			// 一个节点可能有很多翻页，这里稍微设置小一点
			if (++count % 5 == 0) {
				session.clear();
			}
		}
		return count;

	}

	public int generateInfoWhole(String[] treeNumber, Configuration config,
			PathResolver resolver) throws IOException, TemplateException {
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
			PInfo.generate(info, config, resolver);
			if (++count % 20 == 0) {
				session.clear();
			}
		}
		return count;
	}

	private EntityManager em;

	@javax.persistence.PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}
}

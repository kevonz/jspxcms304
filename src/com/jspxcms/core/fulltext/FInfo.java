package com.jspxcms.core.fulltext;

import static org.apache.lucene.document.Field.Index.ANALYZED;
import static org.apache.lucene.document.Field.Index.NOT_ANALYZED;
import static org.apache.lucene.document.Field.Store.NO;
import static org.apache.lucene.document.Field.Store.YES;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;
import static org.apache.lucene.util.Version.LUCENE_36;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.NumericUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.util.NumberUtils;

import com.jspxcms.common.fulltext.LuceneException;
import com.jspxcms.core.domain.Attribute;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Node;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.PathMetadataFactory;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.ArrayPath;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

/**
 * 信息实体的全文检索转换类
 * 
 * @author liufang
 * 
 */
public class FInfo extends EntityPathBase<Document> {
	private static final long serialVersionUID = 1L;

	public FInfo(String var) {
		super(Document.class, PathMetadataFactory.forVariable(var));
	}

	public static final FInfo info = new FInfo("info");

	public static final String ID = "id";
	public static final String SITE_ID = "siteId";
	public static final String NODE_ID = "nodeId";
	public static final String ATTR_ID = "attrId";
	public static final String PUBLISH_DATE = "publishDate";
	public static final String STATUS = "status";
	public static final String TITLE = "title";
	public static final String KEYWORDS = "keywords";
	public static final String DESCRIPTION = "description";
	public static final String TEXT = "text";

	public final NumberPath<Integer> id = createNumber(ID, Integer.class);
	public final NumberPath<Integer> siteId = createNumber(SITE_ID,
			Integer.class);
	public final ArrayPath<Integer> nodeId = createArray(NODE_ID,
			Integer[].class);
	public final ArrayPath<Integer> attrId = createArray(ATTR_ID,
			Integer[].class);
	public final DateTimePath<Date> publishDate = createDateTime(PUBLISH_DATE,
			Date.class);
	public final StringPath status = createString(STATUS);
	public final StringPath title = createString(TITLE);
	public final StringPath keywords = createString(KEYWORDS);
	public final StringPath description = createString(DESCRIPTION);
	public final StringPath text = createString(TEXT);

	public static List<Integer> idsFromDoc(List<Document> docList) {
		if (!docList.isEmpty()) {
			List<Integer> ids = new ArrayList<Integer>(docList.size());
			for (Document doc : docList) {
				ids.add(NumberUtils.parseNumber(doc.get(ID), Integer.class));
			}
			return ids;
		} else {
			return Collections.emptyList();
		}
	}

	public static List<Integer> idsFromString(List<String> idList) {
		if (!idList.isEmpty()) {
			List<Integer> ids = new ArrayList<Integer>(idList.size());
			for (String id : idList) {
				ids.add(NumberUtils.parseNumber(id, Integer.class));
			}
			return ids;
		} else {
			return Collections.emptyList();
		}
	}

	public static Term id(Integer id) {
		return new Term(FInfo.ID, NumericUtils.intToPrefixCoded(id));
	}

	public static Document doc(Info info) {
		Document doc = new Document();

		NumericField id = new NumericField(ID, YES, true);
		doc.add(id.setIntValue(info.getId()));

		NumericField siteId = new NumericField(SITE_ID);
		doc.add(siteId.setIntValue(info.getSite().getId()));

		NumericField nodeId = new NumericField(NODE_ID);
		Node node = info.getNode();
		while (node != null) {
			doc.add(nodeId.setIntValue(node.getId()));
			node = node.getParent();
		}

		List<Attribute> attrs = info.getAttrs();
		if (!CollectionUtils.isEmpty(attrs)) {
			for (Attribute attr : attrs) {
				doc.add(new NumericField(ATTR_ID).setIntValue(attr.getId()));
			}
		}

		NumericField publishDate = new NumericField(PUBLISH_DATE);
		doc.add(publishDate.setLongValue(info.getPublishDate().getTime()));

		doc.add(new Field(STATUS, info.getStatus(), NO, NOT_ANALYZED));

		StringBuilder titleBuff = new StringBuilder();
		String title = info.getTitle();
		if (StringUtils.isNotBlank(title)) {
			titleBuff.append(title);
		}
		title = info.getSubtitle();
		if (StringUtils.isNotBlank(title)) {
			titleBuff.append(title);
		}
		title = info.getFullTitle();
		if (StringUtils.isNotBlank(title)) {
			titleBuff.append(title);
		}
		title = titleBuff.toString();
		if (StringUtils.isNotBlank(title)) {
			doc.add(new Field(TITLE, title, NO, ANALYZED));
		}
		String keywords = info.getTagKeywords();
		if (StringUtils.isNotBlank(keywords)) {
			doc.add(new Field(KEYWORDS, keywords, NO, ANALYZED));
		}
		String description = info.getMetaDescription();
		if (StringUtils.isNotBlank(description)) {
			doc.add(new Field(DESCRIPTION, description, NO, ANALYZED));
		}
		String text = info.getPlainText();
		if (StringUtils.isNotBlank(text)) {
			doc.add(new Field(TEXT, text, NO, ANALYZED));
		}
		return doc;
	}

	public static Query query(Analyzer analyzer, Integer[] siteIds,
			Integer[] nodeIds, Integer[] attrIds, Date startDate, Date endDate,
			String[] status, Integer[] excludeId, String q, String title,
			String keywords, String description, String text) {
		try {
			BooleanQuery query = new BooleanQuery();
			if (ArrayUtils.isNotEmpty(siteIds)) {
				BooleanQuery qy = new BooleanQuery();
				for (Integer id : siteIds) {
					String s = NumericUtils.intToPrefixCoded(id);
					qy.add(new TermQuery(new Term(SITE_ID, s)), SHOULD);
				}
				query.add(qy, MUST);
			}
			if (ArrayUtils.isNotEmpty(nodeIds)) {
				BooleanQuery qy = new BooleanQuery();
				for (Integer id : nodeIds) {
					String s = NumericUtils.intToPrefixCoded(id);
					qy.add(new TermQuery(new Term(NODE_ID, s)), SHOULD);
				}
				query.add(qy, MUST);
			}
			if (ArrayUtils.isNotEmpty(attrIds)) {
				BooleanQuery qy = new BooleanQuery();
				for (Integer id : attrIds) {
					String s = NumericUtils.intToPrefixCoded(id);
					qy.add(new TermQuery(new Term(ATTR_ID, s)), SHOULD);
				}
				query.add(qy, MUST);
			}
			if (startDate != null || endDate != null) {
				Long min = startDate != null ? startDate.getTime() : null;
				Long max = endDate != null ? endDate.getTime() : null;
				NumericRangeQuery.newLongRange(PUBLISH_DATE, min, max, true,
						true);
			}
			if (ArrayUtils.isNotEmpty(status)) {
				BooleanQuery qy = new BooleanQuery();
				for (String s : status) {
					qy.add(new TermQuery(new Term(STATUS, s)), SHOULD);
				}
				query.add(qy, MUST);
			}
			if (ArrayUtils.isNotEmpty(excludeId)) {
				for (Integer id : excludeId) {
					query.add(new TermQuery(id(id)), MUST_NOT);
				}
			}
			if (StringUtils.isNotBlank(q)) {
				Query qy = MultiFieldQueryParser.parse(LUCENE_36, q,
						new String[] { TITLE, KEYWORDS, DESCRIPTION, TEXT },
						new Occur[] { SHOULD, SHOULD, SHOULD, SHOULD },
						analyzer);
				query.add(qy, MUST);
			}
			if (StringUtils.isNotBlank(title)) {
				QueryParser p = new QueryParser(LUCENE_36, TITLE, analyzer);
				query.add(p.parse(title), MUST);
			}
			if (StringUtils.isNotBlank(keywords)) {
				QueryParser p = new QueryParser(LUCENE_36, KEYWORDS, analyzer);
				query.add(p.parse(title), MUST);
			}
			if (StringUtils.isNotBlank(description)) {
				QueryParser p = new QueryParser(LUCENE_36, DESCRIPTION,
						analyzer);
				query.add(p.parse(title), MUST);
			}
			if (StringUtils.isNotBlank(text)) {
				QueryParser p = new QueryParser(LUCENE_36, TEXT, analyzer);
				query.add(p.parse(title), MUST);
			}
			return query;
		} catch (Exception e) {
			throw new LuceneException("Error during create query.", e);
		}
	}

	public static Predicate query(Integer[] siteIds, Integer[] nodeIds,
			Integer[] attrIds, Date startDate, Date endDate, String[] status,
			Integer[] excludeId, String q, String title, String keywords,
			String description, String text) {
		FInfo info = FInfo.info;
		BooleanBuilder exp = new BooleanBuilder();
		if (ArrayUtils.isNotEmpty(siteIds)) {
			exp = exp.and(info.siteId.in(siteIds));
		}
		if (ArrayUtils.isNotEmpty(nodeIds)) {
			exp = exp.and(info.nodeId.in(nodeIds));
		}
		if (ArrayUtils.isNotEmpty(attrIds)) {
			exp = exp.and(info.attrId.in(attrIds));
		}
		if (startDate != null) {
			exp = exp.and(info.publishDate.goe(startDate));
		}
		if (endDate != null) {
			exp = exp.and(info.publishDate.loe(endDate));
		}
		if (ArrayUtils.isNotEmpty(status)) {
			exp = exp.and(info.status.in(status));
		}
		if (ArrayUtils.isNotEmpty(excludeId)) {
			exp = exp.and(info.id.notIn(excludeId));
		}
		if (StringUtils.isNotBlank(q)) {
			exp = exp
					.and(info.title.contains(q).or(info.keywords.contains(q))
							.or(info.description.contains(q))
							.or(info.text.contains(q)));
		}
		if (StringUtils.isNotBlank(title)) {
			exp = exp.and(info.title.contains(title));
		}
		if (StringUtils.isNotBlank(keywords)) {
			exp = exp.and(info.keywords.contains(keywords));
		}
		if (StringUtils.isNotBlank(description)) {
			exp = exp.and(info.description.contains(description));
		}
		if (StringUtils.isNotBlank(text)) {
			exp = exp.and(info.text.contains(text));
		}
		return exp;
	}
}

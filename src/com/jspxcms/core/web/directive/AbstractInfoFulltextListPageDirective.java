package com.jspxcms.core.web.directive;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jspxcms.common.freemarker.Freemarkers;
import com.jspxcms.common.orm.Limitable;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.fulltext.InfoFulltextService;
import com.jspxcms.core.support.ForeContext;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * AbstractInfoFulltextListPageDirective
 * 
 * @author liufang
 * 
 */
public abstract class AbstractInfoFulltextListPageDirective {
	public static final String SITE_ID = "siteId";

	public static final String NODE_ID = "nodeId";
	// public static final String NODE = "node";
	// public static final String NODE_NUMBER = "nodeNumber";

	// public static final String ATTR_ID = "attrId";
	// public static final String ATTR = "attr";

	// public static final String SPECIAL_ID = "specialId";
	// public static final String SPECIAL_TITLE = "specialTitle";

	// public static final String TAG = "tag";
	// public static final String TAG_ID = "tagId";
	// public static final String TAG_NAME = "tagName";

	// public static final String USER = "user";
	// public static final String USER_ID = "userId";

	// public static final String PRIORITY = "priority";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String Q = "q";
	public static final String TITLE = "title";
	// public static final String INCLUDE_ID = "includeId";
	public static final String EXCLUDE_ID = "excludeId";
	public static final String STATUS = "status";

	// public static final String IS_INCLUDE_CHILDREN = "isIncludeChildren";
	// public static final String IS_MAIN_NODE_ONLY = "isMainNodeOnly";
	// public static final String IS_WITH_IMAGE = "isWithImage";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doExecute(Environment env, Map params,
			TemplateModel[] loopVars, TemplateDirectiveBody body, boolean isPage)
			throws TemplateException, IOException {
		if (loopVars.length < 1) {
			throw new TemplateModelException("Loop variable is required.");
		}
		if (body == null) {
			throw new RuntimeException("missing body");
		}

		Integer[] siteId = Freemarkers.getIntegers(params, SITE_ID);
		if (siteId == null && params.get(SITE_ID) == null) {
			siteId = new Integer[] { ForeContext.getSiteId(env) };
		}

		Integer[] nodeId = Freemarkers.getIntegers(params, NODE_ID);
		Date startDate = Freemarkers.getDate(params, START_DATE);
		Date endDate = Freemarkers.getEndDate(params, END_DATE);
		String q = Freemarkers.getString(params, Q);
		String title = Freemarkers.getString(params, TITLE);

		Integer[] excludeId = Freemarkers.getIntegers(params, EXCLUDE_ID);
		String[] status = Freemarkers.getStrings(params, STATUS);
		if (isPage) {
			Pageable pageable = Freemarkers.getPageable(params, env);
			Page<Info> pagedList = fulltext.page(siteId, nodeId, null,
					startDate, endDate, status, excludeId, q, title, null,
					null, null, pageable);
			ForeContext.setTotalPages(pagedList.getTotalPages());
			loopVars[0] = env.getObjectWrapper().wrap(pagedList);
		} else {
			Limitable limitable = Freemarkers.getLimitable(params);
			List<Info> list = fulltext.list(siteId, nodeId, null, startDate,
					endDate, status, excludeId, q, title, null, null, null,
					limitable);
			loopVars[0] = env.getObjectWrapper().wrap(list);
		}

		body.render(env.getOut());
	}

	@Autowired
	private InfoFulltextService fulltext;
}

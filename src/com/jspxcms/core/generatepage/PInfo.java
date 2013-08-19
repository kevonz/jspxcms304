package com.jspxcms.core.generatepage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jspxcms.common.freemarker.Freemarkers;
import com.jspxcms.common.web.PathResolver;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Node;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.support.ForeContext;
import com.jspxcms.core.support.TitleText;

import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * PInfo
 * 
 * @author liufang
 * 
 */
public abstract class PInfo {
	public static void generate(Info info, Configuration config,
			PathResolver resolver) throws IOException, TemplateException {
		if (info == null || !info.getGenerate()) {
			return;
		}
		Site site = info.getSite();
		Node node = info.getNode();
		List<TitleText> textList = info.getTextList();
		Template template = config.getTemplate(info.getTemplate());
		Map<String, Object> rootMap = new HashMap<String, Object>();
		rootMap.put(FreemarkerServlet.KEY_APPLICATION, Collections.EMPTY_MAP);
		rootMap.put(FreemarkerServlet.KEY_SESSION, Collections.EMPTY_MAP);
		rootMap.put(FreemarkerServlet.KEY_REQUEST, Collections.EMPTY_MAP);
		rootMap.put(Freemarkers.KEY_PARAMETERS, Collections.EMPTY_MAP);
		rootMap.put(Freemarkers.KEY_PARAMETER_VALUES, Collections.EMPTY_MAP);
		rootMap.put("info", info);
		rootMap.put("node", node);
		TitleText titleText;
		List<String> items = new ArrayList<String>(1);
		items.add(null);
		Pageable pa;
		int total = textList.size();
		for (int page = 1; page <= total; page++) {
			titleText = textList.get(page - 1);
			String path = info.getUrlStatic(page, false, true);
			String filename = resolver.getPath(path);
			File file = new File(filename);
			file.getParentFile().mkdirs();
			rootMap.put("title", titleText.getTitle());
			rootMap.put("text", titleText.getText());
			items.set(0, titleText.getText());
			pa = new PageRequest(page, 1);
			Page<String> pagedList = new PageImpl<String>(items, pa, total);
			String url = info.getUrlStatic(page);
			ForeContext.setData(rootMap, null, site, url);
			ForeContext.setPage(rootMap, page, info, pagedList);
			FileOutputStream fos = null;
			Writer out = null;
			try {
				fos = new FileOutputStream(file);
				out = new OutputStreamWriter(fos, "UTF-8");
				template.process(rootMap, out);
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
		}
	}
}

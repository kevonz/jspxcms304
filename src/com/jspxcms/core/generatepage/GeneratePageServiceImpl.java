package com.jspxcms.core.generatepage;

import static com.jspxcms.core.domain.Node.STATIC_INFO_NODE;
import static com.jspxcms.core.domain.Node.STATIC_INFO_NODE_PARENT;
import static com.jspxcms.core.domain.Node.STATIC_MANUAL;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.jspxcms.common.web.PathResolver;
import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Node;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * GeneratePageServiceImpl
 * 
 * @author liufang
 * 
 */
@Service
@Transactional
public class GeneratePageServiceImpl implements GeneratePageService {
	public void generateInfo(Info info) throws IOException, TemplateException {
		Node node = info.getNode();
		int method = node.getStaticMethodOrDef();
		if (STATIC_MANUAL != method) {
			PInfo.generate(info, getConfig(), resolver);
			generateNode(node);
		}
	}

	public void generateNode(Node node) throws IOException, TemplateException {
		int method = node.getStaticMethodOrDef();
		if (STATIC_INFO_NODE_PARENT == method) {
			while (node != null) {
				PNode.generate(node, getConfig(), resolver);
				node = node.getParent();
			}
		} else if (STATIC_INFO_NODE == method) {
			PNode.generate(node, getConfig(), resolver);
		} else {

		}
	}

	public void generateNodeWhole(Node[] nodes) throws IOException,
			TemplateException {
		if (ArrayUtils.isEmpty(nodes)) {
			return;
		}
		Set<String> tns = new HashSet<String>();
		for (Node node : nodes) {
			tns.add(node.getTreeNumber());
		}
		String[] tna = tns.toArray(new String[tns.size()]);
		dao.generateInfoWhole(tna, getConfig(), resolver);
		dao.generateNodeWhole(tna, getConfig(), resolver);
	}

	public void deletePage(Node node) {
		if (node == null) {
			return;
		}
		String path = node.getUrlStatic(1, false, true);
		String filename = resolver.getPath(path);
		File file = new File(filename);
		if (file.exists()) {
			FileUtils.deleteQuietly(file);
		}
	}

	private Configuration getConfig() {
		return freeMarkerConfigurer.getConfiguration();
	}

	private FreeMarkerConfigurer freeMarkerConfigurer;
	private PathResolver resolver;
	private GeneratePageDao dao;

	@Autowired
	public void setFreeMarkerConfigurer(
			FreeMarkerConfigurer freeMarkerConfigurer) {
		this.freeMarkerConfigurer = freeMarkerConfigurer;
	}

	@Autowired
	public void setPathResolver(PathResolver resolver) {
		this.resolver = resolver;
	}

	@Autowired
	public void setGeneratePageDao(GeneratePageDao dao) {
		this.dao = dao;
	}
}

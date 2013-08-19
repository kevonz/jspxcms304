package com.jspxcms.core.generatepage;

import java.io.IOException;

import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.Node;

import freemarker.template.TemplateException;

/**
 * GeneratePageService
 * 
 * @author liufang
 * 
 */
public interface GeneratePageService {
	public void generateInfo(Info info) throws IOException, TemplateException;

	public void generateNode(Node node) throws IOException, TemplateException;

	public void generateNodeWhole(Node[] nodes) throws IOException,
			TemplateException;

	public void deletePage(Node node);
}

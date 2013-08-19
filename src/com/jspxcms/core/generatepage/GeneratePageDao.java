package com.jspxcms.core.generatepage;

import java.io.IOException;

import com.jspxcms.common.web.PathResolver;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * GeneratePageDao
 * 
 * @author liufang
 * 
 */
public interface GeneratePageDao {
	public int generateNodeWhole(String[] treeNumber, Configuration config,
			PathResolver resolver) throws IOException, TemplateException;

	public int generateInfoWhole(String[] treeNumber, Configuration config,
			PathResolver resolver) throws IOException, TemplateException;
}

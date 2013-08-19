package com.jspxcms.core.generatepage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jspxcms.core.domain.Node;
import com.jspxcms.core.listener.AbstractNodeListener;

import freemarker.template.TemplateException;

/**
 * GeneratePageNodeListener
 * 
 * @author liufang
 * 
 */
@Component
public class GeneratePageNodeListener extends AbstractNodeListener {
	private static Logger logger = LoggerFactory
			.getLogger(GeneratePageNodeListener.class);

	@Override
	public void postNodeSave(Node[] beans) {
		try {
			for (Node bean : beans) {
				serivce.generateNode(bean);
			}
		} catch (IOException e) {
			logger.error("generate page error!", e);
		} catch (TemplateException e) {
			logger.error("generate page error!", e);
		}
	}

	@Override
	public void postNodeUpdate(Node[] beans) {
		try {
			for (Node bean : beans) {
				serivce.generateNode(bean);
			}
		} catch (IOException e) {
			logger.error("generate page error!", e);
		} catch (TemplateException e) {
			logger.error("generate page error!", e);
		}
	}

	private GeneratePageService serivce;

	@Autowired
	public void setGeneratePageService(GeneratePageService serivce) {
		this.serivce = serivce;
	}

}

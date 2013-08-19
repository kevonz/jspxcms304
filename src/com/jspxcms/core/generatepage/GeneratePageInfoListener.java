package com.jspxcms.core.generatepage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jspxcms.core.domain.Info;
import com.jspxcms.core.listener.AbstractInfoListener;

import freemarker.template.TemplateException;

/**
 * GeneratePageInfoListener
 * 
 * @author liufang
 * 
 */
@Component
public class GeneratePageInfoListener extends AbstractInfoListener {
	private static Logger logger = LoggerFactory
			.getLogger(GeneratePageInfoListener.class);

	@Override
	public void postInfoSave(Info[] beans) {
		try {
			for (Info bean : beans) {
				serivce.generateInfo(bean);
			}
		} catch (IOException e) {
			logger.error("generate page error!", e);
		} catch (TemplateException e) {
			logger.error("generate page error!", e);
		}
	}

	@Override
	public void postInfoUpdate(Info[] beans) {
		try {
			for (Info bean : beans) {
				serivce.generateInfo(bean);
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

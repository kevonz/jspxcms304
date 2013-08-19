package com.jspxcms.core.listener;

import com.jspxcms.core.domain.Info;

/**
 * AbstractInfoListener
 * 
 * @author liufang
 * 
 */
public abstract class AbstractInfoListener implements InfoListener,
		InfoDeleteListener {
	public void postInfoSave(Info[] beans) {
	}

	public void postInfoUpdate(Info[] beans) {
	}

	public void preInfoDelete(Integer[] ids) {
	}

	public void postInfoDelete(Info[] beans) {
	}
}

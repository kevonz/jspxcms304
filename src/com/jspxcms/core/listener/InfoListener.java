package com.jspxcms.core.listener;

import com.jspxcms.core.domain.Info;

/**
 * InfoListener
 * 
 * @author liufang
 * 
 */
public interface InfoListener {
	public void postInfoSave(Info[] beans);

	public void postInfoUpdate(Info[] beans);

	public void postInfoDelete(Info[] beans);
}

package com.jspxcms.core.service;

import java.util.List;
import java.util.Map;

import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.InfoDetail;
import com.jspxcms.core.domain.InfoFile;
import com.jspxcms.core.domain.InfoImage;

/**
 * InfoService
 * 
 * @author liufang
 * 
 */
public interface InfoService {
	public Info save(Info bean, InfoDetail detail, Integer[] nodeIds,
			Integer[] specialIds, Map<String, String> customs,
			Map<String, String> clobs, List<InfoImage> images,
			List<InfoFile> files, Integer[] attrIds,
			Map<String, String> attrImages, String[] tagNames, Integer nodeId,
			Integer creatorId, Boolean draft, Integer siteId);

	public Info update(Info bean, InfoDetail detail, Integer[] nodeIds,
			Integer[] specialIds, Map<String, String> customs,
			Map<String, String> clobs, List<InfoImage> images,
			List<InfoFile> files, Integer[] attrIds,
			Map<String, String> attrImages, String[] tagNames, Integer nodeId,
			Boolean draft);

	public Info[] auditPass(Integer[] ids, Integer userId, String opinion);

	public Info[] auditReject(Integer[] ids, Integer userId, String opinion);

	public Info[] submit(Integer[] ids, Integer userId);

	public Info[] antiSubmit(Integer[] ids);

	public Info delete(Integer id);

	public Info[] delete(Integer[] ids);
}

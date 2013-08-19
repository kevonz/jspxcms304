package com.jspxcms.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;

import com.jspxcms.common.util.RowSide;
import com.jspxcms.core.domain.Model;

/**
 * ModelService
 * 
 * @author liufang
 * 
 */
public interface ModelService {
	public List<Model> findList(String type, Map<String, String[]> params,
			Sort sort);

	public RowSide<Model> findSide(String type, Map<String, String[]> params,
			Model bean, Integer position, Sort sort);

	public List<Model> findList(Integer siteId, String type);

	public Model findDefault(Integer siteId, String type);

	public Model get(Integer id);

	public Model save(Model bean, Integer siteId, Map<String, String> customs);

	public Model[] batchUpdate(Integer[] id, String[] name);

	public Model update(Model bean, Map<String, String> customs);

	public Model delete(Integer id);

	public Model[] delete(Integer[] ids);
}

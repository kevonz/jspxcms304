package com.jspxcms.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.Repository;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.core.domain.RoleSite;

/**
 * RoleSiteDao
 * 
 * @author liufang
 * 
 */
public interface RoleSiteDao extends Repository<RoleSite, Integer> {
	public Page<RoleSite> findAll(Specification<RoleSite> spec,
			Pageable pageable);

	public List<RoleSite> findAll(Specification<RoleSite> spec,
			Limitable limitable);

	public RoleSite findOne(Integer id);

	public RoleSite save(RoleSite bean);

	public void delete(RoleSite bean);

	// --------------------

}

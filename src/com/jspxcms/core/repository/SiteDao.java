package com.jspxcms.core.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.core.domain.Site;

/**
 * SiteDao
 * 
 * @author liufang
 * 
 */
public interface SiteDao extends Repository<Site, Integer>, SiteDaoPlus {
	public List<Site> findAll();

	public Site findOne(Integer id);

	public Site save(Site bean);

	public void delete(Site bean);

	// --------------------

	@Query("select count(*) from Site bean where bean.org.id in ?1")
	public long countByOrgId(Collection<Integer> orgIds);
}
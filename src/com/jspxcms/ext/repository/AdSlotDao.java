package com.jspxcms.ext.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.ext.domain.AdSlot;

public interface AdSlotDao extends Repository<AdSlot, Integer>, AdSlotDaoPlus {
	public List<AdSlot> findAll(Specification<AdSlot> spec, Sort sort);

	public List<AdSlot> findAll(Specification<AdSlot> spec, Limitable limit);

	public AdSlot findOne(Integer id);

	public AdSlot save(AdSlot bean);

	public void delete(AdSlot bean);

	// --------------------

	public List<AdSlot> findByNumberAndSiteId(String number, Integer siteId);

	@Query("from AdSlot bean where bean.site.id=?1 order by bean.id asc")
	public List<AdSlot> findBySiteId(Integer siteId);

	@Query("select count(*) from AdSlot bean where bean.site.id in ?1")
	public long countBySiteId(Collection<Integer> siteIds);
}

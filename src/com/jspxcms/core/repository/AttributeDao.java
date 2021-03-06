package com.jspxcms.core.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.core.domain.Attribute;

/**
 * AttributeDao
 * 
 * @author liufang
 * 
 */
public interface AttributeDao extends Repository<Attribute, Integer>,
		AttributeDaoPlus {

	public Attribute findOne(Integer id);

	public Attribute save(Attribute bean);

	public void delete(Attribute bean);

	// --------------------

	public List<Attribute> findBySiteId(Integer siteId, Sort sort);

	@Query("select count(*) from Attribute bean where bean.number=?1 and bean.site.id=?2")
	public long countByNumber(String number, Integer siteId);

	@Query("select count(*) from Attribute bean where bean.site.id in ?1")
	public long countBySiteId(Collection<Integer> siteIds);
}

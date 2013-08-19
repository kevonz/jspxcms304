package com.jspxcms.ext.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.ext.domain.Guestbook;

/**
 * GuestbookDao
 * 
 * @author yangxing
 * 
 */
public interface GuestbookDao extends Repository<Guestbook, Integer>,
		GuestbookDaoPlus {
	public Page<Guestbook> findAll(Specification<Guestbook> spec,
			Pageable pageable);

	public List<Guestbook> findAll(Specification<Guestbook> spec,
			Limitable limitable);

	public Guestbook findOne(Integer id);

	public Guestbook save(Guestbook bean);

	public void delete(Guestbook bean);

	// --------------------

	@Query("select count(*) from Guestbook bean where bean.site.id in ?1")
	public long countBySiteId(Collection<Integer> siteIds);

	@Query("select count(*) from Guestbook bean where bean.type.id in ?1")
	public long countByTypeId(Collection<Integer> typeIds);

	@Query("select count(*) from Guestbook bean where bean.creator.id in ?1")
	public long countByCreatorId(Collection<Integer> creatorIds);

	@Query("select count(*) from Guestbook bean where bean.replyer.id in ?1")
	public long countByReplyerId(Collection<Integer> replyerIds);

}

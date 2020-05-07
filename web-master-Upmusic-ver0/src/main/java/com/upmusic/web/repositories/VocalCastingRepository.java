package com.upmusic.web.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.VocalCasting;


@Repository
public interface VocalCastingRepository extends JpaRepository<VocalCasting, Long> {

	@Query(value = "SELECT v FROM VocalCasting v WHERE v.subject LIKE %:query% OR v.member.nick LIKE %:query%") // , nativeQuery = true)
	public Page<VocalCasting> findTop10ByQuery(@Param("query") String query, Pageable pageable);
	
	@Query(value = "SELECT v FROM VocalCasting v WHERE v.subject LIKE %:query% OR v.member.nick LIKE %:query%") // , nativeQuery = true)
	public List<VocalCasting> findByQuery(@Param("query") String query);

	public List<VocalCasting> findAllByMemberId(Long memberId);
}

package com.upmusic.web.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Collection;


@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

	Iterable<Collection> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
	
	Page<Collection> findByMemberId(Long memberId, Pageable pageOrderNew);

}

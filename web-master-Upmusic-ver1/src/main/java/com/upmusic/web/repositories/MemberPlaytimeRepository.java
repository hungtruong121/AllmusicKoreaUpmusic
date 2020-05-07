package com.upmusic.web.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MemberPlaytime;


@Repository
public interface MemberPlaytimeRepository extends JpaRepository<MemberPlaytime, Long> {
	
	Iterable<MemberPlaytime> findAllByMemberId(Long memberId);

	MemberPlaytime findOneByMemberIdAndCreatedAtGreaterThan(Long memberId, Date date);

	MemberPlaytime findOneByMemberIdAndCreatedAtGreaterThanAndUpdatedAtLessThan(Long memberId, Date dayStart, Date dayEnd);
	
}

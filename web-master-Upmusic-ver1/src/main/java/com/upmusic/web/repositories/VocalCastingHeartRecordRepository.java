package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.VocalCastingHeartRecord;


@Repository
public interface VocalCastingHeartRecordRepository extends JpaRepository<VocalCastingHeartRecord, Long> {
	
	@Query(value = "select h from VocalCastingHeartRecord h where h.vocalCasting.id = :vocalCastingId and h.member.id = :memberId")
	List<VocalCastingHeartRecord> findAllByVocalCastingIdAndMemberId(@Param("vocalCastingId") Long vocalCastingId, @Param("memberId") Long memberId);

	List<VocalCastingHeartRecord> findByVocalCastingId(Long vocalCastingId);
}

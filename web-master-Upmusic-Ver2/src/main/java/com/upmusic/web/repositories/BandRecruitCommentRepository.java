package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upmusic.web.domain.BandRecruitComment;

@Repository
public interface BandRecruitCommentRepository extends JpaRepository<BandRecruitComment, Long>{

	@Query("SELECT COUNT(*) FROM BandRecruitComment BRC WHERE BRC.bandRecruitId.bandRecruitId = :bandRecruitId")
	int findCountByBandRecruitCommentId(@Param("bandRecruitId") Long bandRecruitId);
	
	@Query("SELECT new com.upmusic.web.domain.BandRecruitComment(BRC, M) FROM BandRecruitComment BRC LEFT JOIN Member M ON BRC.member.id = M.id WHERE BRC.bandRecruitId.bandRecruitId = :bandRecruitId")
	Page<BandRecruitComment> findAllByBandRecruitId(@Param("bandRecruitId") Long bandRecruitId, Pageable pageable);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM BandRecruitComment BRC WHERE BRC.bandRecruitId.bandRecruitId = :bandRecruitId")
	void deleteByBandRecruitId(@Param("bandRecruitId") Long bandRecruitId);
	
	@Query("SELECT new com.upmusic.web.domain.BandRecruitComment(BRC, M) FROM BandRecruitComment BRC LEFT JOIN Member M ON BRC.member.id = M.id WHERE BRC.bandRecruitId.bandRecruitId = :bandRecruitId  ORDER BY BRC.createdAt DESC")
	List<BandRecruitComment> findAllByBandRecruitId(@Param("bandRecruitId") Long bandRecruitId);
	
	@Query("SELECT BRC FROM BandRecruitComment BRC WHERE BRC.bandRecruitCommentId = :bandRecruitCommentId")
	BandRecruitComment findByBandRecruitCommentId(@Param("bandRecruitCommentId") Long bandRecruitCommentId);

	public List<BandRecruitComment> findAllByMemberId(Long memberId);
}

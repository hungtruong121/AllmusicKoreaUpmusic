package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.BandRecruit;

@Repository
public interface BandRecruitRepository extends JpaRepository<BandRecruit, Long>{

	@Query("SELECT new com.upmusic.web.domain.BandRecruit(BR, M) FROM BandRecruit BR LEFT JOIN Member M ON BR.member.id = M.id")
	public Page<BandRecruit> findAllWithMember(Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.BandRecruit(BR, M) FROM BandRecruit BR LEFT JOIN Member M ON BR.member.id = M.id WHERE BR.bandRecruitId = :bandRecruitId")
	BandRecruit findByBandRecruitId(@Param("bandRecruitId") Long bandRecruitId);

	@Query("SELECT BR FROM BandRecruit BR WHERE BR.subject LIKE CONCAT('%', :subject, '%') OR BR.content LIKE CONCAT('%', :subject, '%') OR BR.member.nick LIKE CONCAT('%', :subject, '%')")
	List<BandRecruit> searchBandRecruits(@Param("subject") String subject);
	
	@Query("SELECT BR FROM BandRecruit BR WHERE BR.subject LIKE CONCAT('%', :subject, '%') OR BR.content LIKE CONCAT('%', :subject, '%') OR BR.member.nick LIKE CONCAT('%', :subject, '%')")
	Page<BandRecruit> searchTop4BandRecruits(@Param("subject") String subject, Pageable pageable);
	
	@Query("SELECT new com.upmusic.web.domain.BandRecruit(BR, M) FROM BandRecruit BR LEFT JOIN Member M ON BR.member.id = M.id ORDER BY BR.createdAt DESC")
	public List<BandRecruit> findAllWithMember();

	public List<BandRecruit> findAllByMemberId(Long memberId);

}

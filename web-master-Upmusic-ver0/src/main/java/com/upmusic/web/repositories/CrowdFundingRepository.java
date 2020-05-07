package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;

import com.google.api.client.util.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.CrowdFunding;

@Repository
public interface CrowdFundingRepository extends JpaRepository<CrowdFunding, Long>, JpaSpecificationExecutor<CrowdFunding> {

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.artistMemberId IN (SELECT mem.id from Member mem WHERE mem.email LIKE CONCAT ('%',:member_email,'%'))")
	Page<CrowdFunding> findAllByArtistMemberId(@Param("member_email") String member_email, Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.artistNick LIKE CONCAT ('%',:artist_nick ,'%')")
	Page<CrowdFunding> findAllByArtistNick(@Param("artist_nick") String artist_nick, Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.subject LIKE CONCAT ('%',:subject,'%')")
	Page<CrowdFunding> findAllBySubject(@Param("subject") String subject, Pageable pageable);

	@Query("SELECT CF FROM CrowdFunding CF WHERE CF.createdAt > :standardDate")
	Page<CrowdFunding> findAllByCreatedAt(@Param("standardDate") Date standardDate, Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id")
	Page<CrowdFunding> findAllByCreateAt(Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.crowdFundingId = :crowdFundingId")
	CrowdFunding findByCrowdFundingId(@Param("crowdFundingId") Long crowdFundingId);

	@Query("SELECT CF FROM CrowdFunding CF WHERE CF.closeAt >= :today AND CF.openAt <= :today")
	Page<CrowdFunding> findAllByNow(@Param("today") Date today, Pageable pageable);

	@Query("SELECT CF FROM CrowdFunding CF WHERE CF.closeAt < NOW()")
	Page<CrowdFunding> findAllByCloseAt(Pageable pageable);

	@Query("SELECT COUNT(*) FROM CrowdFunding CF WHERE CF.closeAt < NOW() AND CF.targetPrice <= CF.attainmentPrice")
	int countAllSuccessFunding();

	@Query("SELECT COUNT(*) FROM CrowdFunding CF WHERE CF.closeAt < NOW() AND CF.targetPrice > CF.attainmentPrice")
	int countAllFailFunding();
	
	@Query("SELECT COUNT(*) FROM CrowdFunding CF WHERE CF.closeAt < NOW()")
	int countAllFunding();
	
	@Query("SELECT CF FROM CrowdFunding CF WHERE CF.openAt > NOW()")
	List<CrowdFunding> findAllByStartProject();
	
	@Query("SELECT CF FROM CrowdFunding CF WHERE CF.closeAt > :today AND CF.openAt < :today ORDER BY CF.createdAt DESC")
	List<CrowdFunding> findAllByNewProject(@Param("today") Date today);
	
	@Query("SELECT CF FROM CrowdFunding CF WHERE CF.closeAt > :today AND CF.openAt < :today ORDER BY CF.joinCnt DESC")
	List<CrowdFunding> findAllByHotProject(@Param("today") Date today);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.openAt >= :openAt AND CF.closeAt <= :closeAt")
	Page<CrowdFunding> findAllByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(@Param("openAt") Date openAt, @Param("closeAt") Date closeAt, Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.openAt >= :openAt AND CF.closeAt <= :closeAt AND CF.artistMemberId IN (SELECT mem.id from Member mem WHERE mem.email LIKE CONCAT ('%',:member_email,'%'))")
	Page<CrowdFunding> findAllByArtistMemberIdByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(@Param("openAt") Date openAt, @Param("closeAt") Date closeAt, @Param("member_email") String member_email, Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.openAt >= :openAt AND CF.closeAt <= :closeAt AND CF.artistNick LIKE CONCAT ('%',:artist_nick ,'%')")
	Page<CrowdFunding> findAllByArtistNickByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(@Param("openAt") Date openAt, @Param("closeAt") Date closeAt, @Param("artist_nick") String artist_nick, Pageable pageable);

	@Query("SELECT new com.upmusic.web.domain.CrowdFunding(CF, M) FROM CrowdFunding CF LEFT JOIN Member M ON CF.member.id = M.id WHERE CF.openAt >= :openAt AND CF.closeAt <= :closeAt AND CF.subject LIKE CONCAT ('%',:subject,'%')")
	Page<CrowdFunding> findAllBySubjectByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(@Param("openAt") Date openAt, @Param("closeAt") Date closeAt, @Param("subject") String subject, Pageable pageable);
}

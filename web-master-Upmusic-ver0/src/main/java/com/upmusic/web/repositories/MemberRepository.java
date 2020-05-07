package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Member;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
	
	Member findByEmail(String email);
	
	Member findByPhoneNumber(String phoneNumber);

	Page<Member> findAll(Specification<Member> spec, Pageable pageable);

	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN mp.id IS NULL THEN NULL ELSE mp.playtime END as playtime) FROM Member m LEFT JOIN MemberPlaytime mp ON m.id = mp.memberId AND mp.createdAt >= CURDATE()") // , nativeQuery = true)
	Page<Member> findAllInAdminPage(Pageable pageable);
	
	@Query(value = "SELECT m FROM Member m WHERE m.trackCnt > 0 and m.password <> 'withdrawal'") // , nativeQuery = true)
	List<Member> findTop2Artist(Pageable pageable);
	
	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m LEFT JOIN m.likers ml ON ml.id = :likerId WHERE m.trackCnt > 0 AND m.nick LIKE %:query% AND m.password NOT LIKE 'withdrawal'") // , nativeQuery = true)
	Page<Member> findTop4ArtistByQueryWithHeartByMember(@Param("query") String query, @Param("likerId") Long likerId, Pageable pageable);
	
	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m LEFT JOIN m.likers ml ON ml.id = :likerId WHERE m.trackCnt > 0 AND m.nick LIKE %:query% AND m.password NOT LIKE 'withdrawal'") // , nativeQuery = true)
	List<Member> findArtistByQueryWithHeartByMember(@Param("query") String query, @Param("likerId") Long likerId);

	// @Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m JOIN m.roles mr LEFT JOIN m.likers ml ON ml.id = :likerId WHERE mr.id < 9") // , nativeQuery = true)
	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m LEFT JOIN m.likers ml ON ml.id = :likerId WHERE m.trackCnt > 0 and m.password <> 'withdrawal'") // , nativeQuery = true)
	Page<Member> findAllArtistWithLikeByMember(@Param("likerId") Long likerId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m JOIN m.roles mr LEFT JOIN m.likers ml ON ml.id = :likerId WHERE mr.id = 6 and m.password <> 'withdrawal'") // , nativeQuery = true)
	Page<Member> findAllFamilyArtistWithLikeByMember(@Param("likerId") Long likerId, Pageable pageOrderRequest);

	//@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m JOIN m.roles mr LEFT JOIN m.likers ml ON ml.id = :likerId WHERE mr.id < 9 AND m.createdAt >= :lastMonth") // , nativeQuery = true)
	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m LEFT JOIN m.likers ml ON ml.id = :likerId WHERE m.trackCnt > 0 AND m.createdAt >= :lastMonth and m.password <> 'withdrawal'") // , nativeQuery = true)
	Page<Member> findAllNewArtistWithLikeByMember(@Param("likerId") Long likerId, @Param("lastMonth") Date lastMonth, Pageable pageRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m JOIN m.roles mr LEFT JOIN m.likers ml ON ml.id = :likerId WHERE mr.id = 7 and m.password <> 'withdrawal'") // , nativeQuery = true)
	Page<Member> findAllGuideVocalWithLikeByMember(@Param("likerId") Long likerId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT m FROM Member m JOIN m.roles mr WHERE mr.id < 9") // , nativeQuery = true)
	Iterable<Member> findAllArtists();

//	@Query(value = "SELECT new com.upmusic.web.domain.Member(m, CASE WHEN ml.id IS NULL THEN 0 ELSE 1 END as liked) FROM Member m JOIN m.likers ml WHERE ml.id = :likerId AND m.id = :memberId") // , nativeQuery = true)
//	Member isLiker(@Param("memberId") Long memberId, @Param("likerId") Long likerId);
	
	Member findByToken(String token);
	
	@Query(value = "SELECT M.* FROM member M RIGHT JOIN member_liker ML ON M.id = ML.member_id WHERE ML.liker_id = :likerId", nativeQuery = true)
	Page<Map<String, Object>> findAllByLikerId(@Param("likerId") Long likerId, Pageable pageable);
	
	@Query(value = "SELECT M.* FROM member M RIGHT JOIN member_liker ML ON M.id = ML.member_id WHERE ML.liker_id = :likerId ORDER BY M.created_at DESC", nativeQuery = true)
	List<Map<String, Object>> findAllByLikerIdMobile(@Param("likerId") Long likerId);
	
//	@Query(value = "SELECT M FROM Member M WHERE M.email LIKE CONCAT('%', :email, '%')")
	@Query(value = "SELECT M FROM Member M WHERE M.email LIKE CONCAT('%', :email, '%') AND M.password NOT LIKE 'withdrawal'")
	List<Member> findAllByEmail(@Param("email") String email);

	@Query(value = "SELECT M FROM Member M WHERE M.nick LIKE CONCAT('%', :nickName, '%') AND M.password NOT LIKE 'withdrawal'")
	List<Member> findAllByNickName(@Param("nickName") String nickName);
	
	@Query(value = "SELECT COUNT(*) FROM member_liker ML WHERE ML.member_id = :memberId AND ML.liker_id = :likerId", nativeQuery = true)
	int countIsLiker(@Param("memberId") Long memberId, @Param("likerId") Long likerId);
	
	@Query(value = "INSERT INTO member_liker (member_id, liker_id) VALUES (:memberId, :likerId)", nativeQuery = true)
	void saveMemberLike(@Param("memberId") Long memberId, @Param("likerId") Long likerId);
	
	@Query(value = "DELETE FROM member_liker WHERE member_id = :memberId AND liker_id = :likerId", nativeQuery = true)
	void deleteMemberLike(@Param("memberId") Long memberId, @Param("likerId") Long likerId);

}

package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.config.UPMusicConstants.VideoType;
import com.upmusic.web.domain.Video;


@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
	
	@Query(value = "SELECT v FROM Video v WHERE v.subject LIKE %:query% OR v.member.nick LIKE %:query%") // , nativeQuery = true)
	public Page<Video> findTop4ByQuery(@Param("query") String query, Pageable pageable);
	
	@Query(value = "SELECT v FROM Video v WHERE v.subject LIKE %:query% OR v.member.nick LIKE %:query%") // , nativeQuery = true)
	public List<Video> findByQuery(@Param("query") String query);
	
	public Video findFirstByOrderByHotPointDesc();
	
	public List<Video> findTop3ByOrderByHotPointDesc();
	
	public List<Video> findTop4ByOrderByHotPointDesc();
	
	public List<Video> findTop5ByOrderByHotPointDesc();
	
	public List<Video> findTop3ByVideoTypeOrderByHotPointDesc(VideoType type);
	
	public List<Video> findTop4ByVideoTypeOrderByHotPointDesc(VideoType type);
	
	public List<Video> findTop5ByVideoTypeOrderByHotPointDesc(VideoType type);
	
	public Page<Video> findByOrderByHotPointDesc(Pageable pageOrderByHot);
	
	public Page<Video> findByVideoTypeOrderByHotPointDesc(VideoType type, Pageable pageOrderByHot);
	
	public Page<Video> findAllByVideoType(VideoType type, Pageable pageOrderByHot);
	
	// 회원의 플레이 리스트
	@Query(value = "SELECT v FROM Video v LEFT JOIN VideoPlayRecord p ON v.id = p.video.id WHERE p.member.id = :memberId ORDER BY p.createdAt DESC") // , nativeQuery = true)
	public Page<Video> findAllByPlayWithHeartByMember(@Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	// 회원의 좋아요 리스트
	@Query(value = "SELECT v FROM Video v LEFT JOIN VideoHeartRecord h ON v.id = h.video.id WHERE h.member.id = :memberId ORDER BY h.createdAt DESC") // , nativeQuery = true)
	public Page<Video> findAllByMemberHeart(@Param("memberId") Long memberId, Pageable pageOrderRequest);

	// 업로드 내역
	public List<Video> findTop4ByMemberIdOrderByCreatedAtDesc(Long memberId);
	public Page<Video> findByMemberId(Long memberId, Pageable pageOrderRequest);
	public Page<Video> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageOrderRequest);

	// 아티스트 프로필 상세
	@Query(value = "SELECT COUNT(v) FROM Video v WHERE v.member.id = :memberId") // , nativeQuery = true)
	public Long findCountByMemberId(@Param("memberId") Long memberId);
	public List<Video> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}

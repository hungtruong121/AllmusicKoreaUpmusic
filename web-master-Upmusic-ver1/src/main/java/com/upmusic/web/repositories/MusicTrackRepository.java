package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.domain.MusicTrack;


/**
 * 트랙 리포지토리
 * 기본 검색 조건 : trackStatus = 3 (곡 심사상태 ACCEPTED)
 */
@Repository
public interface MusicTrackRepository extends JpaRepository<MusicTrack, Long> {
	
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.trackStatus = 3") // , nativeQuery = true)
	public List<MusicTrack> findTop10(Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.trackStatus = 3 AND (t.subject LIKE %:query% OR t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.nick LIKE %:query%))") // , nativeQuery = true)
	public Page<MusicTrack> findTop10ByQueryWithHeartByMember(@Param("query") String query, @Param("memberId") Long memberId, Pageable pageable);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.trackStatus = 3 AND (t.subject LIKE %:query% OR t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.nick LIKE %:query%))") // , nativeQuery = true)
	public List<MusicTrack> findByQueryWithHeartByMember(@Param("query") String query, @Param("memberId") Long memberId);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.trackStatus = 3 AND t.inStore = 1 AND (t.subject LIKE %:query% OR t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.nick LIKE %:query%))") // , nativeQuery = true)
	public Page<MusicTrack> findTop10StoreTracksByQueryWithHeartByMember(@Param("query") String query, @Param("memberId") Long memberId, Pageable pageable);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.trackStatus = 3 AND t.inStore = 1 AND (t.subject LIKE %:query% OR t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.nick LIKE %:query%))") // , nativeQuery = true)
	public List<MusicTrack> findStoreTracksByQueryWithHeartByMember(@Param("query") String query, @Param("memberId") Long memberId);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.trackStatus = 3") // , nativeQuery = true)
	public Page<MusicTrack> findAllWithHeartByMember(@Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.genre.id = :genreId AND t.trackStatus = 3") // , nativeQuery = true)
	public Page<MusicTrack> findByGenreIdWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.theme.id = :themeId AND t.trackStatus = 3") // , nativeQuery = true)
	public Page<MusicTrack> findByThemeIdWithHeartByMember(@Param("themeId") int themeId, @Param("memberId") Long memberId, Pageable pageOrderRequest);

	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.genre.id = :genreId AND t.trackStatus = 3 AND t.inStore = 1") // , nativeQuery = true)
	public Page<MusicTrack> findByGenreIdAndInStoreTrueWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);

	// 회원의 최근 플레이 리스트
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackPlayRecord p ON t.id = p.musicTrack.id LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id AND h.member.id = :memberId WHERE p.playedAt IS NOT NULL AND p.member.id = :memberId AND t.trackStatus = 3 ORDER BY p.playedAt DESC") // , nativeQuery = true)
	public Page<MusicTrack> findAllByPlayWithHeartByMember(@Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT t FROM MusicTrack t LEFT JOIN MusicTrackPlayRecord p ON t.id = p.musicTrack.id WHERE p.playedAt IS NOT NULL AND p.member.id = :memberId AND t.trackStatus = 3 ORDER BY p.playedAt DESC") // , nativeQuery = true)
	public List<MusicTrack> findAllByPlayWithHeartByMember(@Param("memberId") Long memberId);
	
	// 회원의 음악 모음 리스트
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN CollectionTrack ct ON t.id = ct.musicTrack.id LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id AND h.member.id = :memberId WHERE ct.collection.id = :collectionId AND t.trackStatus = 3 ORDER BY ct.id ASC") // , nativeQuery = true)
	public Page<MusicTrack> findByCollectionIdWithHeartByMember(@Param("collectionId") Long collectionId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	// 회원의 좋아요 리스트
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, 1 as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id WHERE h.member.id = :memberId and t.trackStatus = 3 ORDER BY h.id DESC") // , nativeQuery = true)
	public Page<MusicTrack> findAllByMemberHeart(@Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, 1 as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id WHERE h.member.id = :memberId and t.trackStatus = 3 ORDER BY h.id DESC") // , nativeQuery = true)
	public List<MusicTrack> findAllByMemberHeart(@Param("memberId") Long memberId);
	
	// 회원의 뮤직 플레이어 리스트
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackPlayer p ON t.id = p.musicTrack.id LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id AND h.member.id = :memberId WHERE p.member.id = :memberId AND t.trackStatus = 3 ORDER BY p.id DESC") // , nativeQuery = true)
	public Iterable<MusicTrack> findPlaylistWithHeartByMember(@Param("memberId") Long memberId);

	// 비디오 페이지의 아티스트 음원 TOP5
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3") // , nativeQuery = true)
	public List<MusicTrack> findTop5ByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);
	
	// 업로드 내역
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id = t.id WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1)") // , nativeQuery = true)
	public Page<MusicTrack> findUploadedByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);

	// 아티스트 음원
	@Query(value = "SELECT COUNT(t) FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3") // , nativeQuery = true)
	public Long findCountByArtistId(@Param("artistId") Long artistId);
	
	@Query(value = "SELECT COUNT(t) FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3 AND t.inStore = 1") // , nativeQuery = true)
	public Long findCountByArtistIdAndInStoreTrue(@Param("artistId") Long artistId);
	
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.musicAlbum.id = :musicAlbumId") // , nativeQuery = true)
	public List<MusicTrack> findAllByMusicAlbumId(@Param("musicAlbumId") Long musicAlbumId);

	@Query(value = "SELECT t FROM MusicTrack t WHERE t.id IN (:musicTrackId)")
	public List<MusicTrack> findAllByMusicTrackId(@Param("musicTrackId") List<Long> musicTrackId);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3") // , nativeQuery = true)
	public List<MusicTrack> findTop10ByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);

	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3") // , nativeQuery = true)
	public Page<MusicTrack> findAllByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3 ORDER BY t.id DESC") // , nativeQuery = true)
	public List<MusicTrack> findByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3 AND t.inStore = 1") // , nativeQuery = true)
	public List<MusicTrack> findTop10ByArtistIdAndInStoreTrueWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);

	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3 AND t.inStore = 1") // , nativeQuery = true)
	public Page<MusicTrack> findAllByArtistIdAndInStoreTrueWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);

	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id = :artistId AND a.published = 1) AND t.trackStatus = 3 AND t.inStore = 1 ORDER BY t.id DESC") // , nativeQuery = true)
	public List<MusicTrack> findByArtistIdAndInStoreTrueWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId);

	public MusicTrack findBySubject(String subject);
	
	/*
	 * 관리자용
	 */
	
	public Page<MusicTrack> findByTrackStatusAndPublishedTrue(MusicTrackStatus beforeExam, Pageable pageOrderRequest);

	// 사용자 전체 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id NOT IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3")
	public Page<MusicTrack> findUploadedByUsers(@Param("memberId") Long memberId, Pageable pageable);

	// 사용자 업리그 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id NOT IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3 AND t.inLeague = true")
	public Page<MusicTrack> findUploadedByUsersByUpleague(@Param("memberId") Long memberId, Pageable pageable);

	// 사용자 최신곡 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id NOT IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3 AND t.inRecent = true")
	public Page<MusicTrack> findUploadedByUsersByRecent(@Param("memberId") Long memberId, Pageable pageable);

	// 사용자 뮤직스토어 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id NOT IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3 AND t.inStore = true")
	public Page<MusicTrack> findUploadedByUsersByMusicStore(@Param("memberId") Long memberId, Pageable pageable);

	//사용자 업리그 심사 대기 음원
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id NOT IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND (t.trackStatus = 1 OR t.trackStatus = 2) AND t.inLeague = true")
	public Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByUpleague(Pageable pageable);

	// 사용자 최신곡 심사 대기 음원
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id NOT IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND (t.trackStatus = 1 OR t.trackStatus = 2) AND t.inRecent = true")
	public Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByRecent(Pageable pageable);

	//사용자 뮤직스토어 심사 대기 음원
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id NOT IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND (t.trackStatus = 1 OR t.trackStatus = 2) AND t.inStore = true")
	public Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByMusicStore(Pageable pageable);

	// 관리자 전체 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3")
	public Page<MusicTrack> findUploadedByAdmin(@Param("memberId") Long memberId, Pageable pageable);

	// 관리자 업리그 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3 AND t.inLeague = true")
	public Page<MusicTrack> findUploadedByAdminByUpleague(@Param("memberId") Long memberId, Pageable pageable);

	// 관리자 최신곡 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3 AND t.inRecent = true")
	public Page<MusicTrack> findUploadedByAdminByRecent(@Param("memberId") Long memberId, Pageable pageable);

	// 관리자 뮤직스토어 음원
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked, CASE WHEN r.id IS NULL THEN NULL ELSE r.reason END as rejectedReason) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id IN (t.id) and h.member.id = :memberId LEFT JOIN MusicTrackRejectedReason r ON r.musicTrack.id IN (t.id) WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND t.trackStatus = 3 AND t.inStore = true")
	public Page<MusicTrack> findUploadedByAdminByMusicStore(@Param("memberId") Long memberId, Pageable pageable);

	//관리자 업리그 심사 대기 음원
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND (t.trackStatus = 1 OR t.trackStatus = 2) AND t.inLeague = true")
	public Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByUpleague(Pageable pageable);

	//관리자 최신곡 심사 대기 음원
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND (t.trackStatus = 1 OR t.trackStatus = 2) AND t.inRecent = true")
	public Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByRecent(Pageable pageable);

	//관리자 뮤직스토어 심사 대기 음원
	@Query(value = "SELECT t FROM MusicTrack t WHERE t.musicAlbum.id IN (SELECT a.id FROM MusicAlbum a WHERE a.member.id IN (SELECT mem.id FROM Member mem JOIN mem.roles mr WHERE mr.id = 1) AND a.published = 1) AND (t.trackStatus = 1 OR t.trackStatus = 2) AND t.inStore = true")
	public Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByMusicStore(Pageable pageable);
	
	//ngocLh 3/1/2019
	@Query(value = "SELECT new com.upmusic.web.domain.MusicTrack(t, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicTrack t LEFT JOIN MusicTrackHeartRecord h ON h.musicTrack.id = t.id and h.member.id = :memberId WHERE t.trackStatus = 3 AND t.inStore = 1") // , nativeQuery = true)
	public Page<MusicTrack> findByStoreTrueWithHeartByMember(@Param("memberId") Long memberId, Pageable pageOrderRequest);
}

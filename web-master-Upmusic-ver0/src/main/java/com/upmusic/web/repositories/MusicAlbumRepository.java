package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicAlbum;


@Repository
public interface MusicAlbumRepository extends JpaRepository<MusicAlbum, Long> {
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND (a.subject LIKE %:query% OR a.member.nick LIKE %:query%)") // , nativeQuery = true)
	public Page<MusicAlbum> findTop4ByQueryWithHeartByMember(@Param("query") String query, @Param("memberId") Long memberId, Pageable pageable);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND (a.subject LIKE %:query% OR a.member.nick LIKE %:query%)") // , nativeQuery = true)
	public List<MusicAlbum> findByQueryWithHeartByMember(@Param("query") String query, @Param("memberId") Long memberId);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 1") // , nativeQuery = true)
	public Page<MusicAlbum> findAllSAWithHeartByMember(@Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 1 AND a.genre.id = :genreId") // , nativeQuery = true)
	public Page<MusicAlbum> findAllSAByGenreIdWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
	
//	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 1 AND a.genre.id = :genreId") // , nativeQuery = true)
//	public Page<MusicAlbum> findHomeSAByGenreIdWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
//	
//	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 1 AND a.genre.id = :genreId") // , nativeQuery = true)
//	public Page<MusicAlbum> findAbroadSAByGenreIdWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 2") // , nativeQuery = true)
	public Page<MusicAlbum> findAllGAWithHeartByMember(@Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 2 AND a.genre.id = :genreId") // , nativeQuery = true)
	public Page<MusicAlbum> findAllGAByGenreIdWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
	
//	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 2 AND a.genre.id = :genreId") // , nativeQuery = true)
//	public Page<MusicAlbum> findHomeGAByGenreIdWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
//	
//	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.albumType = 2 AND a.genre.id = :genreId") // , nativeQuery = true)
//	public Page<MusicAlbum> findAbroadGAByGenreIdWithHeartByMember(@Param("genreId") int genreId, @Param("memberId") Long memberId, Pageable pageOrderRequest);
	
	@Query(value = "SELECT a FROM MusicAlbum a WHERE a.acceptedTrackCnt > 0 AND a.member.id = :memberId")
	List<MusicAlbum> findTop5ByMemberId(@Param("memberId") Long memberId, Pageable pageable);

	@Query(value = "SELECT COUNT(a) FROM MusicAlbum a WHERE a.acceptedTrackCnt > 0 AND a.member.id = :memberId")
	public Long findCountByMemberId(@Param("memberId") Long memberId);
	
	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.member.id = :artistId") // , nativeQuery = true)
	public List<MusicAlbum> findTop4ByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);

	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.member.id = :artistId") // , nativeQuery = true)
	public Page<MusicAlbum> findAllByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId, Pageable pageable);

	@Query(value = "SELECT new com.upmusic.web.domain.MusicAlbum(a, CASE WHEN h.id IS NULL THEN 0 ELSE 1 END as liked) FROM MusicAlbum a LEFT JOIN MusicAlbumHeartRecord h ON h.musicAlbum.id = a.id and h.member.id = :memberId WHERE a.acceptedTrackCnt > 0 AND a.member.id = :artistId ORDER BY a.id DESC") // , nativeQuery = true)
	public List<MusicAlbum> findByArtistIdWithHeartByMember(@Param("artistId") Long artistId, @Param("memberId") Long memberId);

	public List<MusicAlbum> findAllByMemberId(Long memberId);

}

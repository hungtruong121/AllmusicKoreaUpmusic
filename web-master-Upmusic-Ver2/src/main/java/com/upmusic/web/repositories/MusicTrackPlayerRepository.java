package com.upmusic.web.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicTrackPlayer;


@Repository
public interface MusicTrackPlayerRepository extends JpaRepository<MusicTrackPlayer, Long> {
	
	List<MusicTrackPlayer> findByMusicTrackId(Long musicTrackId);

    List<MusicTrackPlayer> findByMemberId(Long memberId);

    @Query("SELECT p.id FROM MusicTrackPlayer p WHERE p.member.id = :memberId ORDER BY p.id DESC")
    public Page<Long> findLatestId(@Param("memberId") Long memberId, Pageable pageMusicOrderByNew);

    @Modifying
    @Transactional
    @Query("DELETE FROM MusicTrackPlayer p WHERE p.member.id = :memberId AND p.id NOT IN (:idList)")
    public void deleteByExcludedId(@Param("memberId") Long memberId, @Param("idList") List<Long> idList);
	
	MusicTrackPlayer findByMusicTrackIdAndMemberId(Long musicTrackId, Long memberId);
	
	Long countByMemberId(Long memberId);

	@Modifying
    @Transactional
	void deleteByMemberId(Long memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MusicTrackPlayer p WHERE p.member.id = :memberId AND p.musicTrack.id IN :ids")
    public void deleteByMusicTrackPlayerIds(@Param("memberId") Long memberId, @Param("ids") List<Long> ids);

}

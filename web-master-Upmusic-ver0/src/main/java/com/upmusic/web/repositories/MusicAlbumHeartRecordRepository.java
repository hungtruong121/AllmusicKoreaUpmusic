package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicAlbumHeartRecord;


@Repository
public interface MusicAlbumHeartRecordRepository extends JpaRepository<MusicAlbumHeartRecord, Long> {
	
	@Query(value = "select h from MusicAlbumHeartRecord h where h.musicAlbum.id = :musicAlbumId and h.member.id = :memberId")
	List<MusicAlbumHeartRecord> findAllByMusicAlbumIdAndMemberId(@Param("musicAlbumId") Long musicAlbumId, @Param("memberId") Long memberId);
	
	List<MusicAlbumHeartRecord> findAllByMusicAlbumId(Long musicAlbumId);
}

package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicTrackHeartRecord;


@Repository
public interface MusicTrackHeartRecordRepository extends JpaRepository<MusicTrackHeartRecord, Long> {
	
	MusicTrackHeartRecord findByMusicTrackIdAndMemberId(Long musicTrackId, Long memberId);
	
	List<MusicTrackHeartRecord> findByMusicTrackId(Long trackId);
}

package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.LeagueSeasonTrack;
import com.upmusic.web.domain.MusicTrack;


@Repository
public interface LeagueSeasonTrackRepository extends JpaRepository<LeagueSeasonTrack, Long> {

	
	/**
	 * 실시간 Top 5 반환
	 * @return track list
	 */
	List<LeagueSeasonTrack> findTop5ByOrderByHotPointDesc();
	
	/**
	 * 실시간 Top 50 반환
	 * @return track list
	 */
	List<LeagueSeasonTrack> findTop50ByOrderByHotPointDesc();
	
	/**
	 * 실시간 장르별 순위 반환
	 * @param genre
	 * @param pageOrderByHot
	 * @return track list
	 */
	Page<LeagueSeasonTrack> findByGenreId(int genreId, Pageable pageOrderByHot);
	
	/**
	 * 실시간 전체 인기순 반환
	 * @return track list
	 */
	List<LeagueSeasonTrack> findAllByOrderByHotPointDesc();
	
	/**
	 * 곡에 해당하는 리그 참가곡을 반환
	 * @param track
	 * @return league track
	 */
	LeagueSeasonTrack findOneByMusicTrack(MusicTrack track);

}

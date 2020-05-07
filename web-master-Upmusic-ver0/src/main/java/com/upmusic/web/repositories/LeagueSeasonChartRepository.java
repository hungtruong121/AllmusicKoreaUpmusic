package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.LeagueSeason;
import com.upmusic.web.domain.LeagueSeasonChart;
import com.upmusic.web.domain.MusicTrack;


@Repository
public interface LeagueSeasonChartRepository extends JpaRepository<LeagueSeasonChart, Long> {
	
	/**
	 * 해당 시즌의 차트 반환
	 * @param season
	 * @return chart
	 */
	Page<LeagueSeasonChart> findAllByLeagueSeason(LeagueSeason season, Pageable pageOrderByHot);

	List<LeagueSeasonChart> findByLeagueSeasonId(Long id);
	
	List<LeagueSeasonChart> findAllByMusicTrack(MusicTrack track);

	LeagueSeasonChart findOneByLeagueSeasonAndMusicTrack(LeagueSeason currentSeason, MusicTrack musicTrack);

}

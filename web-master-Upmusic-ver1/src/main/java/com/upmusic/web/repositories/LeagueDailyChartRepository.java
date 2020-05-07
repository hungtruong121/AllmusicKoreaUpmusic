package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.LeagueDailyChart;
import com.upmusic.web.domain.LeagueSeason;
import com.upmusic.web.domain.MusicTrack;


@Repository
public interface LeagueDailyChartRepository extends JpaRepository<LeagueDailyChart, Long> {
	
	/**
	 * Top 50 일간 차트 반환
	 * @param currentSeason
	 * @param day
	 * @return chart
	 */
	List<LeagueDailyChart> findTop50ByLeagueSeasonAndBackupDateOrderByHotPointDesc(LeagueSeason currentSeason, Date day);
	
	/**
	 * @deprecated
	 * 일간 장르별 순위 반환
	 * @param genreId
	 * @param pageOrderByHot
	 * @return track list
	 */
	Page<LeagueDailyChart> findByLeagueSeasonAndGenreId(LeagueSeason currentSeason, int genreId, Pageable pageOrderByHot);
	
	/**
	 * 일간 장르별 순위 반환
	 * @param genreId
	 * @param pageOrderByHot
	 * @return track list
	 */
	Page<LeagueDailyChart> findByLeagueSeasonAndBackupDateAndGenreId(LeagueSeason currentSeason, Date day, int genreId, Pageable pageOrderByHot);

	/**
	 * 백업된 날짜목록 반환
	 * @return date list
	 */
	@Query(value = "SELECT dc.backupDate FROM LeagueDailyChart dc WHERE dc.leagueSeason.id = :leagueSeasonId GROUP BY dc.backupDate")
	List<java.sql.Date> findAllByLeagueSeasonGroupByBackupDate(@Param("leagueSeasonId") Long leagueSeasonId);

	/**
	 * 현재 순위의 변화를 표시하기 위한 기준 데이터 반환
	 * @param currentSeason
	 * @param musicTrack
	 * @param yesterday
	 * @return track record
	 */
	LeagueDailyChart findOneByLeagueSeasonAndMusicTrackAndBackupDate(LeagueSeason currentSeason, MusicTrack musicTrack, Date yesterday);

	List<LeagueDailyChart> findByLeagueSeasonId(Long id);

	List<LeagueDailyChart> findAllByMusicTrack(MusicTrack track);
	
	Page<LeagueDailyChart> findByLeagueSeasonAndBackupDate(LeagueSeason currentSeason, Date day, Pageable pageOrderByHot);

}

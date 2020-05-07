package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.LeagueMonthlyChart;
import com.upmusic.web.domain.LeagueSeason;
import com.upmusic.web.domain.MusicTrack;


@Repository
public interface LeagueMonthlyChartRepository extends JpaRepository<LeagueMonthlyChart, Long> {
	
	/**
	 * Top 50 월간 차트 반환
	 * @param currentSeason
	 * @param month
	 * @return chart
	 */
	List<LeagueMonthlyChart> findTop50ByLeagueSeasonAndBackupDateOrderByHotPointDesc(LeagueSeason currentSeason, Date month);
	
	/**
	 * @deprecated
	 * 월간 장르별 순위 반환
	 * @param genre
	 * @param pageOrderByHot
	 * @return track list
	 */
	Page<LeagueMonthlyChart> findByLeagueSeasonAndGenreId(LeagueSeason currentSeason, int genreId, Pageable pageOrderByHot);
	
	/**
	 * 월간 장르별 순위 반환
	 * @param genreId
	 * @param pageOrderByHot
	 * @return track list
	 */
	Page<LeagueMonthlyChart> findByLeagueSeasonAndBackupDateAndGenreId(LeagueSeason currentSeason, Date day, int genreId, Pageable pageOrderByHot);
	
	/**
	 * 백업된 날짜목록 반환
	 * @return date list
	 */
	@Query(value = "SELECT mc.backupDate FROM LeagueMonthlyChart mc WHERE mc.leagueSeason.id = :leagueSeasonId GROUP BY mc.backupDate")
	List<java.sql.Date> findAllByLeagueSeasonGroupByBackupDate(@Param("leagueSeasonId") Long leagueSeasonId);
	
	/**
	 * 현재 순위의 변화를 표시하기 위한 기준 데이터 반환
	 * @param currentSeason
	 * @param musicTrack
	 * @param lastMonth
	 * @return track record
	 */
	LeagueMonthlyChart findOneByLeagueSeasonAndMusicTrackAndBackupDate(LeagueSeason currentSeason, MusicTrack musicTrack, Date lastMonth);

	List<LeagueMonthlyChart> findByLeagueSeasonId(Long id);

	List<LeagueMonthlyChart> findAllByMusicTrack(MusicTrack track);
	
	Page<LeagueMonthlyChart> findByLeagueSeasonAndBackupDate(LeagueSeason currentSeason, Date day, Pageable pageOrderByHot);
}

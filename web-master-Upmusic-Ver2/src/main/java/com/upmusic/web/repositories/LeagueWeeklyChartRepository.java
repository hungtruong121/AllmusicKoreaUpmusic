package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.LeagueSeason;
import com.upmusic.web.domain.LeagueWeeklyChart;
import com.upmusic.web.domain.MusicTrack;


@Repository
public interface LeagueWeeklyChartRepository extends JpaRepository<LeagueWeeklyChart, Long> {

	/**
	 * Top 50 주간 차트 반환
	 * @param currentSeason
	 * @param week
	 * @return chart
	 */
	List<LeagueWeeklyChart> findTop50ByLeagueSeasonAndBackupDateOrderByHotPointDesc(LeagueSeason currentSeason, Date week);
	
	/**
	 * @deprecated
	 * 주간 장르별 순위 반환
	 * @param genre
	 * @param pageOrderByHot
	 * @return track list
	 */
	Page<LeagueWeeklyChart> findByLeagueSeasonAndGenreId(LeagueSeason currentSeason, int genreId, Pageable pageOrderByHot);
	
	/**
	 * 주간 장르별 순위 반환
	 * @param genreId
	 * @param pageOrderByHot
	 * @return track list
	 */
	Page<LeagueWeeklyChart> findByLeagueSeasonAndBackupDateAndGenreId(LeagueSeason currentSeason, Date day, int genreId, Pageable pageOrderByHot);
	
	/**
	 * 백업된 날짜목록 반환
	 * @return date list
	 */
	@Query(value = "SELECT wc.backupDate FROM LeagueWeeklyChart wc WHERE wc.leagueSeason.id = :leagueSeasonId GROUP BY wc.backupDate")
	List<java.sql.Date> findAllByLeagueSeasonGroupByBackupDate(@Param("leagueSeasonId") Long leagueSeasonId);
	
	/**
	 * 현재 순위의 변화를 표시하기 위한 기준 데이터 반환
	 * @param currentSeason
	 * @param musicTrack
	 * @param lastWeek
	 * @return track record
	 */
	LeagueWeeklyChart findOneByLeagueSeasonAndMusicTrackAndBackupDate(LeagueSeason currentSeason, MusicTrack musicTrack, Date lastWeek);

	List<LeagueWeeklyChart> findByLeagueSeasonId(Long id);

	List<LeagueWeeklyChart> findAllByMusicTrack(MusicTrack track);
	
	Page<LeagueWeeklyChart> findByLeagueSeasonAndBackupDate(LeagueSeason currentSeason, Date day, Pageable pageOrderByHot);
}

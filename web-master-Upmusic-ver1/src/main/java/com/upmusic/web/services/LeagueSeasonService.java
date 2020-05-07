package com.upmusic.web.services;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.domain.LeagueDailyChart;
import com.upmusic.web.domain.LeagueMonthlyChart;
import com.upmusic.web.domain.LeagueSeason;
import com.upmusic.web.domain.LeagueSeasonChart;
import com.upmusic.web.domain.LeagueSeasonTrack;
import com.upmusic.web.domain.LeagueWeeklyChart;
import com.upmusic.web.domain.MusicTrack;


public interface LeagueSeasonService {
	
	Page<LeagueSeason> listAllLeagueSeasons(PageRequest pageOrderNew);
	
	List<LeagueSeason> listAYearLeagueSeasonsOrderByOpenDate(Date year);

	List<LeagueSeason> listAllYearLeagueSeasonsOrderByOpenDate(Date year);

	LeagueSeason getLeagueSeasonById(Long id);
	
	LeagueSeason getLeagueSeasonBySubject(String subject);
	
	LeagueSeason getCurrentLeagueSeason();

	LeagueSeason saveLeagueSeason(LeagueSeason leagueSeason);

    void deleteLeagueSeason(Long id);
    
    
    void registerTrack(MusicTrack track);
    
    void deleteTrack(MusicTrack track);
    
    
    List<String> listDaysOfDailyChart();
    
    List<String> listWeeksOfWeeklyChart();
    
    List<String> listMonthsOfMonthlyChart();
    
    List<String> listYearsOfAllLeagueSeasons();
    
    List<LeagueSeason> listLastSeasonsOfSeasonChart();
    
    
    List<LeagueSeasonTrack> listTop5ByCurrentSeason(Long memberId);
    
    List<LeagueSeasonTrack> listTop50ByCurrentSeason(Long memberId);
        
    List<LeagueDailyChart> listTop50DailyChartByCurrentSeason(Long memberId, Date day);
    
    List<LeagueWeeklyChart> listTop50WeeklyChartByCurrentSeason(Long memberId, Date week);
    
    List<LeagueMonthlyChart> listTop50MonthlyChartByCurrentSeason(Long memberId, Date month);
    
    
    Page<LeagueSeasonTrack> listAllByCurrentSeasonAndGenre(Long memberId, int genreId, PageRequest pageOrderByHot);
    
    Page<LeagueDailyChart> listAllDailyChartByCurrentSeasonAndGenre(Long memberId, Date day, int genreId, PageRequest pageOrderByHot);
    
    Page<LeagueWeeklyChart> listAllWeeklyChartByCurrentSeasonAndGenre(Long memberId, Date week, int genreId, PageRequest pageOrderByHot);
    
    Page<LeagueMonthlyChart> listAllMonthlyChartByCurrentSeasonAndGenre(Long memberId, Date month, int genreId, PageRequest pageOrderByHot);
 
    
    Page<LeagueSeasonTrack> listAllByCurrentSeason(Long memberId, PageRequest pageOrderByHot);
    
    Page<LeagueSeasonChart> listAllSeasonChartBySeason(Long memberId, LeagueSeason season, PageRequest pageOrderByHot);
    
    
    void updateLeagueTimeChart();
    
    void backupLeagueDailyChart();
    
    void backupLeagueWeeklyChart();
    
    void backupLeagueMonthlyChart();
    
    void backupLeagueSeasonChart() throws ParseException;

    void deleteOverlap();

}

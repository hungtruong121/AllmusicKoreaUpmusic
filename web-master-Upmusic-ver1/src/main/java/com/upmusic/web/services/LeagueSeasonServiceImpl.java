package com.upmusic.web.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.upmusic.web.domain.LeagueDailyChart;
import com.upmusic.web.domain.LeagueMonthlyChart;
import com.upmusic.web.domain.LeagueSeason;
import com.upmusic.web.domain.LeagueSeasonChart;
import com.upmusic.web.domain.LeagueSeasonTrack;
import com.upmusic.web.domain.LeagueTimeChart;
import com.upmusic.web.domain.LeagueWeeklyChart;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.MusicTrackHeartRecord;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.repositories.LeagueDailyChartRepository;
import com.upmusic.web.repositories.LeagueMonthlyChartRepository;
import com.upmusic.web.repositories.LeagueSeasonChartRepository;
import com.upmusic.web.repositories.LeagueSeasonRepository;
import com.upmusic.web.repositories.LeagueSeasonTrackRepository;
import com.upmusic.web.repositories.LeagueTimeChartRepository;
import com.upmusic.web.repositories.LeagueWeeklyChartRepository;
import com.upmusic.web.repositories.MusicTrackHeartRecordRepository;


@Service
public class LeagueSeasonServiceImpl implements LeagueSeasonService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private LeagueSeasonRepository leagueSeasonRepository;
	
	@Autowired
    private LeagueSeasonTrackRepository leagueSeasonTrackRepository;
	
	@Autowired
    private LeagueTimeChartRepository leagueTimeChartRepository;
	
	@Autowired
	private LeagueDailyChartRepository leagueDailyChartRepository;
	
	@Autowired
    private LeagueWeeklyChartRepository leagueWeeklyChartRepository;
	
	@Autowired
    private LeagueMonthlyChartRepository leagueMonthlyChartRepository;
	
	@Autowired
    private LeagueSeasonChartRepository leagueSeasonChartRepository;
	
	@Autowired
	private MusicTrackHeartRecordRepository trackHeartRecordRepository;
	

    @Override
    public Page<LeagueSeason> listAllLeagueSeasons(PageRequest pageOrderNew) {
        logger.debug("listAllLeagueSeasons called");
        return leagueSeasonRepository.findAll(pageOrderNew);
    }
    
    @Override
    public List<LeagueSeason> listAYearLeagueSeasonsOrderByOpenDate(Date year) {
    	logger.debug("listAYearLeagueSeasonsOrderByOpenDate called");
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(year);
    	cal.set(Calendar.DAY_OF_YEAR, 1);
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	Date firstDayOfThisYear = cal.getTime();
    	
    	cal.add(Calendar.YEAR, 1);
    	Date firstDayOfNextYear = cal.getTime();
    	
    	logger.debug("listAllLeagueSeasonsOrderByOpenDate : firstDay is {}", firstDayOfThisYear);
    	logger.debug("listAllLeagueSeasonsOrderByOpenDate : firstDayOfNextYear is {}", firstDayOfNextYear);
    	return leagueSeasonRepository.findByOpenDateGreaterThanEqualAndOpenDateLessThanAndOpenDateLessThanEqualOrderByOpenDate(firstDayOfThisYear, firstDayOfNextYear, new Date());
    }

	@Override
	public List<LeagueSeason> listAllYearLeagueSeasonsOrderByOpenDate(Date year) {
		logger.debug("listAllYearLeagueSeasonsOrderByOpenDate called");
		Calendar cal = Calendar.getInstance();
		cal.setTime(year);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date firstDayOfThisYear = cal.getTime();

		cal.add(Calendar.YEAR, 1);
		Date firstDayOfNextYear = cal.getTime();

		logger.debug("listAllLeagueSeasonsOrderByOpenDate : firstDay is {}", firstDayOfThisYear);
		logger.debug("listAllLeagueSeasonsOrderByOpenDate : firstDayOfNextYear is {}", firstDayOfNextYear);
		return leagueSeasonRepository.findAllByOpenDateGreaterThanEqualAndOpenDateLessThanOrderByOpenDate(firstDayOfThisYear, firstDayOfNextYear);
	}

    @Override
    public LeagueSeason getLeagueSeasonById(Long id) {
        logger.debug("getLeagueSeasonById called");
        return leagueSeasonRepository.findById(id).orElse(null);
    }
    
    @Override
    public LeagueSeason getLeagueSeasonBySubject(String subject) {
    	logger.debug("getLeagueSeasonBySubject called");
        return leagueSeasonRepository.findOneBySubject(subject);
    }
    
    @Override
    public LeagueSeason getCurrentLeagueSeason() {
    	logger.debug("getCurrentLeagueSeason called");
        return leagueSeasonRepository.findOneByOpenDateLessThanEqualAndCloseDateGreaterThanEqual();
    }

    @Override
    public LeagueSeason saveLeagueSeason(LeagueSeason leagueSeason) {
        logger.debug("saveLeagueSeason called");
        return leagueSeasonRepository.save(leagueSeason);
    }

    @Override
    public void deleteLeagueSeason(Long id) {
        logger.debug("deleteLeagueSeason called");
        LeagueSeason season = getLeagueSeasonById(id);
        if (season != null) {
        	List<LeagueSeasonChart> charts = leagueSeasonChartRepository.findByLeagueSeasonId(id);
        	for (LeagueSeasonChart chart: charts) {
        		leagueSeasonChartRepository.delete(chart);
        	}
        	List<LeagueDailyChart> dailyCharts = leagueDailyChartRepository.findByLeagueSeasonId(id);
        	for (LeagueDailyChart chart: dailyCharts) {
        		leagueDailyChartRepository.delete(chart);
        	}
        	List<LeagueWeeklyChart> weeklyCharts = leagueWeeklyChartRepository.findByLeagueSeasonId(id);
        	for (LeagueWeeklyChart chart: weeklyCharts) {
        		leagueWeeklyChartRepository.delete(chart);
        	}
        	List<LeagueMonthlyChart> monthlyCharts = leagueMonthlyChartRepository.findByLeagueSeasonId(id);
        	for (LeagueMonthlyChart chart: monthlyCharts) {
        		leagueMonthlyChartRepository.delete(chart);
        	}
        	leagueSeasonRepository.deleteById(id);
        }
    }
    
    /*
     * Chart
     */
    
    /**
     * 곡을 현재 리그 시즌에 등록 
     */
    @Override
    public void registerTrack(MusicTrack track) {
    	logger.debug("registerTrack called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		LeagueSeasonTrack seasonTrack = leagueSeasonTrackRepository.findOneByMusicTrack(track);
    		if (seasonTrack == null) {
    			seasonTrack = new LeagueSeasonTrack();
        		seasonTrack.setMusicTrack(track);
        		seasonTrack.setGenre(track.getGenre());
        		leagueSeasonTrackRepository.save(seasonTrack);
    		}
    	}
    }
    
    @Override
    public void deleteTrack(MusicTrack track) {
    	logger.debug("registerTrack called");
    	LeagueSeasonTrack seasonTrack = leagueSeasonTrackRepository.findOneByMusicTrack(track);
        if (seasonTrack != null) leagueSeasonTrackRepository.delete(seasonTrack);
        
    	List<LeagueSeasonChart> charts = leagueSeasonChartRepository.findAllByMusicTrack(track);
    	for (LeagueSeasonChart chart: charts) {
    		leagueSeasonChartRepository.delete(chart);
    	}
    	List<LeagueTimeChart> timeCharts = leagueTimeChartRepository.findAllByMusicTrack(track);
    	for (LeagueTimeChart chart: timeCharts) {
    		leagueTimeChartRepository.delete(chart);
    	}
    	List<LeagueDailyChart> dailyCharts = leagueDailyChartRepository.findAllByMusicTrack(track);
    	for (LeagueDailyChart chart: dailyCharts) {
    		leagueDailyChartRepository.delete(chart);
    	}
    	List<LeagueWeeklyChart> weeklyCharts = leagueWeeklyChartRepository.findAllByMusicTrack(track);
    	for (LeagueWeeklyChart chart: weeklyCharts) {
    		leagueWeeklyChartRepository.delete(chart);
    	}
    	List<LeagueMonthlyChart> monthlyCharts = leagueMonthlyChartRepository.findAllByMusicTrack(track);
    	for (LeagueMonthlyChart chart: monthlyCharts) {
    		leagueMonthlyChartRepository.delete(chart);
    	}
    }
    
    /**
     * 시즌 내 최신 top 5
     */
    @Override
    public List<LeagueSeasonTrack> listTop5ByCurrentSeason(Long memberId) {
    	logger.debug("listTop5ByCurrentSeason called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<LeagueSeasonTrack> leagueTracks = leagueSeasonTrackRepository.findTop5ByOrderByHotPointDesc();
    		logger.debug("listTop5ByCurrentSeason called : leagueTracks count is {}", leagueTracks.size());
	    	// 추가 정보 입력
	    	int currentRank = 1;
	    	for (LeagueSeasonTrack leagueTrack : leagueTracks) {
	    		if (0 < memberId) leagueTrack.getMusicTrack().setLiked(likedTrack(leagueTrack.getMusicTrack().getId(), memberId));
	    		leagueTrack.setRank(currentRank);
	    		leagueTrack.setLastRank(getLastRankFromTimeChart(leagueTrack.getMusicTrack()));
	    		currentRank++;
	    	}
	    	return leagueTracks;
    	}
    	return new ArrayList<LeagueSeasonTrack>();
    }
    
    /**
     * 시즌 내 최신 top 50
     */
    @Override
    public List<LeagueSeasonTrack> listTop50ByCurrentSeason(Long memberId) {
    	logger.debug("listTop50ByCurrentSeason called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<LeagueSeasonTrack> leagueTracks = leagueSeasonTrackRepository.findTop50ByOrderByHotPointDesc();
    		logger.debug("listTop50ByCurrentSeason called : leagueTracks count is {}", leagueTracks.size());
	    	// 추가 정보 입력
	    	int currentRank = 1;
	    	for (LeagueSeasonTrack leagueTrack : leagueTracks) {
	    		if (0 < memberId) leagueTrack.getMusicTrack().setLiked(likedTrack(leagueTrack.getMusicTrack().getId(), memberId));
	    		leagueTrack.setRank(currentRank);
	    		leagueTrack.setLastRank(getLastRankFromTimeChart(leagueTrack.getMusicTrack()));
	    		currentRank++;
	    	}
	    	return leagueTracks;
    	}
    	return new ArrayList<LeagueSeasonTrack>();
    }
    
    /**
     * 일간 날짜 목록을 반환
     */
	@Override
   	public List<String> listDaysOfDailyChart() {
   		logger.debug("listDaysOfDailyChart called");
   		List<String> days = new ArrayList<String>();
   		LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<java.sql.Date> dateDays = leagueDailyChartRepository.findAllByLeagueSeasonGroupByBackupDate(currentSeason.getId());
       		for (java.sql.Date date : dateDays) {
       			days.add(new SimpleDateFormat("yyyy-MM-dd").format(date));
       		}
    	}
   		return Lists.reverse(days);
   	}
    
    /**
     * 시즌 내 일간 top 50
     */
    @Override
   	public List<LeagueDailyChart> listTop50DailyChartByCurrentSeason(Long memberId, Date day) {
       	logger.debug("listTop50DailyChartByCurrentSeason called");
       	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<LeagueDailyChart> leagueCharts = leagueDailyChartRepository.findTop50ByLeagueSeasonAndBackupDateOrderByHotPointDesc(currentSeason, day);
    		logger.debug("listTop50DailyChartByCurrentSeason called : leagueTracks count is {}", leagueCharts.size());
	    	// 추가 정보 입력
	    	int currentRank = 1;
	    	for (LeagueDailyChart leagueChart : leagueCharts) {
	    		if (0 < memberId) leagueChart.getMusicTrack().setLiked(likedTrack(leagueChart.getMusicTrack().getId(), memberId));
	    		leagueChart.setRank(currentRank);
	    		leagueChart.setLastRank(getLastRankFromDailyChart(currentSeason, leagueChart.getMusicTrack(), day));
	    		currentRank++;
	    	}
	    	return leagueCharts;
    	}
    	return new ArrayList<LeagueDailyChart>();
   	}
    
    /**
     * top 50의 주간 날짜 목록을 반환
     */
	@Override
   	public List<String> listWeeksOfWeeklyChart() {
		logger.debug("listWeeksOfWeeklyChart called");
		List<String> days = new ArrayList<String>();
		LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<java.sql.Date> dateDays = leagueWeeklyChartRepository.findAllByLeagueSeasonGroupByBackupDate(currentSeason.getId());
       		for (java.sql.Date date : dateDays) {
       			Calendar cal = Calendar.getInstance();
       			cal.setTime(date);
       			cal.add(Calendar.DATE, -6);
       			String startDay = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
       			days.add(startDay);
       			String endDay = new SimpleDateFormat("yyyy-MM-dd").format(date);
       			// days.add(startDay + " ~ " + endDay);
       			days.add(endDay);
       		}
    	}
   		return Lists.reverse(days);
	}

	/**
     * 시즌 내 주간 top 50
     */
   	@Override
   	public List<LeagueWeeklyChart> listTop50WeeklyChartByCurrentSeason(Long memberId, Date week) {
   		logger.debug("listTop50WeeklyChartByCurrentSeason called");
       	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<LeagueWeeklyChart> leagueCharts = leagueWeeklyChartRepository.findTop50ByLeagueSeasonAndBackupDateOrderByHotPointDesc(currentSeason, week);
    		logger.debug("listTop50WeeklyChartByCurrentSeason called : leagueTracks count is {}", leagueCharts.size());
	    	// 추가 정보 입력
	    	int currentRank = 1;
	    	for (LeagueWeeklyChart leagueChart : leagueCharts) {
	    		if (0 < memberId) leagueChart.getMusicTrack().setLiked(likedTrack(leagueChart.getMusicTrack().getId(), memberId));
	    		leagueChart.setRank(currentRank);
	    		leagueChart.setLastRank(getLastRankFromWeeklyChart(currentSeason, leagueChart.getMusicTrack(), week));
	    		currentRank++;
	    	}
	    	return leagueCharts;
    	}
    	return new ArrayList<LeagueWeeklyChart>();
   	}
   	
   	/**
     * top 50의 월간 날짜 목록을 반환
     */
	@Override
   	public List<String> listMonthsOfMonthlyChart() {
		logger.debug("listMonthsOfMonthlyChart called");
		List<String> days = new ArrayList<String>();
		LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<java.sql.Date> dateDays = leagueMonthlyChartRepository.findAllByLeagueSeasonGroupByBackupDate(currentSeason.getId());
       		for (java.sql.Date date : dateDays) {
       			Calendar cal = Calendar.getInstance();
       			cal.setTime(date);
       			cal.add(Calendar.DATE, -1);
       			String lastMonth = new SimpleDateFormat("yyyy-MM").format(cal.getTime());
       			days.add(lastMonth);
       		}
    	}
   		return Lists.reverse(days);
	}
	
	/**
	 * 시즌이 존재하는 연도 목록을 반환
	 */
	@Override
	public List<String> listYearsOfAllLeagueSeasons() {
		logger.debug("listYearsOfAllLeagueSeasons called");
   		List<Integer> dateYears = leagueSeasonRepository.findAllGroupByOpenDate();
   		List<String> years = new ArrayList<String>();
   		for (Integer dateYear : dateYears) {
   			years.add(String.valueOf(dateYear));
   		}
   		return Lists.reverse(years);
	}

   	/**
   	 * 시즌 내 월간 top 50
   	 */
   	@Override
   	public List<LeagueMonthlyChart> listTop50MonthlyChartByCurrentSeason(Long memberId, Date month) {
   		logger.debug("listTop50MonthlyChartByCurrentSeason called");
       	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		List<LeagueMonthlyChart> leagueCharts = leagueMonthlyChartRepository.findTop50ByLeagueSeasonAndBackupDateOrderByHotPointDesc(currentSeason, month);
    		logger.debug("listTop50MonthlyChartByCurrentSeason called : leagueTracks count is {}", leagueCharts.size());
	    	// 추가 정보 입력
	    	int currentRank = 1;
	    	for (LeagueMonthlyChart leagueChart : leagueCharts) {
	    		if (0 < memberId) leagueChart.getMusicTrack().setLiked(likedTrack(leagueChart.getMusicTrack().getId(), memberId));
	    		leagueChart.setRank(currentRank);
	    		leagueChart.setLastRank(getLastRankFromMonthlyChart(currentSeason, leagueChart.getMusicTrack(), month));
	    		currentRank++;
	    	}
	    	return leagueCharts;
    	}
    	return new ArrayList<LeagueMonthlyChart>();
   	}
   	
   	/**
     * 시즌 내 장르별 순위
     */
    @Override
    public Page<LeagueSeasonTrack> listAllByCurrentSeasonAndGenre(Long memberId, int genreId, PageRequest pageOrderByHot) {
    	logger.debug("listAllByCurrentSeasonAndGenre called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		Page<LeagueSeasonTrack> leagueTracks;
    		if(genreId == 0) {
    			leagueTracks = leagueSeasonTrackRepository.findAll(pageOrderByHot);
    		}else leagueTracks = leagueSeasonTrackRepository.findByGenreId(genreId, pageOrderByHot);
    		logger.debug("listAllByCurrentSeasonAndGenre called : leagueTracks count is {}", leagueTracks.getSize());
	    	// 추가 정보 입력
    		int currentRank = pageOrderByHot.getPageNumber() * pageOrderByHot.getPageSize() + 1;
	    	for (LeagueSeasonTrack leagueTrack : leagueTracks) {
	    		if (0 < memberId) leagueTrack.getMusicTrack().setLiked(likedTrack(leagueTrack.getMusicTrack().getId(), memberId));
	    		leagueTrack.setRank(currentRank);
	    		currentRank++;
	    	}
	    	return leagueTracks;
    	}
    	return null;
    }
    
    /**
     * 시즌 내 일간 장르별 순위
     */
    @Override
    public Page<LeagueDailyChart> listAllDailyChartByCurrentSeasonAndGenre(Long memberId, Date day, int genreId, PageRequest pageOrderByHot) {
    	logger.debug("listAllDailyChartByCurrentSeasonAndGenre called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		Page<LeagueDailyChart> leagueTracks;
    		if(genreId == 0) {
    			leagueTracks = leagueDailyChartRepository.findByLeagueSeasonAndBackupDate(currentSeason, day, pageOrderByHot);
    		}else leagueTracks = leagueDailyChartRepository.findByLeagueSeasonAndBackupDateAndGenreId(currentSeason, day, genreId, pageOrderByHot);
    		logger.debug("listAllDailyChartByCurrentSeasonAndGenre called : leagueTracks count is {}", leagueTracks.getSize());
	    	// 추가 정보 입력
    		int currentRank = pageOrderByHot.getPageNumber() * pageOrderByHot.getPageSize() + 1;
	    	for (LeagueDailyChart leagueTrack : leagueTracks) {
	    		if (0 < memberId) leagueTrack.getMusicTrack().setLiked(likedTrack(leagueTrack.getMusicTrack().getId(), memberId));
	    		leagueTrack.setRank(currentRank);
	    		currentRank++;
	    	}
	    	return leagueTracks;
    	}
    	return null;
    }
    
    /**
     * 시즌 내 주간 장르별 순위
     */
    @Override
    public Page<LeagueWeeklyChart> listAllWeeklyChartByCurrentSeasonAndGenre(Long memberId, Date week, int genreId, PageRequest pageOrderByHot) {
    	logger.debug("listAllWeeklyChartByCurrentSeasonAndGenre called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		Page<LeagueWeeklyChart> leagueTracks;
    		if(genreId == 0) {
    			leagueTracks = leagueWeeklyChartRepository.findByLeagueSeasonAndBackupDate(currentSeason, week, pageOrderByHot);
    		}else leagueTracks = leagueWeeklyChartRepository.findByLeagueSeasonAndBackupDateAndGenreId(currentSeason, week, genreId, pageOrderByHot);
    		logger.debug("listAllWeeklyChartByCurrentSeasonAndGenre called : leagueTracks count is {}", leagueTracks.getSize());
	    	// 추가 정보 입력
    		int currentRank = pageOrderByHot.getPageNumber() * pageOrderByHot.getPageSize() + 1;
	    	for (LeagueWeeklyChart leagueTrack : leagueTracks) {
	    		if (0 < memberId) leagueTrack.getMusicTrack().setLiked(likedTrack(leagueTrack.getMusicTrack().getId(), memberId));
	    		leagueTrack.setRank(currentRank);
	    		currentRank++;
	    	}
	    	return leagueTracks;
    	}
    	return null;
    }
    
    /**
     * 시즌 내 월간 장르별 순위
     */
    @Override
    public Page<LeagueMonthlyChart> listAllMonthlyChartByCurrentSeasonAndGenre(Long memberId, Date month, int genreId, PageRequest pageOrderByHot) {
    	logger.debug("listAllMonthlyChartByCurrentSeasonAndGenre called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		Page<LeagueMonthlyChart> leagueTracks;
    		if(genreId == 0) {
    			leagueTracks = leagueMonthlyChartRepository.findByLeagueSeasonAndBackupDate(currentSeason, month, pageOrderByHot);
    		}else leagueTracks = leagueMonthlyChartRepository.findByLeagueSeasonAndBackupDateAndGenreId(currentSeason, month, genreId, pageOrderByHot);
    		logger.debug("listAllMonthlyChartByCurrentSeasonAndGenre called : leagueTracks count is {}", leagueTracks.getSize());
	    	// 추가 정보 입력
	    	int currentRank = pageOrderByHot.getPageNumber() * pageOrderByHot.getPageSize() + 1;
	    	for (LeagueMonthlyChart leagueTrack : leagueTracks) {
	    		if (0 < memberId) leagueTrack.getMusicTrack().setLiked(likedTrack(leagueTrack.getMusicTrack().getId(), memberId));
	    		leagueTrack.setRank(currentRank);
	    		currentRank++;
	    	}
	    	return leagueTracks;
    	}
    	return null;
    }
   	
   	/**
     * 시즌 목록을 반환
     */
   	@Override
   	public List<LeagueSeason> listLastSeasonsOfSeasonChart() {
   		logger.debug("listSeasonsOfSeasonChart called");
   		LeagueSeason currentSeason = getCurrentLeagueSeason();
   		return leagueSeasonRepository.findAllByIdNotOrderByOpenDateAsc(currentSeason != null ? currentSeason.getId() : 0);
   	}
   	
   	/**
   	 * 현재 시즌의 차트 반환
   	 */
   	@Override
   	public Page<LeagueSeasonTrack> listAllByCurrentSeason(Long memberId, PageRequest pageOrderByHot) {
   		logger.debug("listAllByCurrentSeason called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		Page<LeagueSeasonTrack> leagueTracks = leagueSeasonTrackRepository.findAll(pageOrderByHot);
    		logger.debug("listAllByCurrentSeason called : leagueTracks count is {}", leagueTracks.getSize());
	    	// 추가 정보 입력
    		int currentRank = pageOrderByHot.getPageNumber() * pageOrderByHot.getPageSize() + 1;
	    	for (LeagueSeasonTrack leagueTrack : leagueTracks) {
	    		if (0 < memberId) leagueTrack.getMusicTrack().setLiked(likedTrack(leagueTrack.getMusicTrack().getId(), memberId));
	    		leagueTrack.setRank(currentRank);
	    		currentRank++;
	    	}
	    	return leagueTracks;
    	}
    	return null;
   	}
   	
   	/**
   	 * 해당 시즌의 차트 반환
   	 */
   	@Override
   	public Page<LeagueSeasonChart> listAllSeasonChartBySeason(Long memberId, LeagueSeason season, PageRequest pageOrderByHot) {
   		logger.debug("listAllSeasonChartBySeason called");
		Page<LeagueSeasonChart> leagueCharts = leagueSeasonChartRepository.findAllByLeagueSeason(season, pageOrderByHot);
		logger.debug("listAllSeasonChartBySeason called : leagueTracks count is {}", leagueCharts.getSize());
    	// 추가 정보 입력
		int currentRank = pageOrderByHot.getPageNumber() * pageOrderByHot.getPageSize() + 1;
    	for (LeagueSeasonChart leagueChart : leagueCharts) {
    		if (0 < memberId) leagueChart.getMusicTrack().setLiked(likedTrack(leagueChart.getMusicTrack().getId(), memberId));
    		leagueChart.setRank(currentRank);
    		currentRank++;
    	}
    	return leagueCharts;
   	}
   	
    
    /**
     * 현 시즌의 정보를 시간별(LeagueTimeChart)로 업데이트
     * Cronjob에서 호출
     */
    @Override
    public void updateLeagueTimeChart() {
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		logger.debug("updateLeagueTimeChart called : currentSeason is {}", currentSeason.getSubject());
    		leagueTimeChartRepository.deleteAllInBatch();
        	Iterable<LeagueSeasonTrack> seasonTracks = leagueSeasonTrackRepository.findAllByOrderByHotPointDesc();
        	int rank = 1;
        	for (LeagueSeasonTrack seasonTrack : seasonTracks) {
        		LeagueTimeChart timeChart = leagueTimeChartRepository.findOneByMusicTrack(seasonTrack.getMusicTrack());
        		if (timeChart != null) continue;
        		try {
        			timeChart = new LeagueTimeChart();
            		timeChart.setHeartCnt(seasonTrack.getHeartCnt());
            		timeChart.setPlayCnt(seasonTrack.getPlayCnt());
            		timeChart.setRank(rank);
            		timeChart.setMusicTrack(seasonTrack.getMusicTrack());
            		leagueTimeChartRepository.save(timeChart);
            		rank++;
        		} catch (Exception e) {
        			logger.error("updateLeagueTimeChart : e is {}", e.toString());
        		}
        	}
        	leagueTimeChartRepository.deleteOverlap();
    	}
    }
    
    /**
     * 현 시즌의 정보를 일별(LeagueDailyChart)로 백업
     * Cronjob에서 호출
     */
	@Override
    public void backupLeagueDailyChart() {
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		logger.debug("backupLeagueDailyChart called : currentSeason is {}", currentSeason.getSubject());
        	List<LeagueSeasonTrack> seasonTracks = leagueSeasonTrackRepository.findAllByOrderByHotPointDesc();
        	int rank = 1;
        	for (LeagueSeasonTrack seasonTrack : seasonTracks) {
        		LeagueDailyChart dailyChart = leagueDailyChartRepository.findOneByLeagueSeasonAndMusicTrackAndBackupDate(currentSeason, seasonTrack.getMusicTrack(), new Date());
        		if (dailyChart != null) continue;
        		try {
        			dailyChart = new LeagueDailyChart();
            		dailyChart.setHeartCnt(seasonTrack.getHeartCnt());
            		dailyChart.setPlayCnt(seasonTrack.getPlayCnt());
            		dailyChart.setRank(rank);
            		dailyChart.setGenre(seasonTrack.getGenre());
            		dailyChart.setLeagueSeason(currentSeason);
            		dailyChart.setMusicTrack(seasonTrack.getMusicTrack());
            		leagueDailyChartRepository.save(dailyChart);
            		rank++;
        		} catch (Exception e) {
        			logger.error("backupLeagueDailyChart : e is {}", e.toString());
        		}
        	}
    	}
    }
    
    /**
     * 현 시즌의 정보를 주별(LeagueWeeklyChart)로 백업
     * Cronjob에서 호출
     */
    @Override
    public void backupLeagueWeeklyChart() {
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		logger.debug("backupLeagueWeeklyChart called : currentSeason is {}", currentSeason.getSubject());
    		List<LeagueSeasonTrack> seasonTracks = leagueSeasonTrackRepository.findAllByOrderByHotPointDesc();
    		int rank = 1;
        	for (LeagueSeasonTrack seasonTrack : seasonTracks) {
        		LeagueWeeklyChart weeklyChart = leagueWeeklyChartRepository.findOneByLeagueSeasonAndMusicTrackAndBackupDate(currentSeason, seasonTrack.getMusicTrack(), new Date());
        		if (weeklyChart != null) continue;
        		try {
        			weeklyChart = new LeagueWeeklyChart();
            		weeklyChart.setHeartCnt(seasonTrack.getHeartCnt());
            		weeklyChart.setPlayCnt(seasonTrack.getPlayCnt());
            		weeklyChart.setRank(rank);
            		weeklyChart.setGenre(seasonTrack.getGenre());
            		weeklyChart.setLeagueSeason(currentSeason);
            		weeklyChart.setMusicTrack(seasonTrack.getMusicTrack());
            		leagueWeeklyChartRepository.save(weeklyChart);
            		rank++;
        		} catch (Exception e) {
        			logger.error("backupLeagueWeeklyChart : e is {}", e.toString());
        		}
        	}
    	}
    }
    
    /**
     * 현 시즌의 정보를 월별(LeagueMonthlyChart)로 백업
     * Cronjob에서 호출
     */
    @Override
    public void backupLeagueMonthlyChart() {
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		logger.debug("backupLeagueMonthlyChart called : currentSeason is {}", currentSeason.getSubject());
    		List<LeagueSeasonTrack> seasonTracks = leagueSeasonTrackRepository.findAllByOrderByHotPointDesc();
    		int rank = 1;
        	for (LeagueSeasonTrack seasonTrack : seasonTracks) {
        		LeagueMonthlyChart monthlyChart = leagueMonthlyChartRepository.findOneByLeagueSeasonAndMusicTrackAndBackupDate(currentSeason, seasonTrack.getMusicTrack(), new Date());
        		if (monthlyChart != null) continue;
        		try {
        			monthlyChart = new LeagueMonthlyChart();
            		monthlyChart.setHeartCnt(seasonTrack.getHeartCnt());
            		monthlyChart.setPlayCnt(seasonTrack.getPlayCnt());
            		monthlyChart.setRank(rank);
            		monthlyChart.setGenre(seasonTrack.getGenre());
            		monthlyChart.setLeagueSeason(currentSeason);
            		monthlyChart.setMusicTrack(seasonTrack.getMusicTrack());
            		leagueMonthlyChartRepository.save(monthlyChart);
            		rank++;
        		} catch (Exception e) {
        			logger.error("backupLeagueMonthlyChart : e is {}", e.toString());
        		}
        	}
    	}
    }
    
    /**
     * 현 시즌의 정보를 시즌별(LeagueSeasonChart)로 백업
     * Cronjob에서 호출
     * @throws ParseException 
     */
    @Override
    public void backupLeagueSeasonChart() throws ParseException {
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		String closeDateString = UPMusicHelper.formattedTimeDay(currentSeason.getCloseDate());
    		closeDateString += " 23:59:59";
    		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Date closeDate = transFormat.parse(closeDateString);
			if (new Date(new Date().getTime() + (1000 * 60 * 60)).after(closeDate)) {
				logger.debug("backupLeagueSeasonChart called : currentSeason is {}", currentSeason.getSubject());
				List<LeagueSeasonTrack> seasonTracks = leagueSeasonTrackRepository.findAllByOrderByHotPointDesc();
				int rank = 1;
	        	for (LeagueSeasonTrack seasonTrack : seasonTracks) {
	        		LeagueSeasonChart seasonChart = leagueSeasonChartRepository.findOneByLeagueSeasonAndMusicTrack(currentSeason, seasonTrack.getMusicTrack());
	        		if (seasonChart != null) continue;
	        		try {
	        			seasonChart = new LeagueSeasonChart();
		        		seasonChart.setHeartCnt(seasonTrack.getHeartCnt());
		        		seasonChart.setPlayCnt(seasonTrack.getPlayCnt());
		        		seasonChart.setRank(rank);
		        		seasonChart.setLeagueSeason(currentSeason);
		        		seasonChart.setMusicTrack(seasonTrack.getMusicTrack());
		        		leagueSeasonChartRepository.save(seasonChart);
		        		rank++;
	        		} catch (Exception e) {
	        			logger.error("backupLeagueSeasonChart : e is {}", e.toString());
	        		}
	        	}
	        	// 현재 시즌을 저장하는 LeagueSeasonTrack의 정보는 삭제
	        	leagueSeasonTrackRepository.deleteAllInBatch();
	        	// TODO - 현재 기획서는 과거 시즌의 기간별 데이터가 필요하지 않아 아래의 항목을 모두 삭제하는 것이 좋지만 확인이 필요
//	        	leagueTimeChartRepository.deleteAllInBatch();
//	        	leagueDailyChartRepository.deleteAllInBatch();
//	        	leagueWeeklyChartRepository.deleteAllInBatch();
//	        	leagueMonthlyChartRepository.deleteAllInBatch();
	        	
    		}
    	}
    }
    
    /**
     * 해당 비디오와 회원의 좋아요 여부
     */
    private boolean likedTrack(Long id, Long memberId) {
    	MusicTrackHeartRecord record = trackHeartRecordRepository.findByMusicTrackIdAndMemberId(id, memberId);
    	return record != null;
    }
    
    /**
     * 타임 차트에서의 순위 : 1시간 전의 순위
     * @param track
     * @return rank
     */
    private int getLastRankFromTimeChart(MusicTrack track) {
    	LeagueTimeChart timeChart = leagueTimeChartRepository.findOneByMusicTrack(track);
    	if (timeChart != null) return timeChart.getRank();
    	return 0;
    }
    
    /**
     * Daily 차트에서의 순위 : 1일 전의 순위
     * @param track
     * @return rank
     */
    private int getLastRankFromDailyChart(LeagueSeason currentSeason, MusicTrack musicTrack, Date today) {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
    	LeagueDailyChart dailyChart = leagueDailyChartRepository.findOneByLeagueSeasonAndMusicTrackAndBackupDate(currentSeason, musicTrack, yesterday);
    	if (dailyChart != null) return dailyChart.getRank();
		return 0;
	}
    
    /**
     * Weekly 차트에서의 순위 : 1주 전의 순위
     * @param track
     * @return rank
     */
    private int getLastRankFromWeeklyChart(LeagueSeason currentSeason, MusicTrack musicTrack, Date thisWeek) {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(thisWeek);
        calendar.add(Calendar.DATE, -7);
        Date lastWeek = calendar.getTime();
    	LeagueWeeklyChart weeklyChart = leagueWeeklyChartRepository.findOneByLeagueSeasonAndMusicTrackAndBackupDate(currentSeason, musicTrack, lastWeek);
    	if (weeklyChart != null) return weeklyChart.getRank();
		return 0;
	}
    
    /**
     * Monthly 차트에서의 순위 : 1개월 전의 순위
     * @param track
     * @return rank
     */
    private int getLastRankFromMonthlyChart(LeagueSeason currentSeason, MusicTrack musicTrack, Date thisMonth) {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(thisMonth);
        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();
    	LeagueMonthlyChart monthlyChart = leagueMonthlyChartRepository.findOneByLeagueSeasonAndMusicTrackAndBackupDate(currentSeason, musicTrack, lastMonth);
    	if (monthlyChart != null) return monthlyChart.getRank();
		return 0;
	}

	/**
	 * league_time_chart에서 중복되는 데이터를 삭제
	 */
	public void deleteOverlap(){
		leagueTimeChartRepository.deleteOverlap();
	}
    
}

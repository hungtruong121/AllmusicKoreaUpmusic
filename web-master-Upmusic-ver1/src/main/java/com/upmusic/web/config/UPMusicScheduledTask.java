package com.upmusic.web.config;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.upmusic.web.services.AuthenticationEmailService;
import com.upmusic.web.services.LeagueSeasonService;


/**
 * 업 리그의 순위 변동 정보를 제공하기 위한 크론잡
 */
@Component
@Profile({"dev", "prod"})
public class UPMusicScheduledTask {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LeagueSeasonService leagueSeasonService;
	
	@Autowired
	private AuthenticationEmailService authenticationEmailService;
	
	@Scheduled(cron = "30 59 * * * ?") // 매시간 59:59
	public void updateLeagueTimeChart() {
	    logger.debug("updateLeagueTimeChart :: Execution Time - {}", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
	    leagueSeasonService.updateLeagueTimeChart();
	}
	
	@Scheduled(cron = "40 59 23 * * ?") // 매일 23:59:59
	public void backupLeagueDailyChart() throws ParseException {
	    logger.debug("backupLeagueDailyChart :: Execution Time - {}", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
	    leagueSeasonService.backupLeagueDailyChart();
	    // 매일 자정에 시즌이 종료되었는지 확인 후 백업
	    leagueSeasonService.backupLeagueSeasonChart();
	}
	
	@Scheduled(cron = "50 59 23 ? * SUN") // 매주 일요일 23:59:59
	public void backupLeagueWeeklyChart() {
	    logger.debug("backupLeagueWeeklyChart :: Execution Time - {}", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
	    leagueSeasonService.backupLeagueWeeklyChart();
	}
	
	@Scheduled(cron = "1 0 0 1 * ?") // 매달 첫날 0시 spring에서 L 지정 불가 (cron = "59 59 59 L * ?")
	public void backupLeagueMonthlyChart() {
	    logger.debug("backupLeagueMonthlyChart :: Execution Time - {}", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
	    leagueSeasonService.backupLeagueMonthlyChart();
	}
	
	@Scheduled(cron = "59 59 23 * * ?")//delete all authentication email (23:59:59) ngoclh-2018/12/20
	public void deleteAllAuthenticationEmail() {
		logger.debug("deleteAllAuthenticationEmail :: Execution Time - {}", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
		authenticationEmailService.deleteAllAuthenticationEmail();
	}
}

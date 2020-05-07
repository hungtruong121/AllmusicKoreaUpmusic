package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.LeagueSeason;


@Repository
public interface LeagueSeasonRepository extends JpaRepository<LeagueSeason, Long> {
	
	@Query(value = "SELECT ls FROM LeagueSeason ls WHERE ls.openDate <= CURRENT_DATE AND ls.closeDate >= CURRENT_DATE")
	LeagueSeason findOneByOpenDateLessThanEqualAndCloseDateGreaterThanEqual();

	LeagueSeason findOneBySubject(String subject);
	
	List<LeagueSeason> findAllByIdNotOrderByOpenDateAsc(Long id);

	List<LeagueSeason> findByOpenDateGreaterThanEqualAndOpenDateLessThanAndOpenDateLessThanEqualOrderByOpenDate(Date firstDayOfThisYear, Date firstDayOfNextYear, Date today);

	List<LeagueSeason> findAllByOpenDateGreaterThanEqualAndOpenDateLessThanOrderByOpenDate(Date firstDayOfThisYear, Date firstDayOfNextYear);

	/**
	 * 시즌 날짜목록 반환
	 * @return date list
	 */
	@Query(value = "SELECT EXTRACT(YEAR FROM ls.openDate) FROM LeagueSeason ls GROUP BY EXTRACT(YEAR FROM ls.openDate)")
	List<Integer> findAllGroupByOpenDate();

}

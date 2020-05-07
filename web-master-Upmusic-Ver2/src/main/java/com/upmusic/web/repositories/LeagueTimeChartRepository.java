package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.LeagueTimeChart;
import com.upmusic.web.domain.MusicTrack;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface LeagueTimeChartRepository extends JpaRepository<LeagueTimeChart, Long> {
	
	@Query(value = "SELECT new map(tc.music_track_id, IF(tc.heart_cnt=@_last_likes,@curRank:=@curRank,@curRank:=@_sequence) AS rank) FROM league_time_chart tc, (SELECT @curRank := 1, @_sequence:=1, @_last_likes:=0) ORDER BY tc.heart_cnt DESC, tc.play_cnt DESC" , nativeQuery = true)
	List<?> findLeagueTimeChartRanks();

	@Modifying
	@Transactional
	@Query(value = "DELETE t1 FROM league_time_chart t1, league_time_chart t2 WHERE t1.id < t2.id AND t1.rank = t2.rank AND t1.music_track_id = t2.music_track_id", nativeQuery = true)
	void deleteOverlap();
	
	LeagueTimeChart findOneByMusicTrack(MusicTrack track);

	List<LeagueTimeChart> findAllByMusicTrack(MusicTrack track);
}

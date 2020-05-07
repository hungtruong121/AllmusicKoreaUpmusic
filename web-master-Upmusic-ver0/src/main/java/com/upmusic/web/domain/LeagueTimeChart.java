package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.upmusic.web.config.UPMusicConstants;


/**
 * 리그 시간별 순위 엔티티
 * 업 리그의 '전체'탭에서 순위변동을 표시하기 위해 사용 
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LeagueTimeChart {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column
	@ApiModelProperty(notes = "재생수")
	private int playCnt;
	
	@Column
	@ApiModelProperty(notes = "좋아요수")
	private int heartCnt;
	
	@Column
	@ApiModelProperty(notes = "순위")
	private int rank;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "music_track_id", unique=true, nullable = false)
	@ApiModelProperty(notes = "곡")
    private MusicTrack musicTrack;
    

    @Formula(UPMusicConstants.FORMULA_TRACK_POPULARITY)
	private float hotPoint;
}
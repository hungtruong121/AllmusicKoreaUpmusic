package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.config.UPMusicConstants;


/**
 * 리그 일별 순위 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LeagueDailyChart {
	
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
    @JoinColumn(name = "genre_id", nullable = false)
	@ApiModelProperty(notes = "장르")
    @Getter
	@Setter
	@JsonIgnore
    private Genre genre;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "league_season_id", nullable = false)
	@ApiModelProperty(notes = "리그 시즌")
    @Getter
	@Setter
	@JsonIgnore
    private LeagueSeason leagueSeason;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    @CreatedDate
    @JsonIgnore
    private Date backupDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "music_track_id", nullable = false)
	@ApiModelProperty(notes = "곡")
    private MusicTrack musicTrack;
    
    @Formula(UPMusicConstants.FORMULA_TRACK_POPULARITY)
	private float hotPoint;
    
	/**
	 * 업 리그에서 순위 변동을 표시하기 위한 임시 항목
	 */
	@Transient
	@Getter
	private int lastRank = 1;
    

}
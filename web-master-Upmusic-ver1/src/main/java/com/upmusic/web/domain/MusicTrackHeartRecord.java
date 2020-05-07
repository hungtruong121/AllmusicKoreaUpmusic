package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.util.Date;

/**
 * 곡 좋아요 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt"}, allowGetters = true)
public class MusicTrackHeartRecord {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "곡 좋아요 ID")
    private Long id;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @ApiModelProperty(notes = "좋아요 생성일시")
    private Date createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_track_id", nullable = false)
	@ApiModelProperty(notes = "곡")
    private MusicTrack musicTrack;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원")
    private Member member;

}
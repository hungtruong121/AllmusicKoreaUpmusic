package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 곡 심사 거절 정보 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MusicTrackRejectedReason {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "곡 심사 거절 ID")
    private Long id;
	
	@Column(length=255)
    @ApiModelProperty(notes = "심사 거절사유")
    @Getter
    @Setter
    private String reason;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JsonIgnore
    private Date createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_track_id", nullable = false)
	@ApiModelProperty(notes = "곡")
    private MusicTrack musicTrack;	
   
	
}
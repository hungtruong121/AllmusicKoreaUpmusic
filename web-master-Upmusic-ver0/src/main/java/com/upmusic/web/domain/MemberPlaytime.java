package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.UPMusicHelper;

import javax.persistence.*;

import java.util.Date;


/**
 * 재생시간 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MemberPlaytime {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "재생시간 ID")
	private Long id;
	
	@Column(nullable = false)
	@ApiModelProperty(notes = "회원의 ID")
	@Getter
	private Long memberId;
	
	@Column
	@ApiModelProperty(notes = "재생시간")
	private Long playtime;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "재생시간 생성일시")
	@JsonIgnore
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "재생시간 업데이트 일시")
	@JsonIgnore
	private Date updatedAt;

	/**
	 * 재생 시간을 분:초 형식으로 반환
	 * @return duration
	 */
	public String formattedDuration() {
		return UPMusicHelper.secondsToTime(playtime);
	}
}
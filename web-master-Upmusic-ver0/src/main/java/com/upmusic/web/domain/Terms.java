package com.upmusic.web.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 정책 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Terms {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "정책 ID")
	private int id;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "제목")
	private String subject;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용")
	private String content;

	@Column
	@ApiModelProperty(notes = "조회수")
	private int hitCnt;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "생성 일시")
	@JsonIgnore
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "수정 일시")
	@JsonIgnore
	private Date updatedAt;
	
	/**
	 * 수정일
	 * @return date
	 */
	public String formattedUpdateDay() {
		return UPMusicHelper.formattedTimeDay(updatedAt);
	}

}
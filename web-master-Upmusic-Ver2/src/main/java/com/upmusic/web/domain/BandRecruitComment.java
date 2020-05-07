package com.upmusic.web.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 밴드모집 댓글 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BandRecruitComment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "밴드 모집 코멘트 ID")
	private Long bandRecruitCommentId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_recruit_id", nullable = false)
	@ApiModelProperty(notes = "밴드 모집 ID")
	private BandRecruit bandRecruitId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원 ID")
    private Member member;

	@Column(nullable = false, length = 255)
	@ApiModelProperty(notes = "내용")
	private String content;

	@Column(length = 20)
	@ApiModelProperty(notes = "아이피")
	private String ip;

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
	
	
	@Transient
	private boolean registrantFlag = false;
	
	public BandRecruitComment(BandRecruitComment bandRecruitComment, Member member){
		this.bandRecruitCommentId = bandRecruitComment.getBandRecruitCommentId();
		this.content = bandRecruitComment.getContent();
		this.ip = bandRecruitComment.getIp();
		this.createdAt = bandRecruitComment.getCreatedAt();
		this.updatedAt = bandRecruitComment.getUpdatedAt();
		this.member = member;
	}
	
	public String formattedTime() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
	

}
package com.upmusic.web.domain;

import java.io.IOException;
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
 * 피드 댓글 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FeedComment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "피드 파일 ID")
	private Long feedCommentId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
	@ApiModelProperty(notes = "피드 ID")
    private Feed feed;

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
	
	@Transient
	private String createdAtFormat;
	
	@Transient
	private String memberImgUrl;
	
	@Transient
	private String memberUrl;
	
	public FeedComment(FeedComment feedComment, Member member) {
		this.feedCommentId = feedComment.getFeedCommentId();
		this.content = feedComment.getContent();
		this.ip = feedComment.getIp();
		this.createdAt = feedComment.getCreatedAt();
		this.updatedAt = feedComment.getUpdatedAt();
		this.member = member;
	}
	
	public String getCreateAtFormat() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
	
	public void setCreateMemberUrl() {
		try {
			memberImgUrl = member.getProfileImageUrl();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
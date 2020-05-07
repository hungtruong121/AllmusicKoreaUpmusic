package com.upmusic.web.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * 공지사항 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "공지사항 ID")
	private Long noticeId;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "분류")
	private String category;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "제목")
	private String subject;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용")
	private String content;

	@Column
	@ApiModelProperty(notes = "조회수")
	private Integer hitCnt;

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

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원 ID")
    private Member member;

	/**
	 * 생성일시 형식으로 반환
	 * @return createdAt
	 */
	public String formattedTime() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}

	/**
	 * 신규 공지사항 여부 체크
	 */
	public boolean newNoticeCheck() {
		String createdAtStr = UPMusicHelper.formattedTimeDay(createdAt);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date());
		boolean result = false;

		if(createdAtStr.equals(today)) {
			result = true;
		}

		return result;
	}

	/**
	 * 상세보기 url 생성
	 */
	public String getDetailURL() {

		return "/upm_news/notice/detail?noticeId=" + noticeId ;
	}

	/**
	 * 상세보기 url 생성 (관리자 페이지)
	 */
	public String getAdminDetailURL() {

		return "/admin/upm_news/notice/detail?noticeId=" + noticeId ;
	}

	/**
	 * 수정하기 url 생성 (관리자 페이지)
	 */
	public String getAdminEditURL() {
		return "/admin/upm_news/notice/editForm?noticeId=" + noticeId ;
	}
	
	public String limitSubject() {
		String result = subject;
		
		if(result.length() > 20) {
			result = result.substring(0, 17) + "...";
		}
		
		return result;
	}

}
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 이벤트 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "이벤트 ID")
	private Long eventId;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "제목")
	private String subject;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용")
	private String content;

	@Column(length=50)
	@ApiModelProperty(notes = "파일명")
    private String filename;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@ApiModelProperty(notes = "게시 일시")
	private Date openAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@ApiModelProperty(notes = "종료 일시")
	private Date closeAt;

	@Column(nullable = false, length = 1)
	@ApiModelProperty(notes = "노출 여부")
	private String showYn;

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
	 * db에 적용되지 않는 필드
	 */
	@Transient
	@JsonIgnore
	private MultipartFile thumbnailImg;

	@Transient
	private String eventStartDate;

	@Transient
	private String eventEndDate;

	/**
	 * 종료 일시를 기반으로 D-X 형태의 데이터 생성
	 */
	public String getDayCount() {

		Date date = new Date();
		long calDate = closeAt.getTime() - date.getTime();
		double calDay = calDate / (24.0*60.0*60.0*1000.0);
		int calDayInt = (int) Math.ceil(calDay);

		return "D-" + calDayInt;
	}

	/**
	 * 이벤트 유효기간 데이터 생성
	 */
	public String getEventExpiryDate() {
		return UPMusicHelper.formattedTimeDay(openAt) + "~" + UPMusicHelper.formattedTimeDay(closeAt);
	}

	/*
	 * 상세보기 url 생성
	 */
	public String getDetailURL() {

		return "/upm_news/event/detail?eventId=" + eventId;
	}

	/*
	 * 상세보기 url 생성
	 */
	public String getAdminDetailURL() {

		return "/admin/upm_news/event/detail?eventId=" + eventId;
	}

	public String getCreatedAtFormat() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}


	/**
	 * 파일 경로에는 AwsHelper에 있는 메소드를 사용하는데 저장 위치에 대하여 확인 후 진행
	 */
	public String getImgUrl() {
		String result = "";
		if (StringUtils.hasText(AzureHelper.getStorageResourceUrl())) {
			result = String.format("%s/event/%d/%s", AzureHelper.getStorageResourceUrl(), eventId, filename);
		}
System.out.println("result : "+result);
		return result;
	}



}
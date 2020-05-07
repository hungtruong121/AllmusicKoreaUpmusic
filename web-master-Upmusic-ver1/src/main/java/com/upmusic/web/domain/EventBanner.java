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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 이벤트 배너 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class EventBanner {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "이벤트 배너 ID")
	private Long eventBannerId;

	@Column(length=50)
	@ApiModelProperty(notes = "파일명")
    private String filename;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "링크")
	private String link;

	@Column(nullable = false, length = 1)
	@ApiModelProperty(notes = "노출 여부")
	private String showYn;

	@Column(nullable = false)
	@ApiModelProperty(notes = "순서")
	private Integer orderNo;

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


	/*
	 * 상세보기 url 생성
	 */
	public String getAdminDetailURL() {

		return "/admin/upm_news/event_banner/detail?eventBannerId=" + eventBannerId;
	}

	/*
	 * 수정하기 url 생성
	 */
	public String getAdminEditURL() {

		return "/admin/upm_news/event_banner/editForm?eventBannerId=" + eventBannerId;
	}

	public String getCreatedAtFormat() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}


	/**
	 * 파일 경로에는 AwsHelper에 있는 메소드를 사용하는데 저장 위치에 대하여 확인 후 진행
	 */
	public String getImgUrl() {
		return String.format("%s/event_banner/%d/%s", AzureHelper.getStorageResourceUrl(), eventBannerId, filename);
	}
}
package com.upmusic.web.domain;

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
import lombok.NoArgsConstructor;


/**
 * 크라우드 소식 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CrowdNews {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "크라우드 소식 ID")
	private Long crowdNewsId;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "제목")
	private String subject;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용")
	private String content;
	
	@Column(length=50)
	@ApiModelProperty(notes = "파일명")
    private String filename;

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
	
	@Transient
	@JsonIgnore
	private MultipartFile thumbnailImg;
	
	
	public String getDetailUrl() {
		return "/crowd_funding/projectNews/detail?crowdNewsId=" + crowdNewsId;
	}
	
	public String getAdminDetailUrl() {
		return "/admin/crowd_funding/projectNews/detail?crowdNewsId=" + crowdNewsId;
	}
	
	public String getAdminDeleteUrl() {
		return "/admin/crowd_funding/projectNews/delete?crowdNewsId=" + crowdNewsId;
	}
	
	public String getFormatterDate() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
	
	public String getThumbnailImgUrl() {
		
		return String.format("%s/crowdNews/%d/%s", AzureHelper.getStorageResourceUrl(), crowdNewsId, filename);
	}

}
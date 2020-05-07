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

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 크라우드 펀딩 배너 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CrowdFundingBanner {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "크라우드 펀딩 배너 ID")
	private Long crowdFundingBannerId;

	@Column(length=50)
	@ApiModelProperty(notes = "파일명")
    private String filename;

	@Column(length=50)
	@ApiModelProperty(notes = "모바일용 이미지 파일명")
    private String filenameMobile;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "제목")
	private String subject;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "링크")
	private String link;

	@Column(nullable = false)
	@ApiModelProperty(notes = "순서")
	private Integer orderNo;

	@Column(nullable = false, length = 1)
	@ApiModelProperty(notes = "노출 여부")
	private String showYn;

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

	@Transient
	@JsonIgnore
	private MultipartFile thumbnailImgMobile;
	
	public String getAdminEditURL() {
		return "/admin/crowd_funding/banner/detail?crowdFundingBannerId=" + crowdFundingBannerId;
	}
	
	public String getAdminDeleteUrl() {
		return "/admin/crowd_funding/banner/delete?crowdFundingBannerId=" + crowdFundingBannerId;
	}
	
	public String getImgUrl() {
		return String.format("%s/crowdFunding_banner/%d/%s", AzureHelper.getStorageResourceUrl(), crowdFundingBannerId, filename);
	}

	public String getMobileImgUrl() {
		return String.format("%s/crowdFunding_banner/%d/%s", AzureHelper.getStorageResourceUrl(), crowdFundingBannerId, filenameMobile);
	}
	

}
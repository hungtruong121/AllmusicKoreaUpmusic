package com.upmusic.web.domain;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import javax.servlet.http.HttpServletRequest;

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
 * 밴드 모집 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BandRecruit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "밴드 모집 ID")
	private Long bandRecruitId;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "제목")
	private String subject;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용")
	private String content;

	@Column(length=50)
	@ApiModelProperty(notes = "파일명")
    private String filename;

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
	
	private Integer bandCommentCnt;
	
	/**
	 * db에 적용되지 않는 필드
	 */
	@Transient
	@JsonIgnore
	private MultipartFile thumbnailImg;
	
	@Transient
	private boolean registrantFlag = false;
	
	@Transient
	private int commentCnt;
	
	public BandRecruit(BandRecruit bandRecruit, Member member) {
		this.bandRecruitId = bandRecruit.getBandRecruitId();
		this.subject = bandRecruit.getSubject();
		this.content = bandRecruit.getContent();
		this.filename = bandRecruit.getFilename();
		this.hitCnt = bandRecruit.getHitCnt();
		this.createdAt = bandRecruit.getCreatedAt();
		this.updatedAt = bandRecruit.getUpdatedAt();
		this.member = member;
	}
	
	public String getCreateAtFormat() {
		return UPMusicHelper.formattedTimeDay(createdAt);
	}
	
	public String getThumbnailImgUrl() {
		String result = "";
		if(filename != null)
			result = String.format("%s/upm_band/%d/%s", AzureHelper.getStorageResourceUrl(), bandRecruitId, filename);
		
		return result;
	}
	
	public String getDetailUrl() {
		return "/upm_news/upm_band/detail?bandRecruitId="+bandRecruitId;
	}
	
	public String getDeleteUrl() {
		return "/upm_news/upm_band/delete?bandRecruitId="+bandRecruitId;
	}
	
	public String getUpdateUrl() {
		return "/upm_news/upm_band/updateForm?bandRecruitId="+bandRecruitId;
	}
	
	public String limitSubject() {
		String result = subject;
		
		if(result.length() > 15) {
			result = result.substring(0, 12) + "...";
		}
		
		return result;
	}
	
	public String limitSubjectMobile() {
		String result = subject;
		
		if(result.length() > 10) {
			result = result.substring(0, 8) + "...";
		}
		
		return result;
	}

	/**
	 * 소셜 공유를 위한 메타 태그 값
	 * @param baseUrl
	 * @return meta map
	 * @throws IOException
	 */
	public Map<String, String> getMetaTag(HttpServletRequest request) throws IOException {
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("og_url", getAbsoluteUrl(UPMusicHelper.baseUrl(request)));
		tags.put("og_type", "website");
		tags.put("og_title", subject);

		if (content != null && !content.equals("")) {
			String description = "";
			description = content.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>","");
			tags.put("og_description", description);
		} else {
			tags.put("og_description", "UPMusic");
		}

		//tags.put("og_image", member.getProfileImageUrl());
		tags.put("og_image", getThumbnailImgUrl());

		return tags;
	}

	/**
	 * 회원 상세페이지의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException
	 */
	public String getAbsoluteUrl(String baseUrl) {
		return String.format("%s/upm_news/upm_band/detail?bandRecruitId=/%d", baseUrl, bandRecruitId);
	}
	
}
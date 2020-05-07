package com.upmusic.web.domain;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 피드 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Feed {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "피드 ID")
	private Long feedId;

	@Column(nullable = false, columnDefinition="TEXT")
	@ApiModelProperty(notes = "내용")
	private String content;

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

	@Column
	@ApiModelProperty(notes = "좋아요수")
	private Integer likeCnt;

	@Column
	@ApiModelProperty(notes = "댓글수")
	private Integer commentCnt;

	@Column
	@ApiModelProperty(notes = "공개 범위")
	@Enumerated(EnumType.STRING)
	private PublicRange publicRange;

	/*
	 * 공개 범위
	 */
	public enum PublicRange {
		ALL, 		// 전체 공개
		LIKE, 		// 좋아요 공개
		PRIVATE;	//나만 공개
	}
	
	@Transient
	@JsonIgnore
	private List<FeedFile> feedFileList;
	
	@Transient
	private Page<FeedComment> feedCommentListDate;
	
	@Transient
	private List<FeedComment> feedCommentListDateMobile;
	
	@Transient
	private String publicRangeFormValue;
	
	@Transient
	private String feedLikeClass = "";
	
	@Transient
	private String feedLikeImgUrl;
	
	@Transient
	private int commentTotalPage;
	
	@Transient
	private int commentPageNo;
	
	@Transient
	private String firstImg;
	
	@Transient
	private String createAtFomatStr;
	
	@Transient
	private String memberUrl;
	
	@Transient
	private String memberProfileUrl;
	
	@Transient
	private int fileSize;
	
	
	public Feed(Feed feed, Member member) {
		this.feedId = feed.getFeedId();
		this.content = feed.getContent();
		this.createdAt = feed.getCreatedAt();
		this.updatedAt = feed.getUpdatedAt();
		this.likeCnt = feed.getLikeCnt();
		this.commentCnt = feed.getCommentCnt();
		this.publicRange = feed.getPublicRange();
		this.member = member;
	}
	
	public String getCreateAtFormat() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
	
	public void setPublicRangeForFormValue() {
		this.publicRange = PublicRange.valueOf(publicRangeFormValue);
	}
	
	public String getFirstImgUrl() {
		String result = "";
		FeedFile firstFile = null;
		
		if(feedFileList.size() > 0) {
			firstFile = feedFileList.get(0);
			
			if(firstFile.getFilename() != null)
				result = String.format("%s/feed/%d/%s", AzureHelper.getStorageResourceUrl(), firstFile.getFeedFileId(), firstFile.getFilename());
		}
		
		return result;
	}
	
	public String getFeedPublicRangeALLUpdate() {
		return "/feed/feedPublicRangeUpdate?feedId=" + feedId + "&publicRangeFormValue=ALL";
	}
	
	public String getFeedPublicRangeLIKEUpdate() {
		return "/feed/feedPublicRangeUpdate?feedId=" + feedId + "&publicRangeFormValue=LIKE";
	}
	
	public String getFeedPublicRangePRIVATEUpdate() {
		return "/feed/feedPublicRangeUpdate?feedId=" + feedId + "&publicRangeFormValue=PRIVATE";
	}
	
	public void setFeedLikeClass(int cnt) {
		if(cnt > 0) {
			feedLikeClass = "feedbtn_on";
		}
		
	}
	
	public void setFeedLikeClassMobile(int cnt) {
		if(cnt > 0) {
			feedLikeClass = "on";
		}else {
			feedLikeClass = "off";
		}
		
	}
	
	public void setFeedLikeImgUrl(int cnt) {
		if(cnt == 0) {
			feedLikeImgUrl = "/img/feedbtn_2.png";
		}else if(cnt > 0) {
			feedLikeImgUrl = "/img/afeedbtn_2.png";
		}
		
	}

	/**
	 * 회원 상세페이지의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException
	 */
	public String getAbsoluteUrl(String baseUrl) {
		return String.format("%s/feed/share/%d", baseUrl, feedId);
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
		tags.put("og_title", member.getNick());

		if (content != null && !content.equals("")) {
			String description = "";
			description = content.replaceAll("<[^>]*>"," ");
			tags.put("og_description", description);
		} else {
			tags.put("og_description", "UPMusic");
		}

		tags.put("og_image", member.getProfileImageUrl());
		//tags.put("og_image", getFirstImgUrl());

		return tags;
	}
	
}
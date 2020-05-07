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
import com.upmusic.web.config.UPMusicConstants;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * FAQ 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FAQ {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "FAQ ID")
	private Long faqId;
	
	@Column(length = 200)
	@ApiModelProperty(notes = "분류")
	private String category;
	
	@Column
	@ApiModelProperty(notes = "분류")
	private Long parentFaqId;
	
	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용")
	private String content;
	
	@Column
	@ApiModelProperty(notes = "분류")
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
	private FAQ answer;
	
	@Transient
	private String searchText;
	
	@Transient
	private String answerContent;
	
	public String getCategoryStr() {
		String result = "";
		
		if(category.equals(UPMusicConstants.FAQ_UPLOAD)) {
			result = "업로드";
		}else if(category.equals(UPMusicConstants.FAQ_COPYRIGHT)) {
			result = "저작권";
		}else if(category.equals(UPMusicConstants.FAQ_UPLEAGUE)) {
			result = "업 리그 차트";
		}else if(category.equals(UPMusicConstants.FAQ_MUSICSTORE)) {
			result = "뮤직스토어";
		}else if(category.equals(UPMusicConstants.FAQ_ARTIST)) {
			result = "아티스트";
		}else if(category.equals(UPMusicConstants.FAQ_PAYMENT)) {
			result = "결제";
		}else if(category.equals(UPMusicConstants.FAQ_CALCULATE)) {
			result = "정산";
		}else if(category.equals(UPMusicConstants.FAQ_REWARD)) {
			result = "리워드";
		}else if(category.equals(UPMusicConstants.FAQ_POINT)) {
			result = "포인트";
		}else if(category.equals(UPMusicConstants.FAQ_OTHER)) {
			result = "기타";
		}
		
		return result;
	}
	
	public String getDetailUrl() {
		return "/admin/upm_news/faq/detail?faqId=" + faqId;
	}
	
	public String limitSubject() {
		String result = content;
		
		if(result.length() > 20) {
			result = result.substring(0, 17) + "...";
		}
		
		return result;
	}
	
}

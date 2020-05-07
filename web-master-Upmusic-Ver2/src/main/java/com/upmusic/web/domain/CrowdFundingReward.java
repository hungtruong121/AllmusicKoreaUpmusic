package com.upmusic.web.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 크라우드 펀딩 리워드 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CrowdFundingReward {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "크라우드 펀딩 리워드 ID")
	private Long crowdFundingRewardId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crowd_funding_id", nullable = false)
	@ApiModelProperty(notes = "크라우드 펀딩 ID")
    private CrowdFunding crowdFunding;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "리워드 제목")
	private String rewardSubject;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "리워드 내용")
	private String rewardContent;

	@ApiModelProperty(notes = "포인트")
	private Integer point;

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
	
	public String getAdminDeleteUrl() {
		return "/admin/crowd_funding/reward/delete?crowdFundingRewardId=" + crowdFundingRewardId + "&crowdFundingId=" + crowdFunding.getCrowdFundingId();
	}
	
	/**
	 * 리워드 내용을 리스트 형태로 변경 ('\n' 문자 기준)
	 */
	public List<String> getContentStrList(){
		List<String> result = new ArrayList<String>();
		
		String[] temp = rewardContent.split(System.getProperty("line.separator"));
		
		for(String s : temp) {
			result.add(s);
		}
		
		return result;
	}

}
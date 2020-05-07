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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 크라우드 펀딩 참여 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CrowdFundingJoin {

	// [CF(크라우드 펀딩 약자)] + [당일날짜 YYYYMMDD] + [숫자 5자리(순차적으로 증가)]
	@Id
	@Column(nullable = false, unique = true, length = 15)
	@ApiModelProperty(notes = "크라우드 펀딩 참여 ID")
	private String crowdFundingJoinId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crowd_funding_id", nullable = false)
	@ApiModelProperty(notes = "크라우드 펀딩 ID")
    private CrowdFunding crowdFunding;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원 ID")
    private Member member;

	@Column(nullable = false, length = 1)
	@ApiModelProperty(notes = "리워드 선택 유무")
	private String rewardSelectedYn;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crowd_funding_reward_id")
	@ApiModelProperty(notes = "크라우드 펀딩 리워드 ID")
    private CrowdFundingReward crowdFundingReward;

	@Column(nullable = false)
	@ApiModelProperty(notes = "사용 포인트")
	private Integer usePoint;

	@Column(nullable = false, length = 50)
	@ApiModelProperty(notes = "이름")
	private String name;

	@Column(nullable = false, length = 50)
	@ApiModelProperty(notes = "이름")
	private String email;

	@Column(nullable = false, length = 13)
	@ApiModelProperty(notes = "전화번호")
	private String phoneNumber;

	@Column(nullable = false, length = 6)
	@ApiModelProperty(notes = "우편번호")
	private String postNumber;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "주소")
	private String addr;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "상세주소")
	private String detailaddr;

	@Column(length = 1000)
	@ApiModelProperty(notes = "비고")
	private String note;

	@Column(length = 1)
	@ApiModelProperty(notes = "배송 상태")
//	@Enumerated(EnumType.STRING)
	private String deliveryState;

	@Column(length = 20)
	@ApiModelProperty(notes = "택배사")
	private String courier;

	@Column(length = 10)
	@ApiModelProperty(notes = "택배사 코드")
	private String courierCode;

	@Column(length = 20)
	@ApiModelProperty(notes = "송장 번호")
	private String invoiceNumber;


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
	
	public String getCreateAtFormat() {
		return UPMusicHelper.formattedTimeDay(createdAt);
	}
	
	public String deliveryStateStr() {
		String result = "";
		
		if(deliveryState.equals("2"))
			result = "배송중";
		else if(deliveryState.equals("3"))
			result = "배송완료";
		
		return result;
	}
	
	/**
	 * 리워드명 생성 (리워드 없이 후원했을시 리워드 종류에 '-' 리턴)
	 */
	public String getRewardSubject() {
		String result = "";
		
		if(crowdFundingReward != null)
			result = crowdFundingReward.getRewardSubject();
		else
			result = "-";
		
		return result;
	}
	
	/**
	 * 내 펀딩내역 성공여부 색상지정 (모바일)
	 */
	public String getFundingResultColor() {
		String result = "";
		
		String fundingResult = crowdFunding.getTargetAttainmentYnFlag();
		
		if("성공".equals(fundingResult)) {
			result = "#647bdc";
		}else if("실패".equals(fundingResult)){
			result = "#ef326b";
		}
		
		return result;
	}
	
}
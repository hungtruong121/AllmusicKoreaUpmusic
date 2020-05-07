package com.upmusic.web.domain;

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

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 크라우드 펀딩 전시 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CrowdFundingDisplay {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "크라우드 펀딩 전시 ID")
	private Long crowdFundingDisplayId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crowd_funding_id", nullable = false)
	@ApiModelProperty(notes = "크라우드 펀딩 ID")
    private CrowdFunding crowdFunding;

	@Column(nullable = false, length = 10)
	@ApiModelProperty(notes = "전시 종류")
	@Enumerated(EnumType.STRING)
	private displayType displayType;

	@Column(nullable = false)
	@ApiModelProperty(notes = "순서")
	private Integer orderNo;

	/*
	 * 공개 범위
	 */
	public enum displayType {
		NEW, 		// 신규
		POP, 		// 인기
	}

}
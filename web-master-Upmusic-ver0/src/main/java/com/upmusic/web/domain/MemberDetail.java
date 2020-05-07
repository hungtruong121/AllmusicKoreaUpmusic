package com.upmusic.web.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import java.util.Date;


/**
 * 회원 상세정보 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"updatedAt"}, allowGetters = true)
public class MemberDetail {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "회원 상세정보 ID")
    private Long id;

	@Column(columnDefinition = "TEXT")
	@ApiModelProperty(notes = "자기소개")
    private String introduction;
	
	@Column
	@ApiModelProperty(notes = "앨범수")
	private int albumCnt;
	
	@Column
	@ApiModelProperty(notes = "동영상수")
	private int videoCnt;
	
	@Column
	@ApiModelProperty(notes = "조회수")
	private int hitCnt;
	
	@Column
	@ApiModelProperty(notes = "저작권협회 회원 여부")
    private boolean isCopyrightMemeber;
	
	@Column
	@ApiModelProperty(notes = "저작권 신탁단체 회원 여부")
    private boolean isCopyrightTrustMemeber;
	
// 새로운 기획서(180425버전) 팔로우 기능 제외됨	
//	@Column
//	@ApiModelProperty(notes = "팔로잉수")
//	private int followingCnt;
//	
//	@Column
//	@ApiModelProperty(notes = "팔로워수")
//	private int followerCnt;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @ApiModelProperty(notes = "회원정보 업데이트 일시")
    private Date updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @ApiModelProperty(notes = "회원")
    private Member member;

}
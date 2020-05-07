package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.upmusic.web.helper.UPMusicHelper;

import java.math.BigDecimal;


/**
 * 포인트 리워드 정책 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PointRewardCondition {
	
	@Id
	@ApiModelProperty(notes = "리워드 ID")
    private int id;
	
	@Column
	@ApiModelProperty(notes = "청취 1단계 초당 지급 포인트")
	@Getter
    private float listenFirstStepPoint;
	
	@Column
	@ApiModelProperty(notes = "청취 1단계 제한 시간")
	@Getter
    private int listenFirstStepLimit;
	
	@Column
	@ApiModelProperty(notes = "청취 2단계 초당 지급 포인트")
	@Getter
    private float listenSecondStepPoint;
	
	@Column
	@ApiModelProperty(notes = "청취 2단계 제한 시간")
	@Getter
    private int listenSecondStepLimit;
	
	@Column
	@ApiModelProperty(notes = "청취 3단계 초당 지급 포인트")
	@Getter
    private float listenThirdStepPoint;
	
	@Column
	@ApiModelProperty(notes = "청취 3단계 제한 시간")
	@Getter
    private int listenThirdStepLimit;
	
	@Column
	@ApiModelProperty(notes = "저작권자 청취시 초당 지급 포인트")
	@Getter
    private float listenArtistSelfPoint;
	
	@Column
	@ApiModelProperty(notes = "저작권자 청취시 지급 제한 시간")
	@Getter
    private int listenArtistSelfLimit;
	
	@Column
	@ApiModelProperty(notes = "청취곡 저작권자 지급 포인트")
	@Getter
    private float listenArtistPoint;
	
	@Column
	@ApiModelProperty(notes = "음원 업로드 지급 포인트")
	@Getter
    private int uploadPoint;
	
	@Column
	@ApiModelProperty(notes = "공유 지급 포인트")
	@Getter
    private int sharePoint;
	
	@Column
	@ApiModelProperty(notes = "공유 지급 제한수")
	@Getter
    private int shareLimit;

	
	public String getUploadPointString() {
		return UPMusicHelper.intToPrice(uploadPoint);
	}
	
	public String getSharePointString() {
		return UPMusicHelper.intToPrice(sharePoint);
	}

	public String getBigDecimal(){
		BigDecimal bdc = new BigDecimal(listenThirdStepPoint);
//		String pointString = bdc.toString();
		String pointString = String.format("%.4f", listenThirdStepPoint);
		return pointString;
	}
}
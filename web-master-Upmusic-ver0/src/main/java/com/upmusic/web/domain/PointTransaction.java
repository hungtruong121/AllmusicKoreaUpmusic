package com.upmusic.web.domain;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.converter.ChargeTypeConverter;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.config.UPMusicConstants.ChargeType;
import com.upmusic.web.domain.converter.PointTransactionTypeAttributeConverter;
import com.upmusic.web.helper.UPMusicHelper;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 포인트 거래 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "포인트 거래 ID")
	private Long id;
	
	@Column(nullable = false)
	@ApiModelProperty(notes = "회원의 ID")
	@Getter
	private Long memberId;
	
	@Column
	@Convert(converter = PointTransactionTypeAttributeConverter.class)
	@ApiModelProperty(notes = "거래 유형")
	@Getter
    private PointTransactionType transactionType;

	@Column
	@ApiModelProperty(notes = "거래 유형 ID : 존재할 경우")
	@Getter
	private Long transactionTypeId;
	
	@Column
	@ApiModelProperty(notes = "일반 포인트")
	private float point;
	
	@Column
	@ApiModelProperty(notes = "펀딩 포인트")
	private float fundingPoint;

	@Column
	@ApiModelProperty(notes = "충전 포인트")
	private float chargePoint;

	@Column
	@Convert(converter = ChargeTypeConverter.class)
	@ApiModelProperty(notes = "충전 유형")
	@Getter
	private ChargeType chargeType;

	@Column
	@ApiModelProperty(notes = "충전 상태")
	@Getter
	private String chargeStatus;

	@Column
	@ApiModelProperty(notes = "충전 트랜잭션 ID")
	private String chargeTransactionId;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "포인트 거래 생성일시")
	@JsonIgnore
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "포인트 거래 업데이트 일시")
	@JsonIgnore
	private Date updatedAt;
	
	@Column
	@ApiModelProperty(notes = "사용자 제거 여부")
	@JsonIgnore
    private boolean removed = false;
	
	@Transient
	@Getter
	@JsonIgnore
	private String subject = null;
	
	public PointTransaction(PointTransaction transaction, String subject) {
		this.id = transaction.id;
    	this.memberId = transaction.memberId;
    	this.transactionType = transaction.transactionType;
    	this.transactionTypeId = transaction.transactionTypeId;
    	this.point = transaction.point;
    	this.fundingPoint = transaction.fundingPoint;
    	this.createdAt = transaction.createdAt;
    	this.updatedAt = transaction.updatedAt;
    	this.removed = transaction.removed;
    	this.subject = subject;
    	this.chargePoint = transaction.chargePoint;
		this.chargeType = transaction.chargeType;
		this.chargeTransactionId = transaction.chargeTransactionId;
		this.chargeStatus = transaction.chargeStatus;
    }
	
	public BigDecimal getPointDecimal() {
		return new BigDecimal(String.valueOf(point));
	}
	
	public BigDecimal getFundingPointDecimal() {
		return new BigDecimal(String.valueOf(fundingPoint));
	}

	public BigDecimal getChargePoint() {
		return new BigDecimal(String.valueOf(chargePoint));
	}

	public String getChargePointString() {
		return UPMusicHelper.intToPrice(new BigDecimal(String.valueOf(chargePoint)).intValue());
	}

	/**
	 * 거래 유형의 명칭을 반환
	 * @return i18n key
	 */
	public String getTransactionTypeName() {
		return "enum.point.transaction_type." + this.transactionType.name();
	}
	public int getTransactionTypeInteger() {
		PointTransactionTypeAttributeConverter converter = new PointTransactionTypeAttributeConverter();
		return converter.convertToDatabaseColumn(transactionType);
	}
	public int getChargeTypeInteger() {
		ChargeTypeConverter converter = new ChargeTypeConverter();
		return converter.convertToDatabaseColumn(chargeType);
	}
	public int getSumPoint() {
		int sum = new BigDecimal(String.valueOf(point)).add(new BigDecimal(String.valueOf(fundingPoint))).intValue();
		return sum;
	}
	public String getSumPointString() {
		int sum = new BigDecimal(String.valueOf(point)).add(new BigDecimal(String.valueOf(fundingPoint))).intValue();
		return UPMusicHelper.intToPrice(sum);
	}
	
	/**
	 * 생성일시 형식으로 반환
	 * @return createdAt
	 */
	public String formattedDay() {
		return UPMusicHelper.formattedTimeDotDay(createdAt);
	}

}
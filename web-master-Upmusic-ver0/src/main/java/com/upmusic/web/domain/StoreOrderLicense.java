package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.config.UPMusicConstants.StoreOrderStatus;
import com.upmusic.web.domain.converter.StoreOrderStatusAttributeConverter;


/**
 * 라이센스 구매 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StoreOrderLicense {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "라이센스 구매 ID")
    private Long id;
	
	// 구매자와 업뮤직 간 원할한 소통을 위한 주문코드
	@Column(nullable = false, length = 17)
	@ApiModelProperty(notes = "라이센스 주문 코드", required = true)
	@Getter
	private String code;
	
	@Column(nullable = false)
	@ApiModelProperty(notes = "음원 ID", required = true)
	private Long musicTrackId;
	
	@Column(nullable = false)
	@ApiModelProperty(notes = "음원 판매자 ID", required = true)
	private Long sellerId;
	
	@Column(nullable = false)
	@ApiModelProperty(notes = "음원 구매자 ID", required = true)
	private Long buyerId;
	
	@Column(nullable = false)
    @ApiModelProperty(notes = "구매가격", required = true)
    @Getter
    private int price;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_license_id", nullable = false)
	@ApiModelProperty(notes = "라이센스")
    @Getter
	@Setter
    private StoreLicense storeLicense;
	
	@Column
	@Convert(converter = StoreOrderStatusAttributeConverter.class)
	@ApiModelProperty(notes = "주문 상태")
	@Getter
    private StoreOrderStatus orderStatus = StoreOrderStatus.PREPARE;
	
	@Column
	@ApiModelProperty(notes = "결제일시")
    @Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
    private Date paidAt;	
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JsonIgnore
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @JsonIgnore
    private Date updatedAt;	

}
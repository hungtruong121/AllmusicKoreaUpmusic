package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.helper.UPMusicHelper;


/**
 * 콜렉션(앨범 모음) 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class Collection {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length=50)
    @ApiModelProperty(notes = "콜렉션명")
    @Getter
    private String subject;
    
	@Column
	@ApiModelProperty(notes = "음원수")
	@Getter
	private int trackCnt;
	
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;
    
    
    /**
	 * 생성일시 형식으로 반환
	 * @return createdAt
	 */
	public String formattedDay() {
		return UPMusicHelper.formattedTimeDay(createdAt);
	}
	
	public String formattedTime() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
}
package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.helper.AzureHelper;


/**
 * 리그 시즌 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class LeagueSeason {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length=50)
    @ApiModelProperty(notes = "시즌명")
    @Getter
	@Setter
    private String subject;
    
    @Column(length=50)
	@ApiModelProperty(notes = "시즌 커버 이미지")
	@Getter
	@Setter
    private String imageFilename;
    
    @Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @ApiModelProperty(notes = "시즌 시작일")
    private Date openDate;
    
    @Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @ApiModelProperty(notes = "시즌 종료일")
    private Date closeDate;
    
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
    
    
    @Transient
	@Getter
	@JsonIgnore
	private MultipartFile imageMultipart;
    
    /**
	 * 시즌 상세페이지의 url을 반환
	 * @return url
	 */
	public String getUrl() {
		return String.format("/admin/music/season/%d", id);
	}
	
    /**
	 * 이미지의 전체 url을 반환
	 * @return url
	 * @throws IOException 
	 */
	public String getImageUrl() {
		return String.format("%s/seasons/%d/%s", AzureHelper.getStorageResourceUrl(), id, imageFilename);
	}
	
    
}
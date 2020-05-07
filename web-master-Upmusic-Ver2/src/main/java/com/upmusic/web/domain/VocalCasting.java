package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import javax.persistence.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 보컬 캐스팅 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class VocalCasting {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "보컬 캐스팅 ID")
    private Long id;
	
	@Column(nullable = false, length=50)
	@ApiModelProperty(notes = "제목")
	@Getter
	@Setter
    private String subject;
	
	@Column(columnDefinition = "TEXT")
	@ApiModelProperty(notes = "상세 내용")
    private String description;
	
	@Column(length=80)
	@ApiModelProperty(notes = "음원 파일")
	@Getter
	@Setter
    private String filename;
	
	@Column
	@ApiModelProperty(notes = "조회수")
	private int hitCnt;
	
	@Column
	@ApiModelProperty(notes = "좋아요수")
	private int heartCnt;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @ApiModelProperty(notes = "생성일시")
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @ApiModelProperty(notes = "업데이트 일시")
    @JsonIgnore
    private Date updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원")
    private Member member;
	
    @Transient
	@Getter
	@JsonIgnore
	private MultipartFile fileMultipart;
    
    @Transient
	@Getter
	@Setter
	private Long commentCnt = 0L;
    
    @Transient
	@Getter
	@Setter
	private boolean newItem = false;
    
    /**
	 * 회원이 좋아요를 했는지 구분하기 위한 임시 항목
	 */
	@Transient
	@Getter
	private boolean liked = false;
	
	
    /**
	 * 음원 파일의 전체 url을 반환
	 * @return url
	 * @throws IOException 
	 */
	public String getFileUrl() {
		return String.format("%s/castings/%d/%s", AzureHelper.getStorageResourceUrl(), id, filename);
	}
	
	/**
	 * 보컬 캐스팅의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException 
	 */
	public String getAbsoluteUrl(String baseUrl) {
		return String.format("%s/music/vocal_casting/%d", baseUrl, id);
	}
	
	/**
	 * 보컬 캐스팅의 url을 반환
	 * @return url
	 */
	public String getUrl() {
		return String.format("/music/vocal_casting/%d", id);
	}
	
	/**
	 * 생성일시 형식으로 반환
	 * @return createdAt
	 */
	public String formattedTime() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
	public String formattedDay() {
		return UPMusicHelper.formattedTimeDay(createdAt);
	}
	
    
    /**
	 * 소셜 공유를 위한 메타 태그 값
	 * @param baseUrl
	 * @return meta map
	 * @throws IOException
	 */
	public Map<String, String> getMetaTag(String baseUrl) throws IOException {
		Map<String, String> tags = new HashMap<String, String>();
		String shareLink = getAbsoluteUrl(baseUrl);
		String title = String.format("%s - %s", subject, member.getNick()); // 제목 - 아티스트명
		tags.put("og_url", shareLink);
		tags.put("og_type", "website");
		tags.put("og_title", title);
		tags.put("og_description", description);
		tags.put("og_image", member.getProfileImageUrl());
		return tags;
	}

}
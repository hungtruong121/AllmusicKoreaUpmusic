package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.helper.UPMusicHelper;


/**
 * 제작의뢰 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class MusicRequest {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
	@Column(columnDefinition = "TEXT")
	@ApiModelProperty(notes = "의뢰 내용")
	private String description;
	
	@Column(length = 10)
    @ApiModelProperty(notes = "제시 금액")
    private String price;
	
	@Column
	@ApiModelProperty(notes = "협의 여부")
	private boolean discussion = false;
	
	@Column
	@ApiModelProperty(notes = "조회수")
	private int hitCnt;
 
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
    private Member member;
    
    @Transient
	@Getter
	@Setter
	private Long commentCnt = 0L;
    

    public String getFormattedPrice() {
    	if (discussion) return "별도협의";
    	Long requestPrice = 0L;
    	if (this.price != null && !this.price.isEmpty()) requestPrice = Long.parseLong(price);
    	return UPMusicHelper.longToPrice(requestPrice) + "원";
    }
    
    public String getUrl() {
    	return "/music/store/request/" + id;
    }
    
    /**
	 * 제작의뢰 상세페이지의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException 
	 */
	public String getAbsoluteUrl(String baseUrl) throws IOException {
		return String.format("%s/music/store/request/%d", baseUrl, id);
	}
	
	/**
	 * 생성일시 형식으로 반환
	 * @return createdAt
	 */
	public String formattedTime() {
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
		tags.put("og_url", getAbsoluteUrl(baseUrl));
		tags.put("og_type", "website");
		tags.put("og_title", "제작의뢰");

		if (description != null && !description.equals("")) {
			tags.put("og_description", description);
		} else {
			tags.put("og_description", "UPMusic");
		}

		tags.put("og_image", member.getProfileImageUrl());
		return tags;
	}
    
}
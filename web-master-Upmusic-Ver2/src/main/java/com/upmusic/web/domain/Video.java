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

import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.VideoType;
import com.upmusic.web.domain.converter.VideoTypeAttributeConverter;
import com.upmusic.web.helper.UPMusicHelper;

/**
 * 영상 정보 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class Video {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	@Convert(converter = VideoTypeAttributeConverter.class)
	@ApiModelProperty(notes = "영상 종류")
	@Getter
    private VideoType videoType;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
	@ApiModelProperty(notes = "장르")
    @Getter
	@Setter
	@JsonIgnore
    private Genre genre;

	@Column(nullable = false, length = 1000)
	@ApiModelProperty(notes = "영상명")
	@Getter
	@Setter
	private String subject;
	
	@Column(nullable = false, length = 10)
	@ApiModelProperty(notes = "영상 서비스 (youtube, vimeo)")
	@Getter
    private String videoService;

	@Column(nullable = false, length = 20)
	@ApiModelProperty(notes = "영상 서비스의 영상 아이디")
	@Getter
	private String videoServiceObjectId;
	
	@Column(nullable = false, length = 120)
	@ApiModelProperty(notes = "썸네일")
	@Getter
	@Setter
	private String thumbnail;

	@Column(columnDefinition = "TEXT")
	@ApiModelProperty(notes = "영상 설명")
	@Getter
	@Setter
	private String description;
	
	@Column
	@ApiModelProperty(notes = "재생시간")
	private int duration;

	@Column
	@ApiModelProperty(notes = "조회수")
	private int hitCnt;

	@Column
	@ApiModelProperty(notes = "좋아요수")
	private int heartCnt;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updatedAt;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "music_theme_category_id", nullable = false)
	// @ApiModelProperty(notes = "테마")
	// private MusicThemeCategory musicThemeCategory;

	@ManyToOne(optional = false)
	@JoinColumn(name = "member_id")
	@Getter
	@Setter
	private Member member;
	    
    @Formula(UPMusicConstants.FORMULA_DEFAULT_POPULARITY)
	private float hotPoint;
    
	@Transient
	@Getter
	@JsonIgnore
	private MultipartFile thumbnailMultipart;
	
	@Transient
	@Getter
	@JsonIgnore
	private MultipartFile videoMultipart;
	
	/**
	 * 회원이 좋아요를 했는지 구분하기 위한 임시 항목
	 */
	@Transient
	@Getter
	private boolean liked = false;
	
	/**
	 * 썸네일의 전체 url을 반환
	 * @return url
	 * @throws IOException 
	 */
	public String getThumbnailUrl() {
		return thumbnail;
	}
	
	/**
	 * 영상 상세페이지의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException 
	 */
	public String getAbsoluteUrl(String baseUrl) throws IOException {
		return String.format("%s/video/%d", baseUrl, id);
	}
	
	/**
	 * 영상 상세페이지의 url을 반환
	 * @return url
	 */
	public String getUrl() {
		return String.format("/video/%d", id);
	}
	
	/**
	 * 관리페이지의 영상 상세페이지의 url을 반환
	 * 관리페이지의 영상 상세페이지
	 * @return url
	 */
	public String getAdminUrl() {
		return String.format("/admin/video/%d", id);
	}
	
	/**
	 * 아티스트 url 반환
	 * @return url
	 */
	public String getArtistUrl() {
		return member.getUrl();
	}
	
	/**
	 * 아티스트 닉네임 반환
	 * @return nick
	 */
	public String getArtistNick() {
		return member.getNick();
	}
	
	/**
	 * 타입명을 반환
	 * @return type
	 */
	public String getTypeName() {
		if (StringUtils.isEmpty(genre)) return null;
		return "enum.videotype."+ videoType.name();
	}
	
	/**
	 * 장르명을 반환
	 * @return genre
	 */
	public String getGenreName() {
		if (StringUtils.isEmpty(genre)) return null;
		return "enum.genre."+ genre.getName();
	}
	
	/**
	 * 재생 시간을 분:초 형식으로 반환
	 * @return duration
	 */
	public String formattedDuration() {
		return UPMusicHelper.secondsToTime(duration);
	}
	
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
		tags.put("og_title", subject);

		if (description != null && !description.equals("")) {
			tags.put("og_description", description);
		} else {
			tags.put("og_description", "UPMusic");
		}

		tags.put("og_image", getThumbnailUrl());
		return tags;
	}
	
}
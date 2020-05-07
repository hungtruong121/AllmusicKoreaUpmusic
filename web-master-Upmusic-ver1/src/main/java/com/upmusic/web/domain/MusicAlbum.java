package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.MusicAlbumType;
import com.upmusic.web.domain.converter.MusicAlbumTypeAttributeConverter;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 앨범 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class MusicAlbum {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "앨범 ID")
    private Long id;
	
	@Column
	@Convert(converter = MusicAlbumTypeAttributeConverter.class)
	@ApiModelProperty(notes = "앨범 종류")
	@Getter
    private MusicAlbumType albumType;
	
	@Column(nullable = false, length=50)
	@ApiModelProperty(notes = "앨범명")
	@Getter
	@Setter
    private String subject;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
	@ApiModelProperty(notes = "장르")
    @Getter
	@Setter
	@JsonIgnore
    private Genre genre;
	
//	@Column(nullable = false, unique = true, length=50)
//	@ApiModelProperty(notes = "앨범 ap:access point - 앨범명을 경로로 활용")
// 	private String ap;
	
	@Column(columnDefinition = "TEXT")
	@ApiModelProperty(notes = "앨범 설명")
    private String description;
	
	@Column(length=80)
	@ApiModelProperty(notes = "앨범 커버 이미지")
	@Getter
	@Setter
    private String imageFilename;
	
	@Column
	@ApiModelProperty(notes = "조회수")
	private int hitCnt;
	
	@Column
	@ApiModelProperty(notes = "좋아요수")
	private int heartCnt;
	
	@Column
	@ApiModelProperty(notes = "심사 통과된 수록곡의 수")
	private int acceptedTrackCnt;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @ApiModelProperty(notes = "앨범 생성일시")
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @ApiModelProperty(notes = "앨범 업데이트 일시")
    @JsonIgnore
    private Date updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원")
    private Member member;
	
	@Column
	@JsonIgnore
    private boolean published = false;
	
	/**
	 * 수록곡
	 */
	@OneToMany(
        mappedBy = "musicAlbum", 
        cascade = CascadeType.REMOVE, 
        orphanRemoval = true
    )
	@OrderBy(value = "sortNo")
    private List<MusicTrack> tracks = new ArrayList<MusicTrack>();
    public void addTrack(MusicTrack track) {
    	tracks.add(track);
    	track.setMusicAlbum(this);
    }
    public void removeTrack(MusicTrack track) {
    	tracks.remove(track);
    	track.setMusicAlbum(null);
    }
    public List<MusicTrack> getAcceptedTracks() {
    	List<MusicTrack> acceptedTracks = new ArrayList<MusicTrack>();
    	for (MusicTrack track : tracks) {
    		if (UPMusicConstants.MusicTrackStatus.ACCEPTED == track.getTrackStatus()) acceptedTracks.add(track);
    	}
    	return acceptedTracks;
    }
    private MusicTrack getTrackById(Long trackId) {
    	MusicTrack selectedTrack = null;
    	for (MusicTrack track : tracks) {
    		if (trackId == track.getId()) {
    			selectedTrack = track;
    			break;
    		}
    	}
    	return selectedTrack;
    }
    
    @Formula(UPMusicConstants.FORMULA_DEFAULT_POPULARITY)
	private float hotPoint;
    
    @Transient
	@Getter
	@JsonIgnore
	private MultipartFile coverImageMultipart;
    
    
    public MusicAlbum(MusicAlbum album, int liked) {
    	this.id = album.id;
    	this.albumType = album.albumType;
    	this.subject = album.subject;
    	this.description = album.description;
    	this.imageFilename = album.imageFilename;
    	this.hitCnt = album.hitCnt;
    	this.heartCnt = album.heartCnt;
    	this.createdAt = album.createdAt;
    	this.updatedAt = album.updatedAt;
    	this.member = album.member;
    	this.tracks = album.tracks;
    	this.liked = liked == 1;
    }
    
    /**
	 * 회원이 좋아요를 했는지 구분하기 위한 임시 항목
	 */
	@Transient
	@Getter
	private boolean liked = false;
    
    
    /**
	 * 커버이미지의 전체 url을 반환
	 * @return url
	 * @throws IOException 
	 */
	public String getCoverImageUrl() {
		return String.format("%s/albums/%d/%s", AzureHelper.getStorageResourceUrl(), id, imageFilename);
	}
	
	/**
	 * 앨범 상세페이지의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException 
	 */
	public String getAbsoluteUrl(String baseUrl) {
		return String.format("%s/music/album/%d", baseUrl, id);
	}
	
	/**
	 * 앨범 상세페이지의 url을 반환
	 * @return url
	 */
	public String getUrl() {
		return String.format("/music/album/%d", id);
	}
	
	/**
	 * 아티스트의 url 반환
	 * @return url
	 */
	public String getArtistUrl() {
		return member.getUrl();
	}
	
	/**
	 * 아티스트의 닉네임 반환
	 * @return nick
	 */
	public String getArtistNick() {
		return member.getNick();
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
	 * 생성일시 형식으로 반환
	 * @return createdAt
	 */
	public String formattedTime() {
		return UPMusicHelper.formattedTimeDay(createdAt);
	}
	public String formattedDay() {
		return UPMusicHelper.formattedTimeDotDay(createdAt);
	}
	
	public boolean hasUnacceptedTrack() {
		return 0 < this.getTracks().size() - this.getAcceptedTrackCnt();
	}
    
    /**
	 * 소셜 공유를 위한 메타 태그 값
	 * @param baseUrl
	 * @return meta map
	 * @throws IOException
	 */
	public Map<String, String> getMetaTag(HttpServletRequest request) throws IOException {
		Map<String, String> tags = new HashMap<String, String>();
		String shareLink = getAbsoluteUrl(UPMusicHelper.baseUrl(request));
		String title = String.format("%s - %s", subject, member.getNick()); // 앨범명 - 아티스트명
		if (!StringUtils.isEmpty(request.getParameter("ti"))) {
			MusicTrack track = getTrackById(Long.valueOf(request.getParameter("ti")));
			if (track != null) {
				shareLink = String.format("%s?ti=%d", shareLink, track.getId());
				title = String.format("%s (%s) - %s", track.getSubject(), subject, member.getNick()); // 곡명 (앨범명) - 아티스트명
			}
		}
		tags.put("og_url", shareLink);
		tags.put("og_type", "website");
		tags.put("og_title", title);
		if (description != null && !description.equals("")) {
			tags.put("og_description", description);
		} else {
			tags.put("og_description", "UPMusic");
		}
		tags.put("og_image", getCoverImageUrl());
		tags.put("og_image:width", "400");
		tags.put("og_image:height", "400");
		return tags;
	}

}
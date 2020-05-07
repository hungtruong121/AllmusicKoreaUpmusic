package com.upmusic.web.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.config.UPMusicConstants.MusicTrackType;
import com.upmusic.web.domain.converter.MusicTrackStatusAttributeConverter;
import com.upmusic.web.domain.converter.MusicTrackTypeAttributeConverter;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * 곡 정보 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)

public class MusicTrack {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
    @Column(nullable = false, length=50)
    @ApiModelProperty(notes = "곡명")
    @Getter
    private String subject;
    
    @Column(nullable = false, length=80)
    @ApiModelProperty(notes = "파일명")
    @Getter
    private String filename;
    
    @Column(length=80)
    @ApiModelProperty(notes = "트랙 모음 파일명")
    @JsonIgnore
    @Getter
    private String extraSource;
    
    @Column
	@Convert(converter = MusicTrackTypeAttributeConverter.class)
    @JsonIgnore
	@ApiModelProperty(notes = "음원 유형")
	@Getter
    private MusicTrackType trackType;
    
//    @Column
//    @ApiModelProperty(notes = "가사")
//    @Getter
//    private String lyric;
    
    @Column
    @ApiModelProperty(notes = "독점 라이센스 판매가격", required = true)
    @JsonIgnore
    @Getter
    private int price;
    
    @Column
    @ApiModelProperty(notes = "임대 라이센스 판매가격", required = true)
    @JsonIgnore
    @Getter
    private int rentalFee;
    
    @Column
	@ApiModelProperty(notes = "재생시간")
    @Getter
	private int duration;
    
    @Column
	@ApiModelProperty(notes = "앨범 내의 순서")
    @JsonIgnore
	private int sortNo;
    
	@Column
	@ApiModelProperty(notes = "재생수")
	@JsonIgnore
	private int playCnt;
	
	@Column
	@ApiModelProperty(notes = "좋아요수")
	private int heartCnt;
	
	@Formula(UPMusicConstants.FORMULA_TRACK_POPULARITY)
	@JsonIgnore
	private float hotPoint;
	
	@Column
	@ApiModelProperty(notes = "최신곡 등록 여부")
	@JsonIgnore
	@Getter
	private boolean inRecent;
	
	@Column
	@ApiModelProperty(notes = "리그 등록 여부")
	@JsonIgnore
	@Getter
	private boolean inLeague;
	
	@Column
	@ApiModelProperty(notes = "스토어 등록 여부")
	@JsonIgnore
	@Getter
	private boolean inStore;
	
	@Column
	@ApiModelProperty(notes = "타이틀곡 여부")
	private boolean titleTrack = false;
	
	@Column
	@Convert(converter = MusicTrackStatusAttributeConverter.class)
	@ApiModelProperty(notes = "심사 상태")
	@JsonIgnore
	@Getter
	@Setter
    private MusicTrackStatus trackStatus = MusicTrackStatus.BEFORE_EXAM;
	
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
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "music_album_id", nullable = false)
	@ApiModelProperty(notes = "앨범")
    @Getter
	@Setter
	@JsonIgnore
    private MusicAlbum musicAlbum;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id", nullable = false)
	@ApiModelProperty(notes = "장르")
    @Getter
	@Setter
	@JsonIgnore
    private Genre genre;
     
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_id", nullable = false)
	@ApiModelProperty(notes = "테마")
    @Getter
	@Setter
	@JsonIgnore
    private Theme theme;
    
    @Column
	@JsonIgnore
    private boolean published = false;

	@Column
	@ApiModelProperty(notes = "저작권 협회 회원 여부")
	@JsonIgnore
	@Getter
	private boolean inCopyrighter;

	@Column(length=20)
	@ApiModelProperty(notes = "저작권 협회 등록 이름 / 별명")
	@Getter
	private String copyrighterName;

	@Column(length=20)
	@ApiModelProperty(notes = "저작권 협회 신탁자 코드")
	@Getter
	private String copyrighterCode;
    
    /**
	 * 협력자
	 */
	@OneToMany(
        mappedBy = "musicTrack", 
        cascade = CascadeType.REMOVE, 
        orphanRemoval = true
    )
	@JsonIgnore
    private List<MusicTrackCooperator> cooperators = new ArrayList<MusicTrackCooperator>();
    public void addTrack(MusicTrackCooperator cooperator) {
    	cooperators.add(cooperator);
    	cooperator.setMusicTrack(this);
    }
    public void removeTrack(MusicTrackCooperator cooperator) {
    	cooperators.remove(cooperator);
    	cooperator.setMusicTrack(null);
    }
    
    public MusicTrack(MusicTrack track, int liked) {
    	this.cooperators = track.cooperators;
    	this.createdAt = track.createdAt;
    	this.duration = track.duration;
    	this.extraSource = track.extraSource;
    	this.filename = track.filename;
    	this.genre = track.genre;
    	this.theme = track.theme;
    	this.heartCnt = track.heartCnt;
    	this.id = track.id;
    	this.inRecent = track.inRecent;
    	this.inLeague = track.inLeague;
    	this.inStore = track.inStore;
    	this.musicAlbum = track.musicAlbum;
    	this.playCnt = track.playCnt;
    	this.rentalFee = track.rentalFee;
    	this.price = track.price;
    	this.sortNo = track.sortNo;
    	this.subject = track.subject;
    	this.trackType = track.trackType;
    	this.updatedAt = track.updatedAt;
    	this.trackStatus = track.trackStatus;
		this.inCopyrighter = track.inCopyrighter;
		this.copyrighterName = track.copyrighterName;
		this.copyrighterCode = track.copyrighterCode;
		this.liked = liked == 1;
    }
    
    /**
	 * 회원이 해당 곡에 좋아요를 했는지 구분하기 위한 임시 항목
	 */
	@Transient
	@Getter
	private boolean liked = false;
	
	
	public MusicTrack(MusicTrack track, int liked, String rejectedReason) {
    	this.cooperators = track.cooperators;
    	this.createdAt = track.createdAt;
    	this.duration = track.duration;
    	this.extraSource = track.extraSource;
    	this.filename = track.filename;
    	this.genre = track.genre;
    	this.theme = track.theme;
    	this.heartCnt = track.heartCnt;
    	this.id = track.id;
    	this.inRecent = track.inRecent;
    	this.inLeague = track.inLeague;
    	this.inStore = track.inStore;
    	this.musicAlbum = track.musicAlbum;
    	this.playCnt = track.playCnt;
    	this.rentalFee = track.rentalFee;
    	this.price = track.price;
    	this.sortNo = track.sortNo;
    	this.subject = track.subject;
    	this.trackType = track.trackType;
    	this.updatedAt = track.updatedAt;
    	this.trackStatus = track.trackStatus;
		this.inCopyrighter = track.inCopyrighter;
		this.copyrighterName = track.copyrighterName;
		this.copyrighterCode = track.copyrighterCode;
    	this.liked = liked == 1;
    	this.rejectedReason = rejectedReason;
    }
	
	/**
	 * 곡이 심사반려인 경우 그 이유를 담기 위한 임시 항목
	 */
	@Transient
	@Getter
	@JsonIgnore
	private String rejectedReason = null;
    
    @Transient
	@Getter
	@JsonIgnore
	private MultipartFile fileMultipart;
    
    @Transient
	@Getter
	@JsonIgnore
	private MultipartFile extraFileMultipart;
    
    public void setFilename(String filename) {
    	if (filename != null && 0 < filename.length()) {
    		if (filename.contains(".")) {
    			this.filename = filename.substring(0, filename.lastIndexOf('.')) + ".mp3";
    		} else {
    			this.filename = filename + ".mp3";
    		}
    	} else {
    		this.filename = null;
    	}
	}
    
    /**
	 * 트랙의 전체 url을 반환
	 * @return url
	 * @throws IOException 
	 */
	public String getFilenameUrl() {
		if (musicAlbum == null)	return null;
		return String.format("%s/%d/%s", AzureHelper.getStorageAlbumUrl(), musicAlbum.getId(), filename);
	}
	
	/**
	 * 앨범 상세페이지의 url을 반환
	 * 트랙 상세페이지=앨범 상세페이지
	 * @return url
	 */
	public String getUrl() {
		if (musicAlbum == null)	return null;
		return String.format("/music/album/%d", musicAlbum.getId());
	}
	
	/**
	 * 관리페이지의 트랙 상세페이지의 url을 반환
	 * 관리페이지의 트랙 상세페이지
	 * @return url
	 */
	@JsonIgnore
	public String getAdminUrl() {
		return String.format("/admin/music/track/%d", id);
	}
	
	/**
	 * 장르명을 반환
	 * @return genre
	 */
	@JsonIgnore
	public String getGenreName() {
		if (genre == null) return null;
		return "enum.genre."+ genre.getName();
	}
	
	/**
	 * 음원유형을 반환
	 * @return type
	 */
	@JsonIgnore
	public String getTrackTypeName() {
		if (StringUtils.isEmpty(trackType)) return null;
		return "enum.tracktype."+ trackType.name();
	}
	
	/**
	 * 테마명을 반환
	 * @return String
	 */
	@JsonIgnore
	public String getThemeName() {
		if (theme == null) return null;
		return "enum.theme."+ theme.getName();
	}
	
	/**
	 * 앨범의 id를 반환
	 * @return album id
	 */
	public Long getMusicAlbumId() {
		return musicAlbum.getId();
	}
	
	/**
	 * 앨범명을 반환
	 * @return album subject
	 */
	public String getMusicAlbumSubject() {
		return musicAlbum.getSubject();
	}
	
	/**
	 * 커버이미지를 반환
	 * @return cover image
	 */
	public String getCoverImageUrl() {
		return musicAlbum.getCoverImageUrl();
	}
	
	/**
	 * 아티스트의 id를 반환
	 * @return artist id
	 */
	public Long getArtistId() {
		return musicAlbum.getMember().getId();
	}
	
	/**
	 * 아티스트 닉네임을 반환
	 * @return nick
	 */
	public String getArtistNick() {
		return musicAlbum.getMember().getNick();
	}
	
	/**
	 * 아티스트 url을 반환
	 * @return url
	 */
	public String getArtistUrl() {
		return musicAlbum.getMember().getUrl();
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
	public String formattedTime() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
	
	/**
	 * 음원의 상태가 심사통과가 아닌 경우만 수정 및 삭제 허가
	 * @return boolean
	 */
	@JsonIgnore
	public boolean isEditable() {
		return trackStatus != MusicTrackStatus.ACCEPTED;
	}
}
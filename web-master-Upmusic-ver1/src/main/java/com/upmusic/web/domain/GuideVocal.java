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
import org.thymeleaf.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.domain.converter.MusicTrackStatusAttributeConverter;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import javax.persistence.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * 가이드 보컬 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class GuideVocal {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "가이드 보컬 ID")
    private Long id;
	
	@Column
	@Getter
	@Setter
	@ApiModelProperty(notes = "나이")
	private int age;
	
	@Column(nullable = false, length=50)
	@Getter
	@Setter
	@ApiModelProperty(notes = "가능 지역")
    private String area;
	
	@Column(length=80)
	@Getter
	@Setter
	@ApiModelProperty(notes = "음원 파일")
    private String filename;
	
	@Column(length=120)
	@Getter
	@Setter
	@ApiModelProperty(notes = "음웎 링크")
    private String filelink;
	
	@Column(length=120)
	@Getter
	@Setter
	@ApiModelProperty(notes = "가능 장르")
    private String genre;
	
	@ManyToMany
	@JoinTable(name = "guide_vocal_guide_vocal_scope", joinColumns = @JoinColumn(name = "guide_vocal__id"), inverseJoinColumns = @JoinColumn(name = "guide_vocal_scope_id"))
	@Getter
	@Setter
	@JsonIgnore
	@ApiModelProperty(notes = "가이드 범위")
	private Set<GuideVocalScope> vocalGuideScopes = new HashSet<GuideVocalScope>();
	public void addGuideVocalScope(GuideVocalScope scope) {
		vocalGuideScopes.add(scope);
    }
    public void removeGuideVocalScope(GuideVocalScope scope) {
    	vocalGuideScopes.remove(scope);
    }
    public void resetGuideVocalScopes() {
    	vocalGuideScopes = new HashSet<GuideVocalScope>();
    }
    
    @Column
	@Convert(converter = MusicTrackStatusAttributeConverter.class)
	@ApiModelProperty(notes = "심사 상태")
	@JsonIgnore
	@Getter
	@Setter
    private MusicTrackStatus guideStatus = MusicTrackStatus.BEFORE_EXAM;
	
	@Column
    @Getter
    @Setter
    @ApiModelProperty(notes = "비용", required = true)
    private int cost;
	
	@Column
    @ApiModelProperty(notes = "가능 시작시간")
    private int openHour;
    
	@Column
    @ApiModelProperty(notes = "가능 종료시간")
    private int closeHour;
	
	@Column(columnDefinition = "TEXT")
	@ApiModelProperty(notes = "상세 내용")
    private String description;
	
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
	@JsonIgnore
	private boolean guideScopeGuide;
    
    @Transient
	@Getter
	@JsonIgnore
	private boolean guideScopeChorus;
    
    @Transient
	@Getter
	@JsonIgnore
	private boolean guideScopeRap;
    
    /**
	 * 가이드 보컬의 음원 url을 반환
	 * @return url
	 * @throws IOException 
	 */
	public String getFileUrl() {
		if (!StringUtils.isEmpty(filelink)) return filelink;
		return String.format("%s/guide/%d/%s", AzureHelper.getStorageResourceUrl(), id, filename);
	}
	
	/**
	 * 가이드 보컬의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException 
	 */
	public String getAbsoluteUrl(String baseUrl) {
		return String.format("%s/music/guide_vocal/%d", baseUrl, id);
	}
	
	/**
	 * 가이드 보컬의 url을 반환
	 * @return url
	 */
	public String getUrl() {
		return String.format("/music/guide_vocal/%d", id);
	}
	
	public String getCostString() {
		return UPMusicHelper.intToPrice(cost);
	}
	
	public String getGuideVocalScopesString() {
		String scopes = "";
		for (GuideVocalScope scope : vocalGuideScopes) {
			if (!scopes.isEmpty()) scopes += " / ";
			scopes += scope.getNameString();
		}
		return scopes;
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

}
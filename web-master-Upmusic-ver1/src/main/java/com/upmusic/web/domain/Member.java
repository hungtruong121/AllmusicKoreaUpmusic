package com.upmusic.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.Gender;
import com.upmusic.web.domain.converter.GenderAttributeConverter;
import com.upmusic.web.helper.UPMusicHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 회원 엔티티
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "회원 ID")
	private Long id;
	
// 새로운 기획서(180515버전) 이메일로 아이디 대체
//	@Column(nullable = false, unique = true, length = 50)
//	@ApiModelProperty(notes = "로그인에 사용되는 아이디")
//	@Getter
//	private String uid;

	@Column(nullable = false, unique = true, length = 50)
	@ApiModelProperty(notes = "회원의 이메일주소")
	@Getter
	private String email;
	
	@Column(nullable = false, length = 60)
	@ApiModelProperty(notes = "로그인에 사용되는 비밀번호")
	@Getter
	@Setter
	@JsonIgnore
	private String password;
	
	@Column(unique = true, length = 100)
	@ApiModelProperty(notes = "앱 회원인증을 위한 토큰")
	@Getter
	private String token;
	
	@Column(nullable = false, length = 50)
	@ApiModelProperty(notes = "회원의 닉네임")
	private String nick;
	
	@Column(nullable = false, length = 8)
	@ApiModelProperty(notes = "회원의 생년월일")
	@Getter
	@JsonIgnore
	private String birthday;
	
	@Column
	@Convert(converter = GenderAttributeConverter.class)
	@ApiModelProperty(notes = "회원의 성별")
	@Getter
    private Gender gender;

	@Column(nullable = false, length = 13)
	@ApiModelProperty(notes = "인증에 사용되는 휴대폰 번호")
	@Getter
	@JsonIgnore
	private String phoneNumber;
	
	@Column(length=80)
	@ApiModelProperty(notes = "회원의 프로필 이미지")
	@Getter
	private String profileImage;
	
	@Column
	@ApiModelProperty(notes = "좋아요수")
	private int heartCnt;
	
	@Column
	@ApiModelProperty(notes = "업로드 음원수")
	private int trackCnt;
	
	@Column
	@ApiModelProperty(notes = "업로드 영상수")
	private int videoCnt;
	
	@Column
	@ApiModelProperty(notes = "upm 포인트")
	private BigDecimal upmPoint;
	
	@Column
	@ApiModelProperty(notes = "펀딩 포인트")
	private BigDecimal fundingPoint;

	@Column
	@ApiModelProperty(notes = "충전 포인트")
	private BigDecimal chargePoint;

	@Column
	@ApiModelProperty(notes = "HYC")
	private int hyc;
	
	@Column
	@ApiModelProperty(notes = "이메일 수신 여부")
	@JsonIgnore
    private boolean receiveEmail = true;
	
	@Column
	@ApiModelProperty(notes = "저작권 신탁 단체의 회원 여부")
	@JsonIgnore
    private boolean copyrightMembership = false;
	
	@Column(length = 8)
	@ApiModelProperty(notes = "소셜 로그인 정보")
	@Getter
	@JsonIgnore
	private String social;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "회원 가입일시")
	@JsonIgnore
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "회원정보 업데이트 일시")
	@JsonIgnore
	private Date updatedAt;

	// 새로운 기획서(180425버전) 팔로우 기능 제외됨
	// @ManyToMany(fetch = FetchType.EAGER)
	// @JoinTable(name = "member_follow",
	// joinColumns = @JoinColumn(name = "member_id"),
	// inverseJoinColumns = @JoinColumn(name = "follower_id"))
	// private List<Member> followers;
	//
	// @ManyToMany(mappedBy = "followers")
	// private List<Member> following;

	@ManyToMany
	@JoinTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@ApiModelProperty(notes = "회원 권한")
	@Getter
	@Setter
	@JsonIgnore
	private Set<Role> roles = new HashSet<Role>();
	public void addRole(Role role) {
		roles.add(role);
    }
    public void removeRole(Role role) {
    	roles.remove(role);
    }
	
	@ManyToMany
	@JoinTable(name = "member_liker", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "liker_id"))
    @ApiModelProperty(notes = "좋아요 한 사람들")
	@Getter
	@Setter
	@JsonIgnore
	private Set<Member> likers = new HashSet<Member>();
    public void addLiker(Member liker) {
    	likers.add(liker);
    }
    public void removeLiker(Member liker) {
    	likers.remove(liker);
    }
    
//	@OneToMany(cascade = CascadeType.ALL, mappedBy = "member", fetch = FetchType.LAZY)
//	@ApiModelProperty(notes = "회원이 등록한 앨범")
//	@Getter
//	@Setter
//	private Collection<MusicAlbum> albums;
    
    @Override
    public int hashCode() { 
        return id.hashCode(); 
    }
    
    @Override
    public boolean equals(Object obj) {
        return 0 == id.compareTo(((Member) obj).id);
    }
	
	public Member(Member member, int liked) {
		this.id = member.id;
		this.birthday = member.birthday;
    	this.createdAt = member.createdAt;
    	this.heartCnt = member.heartCnt;
    	this.trackCnt = member.trackCnt;
    	this.videoCnt = member.videoCnt;
    	this.upmPoint = member.upmPoint;
    	this.fundingPoint = member.fundingPoint;
		this.chargePoint = member.chargePoint;
    	this.hyc = member.hyc;
    	this.updatedAt = member.updatedAt;
    	this.email = member.email;
    	this.gender = member.gender;
    	// this.likers = member.likers;
    	this.nick = member.nick;
    	this.password = member.password;
    	this.token = member.token;
    	this.phoneNumber = member.phoneNumber;
    	this.profileImage = member.profileImage;
    	this.receiveEmail = member.receiveEmail;
    	this.roles = member.roles;
    	
    	this.liked = liked == 1;
    }
	
	public Member(Member member, Long playtime) {
		this.id = member.id;
		this.birthday = member.birthday;
    	this.createdAt = member.createdAt;
    	this.heartCnt = member.heartCnt;
    	this.trackCnt = member.trackCnt;
    	this.videoCnt = member.videoCnt;
    	this.upmPoint = member.upmPoint;
    	this.fundingPoint = member.fundingPoint;
		this.chargePoint = member.chargePoint;
    	this.hyc = member.hyc;
    	this.updatedAt = member.updatedAt;
    	this.email = member.email;
    	this.gender = member.gender;
    	// this.likers = member.likers;
    	this.nick = member.nick;
    	this.password = member.password;
    	this.token = member.token;
    	this.phoneNumber = member.phoneNumber;
    	this.profileImage = member.profileImage;
    	this.receiveEmail = member.receiveEmail;
    	this.roles = member.roles;
    	
    	if (playtime != null) this.playtime = playtime;
    }
	
	// 피드 메뉴 좋아요 리스트 용
	public Member(Map<String, Object> map) {
		this.id = Long.valueOf(String.valueOf(map.get("id")));
    	this.nick = String.valueOf(map.get("nick"));
    	this.profileImage = String.valueOf(map.get("profile_image"));
	}
	
	/**
	 * 회원이 해당 아티스트에 좋아요를 했는지 구분하기 위한 임시 항목
	 */
	@Transient
	@Getter
	private boolean liked = false;
	
	/**
	 * 관리자페이지에서 회원의 음원 재생시간을 확인하기 위한 임시 항목
	 */
	@Transient
	@Getter
	private Long playtime = 0L;
	
	/*
	 * 폼 유효성 검사에 필요한 임시 항목들 
	 */
	
	@Transient
	@Getter
	@JsonIgnore
	private String passwordConfirm;
	
	@Transient
	@Getter
	@JsonIgnore
	private String authenticationCode;
	
	@Transient
	@Getter
	@JsonIgnore
	private String genderNo;
	
	@Transient
	@Getter
	@JsonIgnore
	private boolean agreeTermsUse;
	
	@Transient
	@Getter
	@JsonIgnore
	private boolean agreeTermsPrivacy;
	
	@Transient
	@Getter
	@JsonIgnore
	private MultipartFile profileImageMultipart;
	
	@Transient
	private String createAtStr;
	
	@Transient
	private String copyrightMembershipStr;
	
	@Transient
	private String imgUrl;
	
	@Transient
	private String memberUrl;

	@Transient
	@JsonIgnore
	@Value("${upm.azure.storage.resource.url}")
	private String resourceStorageUrl;
	
	public String getProfileImageUrl() throws IOException {
		if (!StringUtils.isEmpty(profileImage) && !profileImage.equals("null")) {
			// 기본 이미지 반환
			if (profileImage.chars().allMatch( Character::isDigit )) {
				return String.format("/img/basic_img%s.png", profileImage);
			}
			return String.format("%s/profiles/%d/%s", UPMusicConstants.loadPropertyValue("upm.azure.storage.resource.url"), this.id, this.profileImage);
		}
		return "/img/basic_img1.png";
	}
	
	public String getAbsoluteProfileImageUrl(String baseUrl) throws IOException {
		if (!StringUtils.isEmpty(profileImage) && !profileImage.equals("null")) {
			// 기본 이미지 반환
			if (profileImage.chars().allMatch( Character::isDigit )) {
				return String.format("%s/img/basic_img%s.png", baseUrl, profileImage);
			}
			return String.format("%s/profiles/%d/%s", UPMusicConstants.loadPropertyValue("upm.azure.storage.resource.url"), this.id, this.profileImage);
		}
		return String.format("%s/img/basic_img1.png", baseUrl);
	}
	
	/**
	 * 성별을 반환
	 * @return gender
	 */
	public String getGenderName() {
		if (gender == null) return null;
		return "enum.gender."+ gender.name();
	}
	
	/**
	 * 회원 상세페이지의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException 
	 */
	public String getAbsoluteUrl(String baseUrl) {
		return String.format("%s/music/artist/%d", baseUrl, id);
	}
	
	/**
	 * 회원 상세페이지의 url을 반환
	 * @return url
	 */
	public String getUrl() { return String.format("/music/artist/%d", id); }
//	public String getUrl() { return String.format("/auth/artist/%d", id); }

	/**
     * 회원 등급을 반환
     * @return String
     */
    public String getGrade() {
        String grade = "common.grade.user";
        if (0 < trackCnt) grade = "common.grade.artist";
        return grade;
    }
	
	/**
	 * 가입일시 형식으로 반환
	 * @return createdAt
	 */
	public String formattedDay() {
		return UPMusicHelper.formattedTimeDotDay(createdAt);
	}
	
	/**
	 * 소셜 공유를 위한 메타 태그 값
	 * @return meta map
	 * @throws IOException
	 */
	public Map<String, String> getMetaTag(HttpServletRequest request) throws IOException {
		Map<String, String> tags = new HashMap<String, String>();
		String shareLink = getAbsoluteUrl(UPMusicHelper.baseUrl(request));
		tags.put("og_url", shareLink);
		tags.put("og_type", "website");
		tags.put("og_title", nick);
		tags.put("og_description", "UPMusic");
		tags.put("og_image", getAbsoluteProfileImageUrl(UPMusicHelper.baseUrl(request)));
		return tags;
	}
	
	public boolean hasLiker(Long likerId) {
		boolean found = false;
		for (Iterator<Member> it = likers.iterator(); it.hasNext(); ) {
			Member m = it.next();
	        if (0 == m.id.compareTo(likerId)) found = true;
	    }
		return found;
	}
	
	/**
	 * 생성일시 yyyy-MM-dd hh:mm 형태로 반환
	 */
	public String fomattedDayMin() {
		return UPMusicHelper.formattedTimeHourMin(createdAt);
	}
	
	/**
	 * @deprecated getUpmPointDecimal()로 대체
	 * @return
	 */
	public float getUpmPoint() {
		return upmPoint.add(chargePoint).floatValue();
	}

	/**
	 * @deprecated getFundingPointDecimal()로 대체
	 * @return
	 */
	public float getFundingPoint() {
		return fundingPoint.floatValue();
	}
	
	/**
	 * @deprecated getUsablePointDecimal()로 대체
	 * @return
	 */
	public float getUsablePoint() {
		return getUpmPointDecimal().subtract(getFundingPointDecimal()).floatValue();
	}

	public BigDecimal getUpmPointDecimal() {
		return upmPoint;
	}
	
	public BigDecimal getFundingPointDecimal() {
		return fundingPoint;
	}

	public BigDecimal getChargePointDecimal() {
		return chargePoint;
	}

	public BigDecimal getUsablePointDecimal() {
		return getUpmPointDecimal().subtract(getFundingPointDecimal());
	}
	
	public String getUpmPointString() {
		return UPMusicHelper.decimalToPrice(upmPoint.add(chargePoint));
	}
//	public String getUpmPointString() {
//		return UPMusicHelper.decimalToPrice(upmPoint);
//	}
	
	public String getFundingPointString() {
		return UPMusicHelper.decimalToPrice(fundingPoint);
	}
	
	public String getUsablePointString() {
		return UPMusicHelper.decimalToPrice(this.getUsablePointDecimal());
	}

	public String getChargePointString() { return UPMusicHelper.decimalToPrice(chargePoint);
	}

	/**
     * 관리페이지의 회원 상세페이지의 url을 반환
     * 관리페이지의 회원 상세페이지
     * @return url
     */
    @JsonIgnore
    public String getAdminUrl() {
    	return String.format("/admin/member/%d", id);
    }
    
    /**
     * 아티스트 등급을 반환
     * @return String
     */
    @SuppressWarnings("unlikely-arg-type")
	public String getArtistGrade() {
    	final int[] normalGrades = {1,2,8,9}; // data-mysql.sql 파일의 role 데이터 확인
        String grade = "common.grade.normal";
        int highestRoleId = 9;
    	for (Role role : roles) {
    		if (!Arrays.asList(normalGrades).contains(role.getId()) && highestRoleId > role.getId()) {
    			if (role.getName().equals("ROLE_FAMILY")) {
    				grade = "common.grade.family";
    			} else if (role.getName().equals("ROLE_GUIDE")) {
    				grade = "common.grade.guide";
    			}
    		}
    	}
        return grade;
    }

    /**
     * 회원 가입형태를 반환
     * @return String
     */
    public String getRegistrationType() {
        if (this.social == null || this.social.isEmpty()) return "이메일";
        if (this.social.equals(UPMusicConstants.SOCIAL_GOOGLE)) return "구글";
        if (this.social.equals(UPMusicConstants.SOCIAL_FACEBOOK)) return "페이스북";
        if (this.social.equals(UPMusicConstants.SOCIAL_KAKAO)) return "카카오";
        if (this.social.equals(UPMusicConstants.SOCIAL_NAVER)) return "네이버";
        return "불분명 - DB확인";
    }

}
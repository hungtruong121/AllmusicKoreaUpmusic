package com.upmusic.web.domain;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 크라우드 펀딩 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CrowdFunding {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "크라우드 펀딩 ID")
	private Long crowdFundingId;

	@Column(nullable = false, length = 200)
	@ApiModelProperty(notes = "제목")
	private String subject;

	@Column(nullable = false, length = 500)
	@ApiModelProperty(notes = "요약")
	private String summary;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용1")
	private String content1;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용2")
	private String content2;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	@ApiModelProperty(notes = "내용3")
	private String content3;

	/*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_member_id", nullable = false)
	@ApiModelProperty(notes = "아티스트 회원 ID")
    private Member artistMember;*/

	@Column(nullable = false)
	@ApiModelProperty(notes = "아티스트 회원 ID")
    private Long artistMemberId;

	@Column(nullable = false, length=50)
	@ApiModelProperty(notes = "아티스트 닉네임")
	private String artistNick;

	@Column(nullable = false, length=50)
	@ApiModelProperty(notes = "곡명")
    private String songname;

	@Column(length=50)
	@ApiModelProperty(notes = "파일명")
    private String filename;

	@Column(nullable = false)
	@ApiModelProperty(notes = "게시 일시")
	@JsonIgnore
	private Date openAt;

	@Column(nullable = false)
	@ApiModelProperty(notes = "종료 일시")
	@JsonIgnore
	private Date closeAt;

	@Column(nullable = false)
	@ApiModelProperty(notes = "목표 금액")
	private Integer targetPrice;

	@Column(nullable = false)
	@ApiModelProperty(notes = "달성 금액")
	private Integer attainmentPrice;

	@Column(nullable = false)
	@ApiModelProperty(notes = "참여 수")
	private Integer joinCnt;

	@Column(nullable = false)
	@ApiModelProperty(notes = "목표 달성 여부")
	private Integer targetAttainmentYn;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "생성 일시")
	@JsonIgnore
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "수정 일시")
	@JsonIgnore
	private Date updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원 ID")
    private Member member;


	@Transient
	private String fundStartDate;

	@Transient
	private String fundEndDate;

	@Transient
	private Member artistMember;
	
	@Transient
	private String noData;

	@Transient
	@JsonIgnore
	private MultipartFile thumbnailImg;


	@Column(nullable = false, length = 20)
	@ApiModelProperty(notes = "은행명")
	private String bankname;

	@Column(length = 20)
	@ApiModelProperty(notes = "은행 코드")
	private String bankCode;

	@Column(length = 20)
	@ApiModelProperty(notes = "예금주")
	private String accounter;

	@Column(nullable = false, length = 30)
	@ApiModelProperty(notes = "계좌 번호")
	private String accountNumber;

	@Column(nullable = false, length = 1)
	@ApiModelProperty(notes = "정산 상태")	// 정산 상태	0: 정산대기	1: 정산완료	2: 환불완료
	private String calculateState;

	@Column(length = 1)
	@ApiModelProperty(notes = "정산 수수료")
	private String calculateFee;

	@Column(length = 1)
	@ApiModelProperty(notes = "정산 일시")
	private Date calculateAt;


	public CrowdFunding(CrowdFunding crowdFunding, Member member) {
		this.crowdFundingId = crowdFunding.getCrowdFundingId();
		this.subject = crowdFunding.getSubject();
		this.summary = crowdFunding.getSummary();
		this.content1 = crowdFunding.getContent1();
		this.content2 = crowdFunding.getContent2();
		this.content3 = crowdFunding.getContent3();
	    this.artistMemberId = crowdFunding.getArtistMemberId();
		this.artistNick = crowdFunding.getArtistNick();
	    this.songname = crowdFunding.getSongname();
	    this.filename = crowdFunding.getFilename();
		this.openAt = crowdFunding.getOpenAt();
		this.closeAt = crowdFunding.getCloseAt();
		this.targetPrice = crowdFunding.getTargetPrice();
		this.attainmentPrice = crowdFunding.getAttainmentPrice();
		this.joinCnt = crowdFunding.getJoinCnt();
		this.targetAttainmentYn = crowdFunding.getTargetAttainmentYn();
		this.createdAt = crowdFunding.getCreatedAt();
		this.updatedAt = crowdFunding.getUpdatedAt();
		this.bankname = crowdFunding.getBankname();
		this.bankCode = crowdFunding.getBankCode();
		this.accounter = crowdFunding.getAccounter();
		this.accountNumber = crowdFunding.getAccountNumber();
		this.calculateState = crowdFunding.getCalculateState();
		this.calculateFee = crowdFunding.getCalculateFee();
		this.calculateAt = crowdFunding.getCalculateAt();
		this.member = member;
	}


	/**
	 * 목표달성률 계산
	 */
	public String getPercent() {
		double attainmentPriceInt = (double) attainmentPrice;
		double targetPriceInt = (double) targetPrice;
		return Math.round(attainmentPriceInt / targetPriceInt * 100.0) + "%";
	}

	/**
	 * 목표달성률 계산 (로딩바)
	 */
	public String getPercentForLoadingBar() {
		double attainmentPriceInt = (double) attainmentPrice;
		double targetPriceInt = (double) targetPrice;
		int result = (int) Math.round(attainmentPriceInt / targetPriceInt * 100.0);

		if(result >= 100) {
			result = 100;
		}

		return result + "%";
	}

	/**
	 * 펀딩 남은 기간 계산
	 */
	public int getDateTerm() {
		Date today = new Date();
		long dateTerm = closeAt.getTime() - today.getTime();
		int dayTerm = Math.abs((int) (dateTerm / (24*60*60*1000)));

		return dayTerm;
	}


	/**
	 * 이미지 경로 생성
	 */
	public String getThumbnailImgUrl() {

		return String.format("%s/crowdFunding/%d/%s", AzureHelper.getStorageResourceUrl(), crowdFundingId, filename);
	}

	/**
	 * 프로젝트 유효기간 데이터 생성
	 */
	public String getexpiryDate() {
		return UPMusicHelper.formattedTimeHourMin(openAt) + "~" + UPMusicHelper.formattedTimeHourMin(closeAt);
	}

	/**
	 * 프로젝트 상세보기 url
	 */
	public String getDetailUrl() {
		return "/crowd_funding/participation/detail?crowdFundingId=" + crowdFundingId;
	}

	/**
	 * 프로젝트 상세보기 url (관리자)
	 */
	public String getAdminDetailUrl() {
		return "/admin/crowd_funding/project_detail?crowdFundingId=" + crowdFundingId;
	}

	/**
	 * 프로젝트 삭제 url (관리자)
	 */
	public String getAdminDeleteUrl() {
		return "/admin/crowd_funding/project_delete?crowdFundingId=" + crowdFundingId;
	}

	/**
	 * 프로젝트 리워드 url
	 */
	public String getRewardUrl() {
		return "/crowd_funding/participation/reward?crowdFundingId=" + crowdFundingId;
	}

	/**
	 * 프로젝트 후원 추가 적용
	 */
	public void setFundingJoin(int point) {
		// 달성 금액 증가
		this.attainmentPrice += point;

		// 참여 인원 증가
		//this.joinCnt += 1;

		// 목표금액 달성여부 체크
		if(targetPrice <= attainmentPrice)
			targetAttainmentYn = 1;
	}

	/**
	 * 프로젝트 목표달성 여부
	 */
	public String getTargetAttainmentYnFlag() {
		String flag = "";

		Date today = new Date();

		int compare = closeAt.compareTo(today);

		if(compare > 0)
			flag = "진행중";
		else {
			if(targetPrice <= attainmentPrice)
				flag = "성공";
			else
				flag = "실패";
		}

		return flag;
	}

	/**
	 * 프로젝트 진행상태 (관리자)
	 */
	public String getProjectProgress() {
		String result = "";

		Date today = new Date();

		int compare = closeAt.compareTo(today);
		int compare2 = openAt.compareTo(today);

		if(compare2 > 0) {
			result = "예약중";
		}else {
			if(compare > 0)
				result = "진행중";
			else {
				if(targetPrice <= attainmentPrice)
					result = "성공";
				else
					result = "실패";
			}
		}

		return result;
	}

	/**
	 * 프로젝트 달성여부 (class 명)
	 * t01 : 진행중
	 * t02 : 성공
	 * t03 : 실패
	 */
	public String getTargetAttainmentYnFlagForClass() {
		String flag = "";

		Date today = new Date();

		int compare = closeAt.compareTo(today);

		if(compare > 0)
			flag = "t01";
		else {
			if(targetPrice <= attainmentPrice)
				flag = "t02";
			else
				flag = "t03";
		}

		return flag;
	}

	/**
	 * 수수료
	 */
	public int getCommission() {
		double result = 0;

		result = attainmentPrice / 10.0;

		return (int) result;
	}

	/**
	 * 정산 상태
	 */
	public String getCalculateStateStr() {
		String result = "";

		if(calculateState.equals("1"))
			result = "정산완료";
		else if(calculateState.equals("2"))
			result = "환불완료";
		else if(calculateState.equals("0")) {
			String flag = getTargetAttainmentYnFlag();

			if(flag.equals("성공")) {
				result = "정산하기";
			}else if(flag.equals("실패")) {
				result = "환불하기";
			}
		}

		return result;
	}

	/**
	 * 종료일자
	 */
	public String getCloseAtFormat() {
		return UPMusicHelper.formattedTimeHourMin(closeAt);
	}

	/**
	 * 등록일자
	 */
	public String getOpenAtFormat() {
		return UPMusicHelper.formattedTimeHourMin(openAt);
	}

	/**
	 * 정산하기 url
	 */
	public String getBalanceUrl() {
		return "/admin/crowd_funding/balance/balanceForm?crowdFundingId=" + crowdFundingId;
	}

	/**
	 * 환불하기 url
	 */
	public String getReturnPayUrl() {
		return "/admin/crowd_funding/balance/returnPayForm?crowdFundingId=" + crowdFundingId;
	}

	/**
	 * 소셜 공유를 위한 메타 태그 값
	 * @param baseUrl
	 * @return meta map
	 * @throws IOException
	 */
	public Map<String, String> getMetaTag(HttpServletRequest request) throws IOException {
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("og_url", getAbsoluteUrl(UPMusicHelper.baseUrl(request)));
		tags.put("og_type", "website");
		tags.put("og_title", subject);

		if (content1 != null && !content1.equals("")) {
			String content = "";
			content = content1.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>","");
			tags.put("og_description", content);
		} else {
			tags.put("og_description", "UPMusic");
		}

		//tags.put("og_image", member.getProfileImageUrl());
		tags.put("og_image", getThumbnailImgUrl());

		return tags;
	}

	/**
	 * 회원 상세페이지의 전체 url을 반환 - 공유 링크
	 * @return url
	 * @throws IOException
	 */
	public String getAbsoluteUrl(String baseUrl) {
		return String.format("%s/crowd_funding/participation/detail?crowdFundingId=/%d", baseUrl, crowdFundingId);
	}
}
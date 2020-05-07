package com.upmusic.web.controllers.admin;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.util.DateTime;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.message.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.domain.CrowdFunding;
import com.upmusic.web.domain.CrowdFundingBanner;
import com.upmusic.web.domain.CrowdFundingJoin;
import com.upmusic.web.domain.CrowdFundingReward;
import com.upmusic.web.domain.CrowdNews;
import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.CrowdFundingBannerService;
import com.upmusic.web.services.CrowdFundingJoinService;
import com.upmusic.web.services.CrowdFundingRewardService;
import com.upmusic.web.services.CrowdFundingService;
import com.upmusic.web.services.CrowdNewsService;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.PointTransactionService;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/crowd_funding")
public class AdminCrowdFundingController extends AdminAbstractController{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private CrowdFundingService crowdFundingService;
	
	@Autowired
	private CrowdNewsService crowdNewsService;
	
	@Autowired
	private CrowdFundingBannerService crowdFundingBannerService;
	
	@Autowired
	private CrowdFundingRewardService crowdFundingRewardService;
	
	@Autowired
	private CrowdFundingJoinService crowdFundingJoinService;
	
	@Autowired
	private AzureStorageService azureStorageService;
	
	@Autowired
	private PointTransactionService pointTransactionService;
	
	
	/**
	 * 프로젝트 관리
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/project")
	public String adminCrowdFundingProject(Model model, HttpServletRequest request) {
		
		int pageIndex = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
		PageRequest pageable = new PageRequest(pageIndex, 10, new Sort(Direction.DESC, "createdAt"));
		Page<CrowdFunding> projectList = crowdFundingService.findAllByCreateAt(pageable);
		Date openAt = new Date();
		Date closeAt = new Date();

		//검색처리를 위한 항목들
		String column = request.getParameter("searchColumn"); //검색할 column (email || nick || phoneNumber)
		String value = request.getParameter("searchValue"); //검색어
		String openAtString = request.getParameter("openAt");
		String closeAtString = request.getParameter("closeAt");
		if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
		if((openAtString == null && closeAtString == null) || (openAtString.equals("") && closeAtString.equals(""))){
			if (value == null || value.equals("")) {
			} else {
				switch (column) {
					case "subject" : projectList = crowdFundingService.findAllBySubject(pageable, value); break;
					case "email" : projectList = crowdFundingService.findAllByArtistMemberId(pageable, value); break;
					case "artist_nick" : projectList = crowdFundingService.findAllByArtistNick(pageable, value); break;
				}
			}
		} else {
			try {
				openAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(openAtString + " 00:00:00.0");
				closeAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(closeAtString + " 23:59:59.9");
			} catch (Exception e){}
			if (value == null || value.equals("")) {
				projectList = crowdFundingService.findAllByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, pageable);
			} else {
				switch (column) {
					case "subject" : projectList = crowdFundingService.findAllBySubjectByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, pageable, value); break;
					case "email" : projectList = crowdFundingService.findAllByArtistMemberIdByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, pageable, value); break;
					case "artist_nick" : projectList = crowdFundingService.findAllByArtistNickByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, pageable, value); break;
				}
			}
		}

		model.addAttribute("projectList", projectList);
		return "fragments/admin/page/crowd_funding/project";
	}
	
	/**
	 * 프로젝트 등록 화면 이동
	 */
	@GetMapping("/project_insertForm")
	public String adminCrowdFundingInsertForm() {
		
		return "fragments/admin/page/crowd_funding/project_insert";
	}
	
	/**
	 * 프로젝트 등록
	 */
	@PostMapping("/project_insert")
	public String adminCrowdFundingInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData,
			@ModelAttribute("paramData2") Member paramData2) {
		
		Member member = super.getCurrentUser(principal);
		
		paramData.setMember(member);
		//paramData.setArtistMember(paramData2);
		paramData.setArtistNick(paramData2.getNick());
		paramData.setArtistMemberId(paramData2.getId());
		paramData.setAttainmentPrice(0);
		paramData.setJoinCnt(0);
		paramData.setSongname("");
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			paramData.setOpenAt(sdf.parse(paramData.getFundStartDate() + " 00:00:00"));
			paramData.setCloseAt(sdf.parse(paramData.getFundEndDate() + " 23:59:59"));
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		paramData.setJoinCnt(0);
		paramData.setTargetAttainmentYn(0);
		paramData.setCalculateState("0");
		
		paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
		
		CrowdFunding result = crowdFundingService.save(paramData);
		
		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "crowdFunding/" + result.getCrowdFundingId() + "/");
		}
		
		return "redirect:/admin/crowd_funding/project";
	}

    /**
     * 프로젝트 에디터이미지 등록
     */
    @RequestMapping(value = "/project_image_insert", method=RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<APIResponse> adminCrowdFundingImageInsert(@RequestParam("file") MultipartFile file) {

		if (file != null && file.getOriginalFilename() != null) {
			azureStorageService.uploadResource(file, "crowdFundingImage/");
		}

		APIResponse response = new APIResponse(true, "true", AzureHelper.getStorageResourceUrl()+"/crowdFundingImage/"+UPMusicHelper.makeReadableUrl(file.getOriginalFilename()));

        return ResponseEntity.ok(response);
    }

	/**
	 * 프로젝트 상세보기
	 */
	@GetMapping("/project_detail")
	public String adminCrowdFundingDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {

		CrowdFunding result = crowdFundingService.findByCrowdFundingId(paramData.getCrowdFundingId());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		result.setFundStartDate(sdf.format(result.getOpenAt()));
		result.setFundEndDate(sdf.format(result.getCloseAt()));
		
		model.addAttribute("result", result);
		return "fragments/admin/page/crowd_funding/project_detail";
	}
	
	
	/**
	 * 프로젝트 삭제
	 */
	@GetMapping("/project_delete")
	public String adminCrowdFundingDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
		
		crowdFundingService.deleteByCrowdFundingId(paramData.getCrowdFundingId());
		
		return "redirect:/admin/crowd_funding/project";
	}
	
	/**
	 * 프로젝트 수정
	 */
	@PostMapping("/project_update")
	public String adminCrowdFundingUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData,
			@ModelAttribute("paramData2") Member paramData2) {
		
		Member member = super.getCurrentUser(principal);
		
		paramData.setMember(member);
		paramData.setArtistNick(paramData2.getNick());
		paramData.setArtistMemberId(paramData2.getId());
		paramData.setUpdatedAt(new Date());
		paramData.setSongname("");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			paramData.setOpenAt(sdf.parse(paramData.getFundStartDate() + " 00:00:00"));
			paramData.setCloseAt(sdf.parse(paramData.getFundEndDate() + " 23:59:59"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "crowdFunding/" + paramData.getCrowdFundingId() + "/");
		}
		
		crowdFundingService.save(paramData);
		
		return "redirect:/admin/crowd_funding/project";
	}
	
	/**
	 * 프로젝트 소식 관리
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/projectNews")
	public String adminProjectNews(Model model, HttpServletRequest request) {
		
		int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 10, new Sort(Direction.DESC, "createdAt"));
    	
    	Page<CrowdNews> crowdNewsList = crowdNewsService.findAll(pageable);
		
    	model.addAttribute("crowdNewsList", crowdNewsList);
		return "fragments/admin/page/crowd_funding/projectNews";
	}
	
	/**
	 * 프로젝트 소식 등록창 이동
	 */
	@GetMapping("/projectNews_insertForm")
	public String adminProjectNewsInsertForm() {
		
		return "fragments/admin/page/crowd_funding/projectNews_insert";
	}
	
	/**
	 * 프로젝트 소식 등록
	 */
	@PostMapping("/projectNews_insert")
	public String adminProjectNewsInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdNews paramData) {
		Member member = super.getCurrentUser(principal);
		
		paramData.setMember(member);
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
		CrowdNews result = crowdNewsService.save(paramData);
		
		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "crowdNews/" + result.getCrowdNewsId() + "/");
		}
		
		return "redirect:/admin/crowd_funding/projectNews";
	}
	
	/**
	 * 프로젝트 소식 상세보기
	 */
	@GetMapping("/projectNews/detail")
	public String adminProjectNewsDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdNews paramData) {
		
		CrowdNews result = crowdNewsService.findByCrowdNewsId(paramData.getCrowdNewsId());
		
		model.addAttribute("result", result);
		return "fragments/admin/page/crowd_funding/projectNews_detail";
	}
	
	/**
	 * 프로젝트 소식 삭제
	 */
	@GetMapping("/projectNews/delete")
	public String adminProjectNewsDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdNews paramData) {
		
		crowdNewsService.deleteByCrowdNewsId(paramData.getCrowdNewsId());
		
		return "redirect:/admin/crowd_funding/projectNews";
	}
	
	/**
	 * 프로젝트 소식 수정
	 */
	@PostMapping("/projectNews/update")
	public String adminProjectNewsUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdNews paramData) {
		Member member = super.getCurrentUser(principal);
		
		paramData.setMember(member);
		paramData.setUpdatedAt(new Date());
		
		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "crowdNews/" + paramData.getCrowdNewsId() + "/");
		}
		
		crowdNewsService.save(paramData);
		
		return "redirect:/admin/crowd_funding/projectNews";
	}
	
	/**
	 * 배너 관리 
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/banner")
	public String adminBanner(Principal principal, Model model, HttpServletRequest request) {
		
		int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 10, new Sort(Direction.DESC, "createdAt"));
		Page<CrowdFundingBanner> bannerList = crowdFundingBannerService.findAll(pageable);
		
		model.addAttribute("bannerList", bannerList);
		return "fragments/admin/page/crowd_funding/crowdFunding_banner";
	}
	
	/**
	 * 배너 등록하기 폼 이동
	 */
	@GetMapping("/banner/insertForm")
	public String adminBannerInsertForm(Model model) {
		model.addAttribute("bannerForm", new CrowdFundingBanner());
		return "fragments/admin/page/crowd_funding/crowdFunding_banner_insert";
	}
	
	/**
	 * 배너 등록 하기
	 */
//	@PostMapping("/banner/insert")
	@RequestMapping(value = "/banner/insert", method= RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> adminBannerInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("bannerForm") CrowdFundingBanner bannerForm) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		Member member = super.getCurrentUser(principal);
		bannerForm.setMember(member);
		bannerForm.setCreatedAt(new Date());
		bannerForm.setUpdatedAt(new Date());
		bannerForm.setSubject("");
		bannerForm.setFilename(UPMusicHelper.makeReadableUrl(bannerForm.getThumbnailImg().getOriginalFilename()));
		bannerForm.setFilenameMobile(UPMusicHelper.makeReadableUrl(bannerForm.getThumbnailImgMobile().getOriginalFilename()));
		CrowdFundingBanner result = crowdFundingBannerService.save(bannerForm);
		
		if(bannerForm.getThumbnailImg() != null && !StringUtils.isEmpty(bannerForm.getThumbnailImg().getOriginalFilename())) {
			azureStorageService.uploadResource(bannerForm.getThumbnailImg(), "crowdFunding_banner/" + result.getCrowdFundingBannerId() + "/");
			azureStorageService.uploadResource(bannerForm.getThumbnailImgMobile(), "crowdFunding_banner/" + result.getCrowdFundingBannerId() + "/");
		}
		response = new APIResponse(true, "true", bannerForm.getSubject());
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 배너 상세보기
	 */
	@GetMapping("/banner/detail")
	public String adminBannerDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("bannerForm") CrowdFundingBanner bannerForm) {
		
		CrowdFundingBanner result = crowdFundingBannerService.findByCrowdFundingBannerId(bannerForm.getCrowdFundingBannerId());
		
		model.addAttribute("result", result);
		return "fragments/admin/page/crowd_funding/crowdFunding_banner_detail";
	}
	
	/**
	 * 배너 삭제
	 */
	@GetMapping("/banner/delete")
	public String adminBannerDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFundingBanner paramData) {
		crowdFundingBannerService.deleteByCrowdFundingBannerId(paramData.getCrowdFundingBannerId());
		
		return "redirect:/admin/crowd_funding/banner";
	}
	
	/**
	 * 배너 수정
	 */
	@RequestMapping(value = "/banner/update", method= RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> adminBannerUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("bannerForm") CrowdFundingBanner bannerForm) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		Member member = super.getCurrentUser(principal);
		bannerForm.setMember(member);
		bannerForm.setUpdatedAt(new Date());
		bannerForm.setSubject("");
		
		if(bannerForm.getThumbnailImg() != null && !StringUtils.isEmpty(bannerForm.getThumbnailImg().getOriginalFilename())) {
			bannerForm.setFilename(UPMusicHelper.makeReadableUrl(bannerForm.getThumbnailImg().getOriginalFilename()));
			bannerForm.setFilenameMobile(UPMusicHelper.makeReadableUrl(bannerForm.getThumbnailImgMobile().getOriginalFilename()));
			azureStorageService.uploadResource(bannerForm.getThumbnailImg(), "crowdFunding_banner/" + bannerForm.getCrowdFundingBannerId() + "/");
			azureStorageService.uploadResource(bannerForm.getThumbnailImgMobile(), "crowdFunding_banner/" + bannerForm.getCrowdFundingBannerId() + "/");
		}
		
		crowdFundingBannerService.save(bannerForm);
		
//		return "redirect:/admin/crowd_funding/banner";
		response = new APIResponse(true, "true", bannerForm.getSubject());
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 리워드 관리
	 */
	@GetMapping("/reward")
	public String adminReward(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
		List<CrowdFunding> fundingList = crowdFundingService.findAllByStartProject();
		
		List<CrowdFundingReward> rewardList = crowdFundingRewardService.findByCrowdFundingId(paramData.getCrowdFundingId());
		
		model.addAttribute("fundingList", fundingList);
		model.addAttribute("selectFlag", paramData.getCrowdFundingId());
		model.addAttribute("rewardList", rewardList);
		
		return "fragments/admin/page/crowd_funding/crowdFunding_reward";
	}
	
	/**
	 * 리워드 등록
	 */
	@PostMapping("/reward/insert")
	public String adminRewardInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFundingReward paramData, 
			@RequestParam("crowdFundingId") Long crowdFundingId) {
		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		
		CrowdFunding paramData2 = crowdFundingService.findByCrowdFundingId(crowdFundingId);
		paramData.setCrowdFunding(paramData2);
		
		crowdFundingRewardService.save(paramData);
		
		
		return "redirect:/admin/crowd_funding/reward?crowdFundingId=" + crowdFundingId;
	}
	
	/**
	 * 리워드 삭제
	 */
	@GetMapping("/reward/delete")
	public String adminRewardDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFundingReward paramData,
			@RequestParam("crowdFundingId") Long crowdFundingId) {
		crowdFundingRewardService.deleteByCrowdFundingRewardId(paramData.getCrowdFundingRewardId());
		
		return "redirect:/admin/crowd_funding/reward?crowdFundingId=" + crowdFundingId;
	}
	
	/**
	 * 리워드 수정
	 */
	@PostMapping("/reward/update")
	public String adminRewardUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFundingReward paramData,
			@RequestParam("crowdFundingId") Long crowdFundingId) {
		
		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);
		paramData.setUpdatedAt(new Date());
		
		CrowdFunding paramData2 = crowdFundingService.findByCrowdFundingId(crowdFundingId);
		paramData.setCrowdFunding(paramData2);
		
		crowdFundingRewardService.save(paramData);
		
		return "redirect:/admin/crowd_funding/reward?crowdFundingId=" + crowdFundingId;
	}
	
	/**
	 * 프로젝트 정산
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/balance")
	public String adminBalance(Principal principal, Model model, HttpServletRequest request) {
		
		int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
		PageRequest pageable = new PageRequest(pageIndex, 10, new Sort(Direction.DESC, "createdAt"));
		Page<CrowdFunding> fundingList = crowdFundingService.findAllByCloseAt(pageable);
		
		setPageNo(fundingList);
		
		model.addAttribute("fundingList", fundingList);
		model.addAttribute("allCnt", crowdFundingService.countAllFunding());
		model.addAttribute("successCnt", crowdFundingService.countAllSuccessFunding());
		model.addAttribute("failCnt", crowdFundingService.countAllFailFunding());
		return "fragments/admin/page/crowd_funding/project_balance";
	}
	
	/**
	 * 정산하기 화면 이동
	 */
	@GetMapping("/balance/balanceForm")
	public String adminBalanceForm(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
		
		CrowdFunding result = crowdFundingService.findByCrowdFundingId(paramData.getCrowdFundingId());
		
		model.addAttribute("result", result);
		return "fragments/admin/page/crowd_funding/project_balance_balance";
	}
	
	/**
	 * 환불하기 화면 이동
	 */
	@GetMapping("/balance/returnPayForm")
	public String adminBalanceReturnPayForm(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
		
		CrowdFunding result = crowdFundingService.findByCrowdFundingId(paramData.getCrowdFundingId());
		
		model.addAttribute("result", result);
		
		return "fragments/admin/page/crowd_funding/project_balance_returnPay.html";
	}
	
	/**
	 * 정산하기
	 */
	@GetMapping("/balance/balance")
	public String adminBalanceBalance(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
		
		CrowdFunding result = crowdFundingService.findByCrowdFundingId(paramData.getCrowdFundingId());
		
		result.setCalculateAt(new Date());
		result.setCalculateState("1");
		//result.setCalculateFee(String.valueOf(result.getCommission()));
		
		crowdFundingService.save(result);
		
		return "redirect:/admin/crowd_funding/balance/balanceForm?crowdFundingId=" + paramData.getCrowdFundingId();
	}
	
	/**
	 * 환불하기
	 */
	@GetMapping("/balance/returnPay")
	public String adminBalanceReturnPay(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
		
		CrowdFunding result = crowdFundingService.findByCrowdFundingId(paramData.getCrowdFundingId());
		
		result.setCalculateAt(new Date());
		result.setCalculateState("2");
		
		// 포인트 재적립
		List<CrowdFundingJoin> crowdFundingJoinList = crowdFundingJoinService.findByCrowdFundingId(paramData.getCrowdFundingId());
		
		for(CrowdFundingJoin join : crowdFundingJoinList) {
			pointTransactionService.accumulatePoints(join.getMember().getId(), PointTransactionType.FUNDING, join.getCrowdFunding().getCrowdFundingId(), join.getUsePoint());
		}
		
		crowdFundingService.save(result);
		
		return "redirect:/admin/crowd_funding/balance/returnPayForm?crowdFundingId=" + paramData.getCrowdFundingId();
	}
	
	
	public void setPageNo(Page<CrowdFunding> list) {
		
		int number = list.getNumber();
		int totalData = Integer.parseInt(String.valueOf(list.getTotalElements()));
		int nowPageDataCnt = list.getNumberOfElements();
		List<String> indexList = new ArrayList<String>();
		List<String> indexList2 = new ArrayList<String>();
		
		while (totalData > 0) {
			indexList.add(String.valueOf(totalData));
			totalData--;
		}
		
		number = (number * 10);
		
		int cnt = 1;
		
		if(indexList.size() > 0) {
			while (cnt <= nowPageDataCnt) {
				indexList2.add(indexList.get(number));
				number++;
				cnt++;
			}
			
			for(int i=0; i<list.getContent().size(); i++) {
				CrowdFunding cf = list.getContent().get(i);
				cf.setNoData(indexList2.get(i));
			}
		}
		
	}
	
}

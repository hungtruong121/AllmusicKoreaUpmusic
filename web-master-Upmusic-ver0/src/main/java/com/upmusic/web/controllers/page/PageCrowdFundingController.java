package com.upmusic.web.controllers.page;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.domain.CrowdFunding;
import com.upmusic.web.domain.CrowdFundingBanner;
import com.upmusic.web.domain.CrowdFundingJoin;
import com.upmusic.web.domain.CrowdFundingReward;
import com.upmusic.web.domain.CrowdNews;
import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.PointTransactionResponse;
import com.upmusic.web.services.CrowdFundingBannerService;
import com.upmusic.web.services.CrowdFundingJoinService;
import com.upmusic.web.services.CrowdFundingRewardService;
import com.upmusic.web.services.CrowdFundingService;
import com.upmusic.web.services.CrowdNewsService;
import com.upmusic.web.services.PointTransactionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Controller
@RequestMapping("/crowd_funding")
@Api(value="crowd_funding", description="크라우드 펀딩 페이지를 담당하는 컨트롤러")
public class PageCrowdFundingController extends PageAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	private PointTransactionService pointTransactionService;


    @ApiOperation(value = "크라우드 펀딩 페이지의 메인 템플릿을 반환",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @GetMapping()
    public String page(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);
    	return "fragments/page/crowd_funding/index";
    }

    @SuppressWarnings("deprecation")
	@ApiOperation(value = "참여형 크라우드 펀딩 템플릿을 반환", response = String.class)
    @GetMapping("/participation")
    public String participation(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);


    	PageRequest pageable = new PageRequest(0, 1, new Sort(Direction.DESC, "createdAt"));
    	PageRequest hotPageable = new PageRequest(0, 3, new Sort(Direction.DESC, "joinCnt"));
    	
    	Date date = new Date();
    	/* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String today = sdf.format(date) + " 23:59:59";
    	
    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date today2 = null;
    	
		try {
			today2 = sdf2.parse(today);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/


    	// 신규 프로젝트 조회 (홈)
    	Page<CrowdFunding> newProjectList = crowdFundingService.findAll(date, pageable);

    	// 인기 프로젝트 조회 (홈)
    	Page<CrowdFunding> hotProjectList = crowdFundingService.findAll(date, hotPageable);

    	// 신규 프로젝트 (메뉴)
    	int newPageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("newPage"))) newPageIndex = Integer.valueOf(request.getParameter("newPage"));
    	PageRequest NewPageable = new PageRequest(newPageIndex, 6, new Sort(Direction.DESC, "createdAt"));
    	Page<CrowdFunding> newProjectMenuList = crowdFundingService.findAll(date, NewPageable);

    	// 인기 프로젝트(메뉴)
    	int hotPageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("hotPage"))) hotPageIndex = Integer.valueOf(request.getParameter("hotPage"));
    	PageRequest hotMenuPageable = new PageRequest(hotPageIndex, 6, new Sort(Direction.DESC, "joinCnt"));
    	Page<CrowdFunding> hotProjectMenuList = crowdFundingService.findAll(date, hotMenuPageable);

    	// 프로젝트 소식
    	int newsPageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("newsPage"))) newsPageIndex = Integer.valueOf(request.getParameter("newsPage"));
    	PageRequest newsPageable = new PageRequest(newsPageIndex, 3, new Sort(Direction.DESC, "createdAt"));
    	Page<CrowdNews> projectNewsList = crowdNewsService.findAll(newsPageable);

    	PageRequest newsPageable2 = new PageRequest(0, 1, new Sort(Direction.DESC, "createdAt"));
    	Page<CrowdNews> newProjectNews = crowdNewsService.findAll(newsPageable2);

    	// 배너
    	PageRequest bannerPageable = new PageRequest(0, 5, new Sort(Direction.ASC, "orderNo"));
    	Page<CrowdFundingBanner> bannerList = crowdFundingBannerService.findByCrowdFundingBannerShowYn(bannerPageable);


    	model.addAttribute("newProjectList", newProjectList);
    	model.addAttribute("hotProjectList", hotProjectList);
    	model.addAttribute("newProjectMenuList", newProjectMenuList);
    	model.addAttribute("hotProjectMenuList", hotProjectMenuList);
    	model.addAttribute("projectNewsList", projectNewsList);
    	model.addAttribute("newProjectNewsList", newProjectNews);
    	model.addAttribute("bannerList", bannerList);
    	
    	// mobile
    	List<CrowdFunding> newProjectMenuListMobile = crowdFundingService.findAllByNewProject(date);
    	List<CrowdFunding> hotProjectMenuListMobile = crowdFundingService.findAllByHotProject(date);
    	List<CrowdNews> projectNewsListMobile = crowdNewsService.findByNewCrowdNews();
    	
    	model.addAttribute("newProjectMenuListMobile", newProjectMenuListMobile);
    	model.addAttribute("hotProjectMenuListMobile", hotProjectMenuListMobile);
    	model.addAttribute("projectNewsListMobile", projectNewsListMobile);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/crowd_funding/crowdFunding_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/crowd_funding/crowdFunding_mobile";
		}

        return "fragments/page/crowd_funding/participation";
    }

    @ApiOperation(value = "신규 프로젝트 템플릿을 반환", response = String.class)
    @GetMapping("/new_project")
    public String newProject(Principal principal, Model model, HttpServletRequest request){
        return participation(principal, model, request);
    }

    @ApiOperation(value = "인기 프로젝트 템플릿을 반환", response = String.class)
    @GetMapping("/hot_project")
    public String hotProject(Principal principal, Model model, HttpServletRequest request){
    	return participation(principal, model, request);
    }

    @ApiOperation(value = "프로젝트 소식 템플릿을 반환", response = String.class)
    @GetMapping("/information")
    public String information(Principal principal, Model model, HttpServletRequest request){
    	return participation(principal, model, request);
    }

    /**
     * 크라우드 펀딩 상세 화면 조회
     * @return
     */
    @GetMapping("/participation/detail")
    public String detail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);
    	CrowdFunding result = crowdFundingService.findByCrowdFundingId(paramData.getCrowdFundingId());
    	Member member = super.getCurrentUser(principal);

    	String path = request.getServletPath();
    	String query = request.getQueryString();

    	if(query == null)
    		query = "";

    	// 공유
    	try {
			super.setMetaTag(model, result.getMetaTag(request));
			/*model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), member.getId()));*/
			model.addAttribute("shareUrl", String.format("%s/crowd_funding/participation/detail?crowdFundingId=" + paramData.getCrowdFundingId(), UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	model.addAttribute("result", result);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/crowd_funding/crowdFunding_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/crowd_funding/crowdFunding_detail_mobile";
		}

    	return "fragments/page/crowd_funding/participation_detail";
    }

    /**
     * 프로젝트 소식 상세 화면 조회
     */
    @GetMapping("/projectNews/detail")
    public String projectNewsDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdNews paramData) {
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);
    	CrowdNews result = crowdNewsService.findByCrowdNewsId(paramData.getCrowdNewsId());

    	model.addAttribute("result", result);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/crowd_funding/project_news_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/crowd_funding/project_news_detail_mobile";
		}
    	
    	return "fragments/page/crowd_funding/project_news_detail";
    }

    /**
     * 프로젝트 리워드 화면 조회
     */
    @GetMapping("/participation/reward")
    public String reward(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFunding paramData) {
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);
    	List<CrowdFundingReward> rewardList = crowdFundingRewardService.findByCrowdFundingId(paramData.getCrowdFundingId());

    	model.addAttribute("rewardList", rewardList);
    	model.addAttribute("crowdFundingId", paramData.getCrowdFundingId());
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/crowd_funding/crowdFunding_reward_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/crowd_funding/crowdFunding_reward_mobile";
		}

    	return "fragments/page/crowd_funding/participation_reward";
    }

    /**
     * 프로젝트 펀딩 포인트 결제
     */
    @GetMapping("/participation/point_payment")
    public String pointPayment(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") CrowdFundingJoin paramData,
    		@RequestParam("crowdFundingId") Long crowdFundingId, @RequestParam("crowdFundingRewardId") Long crowdFundingRewardId, @RequestParam(value="point", defaultValue="0") int point) {
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);

    	System.out.println("# pointPayment.crowdFundingId :"+crowdFundingId);
    	System.out.println("# pointPayment.crowdFundingRewardId :"+crowdFundingRewardId);
    	System.out.println("# pointPayment.point :"+point);
    	System.out.println("# pointPayment.paramData :"+paramData.toString());

    	Member member = super.getCurrentUser(principal);

    	// 사용가능 포인트
    	float usablePoint = member.getUsablePoint();

    	// 펀딩포인트
    	float fundingPoint =  member.getFundingPoint();

    	// 필요한 전체 포인트
    	float needPoint = 0f;

    	// 사용할 펀딩 포인트
    	float useFundingPoint = 0f;

    	// 사용할 사용가능 포인트
    	float useUsablePoint = 0f;

    	// 부족한 포인트
    	float tribalPoint = 0;

    	CrowdFundingReward reward = crowdFundingRewardService.findByCrowdFundingRewardId(crowdFundingRewardId);

    	// 펀딩 금액 확인
    	if(reward != null) {
    		needPoint = reward.getPoint();
    	}else {
    		needPoint = point;
    	}

    	// 포인트 차감
    	if ((usablePoint+fundingPoint) < needPoint) {
    		// 포인트가 부족한 경우
    		tribalPoint = needPoint - (usablePoint+fundingPoint);

    	} else if (fundingPoint >= needPoint) {
    		// 보유 펀딩 포인트 사용
    		useFundingPoint = needPoint;

		} else {
			// 보유 펀딩 포인트가 부족하면 부족한 포인트 부분을 사용가능 포인트에서 차감
			useFundingPoint = fundingPoint;
			useUsablePoint = needPoint - fundingPoint;
		}


    	/*if(usablePoint < NeedPoint) {
    		tribalPoint = NeedPoint - usablePoint;
    	}else {
    		tribalPoint = 0;
    	}*/

    	model.addAttribute("reward", reward);
    	model.addAttribute("point", point);
//    	model.addAttribute("useFundingPoint", useFundingPoint);
//    	model.addAttribute("useUsablePoint", useUsablePoint);
    	model.addAttribute("tribalPoint", tribalPoint);
    	model.addAttribute("crowdFundingId", crowdFundingId);
    	model.addAttribute("crowdFundingRewardId", crowdFundingRewardId);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/crowd_funding/crowdFunding_payment_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/crowd_funding/crowdFunding_payment_mobile";
		}

    	return "fragments/page/crowd_funding/participation_funding_payment";
    }
    
    /**
     * 펀딩 완료 처리
     * @param principal
     * @param model
     * @param request
     * @param paramData
     * @return
     */
	@RequestMapping(value = "/point_payment", method=RequestMethod.POST)
	@ResponseBody
    public Map<String, Object> feedCommentInsertAjax(Principal principal, Model model, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("paramData") CrowdFundingJoin paramData,
    		@RequestParam("crowdFundingId") Long crowdFundingId, @RequestParam("crowdFundingRewardId") Long crowdFundingRewardId, @RequestParam("point") String point) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	
    	Member member = super.getCurrentUser(principal);
    	paramData.setMember(member);
    	paramData.setCreatedAt(new Date());
    	paramData.setUpdatedAt(new Date());
    	CrowdFunding crowdFunding = crowdFundingService.findByCrowdFundingId(crowdFundingId);
    	paramData.setCrowdFunding(crowdFunding);
    	paramData.setDeliveryState("1");
    	
    	// 동일 사용자가 중복으로 펀딩을 하면 펀팅카운터는 올라가지 않는다.
    	int crowdFundingJoinCnt = crowdFundingJoinService.findByCrowdFundingIdAndMemberId(paramData);
    	if (crowdFundingJoinCnt == 0) {
    		crowdFunding.setJoinCnt(crowdFunding.getJoinCnt()+1);
		}

    	if(crowdFundingRewardId == -1) {
    		paramData.setUsePoint(Integer.parseInt(point));
    		paramData.setRewardSelectedYn("0");
    		crowdFunding.setFundingJoin(Integer.parseInt(point));
    	}else {
    		CrowdFundingReward reward = crowdFundingRewardService.findByCrowdFundingRewardId(crowdFundingRewardId);
    		paramData.setCrowdFundingReward(reward);
    		paramData.setRewardSelectedYn("1");
    		paramData.setUsePoint(reward.getPoint());
    		crowdFunding.setFundingJoin(reward.getPoint());
    	}

    	int cnt = crowdFundingJoinService.countByCreatedAt();

    	// crowdfundingjoinId 생성
    	paramData.setCrowdFundingJoinId(createCrowdFundingJoinId(cnt));

    	CrowdFundingJoin result = crowdFundingJoinService.save(paramData);
    	crowdFundingService.save(crowdFunding);

    	// 펀딩포인트
    	float fundingPoint =  member.getFundingPoint();

    	// 필요한 전체 포인트
    	float needPoint = Float.parseFloat(point);

    	// 사용할 펀딩 포인트
    	float useFundingPoint = 0f;

    	// 사용할 사용가능 포인트
    	float useUsablePoint = 0f;

    	// 포인트 차감
    	if (fundingPoint >= needPoint) {
    		// 보유 펀딩 포인트 사용
    		useFundingPoint = needPoint;

		} else {
			// 보유 펀딩 포인트가 부족하면 부족한 포인트 부분을 사용가능 포인트에서 차감
			useFundingPoint = fundingPoint;
			useUsablePoint = needPoint - fundingPoint;
		}


    	// 포인트 결제 & 현재 남은 포인트 조회
    	PointTransactionResponse pointResult = pointTransactionService.usePoints(member.getId(), PointTransactionType.FUNDING, crowdFundingId, Float.parseFloat(point));
        
    	resultMap.put("result", result.getCrowdFundingJoinId());
    	resultMap.put("pointResult", pointResult);
    	resultMap.put("useFundingPoint", useFundingPoint);
    	resultMap.put("useUsablePoint", useUsablePoint);
    	
    	return resultMap;
    }
    
    

    /**
     * 프로젝트 펀딩 완료 페이지 이동
     */
    @PostMapping("/participation/funding_complete")
    public String fundingComplete(Principal principal, Model model, HttpServletRequest request, HttpServletResponse response,
    		@RequestParam("crowdFundingJoinId") String crowdFundingJoinId, @RequestParam("point") String point
    		, @RequestParam("useFundingPoint") float useFundingPoint, @RequestParam("useUsablePoint") float useUsablePoint) {
    	
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);
    	/*Member member = super.getCurrentUser(principal);
    	paramData.setMember(member);
    	paramData.setCreatedAt(new Date());
    	paramData.setUpdatedAt(new Date());
    	CrowdFunding crowdFunding = crowdFundingService.findByCrowdFundingId(crowdFundingId);
    	paramData.setCrowdFunding(crowdFunding);
    	paramData.setDeliveryState("1");
    	
    	// 동일 사용자가 중복으로 펀딩을 하면 펀팅카운터는 올라가지 않는다.
    	int crowdFundingJoinCnt = crowdFundingJoinService.findByCrowdFundingIdAndMemberId(paramData);
    	if (crowdFundingJoinCnt == 0) {
    		crowdFunding.setJoinCnt(crowdFunding.getJoinCnt()+1);
		}

    	if(crowdFundingRewardId == -1) {
    		paramData.setUsePoint(Integer.parseInt(point));
    		paramData.setRewardSelectedYn("0");
    		crowdFunding.setFundingJoin(Integer.parseInt(point));
    	}else {
    		CrowdFundingReward reward = crowdFundingRewardService.findByCrowdFundingRewardId(crowdFundingRewardId);
    		paramData.setCrowdFundingReward(reward);
    		paramData.setRewardSelectedYn("1");
    		paramData.setUsePoint(reward.getPoint());
    		crowdFunding.setFundingJoin(reward.getPoint());
    	}

    	int cnt = crowdFundingJoinService.countByCreatedAt();

    	// crowdfundingjoinId 생성
    	paramData.setCrowdFundingJoinId(createCrowdFundingJoinId(cnt));

    	CrowdFundingJoin result = crowdFundingJoinService.save(paramData);
    	crowdFundingService.save(crowdFunding);*/

    	// 펀딩포인트
    	/*float fundingPoint =  member.getFundingPoint();

    	// 필요한 전체 포인트
    	float needPoint = Float.parseFloat(point);

    	// 사용할 펀딩 포인트
    	float useFundingPoint = 0f;

    	// 사용할 사용가능 포인트
    	float useUsablePoint = 0f;

    	// 포인트 차감
    	if (fundingPoint >= needPoint) {
    		// 보유 펀딩 포인트 사용
    		useFundingPoint = needPoint;

		} else {
			// 보유 펀딩 포인트가 부족하면 부족한 포인트 부분을 사용가능 포인트에서 차감
			useFundingPoint = fundingPoint;
			useUsablePoint = needPoint - fundingPoint;
		}


    	// 포인트 결제 & 현재 남은 포인트 조회
    	PointTransactionResponse pointResult = pointTransactionService.usePoints(member.getId(), PointTransactionType.FUNDING, crowdFundingId, Float.parseFloat(point));*/

    	CrowdFundingJoin result = crowdFundingJoinService.findByCrowdFundingJoinId(crowdFundingJoinId);
    	
    	model.addAttribute("result", result);
    	// model.addAttribute("pointResult", pointResult);
    	model.addAttribute("point", point);
    	model.addAttribute("useFundingPoint", useFundingPoint);
    	model.addAttribute("useUsablePoint", useUsablePoint);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/crowd_funding/crowdFunding_complete_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/crowd_funding/crowdFunding_complete_mobile";
		}
    	
    	return "fragments/page/crowd_funding/participation_funding_complete";
    }

    /**
     * 내 펀딩 내역
     */
    @SuppressWarnings("deprecation")
	@GetMapping("/participation/funding_result")
    public String fundingList(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/crowd_funding", super.getCurrentUser(principal), request);
    	Member member = super.getCurrentUser(principal);

    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 6, new Sort(Direction.DESC, "createdAt"));

    	Page<CrowdFundingJoin> fundingJoinList = crowdFundingJoinService.findByMemberId(member.getId(), pageable);

    	model.addAttribute("fundingJoinList", fundingJoinList);
    	
    	// mobile
    	List<CrowdFundingJoin> fundingJoinListMobile = crowdFundingJoinService.findByMemberId(member.getId());
    	model.addAttribute("fundingJoinListMobile", fundingJoinListMobile);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/crowd_funding/crowdFunding_funding_result_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/crowd_funding/crowdFunding_funding_result_mobile";
		}
    	
    	return "fragments/page/crowd_funding/participation_funding_result";
    }




    /**
     * 크라우드 펀딩 참여 모델 id 생성
     */
    public String createCrowdFundingJoinId(int cnt) {
    	String cntStr = String.format("%05d", (cnt+1));

    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	String today = sdf.format(new Date());

    	return "CF" + today + cntStr;
    }

}

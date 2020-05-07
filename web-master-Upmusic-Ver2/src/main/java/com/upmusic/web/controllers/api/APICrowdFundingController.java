package com.upmusic.web.controllers.api;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.domain.CrowdFunding;
import com.upmusic.web.domain.CrowdFundingJoin;
import com.upmusic.web.domain.CrowdFundingReward;
import com.upmusic.web.domain.Member;
import com.upmusic.web.message.PointTransactionResponse;
import com.upmusic.web.services.CrowdFundingBannerService;
import com.upmusic.web.services.CrowdFundingJoinService;
import com.upmusic.web.services.CrowdFundingRewardService;
import com.upmusic.web.services.CrowdFundingService;
import com.upmusic.web.services.CrowdNewsService;
import com.upmusic.web.services.PointTransactionService;

@RestController
@RequestMapping("/api/crowdFunding")
public class APICrowdFundingController extends APIAbstractController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PointTransactionService pointTransactionService;
	
	@Autowired
	private CrowdFundingService crowdFundingService;

	@Autowired
	private CrowdFundingRewardService crowdFundingRewardService;

	@Autowired
	private CrowdFundingJoinService crowdFundingJoinService;
	
	@Autowired
	private Gson gson;
	
	/**
     * 포인트 결제
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
     * 크라우드 펀딩 참여 모델 id 생성
     */
    public String createCrowdFundingJoinId(int cnt) {
    	String cntStr = String.format("%05d", (cnt+1));

    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	String today = sdf.format(new Date());

    	return "CF" + today + cntStr;
    }
	
}

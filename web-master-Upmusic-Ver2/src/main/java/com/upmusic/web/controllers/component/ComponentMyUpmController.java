package com.upmusic.web.controllers.component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.security.Principal;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.domain.Member;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.PointTransactionService;
import com.upmusic.web.services.VideoService;


@Controller
@RequestMapping("/component/my_upm")
@Api(value="component.my_upm", description="My UPM 컴포넌트를 담당하는 컨트롤러")
public class ComponentMyUpmController extends ComponentAbstractController {

	@Autowired
	private MusicTrackService trackService;
	
	@Autowired
	private VideoService videoService;
	
	@Autowired
    private PointTransactionService pointTransactionService;
	
	
    // --------------------------------------------------------------------------------------------
 	// UPLOAD
    
    @ApiOperation(value = "업로드 음원 내역 컴포넌트를 반환", response = String.class)
    @GetMapping("/uploaded_track")
    public String uploadedTrack(Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
    		model.addAttribute("tracks", trackService.findUploadedByArtistIdWithHeartByMember(member.getId(), member.getId(), super.getPageMusicOrderByNew(page)));
    		model.addAttribute("paginationTrackUrl", "/component/my_upm/uploaded_track");
    	}
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/component/my_upm/uploaded_track_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_upm/uploaded_track_mobile";
		}
        return "fragments/component/my_upm/uploaded_track";
    }
    
    @ApiOperation(value = "업로드 비디오 내역 컴포넌트를 반환", response = String.class)
    @GetMapping("/uploaded_video")
    public String uploadedVideo(Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
    		model.addAttribute("videos", videoService.findByMemberId(member.getId(), super.getPageVideoOrderByNew(page)));
    		model.addAttribute("paginationVideoUrl", "/component/my_upm/uploaded_video");
    	}
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/component/my_upm/uploaded_video_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_upm/uploaded_video_mobile";
		}
        return "fragments/component/my_upm/uploaded_video";
    }
    
    // --------------------------------------------------------------------------------------------
  	// POINT
    
    @ApiOperation(value = "회원의 해당월 포인트 정산내역 반환",response = String.class)
    @GetMapping(value = "/point/{type}/{month}")
    public String monthlyTransaction(@PathVariable String month, @PathVariable String type,Principal principal, HttpServletRequest request, Model model) throws ParseException {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
			if (type.equals("use")) {
				model.addAttribute("transactions", pointTransactionService.getMonthlyUseTransaction(member.getId(), month, super.getTransactionsOrderByNew(page)));
				model.addAttribute("paginationTransactionUrl", "/component/my_upm/point");
				model.addAttribute("type","use");
			} else if (type.equals("all")) {
				model.addAttribute("transactions", pointTransactionService.getMonthlyAllTransaction(member.getId(), month, super.getTransactionsOrderByNew(page)));
				model.addAttribute("paginationTransactionUrl", "/component/my_upm/point");
				model.addAttribute("type","all");
			} else if (type.equals("charge")) {
				model.addAttribute("transactions", pointTransactionService.getMonthlyChargeTransaction(member.getId(), month, super.getTransactionsOrderByNew(page)));
				model.addAttribute("paginationTransactionUrl", "/component/my_upm/point");
				model.addAttribute("type","charge");
			}
    	}
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			if (0 < page) return "fragments/component/my_upm/point_page_mobile";
	    	return "fragments/component/my_upm/point_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 < page) return "fragments/component/my_upm/point_page_mobile";
	    	return "fragments/component/my_upm/point_mobile";
		}

		if (0 < page) return "fragments/component/my_upm/point_page";
		return "fragments/component/my_upm/point";
    }
    
}

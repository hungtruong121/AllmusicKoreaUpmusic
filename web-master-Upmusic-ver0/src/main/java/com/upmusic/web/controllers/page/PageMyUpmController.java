package com.upmusic.web.controllers.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.net.MalformedURLException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.PointRewardCondition;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.PointRewardConditionService;
import com.upmusic.web.services.VideoService;


@Controller
@RequestMapping("/my_upm")
@Api(value="my_upm", description="MY UPM 페이지를 담당하는 컨트롤러")
public class PageMyUpmController extends PageAbstractController {

	@Autowired
    private VideoService videoService;

//	@Autowired
//	private MusicAlbumService albumService;

	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
	private PointRewardConditionService pointRewardConditionService;


    @ApiOperation(value = "MY UPM 페이지의 메인 템플릿을 반환",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @GetMapping()
    public String page(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/my_upm", super.getCurrentUser(principal), request);
    	return "fragments/page/my_upm/index";
    }

    /* music/artist url 사용 : mantis 0020275
    @ApiOperation(value = "프로필 템플릿을 반환", response = String.class)
    @GetMapping("/profile")
    public String profile(Principal principal, HttpServletRequest request, Model model){
    	Member artist = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/my_upm/profile", artist);
    	if (artist != null) {
			model.addAttribute("artist", artist);
			model.addAttribute("artistTracks", trackService.findTop10ByArtistIdWithHeartByMember(artist.getId(), artist.getId()));
			model.addAttribute("artistAlbums", albumService.findTop4ByArtistIdWithHeartByMember(artist.getId(), artist.getId()));
			model.addAttribute("artistVideos", videoService.findTop4ByMemberId(artist.getId()));
			model.addAttribute("artistStoreTracks", trackService.findTop10ByArtistIdAndInStoreTrueWithHeartByMember(artist.getId(), artist.getId()));

			// 공유
			try {
				super.setMetaTag(model, artist.getMetaTag(request));
				model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), artist.getId()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			model.addAttribute("isOwner", true);
			model.addAttribute("likedArtist", false);
		}
		return "fragments/page/music/artist_detail";
    }
    */

    @ApiOperation(value = "음원 거래 템플릿을 반환", response = String.class)
    @GetMapping("/transaction")
    public String transaction(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/my_upm/transaction", super.getCurrentUser(principal), request);
        return "fragments/page/my_upm/transaction";
    }

    @ApiOperation(value = "업로드 내역 템플릿을 반환", response = String.class)
    @GetMapping("/upload")
    public String upload(Principal principal, HttpServletRequest request, Model model) {
    	Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/my_upm/upload", member, request);
    	if (member != null) {
    		model.addAttribute("tracks", trackService.findUploadedByArtistIdWithHeartByMember(member.getId(), member.getId(), super.getPageMusicOrderByNew(0)));
    		model.addAttribute("paginationTrackUrl", "/component/my_upm/uploaded_track");
    		model.addAttribute("videos", videoService.findByMemberId(member.getId(), super.getPageVideoOrderByNew(0)));
    		model.addAttribute("paginationVideoUrl", "/component/my_upm/uploaded_video");
    	}

    	try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_upm/upload_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_upm/upload_mobile";
		}

        return "fragments/page/my_upm/upload";
    }

    @ApiOperation(value = "리워드 정산 내역 템플릿을 반환", response = String.class)
    @GetMapping("/reward")
    public String reward(Principal principal, HttpServletRequest request, Model model) {
    	Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/my_upm/reward", member, request);
    	if (member != null) {
    		model.addAttribute("member", member);
    		PointRewardCondition condition = pointRewardConditionService.getCondition();
    		model.addAttribute("rewardCondition", condition);
    	}
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_upm/reward_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_upm/reward_mobile";
		}
		
        return "fragments/page/my_upm/reward";
    }

    @ApiOperation(value = "포인트 사용 내역 템플릿을 반환", response = String.class)
    @GetMapping("/point")
    public String point(Principal principal, HttpServletRequest request, Model model) {
    	Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/my_upm/point", member, request);
    	if (member != null) {
    		model.addAttribute("member", member);
    	}
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_upm/point_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_upm/point_mobile";
		}
        return "fragments/page/my_upm/point";
    }

    @ApiOperation(value = "내 펀딩 내역 템플릿을 반환", response = String.class)
    @GetMapping("/my_funding")
    public String myFunding(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/my_upm/my_funding", super.getCurrentUser(principal), request);
//        return "fragments/page/my_upm/my_funding";
        return "redirect:/crowd_funding/participation/funding_result";

    }
    
    @ApiOperation(value = "회원탈퇴 템플릿을 반환", response = String.class)
    @GetMapping("/withdraw")
    public String withdraw(Principal principal, HttpServletRequest request, Model model){
    	super.setSubMenu(model, "/my_upm/withdraw", super.getCurrentUser(principal), request);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_upm/withdraw_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_upm/withdraw_mobile";
		}
        return "fragments/page/my_upm/withdraw";
    }
    
}

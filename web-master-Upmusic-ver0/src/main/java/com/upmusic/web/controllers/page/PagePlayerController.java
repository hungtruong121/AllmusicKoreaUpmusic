package com.upmusic.web.controllers.page;

import java.net.MalformedURLException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.PointRewardConditionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Controller
@RequestMapping("/player")
@Api(value="player", description="뮤직 플레이어 페이지를 담당하는 컨트롤러")
public class PagePlayerController extends PageAbstractController {

	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
	private PointRewardConditionService pointRewardConditionService;
	
    
    @ApiOperation(value = "플레이어 페이지의 메인 템플릿을 반환", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @GetMapping()
    public String page(Principal principal, HttpServletRequest request, Model model) {
    	Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/player", member, request);
    	if (member != null) {
    		try {
    			model.addAttribute("baseUrl", UPMusicHelper.baseUrl(request));
    			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    		}
    		
    		model.addAttribute("rewardObj", pointRewardConditionService.getCondition());
        	
    		// 음원
    		Iterable<MusicTrack> tracks = trackService.findPlaylistWithHeartByMember(member.getId());
    		model.addAttribute("tracksFragmentId", "player-track-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    		model.addAttribute("tracks", tracks);
    		model.addAttribute("trackPaginationUrl", "/component/my_playlist/recent_track");
    	}
    	return "fragments/page/player/index";
    }
    

}

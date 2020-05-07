package com.upmusic.web.controllers.page;

import java.net.MalformedURLException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.upmusic.web.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MemberPlaytime;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.Video;
import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Controller
@RequestMapping("/")
@Api(value="home", description="서비스의 시작페이지를 담당하는 컨트롤러")
public class PageHomeController extends PageAbstractController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RecommendedMediaService recommendedMediaService;
	
	@Autowired
	private LeagueSeasonService leagueSeasonService;
	
	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
    private VideoService videoService;
	
	@Autowired
    private MemberService memberService;
	
	@Autowired
    private PointTransactionService pointTransactionService;

	@Autowired
	private MainBannerService mainBannerService;
	
	
	@ApiOperation(value = "홈페이지의 템플릿을 반환", response = String.class)
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
		super.setSubMenu(model, "/", member, request);
		try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		// 추천영상
		model.addAttribute("recommendedVideos", recommendedMediaService.getRecommendedVideo());
		// 추천음원
		model.addAttribute("recommendedTracks", recommendedMediaService.getRecommendedMusicTrack(member != null ? member.getId() : 0));
		// TOP 5
		model.addAttribute("top5Tracks", leagueSeasonService.listTop5ByCurrentSeason(member != null ? member.getId() : 0));
				
		// 인기 아티스트 : 모바일용
		model.addAttribute("top2Artists", memberService.findTop2Artist());

		//메인화면 배너
		model.addAttribute("eventBannerList", mainBannerService.findAllShownEventBanner());
		model.addAttribute("artistBannerList", mainBannerService.findAllShownArtistBanner());

		Page<MusicTrack> recentPlayedTracks = Page.empty();
		Page<Video> recentPlayedVideos = Page.empty();
		Page<MusicTrack> likedTracks = Page.empty();
		Page<Video> likedVideos = Page.empty();
		boolean hasRecentPlayedList = true;
		boolean hasLikedList = true;
		if (member != null) {
			// 스트리밍 리워드
			MemberPlaytime playtime = pointTransactionService.findOrCreatePlaytime(member.getId());
			//ngoclh format playtime
			long minutes = playtime.getPlaytime() / 60;
			long second = playtime.getPlaytime() % 60;
			String streamingRewardPlaytime = minutes + "분 " + second + "초";
			
			model.addAttribute("streamingRewardPlaytime", streamingRewardPlaytime);
			model.addAttribute("streamingRewardPoint", pointTransactionService.getTodayStreamingPoint(member.getId()));
			// 최근 플레이리스트
			recentPlayedTracks = trackService.findPlayedTrackByMemberId(member.getId(), super.getPageMediaListInHome(0));
			recentPlayedVideos = videoService.findPlayedVideoByMemberId(member.getId(), super.getPageMediaListInHome(0));
			// 좋아요 리스트
			likedTracks = trackService.findHeartTrackByMemberId(member.getId(), super.getPageMediaListInHome(0));
			likedVideos = videoService.findHeartVideoByMemberId(member.getId(), super.getPageMediaListInHome(0));
		} else {
			// 스트리밍 리워드
			model.addAttribute("streamingRewardPlaytime", 0);
			model.addAttribute("streamingRewardPoint", 0);
		}
		
		// 리스트가 없을 경우
		if (0 == recentPlayedTracks.getTotalElements() || 0 == recentPlayedVideos.getTotalElements()) {
			recentPlayedTracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0, super.getPageMediaListInHomeOrderByPlayCnt(0));
			recentPlayedVideos = videoService.findAll(super.getPageMediaListInHomeOrderByHitCnt(0));
			hasRecentPlayedList = false;
		}
		if (0 == likedTracks.getTotalElements() || 0 == likedVideos.getTotalElements()) {
			likedTracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0, super.getPageMediaListInHomeOrderByHeartCnt(0));
			likedVideos = videoService.findAll(super.getPageMediaListInHomeOrderByHeartCnt(0));
			hasLikedList = false;
		}
		
		model.addAttribute("hasRecentPlayedList", hasRecentPlayedList);
		model.addAttribute("recentPlayedTracks", recentPlayedTracks);
		model.addAttribute("recentPlayedVideos", recentPlayedVideos);
		model.addAttribute("hasLikedList", hasLikedList);
		model.addAttribute("likedTracks", likedTracks);
		model.addAttribute("likedVideos", likedVideos);

		String userAgent = request.getHeader("user-agent");
		logger.debug("page : userAgent is {}", userAgent);
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			// 인기 동영상
			model.addAttribute("top4Videos", videoService.findTop4GV());
			model.addAttribute("userAgent", userAgent);

			return "fragments/page/home/index_mobile";

		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			// 인기 동영상
			model.addAttribute("top4Videos", videoService.findTop4GV());

			return "fragments/page/home/index_mobile";
		}
		
		// 인기 동영상
		model.addAttribute("top4Videos", videoService.findTop3GV());
        return "fragments/page/home/index";
    }
}

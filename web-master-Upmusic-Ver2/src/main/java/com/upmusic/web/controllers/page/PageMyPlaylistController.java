package com.upmusic.web.controllers.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.net.MalformedURLException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.domain.Collection;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.Video;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.CollectionService;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.VideoService;


@Controller
@RequestMapping("/my_playlist")
@Api(value="my_upm", description="나의 플레이 리스트 페이지를 담당하는 컨트롤러")
public class PageMyPlaylistController extends PageAbstractController {

	@Autowired
    private CollectionService collectionService;
	
	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
    private VideoService videoService;
	
	
    @ApiOperation(value = "나의 플레이 리스트 페이지의 메인 템플릿을 반환",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @GetMapping()
    public String page(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/my_playlist", super.getCurrentUser(principal), request);
    	return "fragments/page/my_playlist/index";
    }

    // --------------------------------------------------------------------------------------------
 	// RECENT
    
    @ApiOperation(value = "최근 플레이 리스트 템플릿을 반환", response = String.class)
    @GetMapping("/recent")
    public String recent(Principal principal, HttpServletRequest request, Model model) {
    	Member member = super.getCurrentUser(principal);
    	if (member == null) return "redirect:/auth/login";
    	super.setSubMenu(model, "/my_playlist/recent", member, request);
    	
    	try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	
		// 음원
		Page<MusicTrack> tracks = trackService.findPlayedTrackByMemberId(member.getId(), super.getPagePlaylistTrack(0));
		model.addAttribute("tracksFragmentId", "recent-track-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("trackPaginationUrl", "/component/my_playlist/recent_track");
		
		// 영상
		Page<Video> videos = videoService.findPlayedVideoByMemberId(member.getId(), super.getPagePlaylistVideo(0));
		model.addAttribute("videosFragmentId", "recent-video-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("videos", videos);
		model.addAttribute("videoPaginationUrl", "/component/my_playlist/recent_video");

		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_playlist/recent_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_playlist/recent_mobile";
		}
        return "fragments/page/my_playlist/recent";
    }
    
    // --------------------------------------------------------------------------------------------
 	// COLLECTION
    
    @ApiOperation(value = "내가 담은 리스트 템플릿을 반환", response = String.class)
    @GetMapping("/collection")
    public String collection(Principal principal, HttpServletRequest request, Model model) {
    	Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/my_playlist/collection", member, request);
    	
    	Page<Collection> collections = collectionService.findByMemberId(member.getId(), super.getPageCollectionOrderByNew(0));
    	model.addAttribute("collectionsFragmentId", "collections-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("collections", collections);
		model.addAttribute("paginationUrl", "/component/my_playlist/collection");
		
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_playlist/collection_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_playlist/collection_mobile";
		}
        return "fragments/page/my_playlist/collection";
    }
    
    @ApiOperation(value = "내가 담은 리스트 상세페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/collection/{id}")
	public String albumDetail(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
    	Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/my_playlist/collection", member, request);
		try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Collection collection = collectionService.getCollectionById(id);
		if (0 == (collection.getMember().getId().compareTo(member.getId()))) {
			model.addAttribute("collection", collection);
			Page<MusicTrack> tracks = collectionService.listTrackByCollection(id, member.getId(), super.getPageCollectionTrack(0));
			model.addAttribute("tracksFragmentId", "collection-tracks-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
	    	model.addAttribute("tracks", tracks);
			model.addAttribute("paginationUrl", "/component/my_playlist/collection/" + id);
		}
		
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_playlist/collection_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_playlist/collection_detail_mobile";
		}
		return "fragments/page/my_playlist/collection_detail";
	}
    
    // --------------------------------------------------------------------------------------------
  	// LIKE
    
    @ApiOperation(value = "좋아요 리스트 템플릿을 반환", response = String.class)
    @GetMapping("/like")
    public String like(Principal principal, HttpServletRequest request, Model model){
    	Member member = super.getCurrentUser(principal);
    	if (member == null) return "redirect:/auth/login";
    	
    	super.setSubMenu(model, "/my_playlist/like", member, request);
    	try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	
		// 음원
		Page<MusicTrack> tracks = trackService.findHeartTrackByMemberId(member.getId(), super.getPagePlaylistTrack(0));
		model.addAttribute("tracksFragmentId", "like-track-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("trackPaginationUrl", "/component/my_playlist/like_track");
		
		// 영상
		Page<Video> videos = videoService.findHeartVideoByMemberId(member.getId(), super.getPagePlaylistVideo(0));
		model.addAttribute("videosFragmentId", "like-video-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("videos", videos);
		model.addAttribute("videoPaginationUrl", "/component/my_playlist/like_video");
		
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/my_playlist/like_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/my_playlist/like_mobile";
		}
        return "fragments/page/my_playlist/like";
    }

}

package com.upmusic.web.controllers.component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.domain.Collection;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.Video;
import com.upmusic.web.services.CollectionService;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.VideoService;


@Controller
@RequestMapping("/component/my_playlist")
@Api(value="component.my_playlist", description="마이 플레이리스트 컴포넌트를 담당하는 컨트롤러")
public class ComponentMyPlaylistController extends ComponentAbstractController {

	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
    private VideoService videoService;
	
	@Autowired
	private CollectionService collectionService;
	
	
	// --------------------------------------------------------------------------------------------
	// RECENT
    
    @ApiOperation(value = "최근 곡 플레이 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/recent_track")
    public String recentTrack(Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	Page<MusicTrack> tracks = trackService.findPlayedTrackByMemberId(member.getId(), super.getPagePlaylistTrack(page));
		model.addAttribute("tracksFragmentId", "recent-track-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("trackPaginationUrl", "/component/my_playlist/recent_track");
		model.addAttribute("paginationUrl", "/component/my_playlist/recent_track");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/my_playlist/track_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_playlist/track_item_mobile";
		}
        return "fragments/component/my_playlist/recent_track";
    }
    
    @ApiOperation(value = "최근 영상 플레이 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/recent_video")
    public String recentVideo(Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	Page<Video> videos = videoService.findPlayedVideoByMemberId(member.getId(), super.getPagePlaylistVideo(page));
		model.addAttribute("videosFragmentId", "recent-video-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("videos", videos);
		model.addAttribute("videoPaginationUrl", "/component/my_playlist/recent_video");
		model.addAttribute("paginationUrl", "/component/my_playlist/recent_video");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/my_playlist/video_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_playlist/video_item_mobile";
		}
        return "fragments/component/my_playlist/recent_video";
    }
	
	// --------------------------------------------------------------------------------------------
	// COLLECTION
    
    @ApiOperation(value = "내가 담은 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/collection")
    public String collection(Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	Page<Collection> collections = collectionService.findByMemberId(member.getId(), super.getPageCollectionOrderByNew(page));
    	
    	model.addAttribute("collectionsFragmentId", "collections-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("collections", collections);
		model.addAttribute("paginationUrl", "/component/my_playlist/collection");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/my_playlist/collection_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_playlist/collection_item_mobile";
		}
        return "fragments/component/my_playlist/collection_list";
    }
    
    @ApiOperation(value = "내가 담은 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/collection_modal")
    public String collectionForModal(Principal principal, HttpServletRequest request, Model model) {
//    	int page = 0;
//    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	Iterable<Collection> collections = collectionService.listAllCollectionsByMemberId(member.getId());
    	
//    	model.addAttribute("collectionsFragmentId", "collection-modal-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("collections", collections);
//		model.addAttribute("paginationUrl", "/component/my_playlist/collection_modal");
        return "fragments/component/my_playlist/collection_list_modal";
    }
    
    @ApiOperation(value = "내가 담은 리스트 상세페이지의 컴포넌트를 반환", response = String.class)
	@GetMapping("/collection/{id}")
	public String albumDetail(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		Member member = super.getCurrentUser(principal);
		Collection collection = collectionService.getCollectionById(id);
		if (0 == (collection.getMember().getId().compareTo(member.getId()))) {
			model.addAttribute("collection", collection);
			Page<MusicTrack> tracks = collectionService.listTrackByCollection(id, member.getId(), super.getPageCollectionTrack(page));
			model.addAttribute("tracksFragmentId", "collection-tracks-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
	    	model.addAttribute("tracks", tracks);
			model.addAttribute("paginationUrl", "/component/my_playlist/collection/" + id);
		}
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/my_playlist/track_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_playlist/track_item_mobile";
		}
		return "fragments/component/my_playlist/collection_track_list";
	}
    
    // --------------------------------------------------------------------------------------------
 	// LIKE
     
     @ApiOperation(value = "좋아하는 곡 리스트 컴포넌트를 반환", response = String.class)
     @GetMapping("/like_track")
     public String likeTrack(Principal principal, HttpServletRequest request, Model model) {
     	int page = 0;
     	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
     	Member member = super.getCurrentUser(principal);
     	Page<MusicTrack> tracks = trackService.findHeartTrackByMemberId(member.getId(), super.getPagePlaylistTrack(page));
		model.addAttribute("tracksFragmentId", "like-track-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("trackPaginationUrl", "/component/my_playlist/like_track");
		model.addAttribute("paginationUrl", "/component/my_playlist/like_track");
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/my_playlist/track_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_playlist/track_item_mobile";
		}
		return "fragments/component/my_playlist/like_track";
     }
     
     @ApiOperation(value = "좋아하는 영상 리스트 컴포넌트를 반환", response = String.class)
     @GetMapping("/like_video")
     public String likeVideo(Principal principal, HttpServletRequest request, Model model) {
     	int page = 0;
     	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
     	Member member = super.getCurrentUser(principal);
     	Page<Video> videos = videoService.findHeartVideoByMemberId(member.getId(), super.getPagePlaylistVideo(page));
		model.addAttribute("videosFragmentId", "like-video-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("videos", videos);
		model.addAttribute("videoPaginationUrl", "/component/my_playlist/like_video");
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/my_playlist/video_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/my_playlist/video_item_mobile";
		}
         return "fragments/component/my_playlist/recent_video";
     }
    
}

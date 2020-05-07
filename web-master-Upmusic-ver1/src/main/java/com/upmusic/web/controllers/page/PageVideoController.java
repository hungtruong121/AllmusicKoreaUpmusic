package com.upmusic.web.controllers.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.Video;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.VideoService;
import com.upmusic.web.validator.VideoValidator;


@Controller
@RequestMapping("/video")
@Api(value="video", description="비디오 페이지를 담당하는 컨트롤러")
public class PageVideoController extends PageAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private VideoService videoService;
	
	@Autowired
	private VideoValidator videoValidator;
	
	@Autowired
    private MusicTrackService trackService;
	

	@ApiOperation(value = "비디오 페이지의 메인 템플릿을 반환",response = String.class)
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
    	super.setSubMenu(model, "/video", member, request);
    	
    	// top 5 인기 영상
    	Video video = videoService.getTop1();
    	model.addAttribute("topVideo", video);
    	if (video != null) {
    		// 아티스트의 top 5 음원 
    		List<MusicTrack> tracks = trackService.findTop5ByArtistIdWithHeartByMember(video.getMember().getId(), member != null ? member.getId(): 0);
    		model.addAttribute("tracks", tracks);
    	}
    	model.addAttribute("top5GVideos", videoService.findTop5GV());
    	model.addAttribute("top5MVideos", videoService.findTop5MV());
    	
    	model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/video/index_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/video/index_mobile";
		}
    	return "fragments/page/video/index";
    }
    
    @ApiOperation(value = "비디오 상세페이지의 템플릿을 반환",response = String.class)
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
    	Video video = videoService.getVideoById(id);
    	if (video == null) return "fragments/page/error/error-404";
    	
    	Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/video", member, request);
    	super.setMetaTag(model, video.getMetaTag(UPMusicHelper.baseUrl(request)));
    	model.addAttribute("video", video);
    	model.addAttribute("videoComments", videoService.getComments(id, super.getCommentsOrderByNew(0)));
    	model.addAttribute("paginationUrl", "/component/video/" + id + "/comments");
    	model.addAttribute("owner", video.getMember());
    	model.addAttribute("shareUrl", video.getAbsoluteUrl(UPMusicHelper.baseUrl(request)));
    	if (member != null) {
    		model.addAttribute("currentUserId", member.getId());
        	model.addAttribute("isOwner", 0 == (video.getMember().getId().compareTo(member.getId())));
        	model.addAttribute("likedVideo", videoService.likedVideo(id, member.getId()));
    	} else {
    		model.addAttribute("currentUserId", 0);
    		model.addAttribute("isOwner", false);
    		model.addAttribute("likedVideo", false);
    	}
    	
    	// 쿠키 - 조회수 증가
		boolean existsCookie = false;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(UPMusicConstants.COOKIE_VIDEO + id)) existsCookie = true;
			}
		}

        if (!existsCookie) {
        	videoService.increaseHitCnt(id);
    		Cookie cookie = new Cookie(UPMusicConstants.COOKIE_VIDEO + id, "hit");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
        }
        
        String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/video/detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/video/detail_mobile";
		}
    	return "fragments/page/video/detail";
    }
    
    @ApiOperation(value = "영상 편집 템플릿을 반환", response = String.class)
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Principal principal, Model model, HttpServletRequest request) {
    	logger.debug("edit : id is {}", id);
    	Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/video", member, request);
    	Video video = videoService.getVideoById(id);
    	if (member != null && 0 == (video.getMember().getId().compareTo(member.getId()))) {
    		model.addAttribute("videoform", video);
        	model.addAttribute("videoTypeMap", super.getVideoTypeMap());
        	return "fragments/page/video/edit";
    	}
    	return "redirect:/video/" + id;
    }
    
    @ApiOperation(value = "영상 정보를 업데이트",response = String.class)
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("videoform") Video videoform, BindingResult bindingResult, Principal principal, HttpServletRequest request, Model model) throws MalformedURLException, IOException {
    	logger.debug("detail : id is {}", id);
        Member member = super.getCurrentUser(principal);
        Video video = videoService.getVideoById(id);
        if (member != null && 0 == (video.getMember().getId().compareTo(member.getId()))) {
        	videoValidator.validate(videoform, bindingResult);
            if (bindingResult.hasErrors()) {
            	model.addAttribute("videoTypeMap", super.getVideoTypeMap());
            	model.addAttribute("genreMap", super.getGenreMap());
            	return "fragments/page/video/edit";
            }
            video.setVideoType(videoform.getVideoType());
			video.setSubject(videoform.getSubject());
			video.setDescription(videoform.getDescription());
			video.setVideoService(videoform.getVideoService());
			video.setVideoServiceObjectId(videoform.getVideoServiceObjectId());
			video.setThumbnail(videoform.getThumbnail());
        	video = videoService.saveVideo(video);
        	super.setSubMenu(model, "/video", member, request);
        	model.addAttribute("video", video);
        	model.addAttribute("videoComments", videoService.getComments(id, super.getCommentsOrderByNew(0)));
        	model.addAttribute("paginationUrl", "/component/video/" + id + "/comments");
        	model.addAttribute("owner", video.getMember());
        	model.addAttribute("shareUrl", video.getAbsoluteUrl(UPMusicHelper.baseUrl(request)));
    		model.addAttribute("currentUserId", member.getId());
        	model.addAttribute("isOwner", 0 == (video.getMember().getId().compareTo(member.getId())));
        	model.addAttribute("likedVideo", videoService.likedVideo(id, member.getId()));
        	return "fragments/page/video/detail";
        }
        return "redirect:/auth/login";
    }
    
    @ApiOperation(value = "인기영상 템플릿을 반환", response = String.class)
    @GetMapping("/hot")
    public String hot(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/video/hot", super.getCurrentUser(principal), request);
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	if (10 > page) {
    		Page<Video> videos = videoService.findTop100(super.getPageVideoOrderByHot(page));
        	model.addAttribute("videos", videos);
        	int pages = videos.getTotalPages();
        	if (10 < pages) pages = 10;
        	model.addAttribute("totalPages", pages);
    	}
    	model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/video/hot_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/video/hot_mobile";
		}
    	if (0 < page) return "fragments/page/video/hot_page";
        return "fragments/page/video/hot";
    }
    
    @ApiOperation(value = "뮤직비디오 템플릿을 반환", response = String.class)
    @GetMapping("/mv")
    public String mv(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/video/mv", super.getCurrentUser(principal), request);
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Page<Video> videos = videoService.findAllMV(super.getPageVideoOrderByNew(page));
    	model.addAttribute("videos", videos);
    	int pages = videos.getTotalPages();
    	if (10 < pages) pages = 10;
    	model.addAttribute("totalPages", pages);
    	model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/video/mv_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/video/mv_mobile";
		}
        return "fragments/page/video/mv";
    }
    
    @ApiOperation(value = "일반 영상 템플릿을 반환", response = String.class)
    @GetMapping("/gv")
    public String gv(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/video/gv", super.getCurrentUser(principal), request);
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Page<Video> videos = videoService.findAllGV(super.getPageVideoOrderByNew(page));
    	model.addAttribute("videos", videos);
    	int pages = videos.getTotalPages();
    	if (10 < pages) pages = 10;
    	model.addAttribute("totalPages", pages);
    	model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/video/gv_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/video/gv_mobile";
		}
        return "fragments/page/video/gv";
    }

}

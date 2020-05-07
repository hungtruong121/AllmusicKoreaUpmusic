package com.upmusic.web.controllers.page;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.StringUtils;
import com.upmusic.web.services.MusicAlbumService;
import com.upmusic.web.services.MusicRequestService;
import com.upmusic.web.services.VideoService;
import com.upmusic.web.services.VocalCastingService;

import io.swagger.annotations.Api;


@Controller
@RequestMapping("/comment")
@Api(value="comment", description="모바일 댓글 페이지를 담당하는 컨트롤러")
public class PageCommentController extends PageAbstractController {
	
	@Autowired
    private MusicAlbumService albumService;
	
	@Autowired
    private MusicRequestService requestService;
	
	@Autowired
    private VideoService videoService;
	
	@Autowired
    private VocalCastingService castingService;
	
	
	/**
	 * 댓글 작성
	 */
	@GetMapping
	public String newComment(Principal principal, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/comment", super.getCurrentUser(principal), request);
		String apiUrl = request.getParameter("api_url");
		String origUrl = request.getParameter("orig_url");
		if (!StringUtils.isEmpty(apiUrl)) {
			model.addAttribute("apiUrl", apiUrl);
		}
		if (!StringUtils.isEmpty(origUrl)) {
			model.addAttribute("origUrl", origUrl);
		}
        
        String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
		}
        
		return "fragments/page/comment/create_mobile";
	}
	
	/**
	 * 댓글 수정
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@GetMapping("/{id}")
	public String editComment(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) throws IOException, ClassNotFoundException {
		super.setSubMenu(model, "/comment", super.getCurrentUser(principal), request);
		String apiUrl = request.getParameter("api_url");
		String origUrl = request.getParameter("orig_url");
		if (!StringUtils.isEmpty(apiUrl)) {
			model.addAttribute("apiUrl", apiUrl);
			if (apiUrl.contains("casting")) {
				model.addAttribute("comment", castingService.getCommentById(id));
			} else if (apiUrl.contains("video")) {
				model.addAttribute("comment", videoService.getCommentById(id));
			} else if (apiUrl.contains("request")) {
				model.addAttribute("comment", requestService.getCommentById(id));
			} else if (apiUrl.contains("album")) {
				model.addAttribute("comment", albumService.getCommentById(id));
			}
		}
		if (!StringUtils.isEmpty(origUrl)) {
			model.addAttribute("origUrl", origUrl);
		}
        
        String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
		}
        
		return "fragments/page/comment/edit_mobile";
	}
    
}

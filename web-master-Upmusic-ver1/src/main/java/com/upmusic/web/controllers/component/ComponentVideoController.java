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

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.Video;
import com.upmusic.web.services.VideoService;


@Controller
@RequestMapping("/component/video")
@Api(value="component.video", description="비디오 컴포넌트를 담당하는 컨트롤러")
public class ComponentVideoController extends ComponentAbstractController {

	@Autowired
    private VideoService videoService;
	
	
	@ApiOperation(value = "인기영상 템플릿을 반환", response = String.class)
    @GetMapping("/hot")
    public String hot(Principal principal, Model model, HttpServletRequest request) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	if (10 > page) {
    		Page<Video> videos = videoService.findTop100(super.getPageVideoOrderByHot(page));
        	model.addAttribute("videos", videos);
    	}
    	model.addAttribute("pageNo", page);
        return "fragments/component/video/item_mobile";
    }
    
    @ApiOperation(value = "뮤직비디오 템플릿을 반환", response = String.class)
    @GetMapping("/mv")
    public String mv(Principal principal, Model model, HttpServletRequest request) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Page<Video> videos = videoService.findAllMV(super.getPageVideoOrderByNew(page));
    	model.addAttribute("videos", videos);
    	model.addAttribute("pageNo", page);
        return "fragments/component/video/item_mobile";
    }
    
    @ApiOperation(value = "일반 영상 템플릿을 반환", response = String.class)
    @GetMapping("/gv")
    public String gv(Principal principal, Model model, HttpServletRequest request) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Page<Video> videos = videoService.findAllGV(super.getPageVideoOrderByNew(page));
    	model.addAttribute("videos", videos);
    	model.addAttribute("pageNo", page);
        return "fragments/component/video/item_mobile";
    }
    
    @ApiOperation(value = "비디오의 댓글 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/{id}/comments")
    public String comments(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	model.addAttribute("comments", videoService.getComments(id, super.getCommentsOrderByNew(page)));
    	Member member = super.getCurrentUser(principal);
		model.addAttribute("currentUserId", member != null ? member.getId() : 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/video/comment_list_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/video/comment_list_mobile";
		}
        return "fragments/component/video/comment_list";
    }
    
}

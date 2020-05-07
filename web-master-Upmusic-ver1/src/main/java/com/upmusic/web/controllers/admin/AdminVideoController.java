package com.upmusic.web.controllers.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicAlbum;
import com.upmusic.web.domain.MusicTrack;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.domain.Video;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.RecommendedMediaService;
import com.upmusic.web.services.VideoService;

import java.security.Principal;


@Controller
@RequestMapping("/admin/video")
@Api(value = "admin.video", description = "관리자 영상 페이지를 담당하는 컨트롤러")
public class AdminVideoController extends AdminAbstractController {

	@Autowired
	private VideoService videoService;
	
	@Autowired
	private RecommendedMediaService recommendedMediaService;
	

	// --------------------------------------------------------------------------------------------
	// 전체 영상

	@ApiOperation(value = "관리자 영상 페이지의 메인 템플릿을 반환", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"), @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@GetMapping()
	public String music(HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/video");
		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		model.addAttribute("videos", videoService.findAll(super.getPageMusicOrderByNew(page)));
		
		return "fragments/admin/page/video/index";
	}
	
	@ApiOperation(value = "트랙 상세페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/{id}")
	public String trackDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/video");
		Video video = videoService.getVideoById(id);
		model.addAttribute("video", video);
		model.addAttribute("recommended", recommendedMediaService.recommendedVideo(id));
		
		return "fragments/admin/page/video/video_detail";
	}
	
	@ApiOperation(value = "id에 해당하는 영상을 추천하거나 취소",response = ResponseEntity.class)
    @RequestMapping(value = "/{id}/recommend", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> recomendVideo(@PathVariable Long id, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	if (recommendedMediaService.recommendVideo(id)) {
    		response = new APIResponse(true, "true", id);
    	} else {
    		response = new APIResponse(true, "false", id);
    	}
		return ResponseEntity.ok(response);
    }

	// --------------------------------------------------------------------------------------------
	// 영상 삭제
	@ApiOperation(value = "id에 해당하는 영상을 삭제", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}")
	@RequestMapping(value = "/delete_videos", method = {RequestMethod.POST,RequestMethod.GET})
	public ResponseEntity<APIResponse> deleteTracks(@RequestBody String params, Principal principal, Model model) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		Long lastVideoId = 0L;
		for (JsonElement trackIdElement : trackIds) {
			lastVideoId = trackIdElement.getAsLong();
			videoService.deleteVideo(lastVideoId);
		}
		response = new APIResponse(true, "true", lastVideoId);
		return ResponseEntity.ok(response);
	}
	
}

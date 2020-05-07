package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.VideoService;


@RestController
@RequestMapping("/api/my_playlist")
@Api(value="api.my_playlist", description="재생목록과 관련된 API 컨트롤러")
public class APIMyPlaylistController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
    private VideoService videoService;
	
    
    @ApiOperation(value = "id에 해당하는 곡을 재생목록에서 제거", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/remove_tracks", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> removeTracksFromPlaylist(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("removeTracksFromPlaylist : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			for (JsonElement trackIdElement : trackIds) {
				if (!StringUtils.isEmpty(trackIdElement.getAsString())) trackService.removeTrackFromPlaylist(trackIdElement.getAsLong(), member);
    		}
    		response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 영상을 재생목록에 추가", response = ResponseEntity.class, notes="params의 예 {\"id\":123456}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/add_video", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addVideoToPlaylist(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("addVideoToPlaylist : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			videoService.addVideoToPlaylist(element.getAsJsonObject().get("id").getAsLong(), member);
    		response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 영상을 재생목록에서 제거", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/remove_videos", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> removeVideosFromPlaylist(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("removeVideosFromPlaylist : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray videoIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			for (JsonElement videoIdElement : videoIds) {
				if (!StringUtils.isEmpty(videoIdElement.getAsString())) videoService.removeVideoFromPlaylist(videoIdElement.getAsLong(), member);
    		}
    		response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }

}

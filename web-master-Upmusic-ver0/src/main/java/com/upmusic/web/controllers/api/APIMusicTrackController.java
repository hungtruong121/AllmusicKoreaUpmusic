package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicAlbum;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.MusicAlbumService;
import com.upmusic.web.services.MusicTrackService;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/track")
@Api(value="api.track", description="곡과 관련된 API 컨트롤러")
public class APIMusicTrackController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
    private MusicAlbumService albumService;
	

    @ApiOperation(value = "전체 곡을 반환",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @RequestMapping(method= RequestMethod.GET, produces = "application/json")
    public Iterable<MusicTrack> list(Model model){
        Iterable<MusicTrack> trackList = trackService.listAllTracks();
        return trackList;
    }
    
    @ApiOperation(value = "곡명 중복검사", notes="params의 예 {\"subject\":\"my way\"}")
    @RequestMapping(value = "/check_subject", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> checkSubject(@RequestBody String params) {
		logger.debug("checkSubject called: params is {}", params);
		APIResponse response;
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String subject = element.getAsJsonObject().get("subject").getAsString();
		logger.debug("checkSubject called: subject is {}", subject);
		if (trackService.findBySubejct(subject) == null) {
        	response = new APIResponse(true, "true", null);
        } else {
        	response = new APIResponse(true, "false", null);
        }
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 곡을 삭제", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/delete_tracks", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteTracks(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("deleteTracks : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			Long lastTrackId = 0L;
			for (JsonElement trackIdElement : trackIds) {
				if (!StringUtils.isEmpty(trackIdElement.getAsString())) {
					lastTrackId = trackIdElement.getAsLong();
					MusicTrack  track = trackService.getTrackById(lastTrackId);
					if (track != null) {
						MusicAlbum album = track.getMusicAlbum();
						trackService.deleteTrack(lastTrackId, member.getId());
						album.removeTrack(track);						
						if (0 == album.getTracks().size()) albumService.deleteAlbum(album.getId());
					}
				}
    		}
    		response = new APIResponse(true, "true", lastTrackId);
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 곡을 삭제한 후 곡의 앨범도 삭제", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/delete_tracks_with_album", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteTracksWithAlbum(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("deleteTracks : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			Long lastTrackId = 0L;
			for (JsonElement trackIdElement : trackIds) {
				if (!StringUtils.isEmpty(trackIdElement.getAsString())) {
					lastTrackId = trackIdElement.getAsLong();
					// for문 안이지만 마지막 곡의 경우에만 호출되므로 상관 없음
					MusicTrack  track = trackService.getTrackById(lastTrackId);
					if (track != null) {
						Long albumId = track.getMusicAlbum().getId();
						trackService.deleteTrack(lastTrackId, member.getId());
						albumService.deleteAlbum(albumId);
					}
				}
    		}
    		response = new APIResponse(true, "true", lastTrackId);
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 곡을 반환",response = MusicTrack.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public MusicTrack showTrack(@PathVariable Long id, @RequestParam(required = false, value = "params") String params, Principal principal, Model model){
    	MusicTrack track = trackService.getTrackById(id);
    	if (track != null) {
    		JsonParser parser = new JsonParser();
        	JsonElement element = null;
        	if (params != null) element = parser.parse(params);
    		Member member = super.getCurrentUser(principal);
    		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
    		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
        	if (member != null) track.setLiked(trackService.likedTrack(id, member.getId()));
    	}
    	return track;
    }
    
    @ApiOperation(value = "id에 해당하는 곡에 좋아요 또는 취소",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/like", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> likeMusicTrack(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("likeMusicTrack : userId is {}", member.getId());
        	if (trackService.likedTrack(id, member.getId())) {
        		trackService.decreaseHeartCnt(id, member);
        		response = new APIResponse(true, "false", id);
        	} else {
        		trackService.increaseHeartCnt(id, member);
        		response = new APIResponse(true, "true", id);
        	}
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 곡을 타이틀곡으로 지정",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/title", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> titleMusicTrack(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("titleMusicTrack : userId is {}", member.getId());
    		String message = trackService.setTitleTrack(id, member.getId());
    		response = new APIResponse(true, message, id);
    	}
		return ResponseEntity.ok(response);
    }

	// --------------------------------------------------------------------------------------------
	// Excel 파일을 이용한 음원 업로드 처리
	@RequestMapping(value = "/excelTrackUpload", method=RequestMethod.POST, produces = "application/json")
	public String uploadTrackByExcel(MultipartFile musicFile, MultipartFile trackFile) {

    	return "fragments/admin/page/music/excelTrack";
	}


}

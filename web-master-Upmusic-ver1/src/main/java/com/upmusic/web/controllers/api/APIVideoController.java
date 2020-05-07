package com.upmusic.web.controllers.api;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.helper.customClass.CustomMultipartFile;
import com.upmusic.web.services.AzureStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.Video;
import com.upmusic.web.domain.VideoComment;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.GenreService;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.VideoService;
import com.upmusic.web.validator.VideoValidator;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/video")
@Api(value="api.video", description="영상과 관련된 API 컨트롤러")
public class APIVideoController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private VideoService videoService;
	
	@Autowired
    private MemberService memberService;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
    private VideoValidator videoValidator;
	
	@Autowired
	private Gson gson;

	@Autowired
	private AzureStorageService azureStorageService;
	

    @ApiOperation(value = "전체 영상을 반환",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @RequestMapping(method= RequestMethod.GET, produces = "application/json")
    public Iterable<Video> list(Model model){
        Iterable<Video> videoList = videoService.listAllVideos();
        return videoList;
    }
    
    /**
     * 영상 업로드 템플릿을 처리
     * @param videoform
     * @param bindingResult
     * @param principal
     * @param model
     * @return redirect
     * @throws UnsupportedEncodingException 
     */
	@ApiOperation(value = "영상 업로드 템플릿을 처리", response = String.class)
	@RequestMapping(value = "/create", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> createVideo(@ModelAttribute("videoform") Video videoform, BindingResult bindingResult, Principal principal, Model model) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		videoValidator.validate(videoform, bindingResult);
        if (bindingResult.hasErrors()) {
        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
        } else {
        	Member member = super.getCurrentUser(principal);
        	videoform.setMember(member);
        	videoform.setGenre(genreService.findById(1));
        	Video newVideo = videoService.saveVideo(videoform);
            // 회원의 비디오수 증가
            member.setVideoCnt(member.getVideoCnt() + 1);
            memberService.saveMember(member);
            response = new APIResponse(true, "true", newVideo.getId());
        }
        return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 영상을 반환",response = Video.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public Video showVideo(@PathVariable Long id, @RequestParam(required = false, value = "params") String params, Principal principal, Model model){
    	Video video = videoService.getVideoById(id);
    	if (video != null) {
    		JsonParser parser = new JsonParser();
        	JsonElement element = null;
        	if (params != null) element = parser.parse(params);
    		Member member = super.getCurrentUser(principal);
    		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
    		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
        	if (member != null) video.setLiked(videoService.likedVideo(id, member.getId()));
    	}
    	return video;
    }
    
    @ApiOperation(value = "id에 해당하는 영상을 업데이트", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
	@RequestMapping(value = "/{id}", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> updateVideo(@ModelAttribute("videoform") Video videoform, @PathVariable Long id, BindingResult bindingResult, Principal principal, Model model) {
		logger.debug("updateVideo :  videoform is {}", videoform);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		Member member = super.getCurrentUser(principal);
		Video video = videoService.getVideoById(id);
		videoValidator.validate(videoform, bindingResult);
        if (bindingResult.hasErrors()) {
        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
    	} else {
    		if (member != null && video != null && 0 == (member.getId().compareTo(video.getMember().getId()))) {
    			video.setVideoType(videoform.getVideoType());
    			video.setSubject(videoform.getSubject());
    			video.setDescription(videoform.getDescription());
    			video.setVideoService(videoform.getVideoService());
    			video.setVideoServiceObjectId(videoform.getVideoServiceObjectId());
    			video.setThumbnail(videoform.getThumbnail());
    			if (video.getGenre() == null) video.setGenre(genreService.findById(1));
    			video = videoService.saveVideo(video);
        		response = new APIResponse(true, "true", video.getId());
        	}
    	}
    	return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 영상을 삭제", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/delete_videos", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteVideos(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("deleteVideos : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			JsonArray videoIds = element.getAsJsonObject().get("ids").getAsJsonArray();
			logger.debug("deleteVideos : videoIds is {}", videoIds);
			for (JsonElement videoIdElement : videoIds) {
				if (!StringUtils.isEmpty(videoIdElement.getAsString())) videoService.deleteVideo(videoIdElement.getAsLong(), member.getId());
    		}
    		response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 영상을 플레이 리스트에 등록",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/play", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> playVideo(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("playVideo : userId is {}", member.getId());
    		videoService.addVideoToPlaylist(id, member);
    		response = new APIResponse(true, "true", null);
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 영상에 좋아요 또는 취소",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/like", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> likeVideo(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("likeVideo : userId is {}", member.getId());
        	if (videoService.likedVideo(id, member.getId())) {
        		response = new APIResponse(true, "false", videoService.decreaseHeartCnt(id, member));
        	} else {
        		response = new APIResponse(true, "true", videoService.increaseHeartCnt(id, member));
        	}
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 영상에 댓글을 추가",response = VideoComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment", method= RequestMethod.POST, produces = "application/json")
    public VideoComment commentVideo(@PathVariable Long id, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("commentVideo : userId is {}", member.getId());
    		logger.debug("commentVideo : content is {}", content);
    		return videoService.addComment(id, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 반환",response = VideoComment.class)
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.GET, produces = "application/json")
    public VideoComment getCommentVideo(@PathVariable Long id, @PathVariable Long commentId, HttpServletRequest request) {
		return videoService.getCommentById(id, commentId);
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 업데이트",response = VideoComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.POST, produces = "application/json")
    public VideoComment updateCommentVideo(@PathVariable Long id, @PathVariable Long commentId, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		return videoService.updateComment(id, commentId, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 삭제",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteCommentVideo(@PathVariable Long id, @PathVariable Long commentId, @RequestBody(required = false) String params, Principal principal) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		response = new APIResponse(true, "true", videoService.deleteComment(id, commentId, member.getId()));
    	}
    	return ResponseEntity.ok(response);
    }

	@RequestMapping(value = "/excelVideoUpload", method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<APIResponse> excelVideoUpload(@RequestBody String excelForm, BindingResult bindingResult, Principal principal, Model model) {
		logger.debug("video data from excel : {}", excelForm);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		JSONArray array = new JSONArray(excelForm);
		int list_cnt = array.length();
		for(int i = 0; i < list_cnt; i++){
			JSONObject jsonObject = array.getJSONObject(i);
			Video video = new Video();
			String type = "";
			Member member = super.getCurrentUser(principal);
			switch (jsonObject.getInt("type")){
				case 1 : type = "MV"; break;
				case 2 : type = "GV"; break;
			}
			video.setDescription(jsonObject.getString("description"));
			video.setSubject(jsonObject.getString("subject"));
			video.setVideoType(UPMusicConstants.VideoType.valueOf(type));
			video.setVideoService(jsonObject.getString("service"));
			video.setVideoServiceObjectId(jsonObject.getString("object"));
			video.setThumbnail(jsonObject.getString("thumbnail"));
			video.setDuration(jsonObject.getInt("duration"));
			video.setMember(member);
			video.setGenre(genreService.findById(1));
			Video newVideo = videoService.saveVideo(video);
			// 회원의 비디오수 증가
			member.setVideoCnt(member.getVideoCnt() + 1);
			memberService.saveMember(member);
			response = new APIResponse(true, "true", newVideo.getId());
		}

		Map<String, byte[]> map = azureStorageService.getBlob("mjsong","");
		MultipartFile image = new CustomMultipartFile(map.get("image"));
		MultipartFile mp3 = new CustomMultipartFile(map.get("music"));
		MultipartFile zip = new CustomMultipartFile(map.get("track"));

		logger.debug(image.getOriginalFilename());
		logger.debug(mp3.getOriginalFilename());
		logger.debug(zip.getOriginalFilename());

		return ResponseEntity.ok(response);
	}
}

package com.upmusic.web.controllers.api;

import java.io.InputStream;
import java.security.Principal;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicAlbum;
import com.upmusic.web.domain.MusicAlbumComment;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.MusicAlbumService;
import com.upmusic.web.validator.MusicAlbumValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@RequestMapping("/api/album")
@Api(value="api.album", description="앨범과 관련된 API 컨트롤러")
public class APIMusicAlbumController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MusicAlbumService albumService;
	
	@Autowired
	private AzureStorageService azureStorageService;
	
	@Autowired
    private MusicAlbumValidator albumValidator;
	
	@Autowired
	private Gson gson;
	
	
	@ApiOperation(value = "전체 앨범을 반환",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @RequestMapping(method= RequestMethod.GET, produces = "application/json")
    public Iterable<MusicAlbum> list(Model model){
        Iterable<MusicAlbum> albumList = albumService.listAllAlbums();
        return albumList;
    }
	
	@ApiOperation(value = "앨범 업로드 템플릿을 처리", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
	@RequestMapping(value = "/create", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> createMusicAlbum(@ModelAttribute("musicform") MusicAlbum musicform, BindingResult bindingResult, Principal principal, Model model) {
		logger.debug("createMusicAlbum :  musicform is {}", musicform);
		int maxSize = 300000;
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		if (musicform.getCoverImageMultipart() == null || StringUtils.isEmpty(musicform.getCoverImageMultipart().getOriginalFilename())) {
			musicform.setImageFilename(null);
		}


		albumValidator.validate(musicform, bindingResult);
        if (bindingResult.hasErrors()) {
        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
    	} else {
    		Member member = super.getCurrentUser(principal);
        	musicform.setMember(member);
        	musicform.setImageFilename(UPMusicHelper.makeReadableUrl(musicform.getImageFilename()));
        	MusicAlbum newAlbum = albumService.saveAlbum(musicform);
        	if(musicform.getCoverImageMultipart().getSize() >= maxSize){
        		InputStream is = AzureHelper.scale(musicform.getCoverImageMultipart(), 0.18, 0.18);
				azureStorageService.uploadResource(is, musicform.getCoverImageMultipart().getOriginalFilename(), "albums/" + newAlbum.getId() + "/");
            } else {
                azureStorageService.uploadResource(musicform.getCoverImageMultipart(), "albums/" + newAlbum.getId() + "/");
            }
            response = new APIResponse(true, "true", newAlbum.getId());
    	}
    	return ResponseEntity.ok(response);
    }
	
    @ApiOperation(value = "id에 해당하는 앨범을 반환",response = MusicAlbum.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public MusicAlbum showMusicAlbum(@PathVariable Long id, @RequestParam(required = false, value = "params") String params, Principal principal, Model model) {
    	MusicAlbum album = albumService.getAlbumById(id);
    	if (album != null) {
    		JsonParser parser = new JsonParser();
        	JsonElement element = null;
        	if (params != null) element = parser.parse(params);
    		Member member = super.getCurrentUser(principal);
    		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
    		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
        	if (member != null) album.setLiked(albumService.likedAlbum(id, member.getId()));
    	}
    	return album;
    }
    
    @ApiOperation(value = "id에 해당하는 앨범을 업데이트", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
	@RequestMapping(value = "/{id}", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> updateMusicAlbum(@ModelAttribute("musicform") MusicAlbum musicform, @PathVariable Long id, @RequestParam(required = false, value = "params") String params, BindingResult bindingResult, Principal principal, Model model) {
		logger.debug("updateMusicAlbum :  musicform is {}", musicform);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		albumValidator.validate(musicform, bindingResult);
        if (bindingResult.hasErrors()) {
        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
    	} else {
    		MusicAlbum album = albumService.getAlbumById(id);
    		JsonParser parser = new JsonParser();
        	JsonElement element = null;
        	if (params != null) element = parser.parse(params);
    		Member member = super.getCurrentUser(principal);
    		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
    		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    		if (member != null && album != null && 0 == (member.getId().compareTo(album.getMember().getId()))) {
        		album.setSubject(musicform.getSubject());
        		album.setDescription(musicform.getDescription());
        		album.setGenre(musicform.getGenre());
            	album.setImageFilename(UPMusicHelper.makeReadableUrl(musicform.getImageFilename()));
            	album = albumService.saveAlbum(album);
            	if (musicform.getCoverImageMultipart() != null && !StringUtils.isEmpty(musicform.getCoverImageMultipart().getOriginalFilename())) azureStorageService.uploadResource(musicform.getCoverImageMultipart(), "albums/" + album.getId() + "/");
        		response = new APIResponse(true, "true", album.getId());
        	}
    	}
    	return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 앨범의 등록완료", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/complete", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> completeMusicAlbum(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		MusicAlbum album = albumService.getAlbumById(id);
    	if (member != null && album != null && 0 == (member.getId().compareTo(album.getMember().getId()))) {
        	if (albumService.completeAlbum(id)) {
        		response = new APIResponse(true, "true", null);
        	} else {
        		response = new APIResponse(true, "false", null);
        	}
    	}
		return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "id에 해당하는 앨범에 좋아요 또는 취소", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/like", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> likeMusicAlbum(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("likeMusicAlbum : userId is {}", member.getId());
        	if (albumService.likedAlbum(id, member.getId())) {
        		response = new APIResponse(true, "false", albumService.decreaseHeartCnt(id, member));
        	} else {
        		response = new APIResponse(true, "true", albumService.increaseHeartCnt(id, member));
        	}
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 앨범에 좋아요 또는 취소",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/like_from_list", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> likeMusicAlbumFromList(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("likeMusicAlbum : userId is {}", member.getId());
        	if (albumService.likedAlbum(id, member.getId())) {
        		albumService.decreaseHeartCnt(id, member);
        		response = new APIResponse(true, "false", id);
        	} else {
        		albumService.increaseHeartCnt(id, member);
        		response = new APIResponse(true, "true", id);
        	}
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 앨범에 댓글을 추가",response = MusicAlbumComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment", method= RequestMethod.POST, produces = "application/json")
    public MusicAlbumComment commentMusicAlbum(@PathVariable Long id, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		return albumService.addComment(id, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 반환",response = MusicAlbumComment.class)
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.GET, produces = "application/json")
    public MusicAlbumComment getCommentVideo(@PathVariable Long id, @PathVariable Long commentId, HttpServletRequest request) {
		return albumService.getCommentById(id, commentId);
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 업데이트",response = MusicAlbumComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.POST, produces = "application/json")
    public MusicAlbumComment updateCommentMusicAlbum(@PathVariable Long id, @PathVariable Long commentId, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		return albumService.updateComment(id, commentId, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 삭제",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteCommentMusicAlbum(@PathVariable Long id, @PathVariable Long commentId, @RequestBody(required = false) String params, Principal principal) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		response = new APIResponse(true, "true", albumService.deleteComment(id, commentId, member.getId()));
    	}
    	return ResponseEntity.ok(response);
    }

	@RequestMapping(value = "/excelUpload", method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody void excelUpload(@RequestBody String excelForm) {
		logger.debug("info is : {}", excelForm);
		JSONArray array = new JSONArray(excelForm);
		int list_cnt = array.length();

		String[] albumType = new String[list_cnt];
		String[] albumGenreId = new String[list_cnt];
		String[] albumSubject = new String[list_cnt];
		String[] imageFilename = new String[list_cnt];
		String[] description = new String[list_cnt];
		String[] filename = new String[list_cnt];
		String[] extraSource = new String[list_cnt];
		String[] trackSubject = new String[list_cnt];
		String[] trackType = new String[list_cnt];
		String[] trackGenreId = new String[list_cnt];
		String[] themeId = new String[list_cnt];
		String[] rentalFee = new String[list_cnt];
		String[] price = new String[list_cnt];
		String[] inType = new String[list_cnt];

		for (int i = 0; i < list_cnt; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			albumType[i] = jsonObject.getString("albumType");
			albumGenreId[i] = jsonObject.getString("albumGenreId");
			albumSubject[i] = jsonObject.getString("albumSubject");
			imageFilename[i] = jsonObject.getString("imageFilename");
			description[i] = jsonObject.getString("description");
			filename[i] = jsonObject.getString("filename");
			extraSource[i] = jsonObject.getString("extraSource");
			trackSubject[i] = jsonObject.getString("trackSubject");
			trackType[i] = jsonObject.getString("trackType");
			trackGenreId[i] = jsonObject.getString("trackGenreId");
			themeId[i] = jsonObject.getString("themeId");
			rentalFee[i] = jsonObject.getString("rentalFee");
			price[i] = jsonObject.getString("price");
			inType[i] = jsonObject.getString("inType");
		}
	}
}

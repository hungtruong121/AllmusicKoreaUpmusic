package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicRequest;
import com.upmusic.web.domain.MusicRequestComment;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.MusicRequestService;


@RestController
@RequestMapping("/api/store/request")
@Api(value="api.store.request", description="제작의뢰와 관련된 API 컨트롤러")
public class APIStoreRequestController extends APIAbstractController {

	@Autowired
    private MusicRequestService requestService;

	
	@ApiOperation(value = "전체 제작의뢰를 반환",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @RequestMapping(method= RequestMethod.GET, produces = "application/json")
    public Iterable<MusicRequest> list(Model model){
        Iterable<MusicRequest> musicRequestList = requestService.listAllRequests();
        return musicRequestList;
    }
    
    @ApiOperation(value = "id에 해당하는 제작의뢰를 반환",response = MusicRequest.class)
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public MusicRequest showMusicRequest(@PathVariable Long id, Model model){
    	MusicRequest musicRequest = requestService.getRequestById(id);
    	return musicRequest;
    }
    
    @ApiOperation(value = "id에 해당하는 제작의뢰를 삭제",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteMusicRequest(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	MusicRequest musicRequest = requestService.getRequestById(id);
    	if (musicRequest != null) {
    		JsonParser parser = new JsonParser();
        	JsonElement element = null;
        	if (params != null) element = parser.parse(params);
    		Member member = super.getCurrentUser(principal);
    		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
    		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    		if (member != null && 0 == (musicRequest.getMember().getId().compareTo(member.getId()))) {
    			requestService.deleteRequest(id);
    			response = new APIResponse(true, "true", null);
    		}
    	}
		return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "id에 해당하는 제작의뢰에 댓글을 추가",response = MusicRequestComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment", method= RequestMethod.POST, produces = "application/json")
    public MusicRequestComment commentMusicRequest(@PathVariable Long id, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		return requestService.addComment(id, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 반환",response = MusicRequestComment.class)
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.GET, produces = "application/json")
    public MusicRequestComment getCommentVideo(@PathVariable Long id, @PathVariable Long commentId, HttpServletRequest request) {
		return requestService.getCommentById(id, commentId);
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 업데이트",response = MusicRequestComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.POST, produces = "application/json")
    public MusicRequestComment updateCommentMusicRequest(@PathVariable Long id, @PathVariable Long commentId, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		return requestService.updateComment(id, commentId, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 삭제",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteCommentMusicRequest(@PathVariable Long id, @PathVariable Long commentId, @RequestBody(required = false) String params, Principal principal) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		response = new APIResponse(true, "true", requestService.deleteComment(id, commentId, member.getId()));
    	}
    	return ResponseEntity.ok(response);
    }

}

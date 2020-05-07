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
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.VocalCasting;
import com.upmusic.web.domain.VocalCastingComment;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.VocalCastingService;


@RestController
@RequestMapping("/api/vocal_casting")
@Api(value="api.vocal_casting", description="보컬 캐스팅과 관련된 API 컨트롤러")
public class APIVocalCastingController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private VocalCastingService castingService;

	@ApiOperation(value = "전체 보컬 캐스팅을 반환",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @RequestMapping(method= RequestMethod.GET, produces = "application/json")
    public Iterable<VocalCasting> list(Model model){
        Iterable<VocalCasting> castingList = castingService.listAllVocalCastings();
        return castingList;
    }
    
    @ApiOperation(value = "id에 해당하는 보컬 캐스팅을 반환",response = VocalCasting.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public VocalCasting showVocalCasting(@PathVariable Long id, @RequestParam(required = false, value = "params") String params, Principal principal, Model model){
    	VocalCasting casting = castingService.getVocalCastingById(id);
    	if (casting != null) {
    		JsonParser parser = new JsonParser();
        	JsonElement element = null;
        	if (params != null) element = parser.parse(params);
    		Member member = super.getCurrentUser(principal);
    		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
    		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
        	if (member != null) casting.setLiked(castingService.likedVocalCasting(id, member.getId()));
    	}
    	return casting;
    }
    
    @ApiOperation(value = "id에 해당하는 보컬 캐스팅을 삭제",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteMusicRequest(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	VocalCasting musicRequest = castingService.getVocalCastingById(id);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null && 0 == (musicRequest.getMember().getId().compareTo(member.getId()))) {
			castingService.deleteVocalCasting(id);
			response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "id에 해당하는 보컬 캐스팅에 좋아요 또는 취소",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/like", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> likeVocalCasting(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("likeVocalCasting : userId is {}", member.getId());
        	if (castingService.likedVocalCasting(id, member.getId())) {
        		response = new APIResponse(true, "false", castingService.decreaseHeartCnt(id, member));
        	} else {
        		response = new APIResponse(true, "true", castingService.increaseHeartCnt(id, member));
        	}
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 보컬 캐스팅에 댓글을 추가",response = VocalCastingComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment", method= RequestMethod.POST, produces = "application/json")
    public VocalCastingComment commentVocalCasting(@PathVariable Long id, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("commentVocalCasting : userId is {}", member.getId());
    		logger.debug("commentVocalCasting : content is {}", content);
    		return castingService.addComment(id, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 반환",response = VocalCastingComment.class)
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.GET, produces = "application/json")
    public VocalCastingComment getCommentVideo(@PathVariable Long id, @PathVariable Long commentId, HttpServletRequest request) {
		return castingService.getCommentById(id, commentId);
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 업데이트",response = VocalCastingComment.class, notes="params의 예 {\"content\":\"댓글 내용\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.POST, produces = "application/json")
    public VocalCastingComment updateCommentVocalCasting(@PathVariable Long id, @PathVariable Long commentId, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String content = element.getAsJsonObject().get("content").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		return castingService.updateComment(id, commentId, member, content, request.getRemoteAddr());
    	}
		return null;
    }
    
    @ApiOperation(value = "commentId에 해당하는 댓글을 삭제",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/comment/{commentId}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteCommentVocalCasting(@PathVariable Long id, @PathVariable Long commentId, @RequestBody(required = false) String params, Principal principal) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		response = new APIResponse(true, "true", castingService.deleteComment(id, commentId, member.getId()));
    	}
    	return ResponseEntity.ok(response);
    }

}

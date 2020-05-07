package com.upmusic.web.controllers.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;



@RestController
@RequestMapping("/api/search")
@Api(value="api.search", description="검색과 관련된 API 컨트롤러")
public class APISearchController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    private SearchService searchService;

    @ApiOperation(value = "id에 해당하는 검색내역 삭제", response = ResponseEntity.class)
    @RequestMapping(value = "/{id}/remove", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteSearchKeyword(@PathVariable Long id, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = null;
    	Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		searchService.deleteById(id);
			response = new APIResponse(true, "true", null);
    	}
		return ResponseEntity.ok(response);
    }

	@ApiOperation(value = "전체 검색내역 삭제", response = ResponseEntity.class)
	@RequestMapping(value = "/remove", method= RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<APIResponse> deleteAllSearchKeyword(Principal principal, HttpServletRequest request) throws Exception {
    	logger.debug("deleteAllSearchKeyword start");

		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = null;
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			searchService.deleteByMemberId(member.getId());
			response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
	}

    
}

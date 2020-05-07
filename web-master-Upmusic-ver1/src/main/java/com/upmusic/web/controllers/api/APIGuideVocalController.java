package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.GuideVocal;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.GuideVocalService;


@RestController
@RequestMapping("/api/guide_vocal")
@Api(value="api.guide_vocal", description="가이드보컬과 관련된 API 컨트롤러")
public class APIGuideVocalController extends APIAbstractController {

	@Autowired
	private GuideVocalService guideVocalService;
	
	@Autowired
    private AzureStorageService azureStorageService;
	
    
    @ApiOperation(value = "id에 해당하는 곡을 다운로드",response = MusicTrack.class)
    @RequestMapping(value = "/download/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<byte[]> download(@PathVariable Long id, Principal principal) throws IOException{
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
    		GuideVocal guide = guideVocalService.getGuideVocalById(id);
        	if (guide != null) {
				String filename = guide.getFilename();
				return azureStorageService.downloadVocalGuideFile("guides/" + guide.getId() + "/", filename);
        	}
    	}
    	return null;
    }
    
    @ApiOperation(value = "id에 해당하는 가이드보컬을 삭제",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteMusicRequest(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	GuideVocal guide = guideVocalService.getGuideVocalById(id);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null && 0 == (guide.getMember().getId().compareTo(member.getId()))) {
			guideVocalService.deleteGuideVocal(id);
			response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }
    
}

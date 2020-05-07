package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.util.ArrayList;

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
import com.upmusic.web.domain.Collection;
import com.upmusic.web.domain.Member;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.CollectionService;


@RestController
@RequestMapping("/api/collection")
@Api(value="api.collection", description="내가 담은 리스트와 관련된 API 컨트롤러")
public class APICollectionController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private CollectionService collectionService;
	

	@ApiOperation(value = "전체 내가 담은 리스트를 반환",response = Iterable.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @RequestMapping(method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<APIResponse> list(@RequestParam(required = false, value = "params") String params, Principal principal, Model model) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), new ArrayList<>());
		JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
        if (member != null) response = new APIResponse(true, "true", collectionService.listAllCollectionsByMemberId(member.getId()));
        return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "리스트를 생성", response = Collection.class, notes="params의 예 {\"subject\":\"내가 담은 리스트 제목\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/create", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> createCollection(@RequestBody String params, Principal principal, HttpServletRequest request) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), new ArrayList<>());
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String subject = element.getAsJsonObject().get("subject").getAsString();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("createCollection : userId is {}", member.getId());
    		logger.debug("createCollection : subject is {}", subject);
    		Collection newCollection = new Collection();
    		newCollection.setMember(member);
    		newCollection.setSubject(subject);
    		response = new APIResponse(true, "true", collectionService.saveCollection(newCollection));
    	}
    	return ResponseEntity.ok(response);
    }

	@ApiOperation(value = "리스트 이름 수정", response = Collection.class, notes="params의 예 {\"subject\":\"내가 담은 리스트 제목\"}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
	@RequestMapping(value = "/modify", method= RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> modifyCollection(@RequestBody String params, Principal principal, HttpServletRequest request) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), new ArrayList<>());
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String subject = element.getAsJsonObject().get("subject").getAsString();
		Long id =  element.getAsJsonObject().get("id").getAsLong();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			logger.debug("modifyCollection : subject is {}", subject);
			logger.debug("modifyCollection : id is {}", id);
			Collection collection = collectionService.getCollectionById(id);
			collection.setSubject(subject);
			response = new APIResponse(true, "true", collectionService.saveCollection(collection));
		}
		return ResponseEntity.ok(response);
	}
    
    @ApiOperation(value = "id에 해당하는 리스트를 반환", response = Collection.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public Collection showCollection(@PathVariable Long id, @RequestParam(required = false, value = "params") String params, Principal principal) {
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			Collection collection = collectionService.getCollectionById(id);
			if (0 == (collection.getMember().getId().compareTo(member.getId()))) return collection;
		}
    	return null;
    }
    
    @ApiOperation(value = "id에 해당하는 리스트를 삭제", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteCollection(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	Collection collection = collectionService.getCollectionById(id);
		JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null && 0 == (collection.getMember().getId().compareTo(member.getId()))) {
			collectionService.deleteCollection(id);
			response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 리스트를 삭제", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/delete", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteCollections(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("deleteCollections : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray ids = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		for (JsonElement idElement : ids) {
    			if (!StringUtils.isEmpty(idElement.getAsString())) {
    				Long id = idElement.getAsLong();
        			Collection collection = collectionService.getCollectionById(id);
        			if (collection != null && 0 == (collection.getMember().getId().compareTo(member.getId()))) collectionService.deleteCollection(id);
    			}
    		}
    		response = new APIResponse(true, "true", null);
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 리스트에 곡을 추가", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/add_tracks", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addCollectionTracks(@PathVariable Long id, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("addCollectionTracks : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	Collection collection = collectionService.getCollectionById(id);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null && 0 == (collection.getMember().getId().compareTo(member.getId()))) {
			boolean overflow = false;
			for (JsonElement trackIdElement : trackIds) {
				if (!StringUtils.isEmpty(trackIdElement.getAsString()) && !collectionService.saveCollectionTrack(collection, trackIdElement.getAsLong())) {
					overflow = true;
					break;
				}
    		}
			if (overflow) {
				response = new APIResponse(true, "리스트에는 최대 200곡까지 등록가능 합니다.", null);
			} else {
				// collectionService.removeOverCountTracks(id);
				response = new APIResponse(true, "true", null);
			}
			collection.setTrackCnt(collectionService.countTracksByCollection(id));
			collectionService.saveCollection(collection);
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 곡을 플레이 리스트에 추가", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/play", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> playTracks(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, HttpServletRequest request) {
    	logger.debug("playTracks : id is {}", id);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	Collection collection = collectionService.getCollectionById(id);
    	if (collection == null || 0 == collection.getTrackCnt()) {
    		response = new APIResponse(true, super.getMessage("common.nothing.to_play"), null);
    	} else {
    		try {
    			JsonParser parser = new JsonParser();
            	JsonElement element = null;
            	if (params != null) element = parser.parse(params);
        		Member member = super.getCurrentUser(principal);
        		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
        		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
        		if (member != null && 0 == (collection.getMember().getId().compareTo(member.getId()))) {
            		if (!collectionService.sendToPlaylist(id, member)) {
    					response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", null);
    				} else {
    					// trackService.removeOverCountTracksOfPlayer(member.getId());
    					response = new APIResponse(true, "true", null);
    				}
        		}
    		} catch (Exception e) {
    			logger.error("playTracks : error occured - {}", e.toString());
    			response = new APIResponse(true, e.toString(), null);
    		}
    	}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 리스트의 곡을 제거", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/remove_tracks", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteCollectionTracks(@PathVariable Long id, @RequestBody String params, Principal principal, HttpServletRequest request) {
    	logger.debug("deleteCollections : params is {}", params);
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	Collection collection = collectionService.getCollectionById(id);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null && 0 == (collection.getMember().getId().compareTo(member.getId()))) {
			for (JsonElement trackIdElement : trackIds) {
				if (!StringUtils.isEmpty(trackIdElement.getAsString())) collectionService.deleteCollectionTrack(id, trackIdElement.getAsLong());
    		}
			collection.setTrackCnt(collectionService.countTracksByCollection(id));
			collectionService.saveCollection(collection);
    		response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
    }

}

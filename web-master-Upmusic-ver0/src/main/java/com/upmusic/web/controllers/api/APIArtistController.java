package com.upmusic.web.controllers.api;

import com.upmusic.web.domain.Role;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.UserService;
import com.upmusic.web.validator.MemberValidator;


@RestController
@RequestMapping("/api/artist")
@Api(value="api.artist", description="아티스트와 관련된 API 컨트롤러")
public class APIArtistController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MemberService memberService;
	
	@Autowired
	private AzureStorageService azureStorageService;
	
	@Autowired
	private MemberValidator memberValidator;
	
	@Autowired
	private Gson gson;

	@Autowired
    private UserService userService;
	
	@Autowired
	private MessageSource messageSource;
	/**
	 * 현재는 전체 회원을 반환
	 * TOOD 목적에 맞게 수정하여 사용
	 * @param model
	 * @return Iterable<Member>
	 */
	@ApiOperation(value = "전체 아티스트 반환",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @RequestMapping(method= RequestMethod.GET, produces = "application/json")
    public Iterable<Member> list(Model model){
        Iterable<Member> memberList = memberService.listAllArtists();
        return memberList;
    }
    
    @ApiOperation(value = "id에 해당하는 회원을 반환",response = Member.class)
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public Member showMember(@PathVariable Long id, Model model){
    	Member member = memberService.getMemberById(id);
    	return member;
    }
    
    @ApiOperation(value = "아티스트 편집페이지 템플릿을 처리", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}", method= RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> artistUpdate(@ModelAttribute("artistform") Member artistform, @PathVariable Long id, @RequestParam(required = false, value = "params") String params, BindingResult bindingResult, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		Set<Role> adminRole =  member.getRoles();
		Iterator<Role> adminIter = adminRole.iterator();
		boolean isAdmin = false;
		while(adminIter.hasNext()){
			if(adminIter.next().getId() == 1){
				isAdmin = true;
			}
		}
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	Member artist = memberService.getMemberById(id);
		if ((artist != null && member != null && 0 == (artist.getId().compareTo(member.getId()))) || (artist != null && isAdmin)) {
			memberValidator.validate(artistform, bindingResult);
			if (bindingResult.hasErrors()) {
	        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
	    	} else {
	    		// 아티스트 저장
				artist.setNick(artistform.getNick());
				artist.setProfileImage(UPMusicHelper.makeReadableUrl(artistform.getProfileImage()));
				if(artistform.getGender() != null) {
					artist.setGender(artistform.getGender());
				}
				if(artistform.getPhoneNumber() != null){
					artist.setPhoneNumber(artistform.getPhoneNumber());
				}
				if(artistform.getBirthday() != null){
					artist.setBirthday(artistform.getBirthday());
				}
				artist = memberService.saveMember(artist);
				if (artistform.getProfileImageMultipart() != null && !StringUtils.isEmpty(artistform.getProfileImageMultipart().getOriginalFilename())) azureStorageService.uploadResource(artistform.getProfileImageMultipart(), "profiles/" + artist.getId() + "/");
	    		response = new APIResponse(true, "true", artist.getId());
	    	}
		}
		return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "id에 해당하는 회원에 좋아요 또는 취소", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가. object에 id와 좋아요 합계 반환")
    @RequestMapping(value = "/{id}/like_from_list", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> likeMemberFromList(@PathVariable Long id, @RequestParam(required = false, value = "params") String params, Principal principal, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	logger.debug("likeMemberFromList : params is {}", params);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("likeMemberFromList : userId is {}", member.getId());
    		if (0 == member.getId().compareTo(id)) {
    			response = new APIResponse(true, super.getMessage("common.yourself"), null);
    		} else {
    			List<Object> obj = new ArrayList<Object>();
	    		obj.add(id);
	        	if (memberService.likedMember(id, member.getId())) {
	        		obj.add(memberService.decreaseHeartCnt(id, member));
	        		response = new APIResponse(true, "false", obj);
	        	} else {
	        		obj.add(memberService.increaseHeartCnt(id, member));
	        		response = new APIResponse(true, "true", obj);
	        	}
    		}
    	}
		return ResponseEntity.ok(response);
    }
    
}

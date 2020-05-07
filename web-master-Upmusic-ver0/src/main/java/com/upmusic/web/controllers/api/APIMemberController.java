package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.domain.Member;
import com.upmusic.web.services.MemberService;


@RestController
@RequestMapping("/api/member")
@Api(value="api.member", description="회원 관련된 API 컨트롤러")
public class APIMemberController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MemberService memberService;
	
	
	@ApiOperation(value = "이메일 계정으로 회원 검색", notes="params의 예 {\"email\":\"tester0@gmail.com\"}")
    @RequestMapping(value = "/search", method= RequestMethod.POST, produces = "application/json")
    public Member search(@RequestBody String params) {
		logger.debug("search called");
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String email = element.getAsJsonObject().get("email").getAsString();
		logger.debug("search called : email is {}", email);
		Member member = memberService.findByEmail(email);
		if (member != null) {
	    	return member;
		}
		return null;
    }

}

package com.upmusic.web.controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.repositories.MemberRepository;

@RestController
@RequestMapping("/api/admin")
public class APIAdminController extends APIAbstractController{
	
	@Autowired
	private MemberRepository memberRepository;
	
	
	/**
	 * 이메일 id 찾기
	 */
	@RequestMapping(value = "/crowd_funding/search_email_id", method=RequestMethod.POST)
	public Map<String, Object> adminSearchEmailId(@ModelAttribute("paramData") Member paramData){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Member> result = memberRepository.findAllByEmail(paramData.getEmail());
		
		for(Member m : result) {
			m.setCreateAtStr(UPMusicHelper.formattedTimeHourMin(m.getCreatedAt()));
			if(m.isCopyrightMembership()) {
				m.setCopyrightMembershipStr("Y");
			}else {
				m.setCopyrightMembershipStr("N");
			}
		}
		
		resultMap.put("result", result);
		return resultMap;
	}
}

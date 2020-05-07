package com.upmusic.web.controllers.page;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.Terms;
import com.upmusic.web.services.TermsService;

import io.swagger.annotations.Api;


@Controller
@RequestMapping("/terms")
@Api(value="terms", description="정책 페이지를 담당하는 컨트롤러")
public class PageTermsController extends PageAbstractController {
	
	@Autowired
	private TermsService termsService;
	
	
	/**
	 * 정책 화면 이동
	 */
	@GetMapping("/{type}")
	public String editTerms(@PathVariable String type, Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) {
		super.setSubMenu(model, "/terms", super.getCurrentUser(principal), request);
		int id = type.equals("use") ? 1 : 2;
		Terms terms = termsService.findById(id);
		model.addAttribute("terms", terms);
		
		// 쿠키 - 조회수 증가
		boolean existsCookie = false;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(UPMusicConstants.COOKIE_TERMS + id)) existsCookie = true;
			}
		}

        if (!existsCookie) {
        	termsService.increaseHitCnt(terms);
    		Cookie cookie = new Cookie(UPMusicConstants.COOKIE_TERMS + id, "hit");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
        }
        
        String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/terms/detail_mobile_content";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/terms/detail_mobile_content";
		}
        
		return "fragments/page/terms/detail";
	}
    
}

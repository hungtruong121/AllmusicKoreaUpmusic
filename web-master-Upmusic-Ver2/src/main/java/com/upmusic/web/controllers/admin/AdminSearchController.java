package com.upmusic.web.controllers.admin;

import java.net.MalformedURLException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.SearchService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Controller
@RequestMapping("/admin/search")
@Api(value="admin.search", description="검색 결과 페이지를 담당하는 컨트롤러")
public class AdminSearchController extends AdminAbstractController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SearchService searchService;

    
	@ApiOperation(value = "검색 결과 템플릿을 반환", response = String.class)
	@GetMapping
    public String searchResult(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("search : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
		model.addAttribute("tracks", searchService.searchTracks(q, member != null ? member.getId() : 0));
		model.addAttribute("videos", searchService.searchVideos(q));
		
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
        return "fragments/admin/page/search/index";
    }
	
}

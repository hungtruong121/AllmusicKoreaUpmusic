package com.upmusic.web.controllers.page;

import java.net.MalformedURLException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.upmusic.web.domain.Search;
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
@RequestMapping("/search")
@Api(value="search", description="검색 결과 페이지를 담당하는 컨트롤러")
public class PageSearchController extends PageAbstractController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SearchService searchService;

    
	@ApiOperation(value = "검색 결과 템플릿을 반환", response = String.class)
	@GetMapping
    public String searchResult(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResult : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
    	model.addAttribute("q", q);
		model.addAttribute("tracks", searchService.searchTop10Tracks(q, member != null ? member.getId() : 0));
		model.addAttribute("tracksAll", searchService.searchTracks(q, member != null ? member.getId() : 0));
		model.addAttribute("albums", searchService.searchTop4Albums(q, member != null ? member.getId() : 0));
		model.addAttribute("albumsAll", searchService.searchAlbums(q, member != null ? member.getId() : 0));
		model.addAttribute("storeTracks", searchService.searchTop10StoreTracks(q, member != null ? member.getId() : 0));
		model.addAttribute("storeTracksAll", searchService.searchStoreTracks(q, member != null ? member.getId() : 0));
		model.addAttribute("artists", searchService.searchTop4Artists(q, member != null ? member.getId() : 0));
		model.addAttribute("artistsAll", searchService.searchArtists(q, member != null ? member.getId() : 0));
		model.addAttribute("castings", searchService.searchTop10Castings(q));
		model.addAttribute("castingsAll", searchService.searchCastings(q));
		model.addAttribute("videos", searchService.searchTop4Videos(q));
		model.addAttribute("videosAll", searchService.searchVideos(q));
		model.addAttribute("bands", searchService.searchTop4BandRecruits(q));
		model.addAttribute("bandsAll", searchService.searchBandRecruits(q));
		
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			//최근 검색어 10개 리스트
			model.addAttribute("searchTop10List", searchService.findTop10ByMemberId(member != null ? member.getId() : 0));
			//인기검색어 10개 리스트
			model.addAttribute("searchTop10ListPopular", searchService.findTop10ByPopularKeyword());

			if (q == null || q.equals("")) {
				return "fragments/page/search/index_mobile";
			} else {
				//검색했을시 검색테이블에 저장
				searchService.saveSearchKeyword(q, member);
				return "fragments/page/search/search_result_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			//최근 검색어 10개 리스트
			model.addAttribute("searchTop10List", searchService.findTop10ByMemberId(member != null ? member.getId() : 0));
			//인기검색어 10개 리스트
			model.addAttribute("searchTop10ListPopular", searchService.findTop10ByPopularKeyword());
			if (q == null || q.equals("")) {
				return "fragments/page/search/index_mobile";
			} else {
				//검색했을시 검색테이블에 저장
				searchService.saveSearchKeyword(q, member);
				return "fragments/page/search/search_result_mobile";
			}
		}
		
        return "fragments/page/search/index";
    }
	
	@ApiOperation(value = "검색 결과 음원리스트 템플릿을 반환", response = String.class)
	@GetMapping("/tracks")
    public String searchResultTracks(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResultTracks : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
		model.addAttribute("tracks", searchService.searchTracks(q, member != null ? member.getId() : 0));
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return "fragments/page/search/detail_contents";
    }
	
	@ApiOperation(value = "검색 결과 앨범리스트 템플릿을 반환", response = String.class)
	@GetMapping("/albums")
    public String searchResultAlbums(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResultAlbums : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
    	model.addAttribute("albums", searchService.searchAlbums(q, member != null ? member.getId() : 0));
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return "fragments/page/search/detail_contents";
    }
	
	@ApiOperation(value = "검색 결과 스토어 음원리스트 템플릿을 반환", response = String.class)
	@GetMapping("/store_tracks")
    public String searchResultStoreTracks(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResultStoreTracks : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
    	model.addAttribute("storeTracks", searchService.searchStoreTracks(q, member != null ? member.getId() : 0));
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return "fragments/page/search/detail_contents";
    }
	
	@ApiOperation(value = "검색 결과 스토어 아티스트 리스트 템플릿을 반환", response = String.class)
	@GetMapping("/artists")
    public String searchResultArtists(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResultArtists : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
    	model.addAttribute("artists", searchService.searchArtists(q, member != null ? member.getId() : 0));
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return "fragments/page/search/detail_contents";
    }
	
	@ApiOperation(value = "검색 결과 스토어 보컬캐스팅 리스트 템플릿을 반환", response = String.class)
	@GetMapping("/castings")
    public String searchResultCastings(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResultCastings : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
    	model.addAttribute("castings", searchService.searchCastings(q));
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return "fragments/page/search/detail_contents";
    }
	
	@ApiOperation(value = "검색 결과 비디오 리스트 템플릿을 반환", response = String.class)
	@GetMapping("/videos")
    public String searchResultVideos(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResultVideos : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
    	model.addAttribute("videos", searchService.searchVideos(q));
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return "fragments/page/search/detail_contents";
    }
	
	@ApiOperation(value = "검색 결과 밴드 리스트 템플릿을 반환", response = String.class)
	@GetMapping("/bands")
    public String searchResultBands(@RequestParam("q") String q, Principal principal, HttpServletRequest request, Model model) {
		logger.debug("searchResultBands : q is {}", q);
		model.addAttribute("query", q);
		Member member = super.getCurrentUser(principal);
    	super.setSubMenu(model, "/search", member, request);
    	model.addAttribute("bands", searchService.searchBandRecruits(q));
		try {
			model.addAttribute("shareUrl", UPMusicHelper.baseUrl(request));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return "fragments/page/search/detail_contents";
    }
	
}

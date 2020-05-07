package com.upmusic.web.controllers.page;

import com.upmusic.web.domain.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.GuideVocalScopeService;
import com.upmusic.web.services.GuideVocalService;
import com.upmusic.web.services.LeagueSeasonService;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.MusicAlbumService;
import com.upmusic.web.services.MusicRequestService;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.VideoService;
import com.upmusic.web.services.VocalCastingService;
import com.upmusic.web.validator.MusicAlbumValidator;
import com.upmusic.web.validator.MusicRequestValidator;
import com.upmusic.web.validator.MusicTrackValidator;
import com.upmusic.web.validator.GuideVocalValidator;
import com.upmusic.web.validator.MemberValidator;
import com.upmusic.web.validator.VocalCastingValidator;


@Controller
@RequestMapping("/music")
@Api(value = "music", description = "뮤직 페이지를 담당하는 컨트롤러")
public class PageMusicController extends PageAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private MusicAlbumService albumService;

	@Autowired
	private MusicTrackService trackService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private LeagueSeasonService leagueSeasonService;
	
	@Autowired
	private MusicRequestService musicRequestService;
	
	@Autowired
	private GuideVocalService guideVocalService;
	
	@Autowired
	private GuideVocalScopeService guideScopeService;
	
	@Autowired
	private VocalCastingService vocalCastingService;
	
	@Autowired
	private VideoService videoService;

	@Autowired
	private MusicAlbumValidator albumValidator;
	
	@Autowired
	private MusicTrackValidator trackValidator;
	
	@Autowired
	private MusicRequestValidator requestValidator;
	
	@Autowired
	private MemberValidator memberValidator;
	
	@Autowired
	private GuideVocalValidator guideValidator;
	
	@Autowired
	private VocalCastingValidator castingValidator;

	@Autowired
	private AzureStorageService azureStorageService;
	

	@ApiOperation(value = "뮤직 페이지의 메인 템플릿을 반환", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"), @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@GetMapping()
	public String page(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music", member, request);
		Page<MusicTrack> tracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0,
				super.getPageMusicOrderByNew(0));
		try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		model.addAttribute("tracksFragmentId", "music-fragment");
		model.addAttribute("tracks", tracks);
		model.addAttribute("tracksUrl", "/music");
		return "fragments/page/music/index";
	}

	// --------------------------------------------------------------------------------------------
	// REWARD

	@ApiOperation(value = "리워드 템플릿을 반환", response = String.class)
	@GetMapping("/reward")
	public String reward(Principal principal, HttpServletRequest request, Model model) throws Exception {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/reward", member, request);
		
		Page<MusicTrack> tracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0,
				super.getPageMusicOrderByNew(0));
		
//		Page<MusicTrack> genreTracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicOrderByNew(0));
//		Page<MusicTrack> themeTracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicOrderByNew(0));
		
		try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		//
		// 탭에 해당하는 첫 페이지 목록들을 준비 : 다음 페이지들은 ajax로 ComponentMusicController에서 처리
		//

		// 실시간
		
		model.addAttribute("tracksFragmentId", "reward-realtime-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("paginationUrl", "/component/music/reward_realtime");
		

		// 장르별
		
		model.addAttribute("defaultGenre", UPMusicConstants.DEFAULT_GENRE);
		Map<Integer, String> genreMap = new HashMap<>();
		genreMap.put(0, getMessage("enum.genre."+ "ALL"));
		genreMap.putAll(super.getGenreMap());
		model.addAttribute("genreMap", genreMap);
		model.addAttribute("genreTracksFragmentId", "reward-genre-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		//model.addAttribute("genreTracks", genreTracks);
		model.addAttribute("genreTracks", tracks);
		model.addAttribute("genrePaginationUrl", "/component/music/reward_genre");
		

		// 테마별
		model.addAttribute("defaultTheme", UPMusicConstants.DEFAULT_THEME);
		Map<Integer, String> themeMap = new HashMap<>();
		themeMap.put(0, getMessage("enum.theme."+ "ALL"));
		themeMap.putAll(super.getThemeMap());
		model.addAttribute("themeMap", themeMap);
		model.addAttribute("themeTracksFragmentId", "reward-theme-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		//model.addAttribute("themeTracks", themeTracks);
		model.addAttribute("themeTracks", tracks);
		model.addAttribute("themePaginationUrl", "/component/music/reward_theme");

		model.addAttribute("pageNo", 0);
		
		
		
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/reward_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/reward_mobile";
		}
		return "fragments/page/music/reward";
	}

	// --------------------------------------------------------------------------------------------
	// LEAGUE

	@ApiOperation(value = "업 리그 템플릿을 반환", response = String.class)
	@GetMapping("/league")
	public String league(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/league", member, request);
		// 리그 순위 top 50 (전체)
		LeagueSeason currentSeason = leagueSeasonService.getCurrentLeagueSeason();
		List<LeagueSeasonTrack> tracks = leagueSeasonService.listTop50ByCurrentSeason(member != null ? member.getId() : 0);
		Page<LeagueSeasonTrack> genreTracks = leagueSeasonService.listAllByCurrentSeason(member != null ? member.getId() : 0, super.getPageMusicOrderByHot(0));
		Date year = new Date();
		List<LeagueSeason> seasons = leagueSeasonService.listAYearLeagueSeasonsOrderByOpenDate(year);
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
 		String currentYear = df.format(year);
		model.addAttribute("seasonMap", seasons);
		if (0 < seasons.size()) {
			LeagueSeason season = seasons.get(seasons.size()-1);
			logger.debug("league : defaultSeason is {}", season.getId());
			model.addAttribute("defaultSeason", season.getId());
			// current season
			if (0 == season.getId().compareTo(currentSeason.getId())) {
				Page<LeagueSeasonTrack> seasonTracks = leagueSeasonService.listAllByCurrentSeason(member != null ? member.getId() : 0, super.getPageMusicOrderByHot(0));
				model.addAttribute("seasonTracks", seasonTracks);
			} else {
				Page<LeagueSeasonChart> seasonTracks = leagueSeasonService.listAllSeasonChartBySeason(member != null ? member.getId() : 0, season, super.getPageMusicOrderByHot(0));
				model.addAttribute("seasonTracks", seasonTracks);
			}
		} else {
			model.addAttribute("defaultSeason", 0);
			Page<LeagueSeasonChart> seasonTracks = Page.empty();
			model.addAttribute("seasonTracks", seasonTracks);
		}
		try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		//
		// 탭에 해당하는 첫 페이지 목록들을 준비 : 다음 페이지들은 ajax로 ComponentMusicController에서 처리
		//

		// 공통사항
		List<String> days = leagueSeasonService.listDaysOfDailyChart();
		model.addAttribute("league_days", days);
		List<String> weeks = leagueSeasonService.listWeeksOfWeeklyChart();
		model.addAttribute("league_weeks", weeks);
		List<String> months = leagueSeasonService.listMonthsOfMonthlyChart();
		model.addAttribute("league_months", months);
		List<String> years = leagueSeasonService.listYearsOfAllLeagueSeasons();
		model.addAttribute("league_years", years);
		model.addAttribute("currentYear", currentYear);
		model.addAttribute("defaultPeriod", UPMusicConstants.DEFAULT_PERIOD);
		model.addAttribute("periodMap", super.getLeagueSeasonPeriodMap());

		// TOP 50
		model.addAttribute("tracksFragmentId", "league-top50-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("paginationUrl", "/component/music/league_top50");

		// 장르별
		model.addAttribute("defaultGenre", UPMusicConstants.DEFAULT_GENRE);
		Map<Integer, String> genreMap = new HashMap<>();
		genreMap.put(0, getMessage("enum.genre."+ "ALL"));
		genreMap.putAll(super.getGenreMap());
		model.addAttribute("genreMap", genreMap);
		model.addAttribute("genreTracksFragmentId", "league-genre-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("genreTracks", genreTracks);
		model.addAttribute("genrePaginationUrl", "/component/music/league_genre");

		// 시즌별
		model.addAttribute("seasonTracksFragmentId", "league-season-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("seasonPaginationUrl", "/component/music/league_season");

		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/league_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/league_mobile";
		}
		return "fragments/page/music/league";
	}

	// --------------------------------------------------------------------------------------------
	// STORE

	@ApiOperation(value = "스토어 템플릿을 반환", response = String.class)
	@GetMapping("/store")
	public String store(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/store", member, request);
//		Page<MusicTrack> tracks = trackService.findAllByGenreAndInStoreTrueWithHeartByMember(UPMusicConstants.DEFAULT_GENRE, member != null ? member.getId() : 0, super.getPageMusicOrderByNew(0));
		Page<MusicTrack> tracks = trackService.findAllInStoreTrueWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicOrderByNew(0));
		try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Map<Integer, String> genreMap = new HashMap<>();
		genreMap.put(0, getMessage("enum.genre."+ "ALL"));
		genreMap.putAll(super.getGenreMap());
		model.addAttribute("genreMap", genreMap);
		model.addAttribute("tracksFragmentId", "store-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("paginationUrl", "/component/music/store");
		
		// 제작 의뢰
		Page<MusicRequest> requests = musicRequestService.findAllWithCommentsCount(super.getPageRequestOrderByNew(0));
		model.addAttribute("requestsFragmentId", "store-request-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("requests", requests);
		model.addAttribute("requestPaginationUrl", "/component/music/store/request");

		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/store_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/store_mobile";
		}
		return "fragments/page/music/store";
	}
	
	@ApiOperation(value = "제작의뢰 등록 템플릿을 반환", response = String.class)
    @GetMapping("/store/request")
    public String request(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/store", member, request);
		if (member != null) {
			MusicRequest musicRequest = new MusicRequest();
			musicRequest.setMember(member);
			model.addAttribute("requestform", musicRequest);
			
			String userAgent = request.getHeader("user-agent");
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/request_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/request_mobile";
			}
			return "fragments/page/music/request";
		}
		return "redirect:/auth/login";
    }
	
	@ApiOperation(value = "제작의뢰 등록 템플릿을 처리", response = String.class)
    @PostMapping("/store/request")
    public String createRequest(@ModelAttribute("requestform") MusicRequest requestform, BindingResult bindingResult, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		if (member != null) {
			requestform.setMember(member);
			requestValidator.validate(requestform, bindingResult);
			if (bindingResult.hasErrors()) {
				super.setSubMenu(model, "/music/store", member, request);
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
					model.addAttribute("userAgent", userAgent);
					return "fragments/page/music/request_mobile";
				} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
					return "fragments/page/music/request_mobile";
				}
				return "fragments/page/music/request";
			}
			String price = requestform.getPrice();
			if (price != null && !price.isEmpty()) {
				String priceNummeric = price.trim().replaceAll("[^0-9\\-\\s\\.]", "");
				requestform.setPrice(priceNummeric);
			} else {
				requestform.setPrice("0");
			}
			// 제작의뢰 저장
			MusicRequest musicRequest = musicRequestService.saveRequest(requestform);
			return "redirect:/music/store/request/" + musicRequest.getId();
		}
		return "redirect:/auth/login";
    }
	
	@ApiOperation(value = "제작의뢰 상세페이지 템플릿을 반환", response = String.class)
    @GetMapping("/store/request/{id}")
    public String requestDetail(@PathVariable Long id, Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) {
		MusicRequest musicRequest = musicRequestService.getRequestById(id);
		if (musicRequest == null) return "fragments/page/error/error-404";
		
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/store", member, request);
		model.addAttribute("request", musicRequest);
		model.addAttribute("requestComments", musicRequestService.getComments(id, super.getCommentsOrderByNew(0)));
    	model.addAttribute("paginationUrl", "/component/music/store/request/" + id + "/comments");
		try {
			super.setMetaTag(model, musicRequest.getMetaTag(UPMusicHelper.baseUrl(request)));
			model.addAttribute("shareUrl", String.format("%s/music/store/request/%d", UPMusicHelper.baseUrl(request), id));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (member != null) {
			model.addAttribute("currentUserId", member.getId());
			model.addAttribute("isOwner", 0 == (musicRequest.getMember().getId().compareTo(member.getId())));
		} else {
			model.addAttribute("currentUserId", 0);
			model.addAttribute("isOwner", false);
		}
		
		// 쿠키 - 조회수 증가
		boolean existsCookie = false;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(UPMusicConstants.COOKIE_REQUEST + id)) existsCookie = true;
			}
		}

	    if (!existsCookie) {
	    	musicRequestService.increaseHitCnt(id);
			Cookie cookie = new Cookie(UPMusicConstants.COOKIE_REQUEST + id, "hit");
	        cookie.setMaxAge(60 * 60 * 24);
	        response.addCookie(cookie);
	    }
		
	    String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/request_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/request_detail_mobile";
		}
		return "fragments/page/music/request_detail";
    }
	
	@ApiOperation(value = "제작의뢰 등록 템플릿을 반환", response = String.class)
    @GetMapping("/store/request/{id}/edit")
    public String editRequest(@PathVariable Long id, Principal principal, Model model, HttpServletRequest request) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/store/edit", member, request);
		MusicRequest musicRequest = musicRequestService.getRequestById(id);
		String userAgent = request.getHeader("user-agent");
		if (member != null && 0 == (musicRequest.getMember().getId().compareTo(member.getId()))) {
			model.addAttribute("requestform", musicRequest);
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/request_edit_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/request_edit_mobile";
			}
			return "fragments/page/music/request_edit";
		}
		return "redirect:/auth/login";
    }
	
	@ApiOperation(value = "제작의뢰 등록 템플릿을 처리", response = String.class)
    @PostMapping("/store/request/{id}")
    public String updateRequest(@ModelAttribute("requestform") MusicRequest requestform, @PathVariable Long id, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		MusicRequest musicRequest = musicRequestService.getRequestById(id);
		Member member = super.getCurrentUser(principal);
		if (member != null && 0 == (musicRequest.getMember().getId().compareTo(member.getId()))) {
			requestValidator.validate(requestform, bindingResult);
			if (bindingResult.hasErrors()) {
				super.setSubMenu(model, "/music/store", member, request);
				requestform.setMember(member);
				return "fragments/page/music/request_edit";
			}
			String price = requestform.getPrice();
			if (price != null && !price.isEmpty()) {
				String priceNummeric = price.trim().replaceAll("[^0-9\\-\\s\\.]", "");
				requestform.setPrice(priceNummeric);
			} else {
				requestform.setPrice("0");
			}
			// 제작의뢰 저장
			musicRequest.setDescription(requestform.getDescription());
			musicRequest.setDiscussion(requestform.isDiscussion());
			musicRequest.setPrice(requestform.getPrice());
			musicRequestService.saveRequest(musicRequest);
			return "redirect:/music/store/request/" + id;
		}
		return "redirect:/auth/login";
    }

	// --------------------------------------------------------------------------------------------
	// ALBUM

	@ApiOperation(value = "앨범 템플릿을 반환", response = String.class)
	@GetMapping("/album")
	public String album(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/album", member, request);
		int genreId = UPMusicConstants.DEFAULT_GENRE;
    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
		// 싱글
    	//Page<MusicAlbum> sAlbums = albumService.findAllSAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
    	Page<MusicAlbum> sAlbums ;
    	if(genreId == 0) {
    		 sAlbums = albumService.findAllSAWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
    	}else sAlbums = albumService.findAllSAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
		model.addAttribute("sAlbums", sAlbums);
		model.addAttribute("sPaginationUrl", "/component/music/album/sa");
		// 앨범
		Page<MusicAlbum> gAlbums;
		if(genreId == 0) {
   		 	gAlbums = albumService.findAllGAWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
		}else gAlbums = albumService.findAllGAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
		model.addAttribute("gAlbums", gAlbums);
		model.addAttribute("gPaginationUrl", "/component/music/album/ga");
		
		try {
			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		model.addAttribute("defaultGenre", UPMusicConstants.DEFAULT_GENRE);
		model.addAttribute("genreId", genreId);
		Map<Integer, String> genreMap = new HashMap<>();
		genreMap.put(0, getMessage("enum.genre."+ "ALL"));
		genreMap.putAll(super.getGenreMap());
		model.addAttribute("genreMap", genreMap);
		
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/album_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/album_mobile";
		}
		return "fragments/page/music/album";
	}
	
//	@ApiOperation(value = "국내 앨범 템플릿을 반환", response = String.class)
//	@GetMapping("/album_home")
//	public String albumHome(Principal principal, HttpServletRequest request, Model model) {
//		Member member = super.getCurrentUser(principal);
//		super.setSubMenu(model, "/music/album_home", member);
//		int genreId = UPMusicConstants.DEFAULT_GENRE;
//    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
//		// 싱글
//		Page<MusicAlbum> sAlbums = albumService.findHomeSAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
//		model.addAttribute("sAlbums", sAlbums);
//		model.addAttribute("sPaginationUrl", "/component/music/album_home/sa");
//		// 앨범
//		Page<MusicAlbum> gAlbums = albumService.findHomeGAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
//		model.addAttribute("gAlbums", gAlbums);
//		model.addAttribute("gPaginationUrl", "/component/music/album_home/ga");
//		
//		try {
//			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		model.addAttribute("defaultGenre", UPMusicConstants.DEFAULT_GENRE);
//		model.addAttribute("genreId", genreId);
//		model.addAttribute("genreMap", super.getGenreMap());
//		return "fragments/page/music/album";
//	}
//	
//	@ApiOperation(value = "해외 앨범 템플릿을 반환", response = String.class)
//	@GetMapping("/album_abroad")
//	public String albumAbroad(Principal principal, HttpServletRequest request, Model model) {
//		Member member = super.getCurrentUser(principal);
//		super.setSubMenu(model, "/music/album_abroad", member);
//		int genreId = UPMusicConstants.DEFAULT_GENRE;
//    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
//		// 싱글
//		Page<MusicAlbum> sAlbums = albumService.findAbroadSAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
//		model.addAttribute("sAlbums", sAlbums);
//		model.addAttribute("sPaginationUrl", "/component/music/album_abroad/sa");
//		// 앨범
//		Page<MusicAlbum> gAlbums = albumService.findAbroadGAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(0));
//		model.addAttribute("gAlbums", gAlbums);
//		model.addAttribute("gPaginationUrl", "/component/music/album_abroad/ga");
//		
//		try {
//			model.addAttribute("shareUrl", String.format("%s/music/album/", UPMusicHelper.baseUrl(request)));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		model.addAttribute("defaultGenre", UPMusicConstants.DEFAULT_GENRE);
//		model.addAttribute("genreId", genreId);
//		model.addAttribute("genreMap", super.getGenreMap());
//		return "fragments/page/music/album";
//	}

	@ApiOperation(value = "앨범 상세페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/album/{id}")
	public String albumDetail(@PathVariable Long id, Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("detail : id is {}", id);
		MusicAlbum album = albumService.getAlbumById(id);
		if (album == null || !album.isPublished()) return "fragments/page/error/error-404";
		
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/album", member, request);
		model.addAttribute("album", album);
		model.addAttribute("albumComments", albumService.getComments(id, super.getCommentsOrderByNew(0)));
    	model.addAttribute("paginationUrl", "/component/music/album/" + id + "/comments");
		model.addAttribute("owner", album.getMember());
		try {
			super.setMetaTag(model, album.getMetaTag(request));
			model.addAttribute("shareUrl", album.getAbsoluteUrl(UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (member != null) {
			model.addAttribute("currentUserId", member.getId());
			model.addAttribute("isOwner", 0 == (album.getMember().getId().compareTo(member.getId())));
			model.addAttribute("likedAlbum", albumService.likedAlbum(id, member.getId()));
			for (MusicTrack track : album.getAcceptedTracks()) {
				track.setLiked(trackService.likedTrack(track.getId(), member.getId()));
			}
		} else {
			model.addAttribute("currentUserId", 0);
			model.addAttribute("isOwner", false);
			model.addAttribute("likedAlbum", false);
		}
		
		// 쿠키 - 조회수 증가
		boolean existsCookie = false;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(UPMusicConstants.COOKIE_ALBUM + id)) existsCookie = true;
			}
		}

        if (!existsCookie) {
    		albumService.increaseHitCnt(id);
    		Cookie cookie = new Cookie(UPMusicConstants.COOKIE_ALBUM + id, "hit");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
        }
        String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			model.addAttribute("topNavibar", "nav-down");
			return "fragments/page/music/album_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			model.addAttribute("topNavibar", "nav-down");
			return "fragments/page/music/album_detail_mobile";
		}
		return "fragments/page/music/album_detail";
	}

	@ApiOperation(value = "앨범 편집 템플릿을 반환", response = String.class)
	@GetMapping("/album/{id}/edit")
	public String edit(@PathVariable Long id, Principal principal, Model model, HttpServletRequest request) {
		logger.debug("edit : id is {}", id);
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/album", member, request);
		MusicAlbum album = albumService.getAlbumById(id);
		if (member != null && 0 == (album.getMember().getId().compareTo(member.getId()))) {
			// 앨범 항목
			model.addAttribute("musicform", album);
			model.addAttribute("albumTypeMap", super.getMusicAlbumTypeMap());
			model.addAttribute("genreMap", super.getGenreMap());
			// 트랙 항목
			model.addAttribute("tracks", album.getTracks());
			return "fragments/page/music/album_edit";
		}
		return "redirect:/auth/login";
	}

	@ApiOperation(value = "앨범 정보를 업데이트", response = String.class)
	@PostMapping("/album/{id}")
	public String update(@PathVariable Long id, @ModelAttribute("musicform") MusicAlbum musicform,
			@RequestParam(required = false, value = "addTrack") String addTrackFlag, BindingResult bindingResult,
			Principal principal, HttpServletRequest request, Model model) {
		logger.debug("update :  musicform is {}", musicform);
		logger.debug("update :  musicform.tracks is {}", musicform.getTracks());
		MusicAlbum album = albumService.getAlbumById(id);
		Member member = super.getCurrentUser(principal);
		if (member != null && 0 == (album.getMember().getId().compareTo(member.getId()))) {
			albumValidator.validate(musicform, bindingResult);
			if (!StringUtils.isEmpty(addTrackFlag) || bindingResult.hasErrors()) {
				// 앨범 항목
				model.addAttribute("albumTypeMap", super.getMusicAlbumTypeMap());
				// 트랙 항목
				model.addAttribute("trackCnt", musicform.getTracks().size());
				model.addAttribute("trackTypeMap", super.getMusicTrackTypeMap());
				model.addAttribute("genreMap", super.getGenreMap());
				model.addAttribute("themeMap", super.getThemeMap());
				model.addAttribute("currentUser", member);
				model.addAttribute("cooperatorRoleMap", super.getCooperatorRoleMap());
				if (!StringUtils.isEmpty(addTrackFlag))
					model.addAttribute("addTrack", true);
				super.setSubMenu(model, "/music/album", member, request);

				return "fragments/page/music/album_edit";
			}
			// 앨범 저장
			album.setAlbumType(musicform.getAlbumType());
			album.setDescription(musicform.getDescription());
			album.setGenre(musicform.getGenre());
			album.setSubject(musicform.getSubject());
			if (musicform.getCoverImageMultipart() != null && !StringUtils.isEmpty(musicform.getCoverImageMultipart().getOriginalFilename()))
				album.setImageFilename(UPMusicHelper.makeReadableUrl(musicform.getCoverImageMultipart().getOriginalFilename()));
			albumService.saveAlbum(album);
			if (musicform.getCoverImageMultipart() != null && !StringUtils.isEmpty(musicform.getCoverImageMultipart().getOriginalFilename()))
				azureStorageService.uploadResource(musicform.getCoverImageMultipart(), "albums/" + id + "/");

			return "redirect:/music/album/" + id;
		}

		return "redirect:/auth/login";
	}
	
	@ApiOperation(value = "곡 편집 템플릿을 반환", response = String.class)
	@GetMapping("/track/{id}/edit")
	public String editTrack(@PathVariable Long id, Principal principal, Model model, HttpServletRequest request) {
		logger.debug("editTrack : id is {}", id);
		Member member = super.getCurrentUser(principal);
		MusicTrack track = trackService.getTrackById(id);
		if (track.getTrackStatus() != MusicTrackStatus.ACCEPTED && member != null && 0 == (track.getArtistId().compareTo(member.getId()))) {
			super.setSubMenu(model, "/music/album", member, request);
			// 협력자를 입력폼에 맞게 설정
			for (MusicTrackCooperator cooperator : track.getCooperators()) {
				cooperator.setCooperatorId(cooperator.getMember().getId());
			}
			model.addAttribute("trackform", track);
			model.addAttribute("genreMap", super.getGenreMap());
			model.addAttribute("trackTypeMap", super.getMusicTrackTypeMap());
			model.addAttribute("themeMap", super.getThemeMap());
			model.addAttribute("currentUser", member);
	    	model.addAttribute("cooperatorRoleMap", super.getCooperatorRoleMap());
	    	model.addAttribute("isSA", UPMusicConstants.MusicAlbumType.SA.equals(track.getMusicAlbum().getAlbumType()));
	    	model.addAttribute("albumId", track.getMusicAlbumId());
			return "fragments/page/music/track_edit";
		}
		return "redirect:/";
	}
	
	@ApiOperation(value = "곡 정보를 업데이트", response = String.class)
	@PostMapping("/track/{id}")
	public String updateTrack(@PathVariable Long id, @ModelAttribute("trackform") MusicTrack trackform, BindingResult bindingResult,
			Principal principal, HttpServletRequest request, Model model) {
		logger.debug("update :  trackform is {}", trackform);
		MusicTrack track = trackService.getTrackById(id);
		Member member = super.getCurrentUser(principal);
		if (member != null && 0 == (track.getArtistId().compareTo(member.getId()))) {
			// 유효성 결과와 상관없이 협력자의 회원 정보가 필요함
			for (MusicTrackCooperator cooperator : trackform.getCooperators()) {
				if(cooperator.getCooperatorId() != null) {
					Member cooperatorMember = memberService.getMemberById(cooperator.getCooperatorId());
					cooperator.setMember(cooperatorMember);
				}
			}
			if (trackform.getFilename() == null || trackform.getFilename().isEmpty()) trackform.setFilename(null);
			else if (!trackform.getFilename().equals(track.getFilename()) && (trackform.getFileMultipart() == null || StringUtils.isEmpty(trackform.getFileMultipart().getOriginalFilename()))) trackform.setFilename(null);
			trackValidator.validate(trackform, bindingResult);
			if (bindingResult.hasErrors()) {
				super.setSubMenu(model, "/music/album", member, request);
				model.addAttribute("trackTypeMap", super.getMusicTrackTypeMap());
				model.addAttribute("genreMap", super.getGenreMap());
				model.addAttribute("themeMap", super.getThemeMap());
				model.addAttribute("currentUser", member);
				model.addAttribute("cooperatorRoleMap", super.getCooperatorRoleMap());
				model.addAttribute("isSA", UPMusicConstants.MusicAlbumType.SA.equals(track.getMusicAlbum().getAlbumType()));
				model.addAttribute("albumId", track.getMusicAlbumId());
				return "fragments/page/music/track_edit";
			}

			track.setGenre(trackform.getGenre());
			track.setInLeague(trackform.isInLeague());
			track.setInStore(trackform.isInStore());
			track.setPrice(trackform.getPrice());
			track.setRentalFee(trackform.getRentalFee());
			track.setSubject(trackform.getSubject());
			track.setTrackType(trackform.getTrackType());
			track.setTheme(trackform.getTheme());
			track.setTrackStatus(MusicTrackStatus.UNDER_EXAM);
			// 협력자 초기화
			for (MusicTrackCooperator cooperator : track.getCooperators()) {
				trackService.deleteTrackCooperator(cooperator);
			}
			List<MusicTrackCooperator> cooperators = new ArrayList<MusicTrackCooperator>();
			for (MusicTrackCooperator cooperator : trackform.getCooperators()) {
				if(cooperator.getCooperatorId() != null) {
					cooperators.add(cooperator);
				}
			}
			track.setCooperators(cooperators);
			if (trackform.getFileMultipart() != null && !StringUtils.isEmpty(trackform.getFileMultipart().getOriginalFilename())) {
				track.setFilename(UPMusicHelper.makeReadableUrl(trackform.getFileMultipart().getOriginalFilename()));
				track.setDuration(trackform.getDuration());
			}
			if (trackform.getExtraFileMultipart() != null && !StringUtils.isEmpty(trackform.getExtraFileMultipart().getOriginalFilename()))
				track.setExtraSource(UPMusicHelper.makeReadableUrl(trackform.getExtraFileMultipart().getOriginalFilename()));
			MusicTrack updatedTrack = trackService.saveTrack(track);
			if (trackform.getFileMultipart() != null && !StringUtils.isEmpty(trackform.getFileMultipart().getOriginalFilename()))
				azureStorageService.uploadTrackToTranscode(trackform.getFileMultipart(), updatedTrack.getMusicAlbumId() + "/");
			if (trackform.getExtraFileMultipart() != null && !StringUtils.isEmpty(trackform.getExtraFileMultipart().getOriginalFilename()))
				azureStorageService.uploadResource(trackform.getExtraFileMultipart(), "albums/" + updatedTrack.getMusicAlbumId() + "/");

			// 협력자 저장
			for (MusicTrackCooperator cooperator : cooperators) {
				cooperator.setMusicTrack(updatedTrack);
				trackService.saveTrackCooperator(cooperator);
			}

			return "redirect:/music/album/" + updatedTrack.getMusicAlbumId() + "/edit";
		}

		return "redirect:/auth/login";
	}

	// --------------------------------------------------------------------------------------------
	// ARTIST

	@ApiOperation(value = "아티스트 템플릿을 반환", response = String.class)
	@GetMapping("/artist")
	public String artist(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/artist", member, request);
		
		Page<Member> artists = memberService.findAllArtistWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByHot(0));
		Page<Member> familyArtists = memberService.findAllFamilyArtistWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByHot(0));
		Page<Member> newArtists = memberService.findAllNewArtistWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByNew(0));
		Page<Member> guideVocals = memberService.findAllGuideVocalWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByNew(0));
		
		try {
			model.addAttribute("shareUrl", String.format("%s/music/artist/", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		//
		// 탭에 해당하는 첫 페이지 목록들을 준비 : 다음 페이지들은 ajax로 ComponentMusicController에서 처리
		//

		// 아티스트 홈
		model.addAttribute("artistsFragmentId", "artist-home-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("artists", artists);
		model.addAttribute("paginationUrl", "/component/music/artist");
		
		// 아티스트 패밀리
		model.addAttribute("familyArtistsFragmentId", "artist-family-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("familyArtists", familyArtists);
		model.addAttribute("familyPaginationUrl", "/component/music/family_artist");
		
		// 아티스트 신규
		model.addAttribute("newArtistsFragmentId", "artist-new-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("newArtists", newArtists);
		model.addAttribute("newPaginationUrl", "/component/music/new_artist");
		
		// 가이드 보컬
		model.addAttribute("guideVocalsFragmentId", "artist-guide-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("guideVocals", guideVocals);
		model.addAttribute("guidePaginationUrl", "/component/music/guide_vocal");
		if (member != null) {
			GuideVocal guide = guideVocalService.findOneByMember(member);
			if (guide != null) model.addAttribute("guideVocalId", guide.getId());
		}
		
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/artist_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/artist_mobile";
		}
		return "fragments/page/music/artist";
	}

	@ApiOperation(value = "아티스트 상세페이지 템플릿을 반환", response = String.class)
	@GetMapping("/artist/{id}")
	public String artistDetail(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
		Member artist = memberService.getMemberById(id);
		if (artist == null) return "fragments/page/error/error-404";

		Member member = super.getCurrentUser(principal);

		boolean roleAdmin = false;

		if (member != null) {
			Set<Role> adminRole =  member.getRoles();
			Iterator<Role> adminIter = adminRole.iterator();

			while (adminIter.hasNext()){
				if(adminIter.next().getId() == 1){
					roleAdmin = true;
				}
			}
		}

		Set<Role> artistRole =  artist.getRoles();
		Iterator<Role> artistIter = artistRole.iterator();
		int roleArtist = artistIter.next().getId();
		logger.debug(roleAdmin + ":" + roleArtist);
		super.setSubMenu(model, "/music/artist", member, request);
		model.addAttribute("artist", artist);
		model.addAttribute("artistTracks", trackService.findTop10ByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0));
		model.addAttribute("artistTracksCnt", trackService.findCountByArtistId(artist.getId()));
		model.addAttribute("artistAlbums", albumService.findTop4ByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0));
		model.addAttribute("artistAlbumsCnt", albumService.findCountByArtistId(artist.getId()));
		model.addAttribute("artistVideos", videoService.findTop4ByMemberId(artist.getId()));
		model.addAttribute("artistVideosCnt", videoService.findCountByMemberId(artist.getId()));
		model.addAttribute("artistStoreTracks", trackService.findTop10ByArtistIdAndInStoreTrueWithHeartByMember(artist.getId(), member != null ? member.getId() : 0));
		model.addAttribute("artistStoreTracksCnt", trackService.findCountByArtistIdAndInStoreTrue(artist.getId()));

		model.addAttribute("paginationUrl", "/component/music/artist_track");
		model.addAttribute("albumPaginationUrl", "/component/music/artist_album");
		model.addAttribute("videoPaginationUrl", "/component/music/artist_video");
		model.addAttribute("storePaginationUrl", "/component/music/artist_store");
		model.addAttribute("pageNo", 0);

		// 공유
		try {
			super.setMetaTag(model, artist.getMetaTag(request));
			model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), id));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (member != null) {
			model.addAttribute("isOwner", 0 == (artist.getId().compareTo(member.getId())));
			model.addAttribute("likedArtist", memberService.likedMember(artist.getId(), member.getId()));
		} else {
			model.addAttribute("isOwner", false);
			model.addAttribute("likedArtist", false);
		}

		if(roleAdmin && roleArtist == 6){
			model.addAttribute("isOwner", true);
			model.addAttribute("isAdmin", true);
		}

		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			model.addAttribute("topNavibar", "nav-down");

			//음원
			model.addAttribute("artistTracks", trackService.findAllByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0, PageRequest.of(0, 50, Sort.Direction.DESC, "id")));
			//앨범
			model.addAttribute("artistAlbums", albumService.findAllByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0, PageRequest.of(0, 20, Sort.Direction.DESC, "id")));
			//비디오
			model.addAttribute("artistVideos", videoService.findAllByMemberId(artist.getId(), super.getPageVideoOrderByNew(0)));
			//뮤직스토어
			model.addAttribute("artistStoreTracks", trackService.findAllByArtistIdAndInStoreTrueWithHeartByMember(artist.getId(), member != null ? member.getId() : 0, PageRequest.of(0, 50, Sort.Direction.DESC, "id")));

			return "fragments/page/music/artist_detail_mobile";

		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			model.addAttribute("topNavibar", "nav-down");

			//음원
			model.addAttribute("artistTracks", trackService.findAllByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0, PageRequest.of(0, 50, Sort.Direction.DESC, "id")));
			//앨범
			model.addAttribute("artistAlbums", albumService.findAllByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0, PageRequest.of(0, 20, Sort.Direction.DESC, "id")));
			//비디오
			model.addAttribute("artistVideos", videoService.findAllByMemberId(artist.getId(), super.getPageVideoOrderByNew(0)));
			//뮤직스토어
			model.addAttribute("artistStoreTracks", trackService.findAllByArtistIdAndInStoreTrueWithHeartByMember(artist.getId(), member != null ? member.getId() : 0, PageRequest.of(0, 50, Sort.Direction.DESC, "id")));

			return "fragments/page/music/artist_detail_mobile";
		}
		return "fragments/page/music/artist_detail";
	}
	
	@ApiOperation(value = "아티스트 상세페이지 음원리스트 템플릿을 반환", response = String.class)
	@GetMapping("/artist/{id}/tracks")
	public String artistTracks(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/artist", member, request);
		Member artist = memberService.getMemberById(id);
		if (artist != null) {
			model.addAttribute("artist", artist);
			List<MusicTrack> tracks = trackService.findByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0);
			model.addAttribute("artistTracks", tracks);
			
			// 공유
			try {
				super.setMetaTag(model, artist.getMetaTag(request));
				model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), id));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (member != null) {
				model.addAttribute("isOwner", 0 == (artist.getId().compareTo(member.getId())));
				model.addAttribute("likedArtist", memberService.likedMember(artist.getId(), member.getId()));
			} else {
				model.addAttribute("isOwner", false);
				model.addAttribute("likedArtist", false);
			}
		}
		
		return "fragments/page/music/artist_detail_contents";
	}
	
	@ApiOperation(value = "아티스트 상세페이지 앨범리스트 템플릿을 반환", response = String.class)
	@GetMapping("/artist/{id}/albums")
	public String artistAlbums(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/artist", member, request);
		Member artist = memberService.getMemberById(id);
		if (artist != null) {
			model.addAttribute("artist", artist);
			model.addAttribute("artistAlbums", albumService.findByArtistIdWithHeartByMember(artist.getId(), member != null ? member.getId() : 0));
			
			// 공유
			try {
				super.setMetaTag(model, artist.getMetaTag(request));
				model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), id));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (member != null) {
				model.addAttribute("isOwner", 0 == (artist.getId().compareTo(member.getId())));
				model.addAttribute("likedArtist", memberService.likedMember(artist.getId(), member.getId()));
			} else {
				model.addAttribute("isOwner", false);
				model.addAttribute("likedArtist", false);
			}
		}
		
		return "fragments/page/music/artist_detail_contents";
	}
	
	@ApiOperation(value = "아티스트 상세페이지 비디오리스트 템플릿을 반환", response = String.class)
	@GetMapping("/artist/{id}/videos")
	public String artistVideos(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/artist", member, request);
		Member artist = memberService.getMemberById(id);
		if (artist != null) {
			model.addAttribute("artist", artist);
			model.addAttribute("artistVideos", videoService.findByMemberId(artist.getId()));
			
			// 공유
			try {
				super.setMetaTag(model, artist.getMetaTag(request));
				model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), id));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (member != null) {
				model.addAttribute("isOwner", 0 == (artist.getId().compareTo(member.getId())));
				model.addAttribute("likedArtist", memberService.likedMember(artist.getId(), member.getId()));
			} else {
				model.addAttribute("isOwner", false);
				model.addAttribute("likedArtist", false);
			}
		}
		
		return "fragments/page/music/artist_detail_contents";
	}
	
	@ApiOperation(value = "아티스트 상세페이지 스토어 음원리스트 템플릿을 반환", response = String.class)
	@GetMapping("/artist/{id}/store_tracks")
	public String artistStores(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/artist", member, request);
		Member artist = memberService.getMemberById(id);
		if (artist != null) {
			model.addAttribute("artist", artist);
			model.addAttribute("artistStoreTracks", trackService.findByArtistIdAndInStoreTrueWithHeartByMember(artist.getId(), member != null ? member.getId() : 0));
			
			// 공유
			try {
				super.setMetaTag(model, artist.getMetaTag(request));
				model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), id));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (member != null) {
				model.addAttribute("isOwner", 0 == (artist.getId().compareTo(member.getId())));
				model.addAttribute("likedArtist", memberService.likedMember(artist.getId(), member.getId()));
			} else {
				model.addAttribute("isOwner", false);
				model.addAttribute("likedArtist", false);
			}
		}
		
		return "fragments/page/music/artist_detail_contents";
	}


	@ApiOperation(value = "아티스트 편집페이지 템플릿을 반환", response = String.class)
	@GetMapping("/artist/{id}/edit")
	public String artistEdit(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) throws MalformedURLException {
		Member member = super.getCurrentUser(principal);
        Member artist = memberService.getMemberById(id);
        Set<Role> adminSet = member.getRoles();
        Set<Role> userSet = artist.getRoles();
		Iterator<Role> adminIter = adminSet.iterator();
		Iterator<Role> userIter = userSet.iterator();
		boolean isAdmin = false;
		boolean isFamily = false;
		while (adminIter.hasNext()) {
			if (adminIter.next().getId() == 1) {
				isAdmin = true;
			}
		}
		while (userIter.hasNext()) {
			if (userIter.next().getId() == 6) {
				isFamily = true;
			}
		}
        if(!isAdmin && !isFamily){
            initNiceID(request, model);
        }
        super.setSubMenu(model, "/music/artist", member, request);
		if (artist != null && member != null && 0 == (artist.getId().compareTo(member.getId()))) {
			model.addAttribute("artistform", artist);
			String userAgent = request.getHeader("user-agent");
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/artist_edit_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/artist_edit_mobile";
			}
			return "fragments/page/music/artist_edit";
		} else if(isAdmin && isFamily){
            model.addAttribute("artistform", artist);
            return "fragments/page/music/artist_edit";
        }
		return "redirect:/";
	}
	
	@ApiOperation(value = "아티스트 편집페이지 템플릿을 처리", response = String.class)
	@PostMapping("/artist/{id}")
	public String artistUpdate(@ModelAttribute("artistform") Member artistform, @PathVariable Long id, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		Member artist = memberService.getMemberById(id);
		Member member = super.getCurrentUser(principal);
		if (artist != null && member != null && 0 == (artist.getId().compareTo(member.getId()))) {
			memberValidator.validate(artistform, bindingResult);
			if (bindingResult.hasErrors()) {
				artistform.setEmail(artist.getEmail());
				artistform.setTrackCnt(artist.getTrackCnt());
//				artistform.setPhoneNumber(artist.getPhoneNumber());
				super.setSubMenu(model, "/music/artist", member, request);
				return "fragments/page/music/artist_edit";
			}
			// 아티스트 저장
			logger.debug("Save artist");
			artist.setNick(artistform.getNick());
			artist.setProfileImage(UPMusicHelper.makeReadableUrl(artistform.getProfileImage()));
			artist = memberService.saveMember(artist);
			if (artistform.getProfileImageMultipart() != null) azureStorageService.uploadResource(artistform.getProfileImageMultipart(), "profiles/" + artist.getId() + "/");
			return "redirect:/music/artist/" + artist.getId();
		}
		return "redirect:/auth/login";
    }

	
	@ApiOperation(value = "가이드 보컬 신청페이지 템플릿을 반환", response = String.class)
	@GetMapping("/guide_vocal/new")
	public String guideVocalNew(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/guide_vocal/new", member, request);
		if (member != null) {
			GuideVocal guide = new GuideVocal();
			guide.setMember(member);
			model.addAttribute("guideform", guide);
			
			String userAgent = request.getHeader("user-agent");
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/guide_vocal_new_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/guide_vocal_new_mobile";
			}
			return "fragments/page/music/guide_vocal_new";
		}
		return "redirect:/auth/login";
	}
	
	@ApiOperation(value = "가이드 보컬 등록 템플릿을 처리", response = String.class)
    @PostMapping("/guide_vocal/new/guide")
    public String createGuideVocal(@ModelAttribute("guideform") GuideVocal guideform, BindingResult bindingResult, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		if (member != null) {
			guideform.setMember(member);
			if (guideform.getFileMultipart() == null || StringUtils.isEmpty(guideform.getFileMultipart().getOriginalFilename())) guideform.setFilename(null);
			guideValidator.validate(guideform, bindingResult);
			if (bindingResult.hasErrors()) {
				super.setSubMenu(model, "/music/artist", member, request);
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
					model.addAttribute("userAgent", userAgent);
					return "fragments/page/music/guide_vocal_new_mobile";
				} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
					return "fragments/page/music/guide_vocal_new_mobile";
				}
				return "fragments/page/music/guide_vocal_new";
			}
			// 보컬 캐스팅 저장
			guideform.setFilename(UPMusicHelper.makeReadableUrl(guideform.getFilename()));
			if (guideform.isGuideScopeGuide()) guideform.addGuideVocalScope(guideScopeService.getGuideVocalScopeById(1));
			if (guideform.isGuideScopeChorus()) guideform.addGuideVocalScope(guideScopeService.getGuideVocalScopeById(2));
			if (guideform.isGuideScopeRap()) guideform.addGuideVocalScope(guideScopeService.getGuideVocalScopeById(3));
			GuideVocal guide = guideVocalService.saveGuideVocal(guideform);
			if (guideform.getFileMultipart() != null && !StringUtils.isEmpty(guideform.getFileMultipart().getOriginalFilename())) azureStorageService.uploadResource(guideform.getFileMultipart(), "guides/" + guide.getId() + "/");
			return "redirect:/music/guide_vocal/" + guide.getId();
		}
		return "redirect:/auth/login";
    }
	
	@ApiOperation(value = "가이드 보컬 템플릿을 반환", response = String.class)
	@GetMapping("/guide_vocal/{id}")
	public String guideVocalDetail(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/guide_vocal", member, request);
		GuideVocal guide = guideVocalService.getGuideVocalById(id);
		if (member == null) return "redirect:/auth/login";
		if (guide == null || 0 != member.getId().compareTo(guide.getMember().getId())) return "fragments/page/error/error-404";
		boolean isOwner = false;
		if(guide.getMember().getId().compareTo(member.getId()) == 0){
			if(guide.getGuideStatus() == MusicTrackStatus.BEFORE_EXAM || guide.getGuideStatus() == MusicTrackStatus.UNDER_EXAM || guide.getGuideStatus() == MusicTrackStatus.REJECTED){
				isOwner = true;
			}
		}

		if (member != null) {
			model.addAttribute("currentUserId", member.getId());
			model.addAttribute("isOwner", isOwner);
		} else {
			model.addAttribute("currentUserId", 0);
			model.addAttribute("isOwner", isOwner);
		}
		
		model.addAttribute("guide", guide);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/guide_vocal_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/guide_vocal_detail_mobile";
		}
		return "fragments/page/music/guide_vocal_detail";
	}
	
	@ApiOperation(value = "가이드 보컬 편집 템플릿을 반환", response = String.class)
    @GetMapping("/guide_vocal/{id}/edit")
    public String editGuideVocal(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/guide_vocal/edit", member, request);
		GuideVocal guide = guideVocalService.getGuideVocalById(id);
		if (member != null && 0 == (guide.getMember().getId().compareTo(member.getId()))) {
			Set<GuideVocalScope> vocalGuideScopes = guide.getVocalGuideScopes();
			for (GuideVocalScope scope : vocalGuideScopes) {
				if (1 == scope.getId()) {
					guide.setGuideScopeGuide(true);
				} else if (2 == scope.getId()) {
					guide.setGuideScopeChorus(true);
				} else if (3 == scope.getId()) {
					guide.setGuideScopeRap(true);
				}
			}
			model.addAttribute("guideform", guide);
			
			String userAgent = request.getHeader("user-agent");
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/guide_vocal_edit_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/guide_vocal_edit_mobile";
			}
			return "fragments/page/music/guide_vocal_edit";
		}
		return "redirect:/auth/login";
    }
	
	@ApiOperation(value = "가이드 보컬 편집 템플릿을 처리", response = String.class)
    @PostMapping("/guide_vocal/{id}")
    public String updateGuideVocal(@ModelAttribute("guideform") GuideVocal guideform, @PathVariable Long id, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		GuideVocal guide = guideVocalService.getGuideVocalById(id);
		Member member = super.getCurrentUser(principal);
		if (member != null && 0 == (guide.getMember().getId().compareTo(member.getId()))) {
			guideform.setMember(member);
			guideValidator.validate(guideform, bindingResult);
			if (bindingResult.hasErrors()) {
				super.setSubMenu(model, "/music/artist", member, request);
				return "fragments/page/music/guide_vocal_edit";
			}
			// 가이드 보컬 저장
			guide.setAge(guideform.getAge());
			guide.setArea(guideform.getArea());
			guide.setCloseHour(guideform.getCloseHour());
			guide.setCost(guideform.getCost());
			guide.setDescription(guideform.getDescription());
			guide.setFilelink(guideform.getFilelink());
			guide.setFilename(UPMusicHelper.makeReadableUrl(guideform.getFilename()));
			guide.setGenre(guideform.getGenre());
			guide.setOpenHour(guideform.getOpenHour());
			guide.setGuideStatus(MusicTrackStatus.UNDER_EXAM);
			guide.resetGuideVocalScopes();
			if (guideform.isGuideScopeGuide()) guide.addGuideVocalScope(guideScopeService.getGuideVocalScopeById(1));
			if (guideform.isGuideScopeChorus()) guide.addGuideVocalScope(guideScopeService.getGuideVocalScopeById(2));
			if (guideform.isGuideScopeRap()) guide.addGuideVocalScope(guideScopeService.getGuideVocalScopeById(3));
			guideVocalService.saveGuideVocal(guide);
			if (guideform.getFileMultipart() != null && !StringUtils.isEmpty(guideform.getFileMultipart().getOriginalFilename())) azureStorageService.uploadResource(guideform.getFileMultipart(), "guides/" + guide.getId() + "/");
			return "redirect:/music/guide_vocal/" + id;
		}
		return "redirect:/auth/login";
    }
	
	// --------------------------------------------------------------------------------------------
	// VOCAL CASTING

	@ApiOperation(value = "보컬 캐스팅 템플릿을 반환", response = String.class)
	@GetMapping("/vocal_casting")
	public String vocalCasting(Principal principal, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/music/vocal_casting", super.getCurrentUser(principal), request);
		Page<VocalCasting> castings = vocalCastingService.findAllWithCommentsCount(super.getPageVocalCastingOrderByNew(0));
		model.addAttribute("castingsFragmentId", "store-request-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("castings", castings);
		model.addAttribute("paginationUrl", "/component/music/vocal_casting");
		model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/vocal_casting_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/vocal_casting_mobile";
		}
		return "fragments/page/music/vocal_casting";
	}
	
	@ApiOperation(value = "보컬 캐스팅 등록 템플릿을 반환", response = String.class)
	@GetMapping("/vocal_casting/new/casting")
	public String newVocalCasting(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/vocal_casting", member, request);
		if (member != null) {
			VocalCasting casting = new VocalCasting();
			casting.setMember(member);
			model.addAttribute("castingform", casting);
			
			String userAgent = request.getHeader("user-agent");
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/vocal_casting_new_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/vocal_casting_new_mobile";
			}
			return "fragments/page/music/vocal_casting_new";
		}
		return "redirect:/auth/login";
	}
	
	@ApiOperation(value = "보컬 캐스팅 등록 템플릿을 처리", response = String.class)
    @PostMapping("/vocal_casting/new/casting")
    public String createVocalCasting(@ModelAttribute("castingform") VocalCasting castingform, BindingResult bindingResult, Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		if (member != null) {
			castingform.setMember(member);
			if (castingform.getFileMultipart() == null || StringUtils.isEmpty(castingform.getFileMultipart().getOriginalFilename())) castingform.setFilename(null);
			castingValidator.validate(castingform, bindingResult);
			if (bindingResult.hasErrors()) {
				super.setSubMenu(model, "/music/vocal_casting", member, request);
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
					model.addAttribute("userAgent", userAgent);
					return "fragments/page/music/vocal_casting_new_mobile";
				} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
					return "fragments/page/music/vocal_casting_new_mobile";
				}
				return "fragments/page/music/vocal_casting_new";
			}
			// 보컬 캐스팅 저장
			castingform.setFilename(UPMusicHelper.makeReadableUrl(castingform.getFilename()));
			VocalCasting casting = vocalCastingService.saveVocalCasting(castingform);
			if (castingform.getFileMultipart() != null && !StringUtils.isEmpty(castingform.getFileMultipart().getOriginalFilename())) azureStorageService.uploadResource(castingform.getFileMultipart(), "castings/" + casting.getId() + "/");
			return "redirect:/music/vocal_casting/" + casting.getId();
		}
		return "redirect:/auth/login";
    }
	
	@ApiOperation(value = "보컬 캐스팅 상세페이지 템플릿을 반환", response = String.class)
    @GetMapping("/vocal_casting/{id}")
    public String vocalCastingDetail(@PathVariable Long id, Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) {
		VocalCasting casting = vocalCastingService.getVocalCastingById(id);
		if (casting == null) return "fragments/page/error/error-404";
		
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/vocal_casting/detail", member, request);
		model.addAttribute("casting", casting);
		model.addAttribute("castingComments", vocalCastingService.getComments(id, super.getCommentsOrderByNew(0)));
    	model.addAttribute("paginationUrl", "/component/music/vocal_casting/" + id + "/comments");
		try {
			super.setMetaTag(model, casting.getMetaTag(UPMusicHelper.baseUrl(request)));
			model.addAttribute("shareUrl", String.format("%s/music/vocal_casting/%d", UPMusicHelper.baseUrl(request), id));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (member != null) {
			model.addAttribute("currentUserId", member.getId());
			model.addAttribute("isOwner", 0 == (casting.getMember().getId().compareTo(member.getId())));
			model.addAttribute("likedVocalCasting", vocalCastingService.likedVocalCasting(id, member.getId()));
		} else {
			model.addAttribute("currentUserId", 0);
			model.addAttribute("isOwner", false);
			model.addAttribute("likedVocalCasting", false);
		}
		
		// 쿠키 - 조회수 증가
		boolean existsCookie = false;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(UPMusicConstants.COOKIE_CASTING + id)) existsCookie = true;
			}
		}

	    if (!existsCookie) {
	    	vocalCastingService.increaseHitCnt(id);
			Cookie cookie = new Cookie(UPMusicConstants.COOKIE_CASTING + id, "hit");
	        cookie.setMaxAge(60 * 60 * 24);
	        response.addCookie(cookie);
	    }
		
	    String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/music/vocal_casting_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/music/vocal_casting_detail_mobile";
		}
		return "fragments/page/music/vocal_casting_detail";
    }
	
	@ApiOperation(value = "보컬 캐스팅 편집 템플릿을 반환", response = String.class)
    @GetMapping("/vocal_casting/{id}/edit")
    public String editVocalCasting(@PathVariable Long id, Principal principal, Model model, HttpServletRequest request) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/music/vocal_casting/edit", member, request);
		VocalCasting casting = vocalCastingService.getVocalCastingById(id);
		String userAgent = request.getHeader("user-agent");
		if (member != null && 0 == (casting.getMember().getId().compareTo(member.getId()))) {
			model.addAttribute("castingform", casting);
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/vocal_casting_edit_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/vocal_casting_edit_mobile";
			}
			return "fragments/page/music/vocal_casting_edit";
		}
		return "redirect:/auth/login";
    }
	
	@ApiOperation(value = "보컬 캐스팅 편집 템플릿을 처리", response = String.class)
    @PostMapping("/vocal_casting/{id}")
    public String updateVocalCasting(@ModelAttribute("castingform") VocalCasting castingform, @PathVariable Long id, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		VocalCasting casting = vocalCastingService.getVocalCastingById(id);
		Member member = super.getCurrentUser(principal);
		if (member != null && 0 == (casting.getMember().getId().compareTo(member.getId()))) {
			castingform.setMember(member);
			castingValidator.validate(castingform, bindingResult);
			if (bindingResult.hasErrors()) {
				super.setSubMenu(model, "/music/vocal_casting", member, request);
				return "fragments/page/music/vocal_casting_edit";
			}
			// 보컬 캐스팅 저장
			casting.setDescription(castingform.getDescription());
			casting.setSubject(castingform.getSubject());
			casting.setFilename(UPMusicHelper.makeReadableUrl(castingform.getFilename()));
			vocalCastingService.saveVocalCasting(casting);
			if (castingform.getFileMultipart() != null && !StringUtils.isEmpty(castingform.getFileMultipart().getOriginalFilename())) azureStorageService.uploadResource(castingform.getFileMultipart(), "castings/" + casting.getId() + "/");
			return "redirect:/music/vocal_casting/" + id;
		}
		return "redirect:/auth/login";
    }

	private void initNiceID(HttpServletRequest request, Model model) throws MalformedURLException {
		NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
		String sSiteCode = "BH326";			// NICE로부터 부여받은 사이트 코드
		String sSitePassword = "y1Eqj2m6lbWT";		// NICE로부터 부여받은 사이트 패스워드
		String sRequestNumber = "REQ0000000001";        	// 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로
		// 업체에서 적절하게 변경하여 쓰거나, 아래와 같이 생성한다.
		sRequestNumber = niceCheck.getRequestNO(sSiteCode);
		httpSession.setAttribute("REQ_SEQ" , sRequestNumber);	// 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.
		String sAuthType = "M";      	// 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서
		String popgubun 	= "N";		//Y : 취소버튼 있음 / N : 취소버튼 없음
		String customize 	= "";		//없으면 기본 웹페이지 / Mobile : 모바일페이지
		String sGender = ""; 			//없으면 기본 선택 값, 0 : 여자, 1 : 남자

		// CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
		//리턴url은 인증 전 인증페이지를 호출하기 전 url과 동일해야 합니다. ex) 인증 전 url : http://www.~ 리턴 url : http://www.~
		String sReturnUrl = UPMusicHelper.baseUrl(request) + "/auth/cellphoneSuccess";      // 성공시 이동될 URL
		String sErrorUrl = UPMusicHelper.baseUrl(request) + "/auth/cellphoneFail";          // 실패시 이동될 URL

		// 입력될 plain 데이타를 만든다.
		String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
				"8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
				"9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
				"7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
				"7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
				"11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
				"9:CUSTOMIZE" + customize.getBytes().length + ":" + customize +
				"6:GENDER" + sGender.getBytes().length + ":" + sGender;

		String sMessage = "";
		String sEncData = "";

		int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
		if( iReturn == 0 ) {
			sEncData = niceCheck.getCipherData();
		} else if( iReturn == -1) {
			sMessage = "암호화 시스템 에러입니다.";
		} else if( iReturn == -2) {
			sMessage = "암호화 처리오류입니다.";
		} else if( iReturn == -3) {
			sMessage = "암호화 데이터 오류입니다.";
		} else if( iReturn == -9) {
			sMessage = "입력 데이터 오류입니다.";
		} else {
			sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
		}
		model.addAttribute("sMessage", sMessage);
		model.addAttribute("sEncData", sEncData);
	}

	private String requestReplace (String paramValue, String gubun) {
		String result = "";
		if (paramValue != null) {
			paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			paramValue = paramValue.replaceAll("\\*", "");
			paramValue = paramValue.replaceAll("\\?", "");
			paramValue = paramValue.replaceAll("\\[", "");
			paramValue = paramValue.replaceAll("\\{", "");
			paramValue = paramValue.replaceAll("\\(", "");
			paramValue = paramValue.replaceAll("\\)", "");
			paramValue = paramValue.replaceAll("\\^", "");
			paramValue = paramValue.replaceAll("\\$", "");
			paramValue = paramValue.replaceAll("'", "");
			paramValue = paramValue.replaceAll("@", "");
			paramValue = paramValue.replaceAll("%", "");
			paramValue = paramValue.replaceAll(";", "");
			paramValue = paramValue.replaceAll(":", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll("#", "");
			paramValue = paramValue.replaceAll("--", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll(",", "");
			if (gubun != "encodeData") {
				paramValue = paramValue.replaceAll("\\+", "");
				paramValue = paramValue.replaceAll("/", "");
				paramValue = paramValue.replaceAll("=", "");
			}
			result = paramValue;
		}
		return result;
	}
	
}

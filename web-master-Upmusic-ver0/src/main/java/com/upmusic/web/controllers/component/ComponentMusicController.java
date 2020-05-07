package com.upmusic.web.controllers.component;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.LeagueSeasonPeriod;
import com.upmusic.web.domain.*;
import com.upmusic.web.services.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/component/music")
@Api(value="component.music", description="뮤직 컴포넌트를 담당하는 컨트롤러")
public class ComponentMusicController extends ComponentAbstractController {

	@Autowired
	private MusicAlbumService albumService;
	
	@Autowired
	private MusicTrackService trackService;
	
	@Autowired
    private LeagueSeasonService leagueSeasonService;
	
	@Autowired
	private MusicRequestService musicRequestService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private VocalCastingService vocalCastingService;

	@Autowired
	private VideoService videoService;
	
	// --------------------------------------------------------------------------------------------
	// REWARD
    
    @ApiOperation(value = "리워드 실시간 컴포넌트를 반환", response = String.class)
    @GetMapping("/reward_realtime")
    public String rewardRealtime(Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	Page<MusicTrack> tracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	model.addAttribute("tracksFragmentId", "reward-realtime-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("tracks", tracks);
    	model.addAttribute("paginationUrl", "/component/music/reward_realtime");
    	model.addAttribute("pageNo", page);
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		}
    	return "fragments/component/music/track_list_reward";
    }
    
    @ApiOperation(value = "리워드 장르 컴포넌트를 반환", response = String.class)
    @GetMapping("/reward_genre")
    public String rewardGenre(Principal principal, HttpServletRequest request, Model model) {
    	int genreId = UPMusicConstants.DEFAULT_GENRE;
    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	Page<MusicTrack> tracks;
    	if(genreId == 0) {
    		tracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	}else tracks = trackService.findAllByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	model.addAttribute("tracksFragmentId", "reward-genre-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("tracks", tracks);
    	model.addAttribute("paginationUrl", "/component/music/reward_genre");
    	model.addAttribute("pageNo", page);
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		}
        return "fragments/component/music/track_list_reward";
    }
    
    @ApiOperation(value = "리워드 테마 컴포넌트를 반환", response = String.class)
    @GetMapping("/reward_theme")
    public String rewardTheme(Principal principal, HttpServletRequest request, Model model) {
    	int themeId = UPMusicConstants.DEFAULT_THEME;
    	if (!StringUtils.isEmpty(request.getParameter("theme"))) themeId = Integer.valueOf(request.getParameter("theme"));
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Member member = super.getCurrentUser(principal);
    	Page<MusicTrack> tracks;
    	if(themeId == 0) {
    		tracks = trackService.findAllWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	}else tracks = trackService.findAllByThemeWithHeartByMember(themeId, member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	model.addAttribute("tracksFragmentId", "reward-theme-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("tracks", tracks);
    	model.addAttribute("paginationUrl", "/component/music/reward_theme");
    	model.addAttribute("pageNo", page);
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		}
        return "fragments/component/music/track_list_reward";
    }
    
    // --------------------------------------------------------------------------------------------
    // LEAGUE
    
    @ApiOperation(value = "리그 top 50 컴포넌트를 반환", response = String.class)
    @GetMapping("/league_top50")
    public String leagueTop50(Principal principal, HttpServletRequest request, Model model) throws ParseException {
    	Member member = super.getCurrentUser(principal);
    	LeagueSeasonPeriod period = UPMusicConstants.DEFAULT_PERIOD;
    	if (!StringUtils.isEmpty(request.getParameter("period"))) period = LeagueSeasonPeriod.valueOf(request.getParameter("period"));
    	String dateString = "";
    	if (!StringUtils.isEmpty(request.getParameter("date"))) dateString = request.getParameter("date");
    	if (StringUtils.isEmpty(dateString)) {
			List<String> days = leagueSeasonService.listDaysOfDailyChart();
			if (0 < days.size()) dateString = days.get(0);
			else dateString = "2018-01-01"; // 지난 데이터 없으므로 임의 날짜 지정
		}
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    	switch(period) {
		case ALL:
			List<LeagueSeasonTrack> tracks = leagueSeasonService.listTop50ByCurrentSeason(member != null ? member.getId() : 0);
	    	model.addAttribute("tracks", tracks);
			break;
		case DAILY:
			List<LeagueDailyChart> tracksDaily = leagueSeasonService.listTop50DailyChartByCurrentSeason(member != null ? member.getId() : 0, date);
	    	model.addAttribute("tracks", tracksDaily);
			break;
		case WEEKLY:
			List<LeagueWeeklyChart> tracksWeekly = leagueSeasonService.listTop50WeeklyChartByCurrentSeason(member != null ? member.getId() : 0, date);
	    	model.addAttribute("tracks", tracksWeekly);
			break;
		case MONTHLY:
			List<LeagueMonthlyChart> tracksMonthly = leagueSeasonService.listTop50MonthlyChartByCurrentSeason(member != null ? member.getId() : 0, date);
	    	model.addAttribute("tracks", tracksMonthly);
			break;
		}
    	
    	model.addAttribute("tracksFragmentId", "league-top50-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("paginationUrl", "/component/music/league_top50");
    	model.addAttribute("pageNo", 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/component/music/track_list_league_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/track_list_league_mobile";
		}
    	return "fragments/component/music/track_list_league";
    }
    
    @ApiOperation(value = "리그 장르별 차트 컴포넌트를 반환", response = String.class)
    @GetMapping("/league_genre")
    public String leagueGenre(Principal principal, HttpServletRequest request, Model model) throws ParseException {
    	Member member = super.getCurrentUser(principal);
    	LeagueSeasonPeriod period = UPMusicConstants.DEFAULT_PERIOD;
    	if (!StringUtils.isEmpty(request.getParameter("period"))) period = LeagueSeasonPeriod.valueOf(request.getParameter("period"));
    	int genreId = UPMusicConstants.DEFAULT_GENRE;
    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
    	String dateString = "";
    	if (!StringUtils.isEmpty(request.getParameter("date"))) dateString = request.getParameter("date");
    	if (StringUtils.isEmpty(dateString)) {
			List<String> days = leagueSeasonService.listDaysOfDailyChart();
			if (0 < days.size()) dateString = days.get(0);
			else dateString = "2018-01-01"; // 지난 데이터 없으므로 임의 날짜 지정
		}
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	
    	switch(period) {
		case ALL:
			Page<LeagueSeasonTrack> tracks = leagueSeasonService.listAllByCurrentSeasonAndGenre(member != null ? member.getId() : 0, genreId, super.getPageMusicOrderByHot(page));
			model.addAttribute("tracks", tracks);
			break;
		case DAILY:
			Page<LeagueDailyChart> tracksDaily = leagueSeasonService.listAllDailyChartByCurrentSeasonAndGenre(member != null ? member.getId() : 0, date, genreId, super.getPageMusicOrderByHot(page));
	    	model.addAttribute("tracks", tracksDaily);
			break;
		case WEEKLY:
			Page<LeagueWeeklyChart> tracksWeekly = leagueSeasonService.listAllWeeklyChartByCurrentSeasonAndGenre(member != null ? member.getId() : 0, date, genreId, super.getPageMusicOrderByHot(page));
	    	model.addAttribute("tracks", tracksWeekly);
			break;
		case MONTHLY:
			Page<LeagueMonthlyChart> tracksMonthly = leagueSeasonService.listAllMonthlyChartByCurrentSeasonAndGenre(member != null ? member.getId() : 0, date, genreId, super.getPageMusicOrderByHot(page));
	    	model.addAttribute("tracks", tracksMonthly);
			break;
		}
    	
    	model.addAttribute("tracksFragmentId", "league-genre-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("paginationUrl", "/component/music/league_genre");
    	model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_league_mobile";
			} else {
				return "fragments/component/music/track_list_league_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_league_mobile";
			} else {
				return "fragments/component/music/track_list_league_item_mobile";
			}
		}
    	return "fragments/component/music/track_list_league";
    }
    
 	@ApiOperation(value = "연도별 리그 시즌 컴포넌트를 반환", response = String.class)
 	@GetMapping("/league_seasons_of_year")
 	public String leagueSeasonsOfYear(Principal principal, HttpServletRequest request, Model model) {
 		Member member = super.getCurrentUser(principal);
 		Date year = new Date();
    	if (!StringUtils.isEmpty(request.getParameter("year"))) {
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            try {
            	year = formatter.parse(request.getParameter("year"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
    	}
 		List<LeagueSeason> seasons = leagueSeasonService.listAYearLeagueSeasonsOrderByOpenDate(year);
 		model.addAttribute("seasonMap", seasons);
 		if (0 < seasons.size()) {
 			LeagueSeason season = seasons.get(seasons.size()-1);
 			model.addAttribute("defaultSeason", season.getId());
 			// current season
 			if (season.getOpenDate().before(new Date()) && season.getCloseDate().after(new Date())) {
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
 		SimpleDateFormat df = new SimpleDateFormat("yyyy");
 		String yearFocus = df.format(year);
 		model.addAttribute("year", yearFocus);
 		List<String> years = leagueSeasonService.listYearsOfAllLeagueSeasons();
 		model.addAttribute("league_years", years);
 		model.addAttribute("seasonTracksFragmentId", "league-season-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
 		model.addAttribute("seasonPaginationUrl", "/component/music/league_season");

 		return "fragments/component/music/league_seasons_of_year";
 	}
    
    @ApiOperation(value = "리그 시즌별 차트 컴포넌트를 반환", response = String.class)
    @GetMapping("/league_season")
    public String leagueSeason(Principal principal, HttpServletRequest request, Model model) throws ParseException {
    	Member member = super.getCurrentUser(principal);
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	String seasonString = request.getParameter("season");
    	if (!StringUtils.isEmpty(seasonString)) {
    		Long seasonId = Long.valueOf(seasonString);
    		LeagueSeason season = leagueSeasonService.getLeagueSeasonById(seasonId);
    		if (season.getOpenDate().before(new Date()) && season.getCloseDate().after(new Date())) {
				Page<LeagueSeasonTrack> seasonTracks = leagueSeasonService.listAllByCurrentSeason(member != null ? member.getId() : 0, super.getPageMusicOrderByHot(page));
				model.addAttribute("tracks", seasonTracks);
			} else {
				Page<LeagueSeasonChart> seasonTracks = leagueSeasonService.listAllSeasonChartBySeason(member != null ? member.getId() : 0, season, super.getPageMusicOrderByHot(page));
				model.addAttribute("tracks", seasonTracks);
			}
    	}
    	
    	model.addAttribute("tracksFragmentId", "league-season-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("paginationUrl", "/component/music/league_season");
    	model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_league_mobile";
			} else {
				return "fragments/component/music/track_list_league_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_league_mobile";
			} else {
				return "fragments/component/music/track_list_league_item_mobile";
			}
		}
    	return "fragments/component/music/track_list_league";
    }
    
    // --------------------------------------------------------------------------------------------
 	// STORE
    
    @ApiOperation(value = "스토어 장르 컴포넌트를 반환", response = String.class)
    @GetMapping("/store")
    public String store(Principal principal, HttpServletRequest request, Model model) {
    	int genreId = UPMusicConstants.DEFAULT_GENRE;
		try{
			genreId = Integer.valueOf(request.getParameter("genre"));
		} catch (Exception e){
			genreId = UPMusicConstants.DEFAULT_GENRE;
		}
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))){
			page = Integer.valueOf(request.getParameter("page"));
		}
    	Member member = super.getCurrentUser(principal);
//    	Page<MusicTrack> tracks = trackService.findAllByGenreAndInStoreTrueWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	Page<MusicTrack> tracks;
    	if(genreId == 0) {
    		tracks = trackService.findAllInStoreTrueWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	}else tracks = trackService.findAllByGenreAndInStoreTrueWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicOrderByNew(page));
    	model.addAttribute("tracksFragmentId", "store-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
    	model.addAttribute("tracks", tracks);
    	model.addAttribute("paginationUrl", "/component/music/store");
    	model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_store_mobile";
			} else {
				return "fragments/component/music/track_list_store_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_store_mobile";
			} else {
				return "fragments/component/music/track_list_store_item_mobile";
			}
		}
        return "fragments/component/music/track_list_store";
    }
    
    @ApiOperation(value = "제작의뢰 컴포넌트를 반환", response = String.class)
    @GetMapping("/store/request")
    public String request(Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	Page<MusicRequest> requests = musicRequestService.findAll(super.getPageRequestOrderByNew(page));
		model.addAttribute("requestsFragmentId", "store-request-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("requests", requests);
		model.addAttribute("requestPaginationUrl", "/component/music/store/request");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/request_list_store_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/request_list_store_item_mobile";
		}
        return "fragments/component/music/request_list_store";
    }
    
    @ApiOperation(value = "제작의뢰의 댓글 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/store/request/{id}/comments")
    public String requestComments(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	model.addAttribute("comments", musicRequestService.getComments(id, super.getCommentsOrderByNew(page)));
    	Member member = super.getCurrentUser(principal);
		model.addAttribute("currentUserId", member != null ? member.getId() : 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/comment_list_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/comment_list_mobile";
		}
        return "fragments/component/music/comment_list";
    }
    
    // --------------------------------------------------------------------------------------------
 	// ALBUM

 	@ApiOperation(value = "싱글 컴포넌트를 반환", response = String.class)
 	@GetMapping("/album/sa")
 	public String sAlbum(Principal principal, HttpServletRequest request, Model model) {
 		int genreId = UPMusicConstants.DEFAULT_GENRE;
		try{
			genreId = Integer.valueOf(request.getParameter("genre"));
		} catch (Exception e){
			genreId = UPMusicConstants.DEFAULT_GENRE;
		}
 		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
 		Member member = super.getCurrentUser(principal);
 		Page<MusicAlbum> sAlbums;
 		if(genreId == 0) {
   		 sAlbums = albumService.findAllSAWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
 		}else  sAlbums = albumService.findAllSAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
 		model.addAttribute("fragementId", "album-list-sa");
 		model.addAttribute("albums", sAlbums);
 		model.addAttribute("paginationUrl", "/component/music/album/sa");
 		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/album_list_mobile";
			} else {
				return "fragments/component/music/album_list_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/album_list_mobile";
			} else {
				return "fragments/component/music/album_list_item_mobile";
			}
		}
 		return "fragments/component/music/album_list";
 	}
 	
 	@ApiOperation(value = "앨범 컴포넌트를 반환", response = String.class)
 	@GetMapping("/album/ga")
 	public String gAlbum(Principal principal, HttpServletRequest request, Model model) {
 		int genreId = UPMusicConstants.DEFAULT_GENRE;
		try{
			genreId = Integer.valueOf(request.getParameter("genre"));
		} catch (Exception e){
			genreId = UPMusicConstants.DEFAULT_GENRE;
		}
 		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
 		Member member = super.getCurrentUser(principal);
 		Page<MusicAlbum> gAlbums;
 		if(genreId == 0) {
   		 	gAlbums = albumService.findAllGAWithHeartByMember(member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
		}else gAlbums = albumService.findAllGAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
 		model.addAttribute("fragementId", "album-list-ga");
 		model.addAttribute("albums", gAlbums);
 		model.addAttribute("paginationUrl", "/component/music/album/ga");
 		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/album_list_mobile";
			} else {
				return "fragments/component/music/album_list_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/album_list_mobile";
			} else {
				return "fragments/component/music/album_list_item_mobile";
			}
		}
 		return "fragments/component/music/album_list";
 	}
 	
// 	@ApiOperation(value = "국내 싱글 컴포넌트를 반환", response = String.class)
// 	@GetMapping("/album_home/sa")
// 	public String sAlbumHome(Principal principal, HttpServletRequest request, Model model) {
// 		int genreId = UPMusicConstants.DEFAULT_GENRE;
//    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
// 		int page = 0;
//    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
// 		Member member = super.getCurrentUser(principal);
// 		Page<MusicAlbum> sAlbums = albumService.findHomeSAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
// 		model.addAttribute("fragementId", "album-list-sa");
// 		model.addAttribute("albums", sAlbums);
// 		model.addAttribute("paginationUrl", "/component/music/album_home/sa");
// 		return "fragments/component/music/album_list";
// 	}
// 	
// 	@ApiOperation(value = "국내 앨범 컴포넌트를 반환", response = String.class)
// 	@GetMapping("/album_home/ga")
// 	public String gAlbumHome(Principal principal, HttpServletRequest request, Model model) {
// 		int genreId = UPMusicConstants.DEFAULT_GENRE;
//    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
// 		int page = 0;
//    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
// 		Member member = super.getCurrentUser(principal);
// 		Page<MusicAlbum> gAlbums = albumService.findHomeGAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
// 		model.addAttribute("fragementId", "album-list-ga");
// 		model.addAttribute("albums", gAlbums);
// 		model.addAttribute("paginationUrl", "/component/music/album_home/ga");
// 		return "fragments/component/music/album_list";
// 	}
// 	
// 	@ApiOperation(value = "해외 싱글 컴포넌트를 반환", response = String.class)
// 	@GetMapping("/album_abroad/sa")
// 	public String sAlbumAbroad(Principal principal, HttpServletRequest request, Model model) {
// 		int genreId = UPMusicConstants.DEFAULT_GENRE;
//    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
// 		int page = 0;
//    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
// 		Member member = super.getCurrentUser(principal);
// 		Page<MusicAlbum> sAlbums = albumService.findAbroadSAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
// 		model.addAttribute("fragementId", "album-list-sa");
// 		model.addAttribute("albums", sAlbums);
// 		model.addAttribute("paginationUrl", "/component/music/album_abroad/sa");
// 		return "fragments/component/music/album_list";
// 	}
// 	
// 	@ApiOperation(value = "해외 앨범 컴포넌트를 반환", response = String.class)
// 	@GetMapping("/album_abroad/ga")
// 	public String gAlbumAbroad(Principal principal, HttpServletRequest request, Model model) {
// 		int genreId = UPMusicConstants.DEFAULT_GENRE;
//    	if (!StringUtils.isEmpty(request.getParameter("genre"))) genreId = Integer.valueOf(request.getParameter("genre"));
// 		int page = 0;
//    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
// 		Member member = super.getCurrentUser(principal);
// 		Page<MusicAlbum> gAlbums = albumService.findAbroadGAByGenreWithHeartByMember(genreId, member != null ? member.getId() : 0, super.getPageMusicAlbumOrderByNew(page));
// 		model.addAttribute("fragementId", "album-list-ga");
// 		model.addAttribute("albums", gAlbums);
// 		model.addAttribute("paginationUrl", "/component/music/album_abroad/ga");
// 		return "fragments/component/music/album_list";
// 	}
 	
 	@ApiOperation(value = "앨범의 댓글 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/album/{id}/comments")
    public String albumComments(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	model.addAttribute("comments", albumService.getComments(id, super.getCommentsOrderByNew(page)));
    	Member member = super.getCurrentUser(principal);
		model.addAttribute("currentUserId", member != null ? member.getId() : 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/comment_list_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/comment_list_mobile";
		}
        return "fragments/component/music/comment_list";
    }
 	
 	// --------------------------------------------------------------------------------------------
  	// ARTIST
 	
 	@ApiOperation(value = "아티스트 컴포넌트를 반환", response = String.class)
 	@GetMapping("/artist")
 	public String artist(Principal principal, HttpServletRequest request, Model model) {
 		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
 		Member member = super.getCurrentUser(principal);
		Page<Member> artists = memberService.findAllArtistWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByHot(page));
		model.addAttribute("artistsFragmentId", "artist-home-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("artists", artists);
		model.addAttribute("paginationUrl", "/component/music/artist");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/artist_list_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/artist_list_item_mobile";
		}
		return "fragments/component/music/artist_list";
 	}
 	
 	@ApiOperation(value = "패밀리 아티스트 컴포넌트를 반환", response = String.class)
 	@GetMapping("/family_artist")
 	public String familyArtist(Principal principal, HttpServletRequest request, Model model) {
 		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
 		Member member = super.getCurrentUser(principal);
		Page<Member> artists = memberService.findAllFamilyArtistWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByHot(page));
		model.addAttribute("artistsFragmentId", "artist-family-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("artists", artists);
		model.addAttribute("paginationUrl", "/component/music/family_artist");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/artist_list_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/artist_list_item_mobile";
		}
		return "fragments/component/music/artist_list";
 	}
 	
 	@ApiOperation(value = "신규 아티스트 컴포넌트를 반환", response = String.class)
 	@GetMapping("/new_artist")
 	public String newArtist(Principal principal, HttpServletRequest request, Model model) {
 		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
 		Member member = super.getCurrentUser(principal);
		Page<Member> artists = memberService.findAllNewArtistWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByNew(page));
		model.addAttribute("artistsFragmentId", "artist-new-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("artists", artists);
		model.addAttribute("paginationUrl", "/component/music/new_artist");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/artist_list_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/artist_list_item_mobile";
		}
		return "fragments/component/music/artist_list";
 	}
 	
 	@ApiOperation(value = "가이드 보컬 컴포넌트를 반환", response = String.class)
 	@GetMapping("/guide_vocal")
 	public String guideVocal(Principal principal, HttpServletRequest request, Model model) {
 		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
 		Member member = super.getCurrentUser(principal);
		Page<Member> artists = memberService.findAllGuideVocalWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByHot(page));
		model.addAttribute("artistsFragmentId", "artist-guide-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("artists", artists);
		model.addAttribute("paginationUrl", "/component/music/guide_vocal");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/artist_list_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/artist_list_item_mobile";
		}
		return "fragments/component/music/artist_list";
 	}

	@ApiOperation(value = "아티스트 음원 컴포넌트를 반환", response = String.class)
	@GetMapping("/artist_track")
	public String artistTrack(Principal principal, HttpServletRequest request, Model model) {
		int page = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		Member member = super.getCurrentUser(principal);
		Page<MusicTrack> tracks;
		if (!StringUtils.isEmpty(request.getParameter("artist_id"))) {
			long artist_id = 0;
			artist_id = Integer.valueOf(request.getParameter("artist_id"));
			tracks = trackService.findAllByArtistIdWithHeartByMember(artist_id, member != null ? member.getId() : 0, PageRequest.of(page, 50, Sort.Direction.DESC, "id"));
		}else tracks = trackService.findAllByArtistIdWithHeartByMember(member.getId(), member != null ? member.getId() : 0, PageRequest.of(page, 50, Sort.Direction.DESC, "id"));

		model.addAttribute("tracksFragmentId", "artist-tracks-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("tracks", tracks);
		model.addAttribute("paginationUrl", "/component/music/artist_track");
		model.addAttribute("pageNo", page);

		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		}
		return "fragments/component/music/track_list_reward";
	}

	@ApiOperation(value = "아티스트 앨범 컴포넌트를 반환", response = String.class)
	@GetMapping("/artist_album")
	public String artistAlbum(Principal principal, HttpServletRequest request, Model model) {
		int page = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		Member member = super.getCurrentUser(principal);
		Page<MusicAlbum> albums;
		if (!StringUtils.isEmpty(request.getParameter("artist_id"))) {
			long artist_id = 0;
			artist_id = Integer.valueOf(request.getParameter("artist_id"));
			albums = albumService.findAllByArtistIdWithHeartByMember(artist_id, member != null ? member.getId() : 0, PageRequest.of(page, 20, Sort.Direction.DESC, "id"));
		}else albums = albumService.findAllByArtistIdWithHeartByMember(member.getId(), member != null ? member.getId() : 0, PageRequest.of(page, 20, Sort.Direction.DESC, "id"));

		model.addAttribute("fragmentId", "artist-albums-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("albums", albums);
		model.addAttribute("paginationUrl", "/component/music/artist_album");
		model.addAttribute("pageNo", page);

		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/album_list_mobile";
			} else {
				return "fragments/component/music/album_list_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/album_list_mobile";
			} else {
				return "fragments/component/music/album_list_item_mobile";
			}
		}
		return "fragments/component/music/album_list";
	}

	@ApiOperation(value = "아티스트 비디오 컴포넌트를 반환", response = String.class)
	@GetMapping("/artist_video")
	public String artistVideo(Principal principal, HttpServletRequest request, Model model) {
		int page = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		Member member = super.getCurrentUser(principal);
		Page<Video> videos;
		if (!StringUtils.isEmpty(request.getParameter("artist_id"))) {
			long artist_id = 0;
			artist_id = Integer.valueOf(request.getParameter("artist_id"));
			videos = videoService.findAllByMemberId(artist_id, super.getPageVideoOrderByNew(page));
		}else videos = videoService.findAllByMemberId(member.getId(), super.getPageVideoOrderByNew(page));

		model.addAttribute("fragmentId", "artist-videos-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("videos", videos);
		model.addAttribute("paginationUrl", "/component/music/artist_video");
		model.addAttribute("pageNo", page);
		int pages = videos.getTotalPages();
		model.addAttribute("totalPages", pages);

		return "fragments/component/video/item_mobile";
	}

	@ApiOperation(value = "아티스트 뮤직스토어 컴포넌트를 반환", response = String.class)
	@GetMapping("/artist_store")
	public String artistStore(Principal principal, HttpServletRequest request, Model model) {
		int page = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		Member member = super.getCurrentUser(principal);
		Page<MusicTrack> storeTracks;
		if (!StringUtils.isEmpty(request.getParameter("artist_id"))) {
			long artist_id = 0;
			artist_id = Integer.valueOf(request.getParameter("artist_id"));
			storeTracks = trackService.findAllByArtistIdAndInStoreTrueWithHeartByMember(artist_id, member != null ? member.getId() : 0, PageRequest.of(page, 50, Sort.Direction.DESC, "id"));
		}else storeTracks = trackService.findAllByArtistIdAndInStoreTrueWithHeartByMember(member.getId(), member != null ? member.getId() : 0, PageRequest.of(page, 50, Sort.Direction.DESC, "id"));

		model.addAttribute("tracksFragmentId", "artist-stores-fragment");
		model.addAttribute("tracks", storeTracks);
		model.addAttribute("paginationUrl", "/component/music/artist_store");
		model.addAttribute("pageNo", page);

		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			if (0 == page) {
				return "fragments/component/music/track_list_reward_mobile";
			} else {
				return "fragments/component/music/track_list_reward_item_mobile";
			}
		}
		return "fragments/component/music/track_list_reward";
	}
 	
 	// --------------------------------------------------------------------------------------------
   	// VOCAL CASTING
 	
 	@ApiOperation(value = "보컬 캐스팅 템플릿을 반환", response = String.class)
	@GetMapping("/vocal_casting")
	public String vocalCasting(Principal principal, HttpServletRequest request, Model model) {
 		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		Page<VocalCasting> castings = vocalCastingService.findAllWithCommentsCount(super.getPageVocalCastingOrderByNew(page));
		model.addAttribute("castingsFragmentId", "store-request-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("castings", castings);
		model.addAttribute("paginationUrl", "/component/music/vocal_casting");
		model.addAttribute("pageNo", page);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/component/music/vocal_casting_list_item_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/vocal_casting_list_item_mobile";
		}
		return "fragments/component/music/vocal_casting_list";
	}
 	
 	@ApiOperation(value = "보컬 캐스팅의 댓글 리스트 컴포넌트를 반환", response = String.class)
    @GetMapping("/vocal_casting/{id}/comments")
    public String castingComments(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) {
    	int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	model.addAttribute("comments", vocalCastingService.getComments(id, super.getCommentsOrderByNew(page)));
    	Member member = super.getCurrentUser(principal);
		model.addAttribute("currentUserId", member != null ? member.getId() : 0);
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			return "fragments/component/music/comment_list_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/component/music/comment_list_mobile";
		}
        return "fragments/component/music/comment_list";
    }
    
}

package com.upmusic.web.controllers.admin;

import com.google.gson.JsonArray;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.*;
import com.upmusic.web.services.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.message.PointTransactionResponse;
import com.upmusic.web.validator.LeagueSeasonValidator;


@Controller
@RequestMapping("/admin/music")
@Api(value = "admin.music", description = "관리자 뮤직 페이지를 담당하는 컨트롤러")
public class AdminMusicController extends AdminAbstractController {

	@Autowired
	private MusicTrackService trackService;
	
	@Autowired
	private RecommendedMediaService recommendedMediaService;
	
	@Autowired
	private LeagueSeasonService seasonService;
	
	@Autowired
	private LeagueSeasonValidator seasonValidator;
	
	@Autowired
	private AzureStorageService azureStorageService;
	
	@Autowired
	private PointTransactionService pointTransactionService;
	
	@Autowired
	private PointRewardConditionService pointRewardConditionService;
	
	@Autowired
	private GuideVocalService guideVocalService;

	@Autowired
	private MusicAlbumService albumService;

	@Autowired
	private LeagueSeasonService leagueSeasonService;
	

	// --------------------------------------------------------------------------------------------
	// 전체 음원

	@ApiOperation(value = "관리자 뮤직 페이지의 메인 템플릿을 반환", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"), @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@GetMapping()
	public String music(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/admin/music");
		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		model.addAttribute("tracksAll", trackService.findUploadedByUsers(member.getId(), super.getPageMusicOrderByNew(page)));
		model.addAttribute("tracksRecent", trackService.findUploadedByUsersByRecent(member.getId(), super.getPageMusicOrderByNew(page)));
		model.addAttribute("tracksUpleague", trackService.findUploadedByUsersByUpleague(member.getId(), super.getPageMusicOrderByNew(page)));
//		model.addAttribute("tracksMusicStore", trackService.findUploadedByUsersByMusicStore(member.getId(), super.getPageMusicOrderByNew(page)));

		return "fragments/admin/page/music/index";
	}
	
	// --------------------------------------------------------------------------------------------
	// 심사 대기 음원
	
	@ApiOperation(value = "사용자 심사 대기중인 곡 목록 페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/judging")
	public String judging(Principal principal, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/judging");
		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		model.addAttribute("tracksRecent", trackService.findByTrackStatusAndPublishedTrueByUserByRecent(super.getPageMusicOrderByNew(page)));
		model.addAttribute("tracksUpleague", trackService.findByTrackStatusAndPublishedTrueByUserByUpleague(super.getPageMusicOrderByNew(page)));
//		model.addAttribute("tracksMusicStore", trackService.findByTrackStatusAndPublishedTrueByUserByMusicStore(super.getPageMusicOrderByNew(page)));

		return "fragments/admin/page/music/judging";
	}
	
	@ApiOperation(value = "트랙 상세페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/track/{id}")
	public String trackDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
		MusicTrack track = trackService.getTrackById(id);
		if (track.getTrackStatus() == MusicTrackStatus.BEFORE_EXAM) {
			super.setSubMenu(model, "/admin/music/judging");
		} else {
			super.setSubMenu(model, "/admin/music");
		}
		model.addAttribute("track", track);
		model.addAttribute("BEFORE_EXAM", MusicTrackStatus.BEFORE_EXAM);
		model.addAttribute("recommended", recommendedMediaService.recommendedTrack(id));
		
		return "fragments/admin/page/music/track_detail";
	}
	
	@ApiOperation(value = "id에 해당하는 곡의 심사 승인",response = MusicTrack.class)
    @RequestMapping(value = "/track/{id}/accept", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> acceptMusicTrack(@PathVariable Long id, Model model) {
		APIResponse response = new APIResponse(false, "곡이 존재하지 않습니다", id);
		MusicTrack track = trackService.getTrackById(id);
		if (track != null) {
			if (MusicTrackStatus.ACCEPTED != track.getTrackStatus()) {
				track = trackService.setTrackStatus(id, MusicTrackStatus.ACCEPTED);
				PointRewardCondition condition = pointRewardConditionService.getCondition();
	    		PointTransactionResponse pointTransactionResponse = pointTransactionService.accumulatePoints(track.getArtistId(), PointTransactionType.UPLOAD, id, condition.getUploadPoint());
	        	response = new APIResponse(pointTransactionResponse.isStatus(), pointTransactionResponse.getMessage(), id);
			} else {
				response = new APIResponse(false, "이미 승인된 곡입니다", id);
			}
		}
		return ResponseEntity.ok(response);
    }

	@ApiOperation(value = "id에 해당하는 곡들의 심사 승인",response = MusicTrack.class)
	@RequestMapping(value = "/tracks/accept", method= RequestMethod.POST)
	public ResponseEntity<APIResponse> acceptMusicTracks(@RequestBody String[] ids, Model model) {
		APIResponse response = new APIResponse(false, "심사 승인에 실패하였습니다.", ids[0]);
		Set<Long> longIds = new HashSet<Long>();
		for(String id : ids){
			longIds.add(Long.parseLong(id));
		}

		for(Long id : longIds){
			MusicTrack track = trackService.getTrackById(id);
			if (track != null) {
				if (MusicTrackStatus.ACCEPTED != track.getTrackStatus()) {
					track = trackService.setTrackStatus(id, MusicTrackStatus.ACCEPTED);
					PointRewardCondition condition = pointRewardConditionService.getCondition();
					PointTransactionResponse pointTransactionResponse = pointTransactionService.accumulatePoints(track.getArtistId(), PointTransactionType.UPLOAD, id, condition.getUploadPoint());
					response = new APIResponse(pointTransactionResponse.isStatus(), pointTransactionResponse.getMessage(), id);
				} else {
					response = new APIResponse(false, "이미 승인된 곡입니다", id);
				}
			}
		}

		return ResponseEntity.ok(response);
	}
	
	@ApiOperation(value = "id에 해당하는 곡의 심사 거절",response = MusicTrack.class)
    @RequestMapping(value = "/track/{id}/reject", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> rejectMusicTrack(@PathVariable Long id, @RequestBody String params, Model model) {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String reason = element.getAsJsonObject().get("reason").getAsString();
		trackService.deleteTrackRejectReason(id);
		trackService.setTrackRejectedReason(id, reason);
		trackService.setTrackStatus(id, MusicTrackStatus.REJECTED);
    	APIResponse response = new APIResponse(true, "true", id);
		return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "id에 해당하는 곡들의 심사 거절",response = MusicTrack.class)
    @RequestMapping(value = "/tracks/reject", method= RequestMethod.POST)
    public ResponseEntity<APIResponse> rejectMusicTracks(@RequestBody String params, Model model) {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String reason = element.getAsJsonObject().get("reason").getAsString();
		JSONObject jsonObject = new JSONObject(params);
		JSONArray jsonArray = jsonObject.getJSONArray("ids");
		List<String> ids = new ArrayList<>();
		for(int i = 0; i < jsonArray.length(); i++){
			ids.add(jsonArray.getString(i));
		}
		for(String idString : ids){
			Long id = Long.parseLong(idString);
			trackService.deleteTrackRejectReason(id);
			trackService.setTrackRejectedReason(id, reason);
			trackService.setTrackStatus(id, MusicTrackStatus.REJECTED);
		}
		APIResponse response = new APIResponse(true, "true", ids.size());
		return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "id에 해당하는 곡을 추천하거나 취소",response = ResponseEntity.class)
    @RequestMapping(value = "/track/{id}/recommend", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> recommendTrack(@PathVariable Long id, Model model) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	if (recommendedMediaService.recommendTrack(id)) {
    		response = new APIResponse(true, "true", id);
    	} else {
    		response = new APIResponse(true, "false", id);
    	}
		return ResponseEntity.ok(response);
    }
	
	// --------------------------------------------------------------------------------------------
	// UP LEAGUE
	
	@ApiOperation(value = "업리그 시즌 목록 페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/season")
	public String season(Principal principal, HttpServletRequest request, Model model) {
		int page = 0;
//    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		String returnValue = "fragments/admin/page/music/season";
		boolean seasonLimit = false;

		Date year = new Date();
		if (!StringUtils.isEmpty(request.getParameter("year"))) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			try {
				year = formatter.parse(request.getParameter("year"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			returnValue = "fragments/admin/component/music/league";
		} else {
			super.setSubMenu(model, "/admin/music/season");
		}
		List<LeagueSeason> seasons = leagueSeasonService.listAllYearLeagueSeasonsOrderByOpenDate(year);
		if(seasons.size() >= 10){
			seasonLimit = true;
		} else  {
			seasonLimit = false;
		}
		List<String> years = leagueSeasonService.listYearsOfAllLeagueSeasons();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String currentYear = df.format(year);
		model.addAttribute("seasonLimit", seasonLimit);
		model.addAttribute("currentYear", currentYear);
		model.addAttribute("league_years", years);
//		model.addAttribute("seasons", seasonService.listAllLeagueSeasons(super.getPageMusicOrderByNew(page)));
		model.addAttribute("seasons", seasons);

		return returnValue;
	}
	
	@ApiOperation(value = "시즌 생성페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/season/new")
	public String seasonNew(HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/season");
		model.addAttribute("seasonform", new LeagueSeason());
		return "fragments/admin/page/music/season_new";
	}
	
	@ApiOperation(value = "업리그 시즌 업로드 페이지의 템플릿을 처리", response = String.class)
	@PostMapping("/season")
	public String seasonCreate(@ModelAttribute("seasonform") LeagueSeason seasonform, BindingResult bindingResult, HttpServletRequest request, Model model) {
		seasonValidator.validate(seasonform, bindingResult);
        if (bindingResult.hasErrors()) {
        	return "fragments/admin/page/music/season_new";
        }
    	seasonform.setImageFilename(UPMusicHelper.makeReadableUrl(seasonform.getImageMultipart().getOriginalFilename()));
    	LeagueSeason newSeason = seasonService.saveLeagueSeason(seasonform);
    	azureStorageService.uploadResource(seasonform.getImageMultipart(), "seasons/" + newSeason.getId() + "/");
    	return "redirect:/admin/music/season/" + newSeason.getId();
	}
	
	@ApiOperation(value = "시즌 상세페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/season/{id}")
	public String seasonDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/season");
		model.addAttribute("season", seasonService.getLeagueSeasonById(id));
		return "fragments/admin/page/music/season_detail";
	}
	
	@ApiOperation(value = "시즌 편집페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/season/{id}/edit")
	public String seasonEdit(@PathVariable Long id, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/season");
		model.addAttribute("seasonform", seasonService.getLeagueSeasonById(id));
		return "fragments/admin/page/music/season_edit";
	}
	
	@ApiOperation(value = "시즌 정보를 업데이트", response = String.class)
	@PostMapping("/season/{id}")
	public String update(@PathVariable Long id, @ModelAttribute("seasonform") LeagueSeason seasonform, BindingResult bindingResult, HttpServletRequest request, Model model) {
//		seasonValidator.validate(seasonform, bindingResult);
        if (bindingResult.hasErrors()) {
        	return "fragments/admin/page/music/season_edit";
        }
        LeagueSeason season = seasonService.getLeagueSeasonById(id);
        season.setSubject(seasonform.getSubject());
        season.setOpenDate(seasonform.getOpenDate());
        season.setCloseDate(seasonform.getCloseDate());
        if (seasonform.getImageMultipart() != null && !StringUtils.isEmpty(seasonform.getImageMultipart().getOriginalFilename()))
        	season.setImageFilename(UPMusicHelper.makeReadableUrl(seasonform.getImageMultipart().getOriginalFilename()));
        seasonService.saveLeagueSeason(season);
        if (seasonform.getImageMultipart() != null && !StringUtils.isEmpty(seasonform.getImageMultipart().getOriginalFilename()))
        	azureStorageService.uploadResource(seasonform.getImageMultipart(), "seasons/" + id + "/");
        
        return "redirect:/admin/music/season/" + id;
	}
	
	@ApiOperation(value = "id에 해당하는 시즌을 삭제", response = ResponseEntity.class)
    @RequestMapping(value = "/season/{id}/delete", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteSeason(@PathVariable Long id, HttpServletRequest request) {
		seasonService.deleteLeagueSeason(id);
    	APIResponse response = new APIResponse(true, "true", id);
		return ResponseEntity.ok(response);
    }
	
	// --------------------------------------------------------------------------------------------
	// 심사 대기 가이드 보컬
	
	@ApiOperation(value = "심사 대기중인 가이드 보컬 목록 페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/guide_vocal_judging")
	public String judgingGuideVocal(Principal principal, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/guide_vocal_judging");
		int page = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		model.addAttribute("guides", guideVocalService.findByStatusIsUnderExam(super.getPageMusicOrderByNew(page)));
		
		return "fragments/admin/page/music/guide_vocal_judging";
	}
	
	@ApiOperation(value = "가이드 보컬 상세페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/guide_vocal/{id}")
	public String guideVocalDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/guide_vocal_judging");
		GuideVocal guide = guideVocalService.getGuideVocalById(id);
		model.addAttribute("guide", guide);
		return "fragments/admin/page/music/guide_vocal_detail";
	}
	
	@ApiOperation(value = "id에 해당하는 가이드 보컬의 심사 승인",response = MusicTrack.class)
    @RequestMapping(value = "/guide_vocal/{id}/accept", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> acceptGuideVocal(@PathVariable Long id, Model model) {
		APIResponse response = new APIResponse(false, "가이드 보컬이 존재하지 않습니다", id);
		GuideVocal guide = guideVocalService.getGuideVocalById(id);
		if (guide != null) {
			if (MusicTrackStatus.ACCEPTED != guide.getGuideStatus()) {
				guide = guideVocalService.setGuideStatus(id, MusicTrackStatus.ACCEPTED);
	        	response = new APIResponse(true, "true", id);
			} else {
				response = new APIResponse(false, "이미 승인된 신청입니다", id);
			}
		}
		return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "id에 해당하는 가이드 보컬의 심사 거절",response = MusicTrack.class)
    @RequestMapping(value = "/guide_vocal/{id}/reject", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> rejectGuideVocal(@PathVariable Long id, Model model) {
		APIResponse response = new APIResponse(false, "가이드 보컬이 존재하지 않습니다", id);
		GuideVocal guide = guideVocalService.getGuideVocalById(id);
		if (guide != null) {
			if (MusicTrackStatus.REJECTED != guide.getGuideStatus()) {
				guide = guideVocalService.setGuideStatus(id, MusicTrackStatus.REJECTED);
	        	response = new APIResponse(true, "true", id);
			} else {
				response = new APIResponse(false, "이미 거절된 신청입니다", id);
			}
		}
		return ResponseEntity.ok(response);
    }

	// --------------------------------------------------------------------------------------------
	// 관리자 전체 음뤈
	@ApiOperation(value = "관리자 전체음원 페이지의 메인 템플릿을 반환", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"), @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@GetMapping("/music_admin")
	public String music_admin(Principal principal, HttpServletRequest request, Model model) {
		Member member = super.getCurrentUser(principal);
		super.setSubMenu(model, "/admin/music_admin");
		int page = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		if(member != null){
			model.addAttribute("tracksAll", trackService.findUploadedByAdmin(member.getId(), super.getPageMusicOrderByNew(page)));
			model.addAttribute("tracksRecent", trackService.findUploadedByAdminByRecent(member.getId(), super.getPageMusicOrderByNew(page)));
			model.addAttribute("tracksUpleague", trackService.findUploadedByAdminByUpleague(member.getId(), super.getPageMusicOrderByNew(page)));
//			model.addAttribute("tracksMusicStore", trackService.findUploadedByAdminByMusicStore(member.getId(), super.getPageMusicOrderByNew(page)));
		}

		return "fragments/admin/page/music/index_admin";
	}

	// --------------------------------------------------------------------------------------------
	// 관리자 심사 대기 음원
	@ApiOperation(value = "심사 대기중인 곡 목록 페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/judging_admin")
	public String judging_admin(Principal principal, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/judging_admin");
		int page = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		model.addAttribute("tracksRecent", trackService.findByTrackStatusAndPublishedTrueByAdminByRecent(super.getPageMusicOrderByNew(page)));
		model.addAttribute("tracksUpleague", trackService.findByTrackStatusAndPublishedTrueByAdminByUpleague(super.getPageMusicOrderByNew(page)));
//		model.addAttribute("tracksMusicStore", trackService.findByTrackStatusAndPublishedTrueByAdminByMusicStore(super.getPageMusicOrderByNew(page)));

		return "fragments/admin/page/music/judging_admin";
	}

	// --------------------------------------------------------------------------------------------
	// 관리자 업로드 관리
	@ApiOperation(value = "관리자 업로드 관리 페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/upload_admin")
	public String upload(Principal principal, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/music/upload_admin");
		Member member = super.getCurrentUser(principal);
		int page = 0;
		if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
		model.addAttribute("tracksAll", trackService.findUploadedByAdmin(member.getId(), super.getPageMusicOrderByNew(page)));
		model.addAttribute("tracksUpleague", trackService.findUploadedByAdminByUpleague(member.getId(), super.getPageMusicOrderByNew(page)));
		model.addAttribute("tracksMusicStore", trackService.findUploadedByAdminByMusicStore(member.getId(), super.getPageMusicOrderByNew(page)));
		return "fragments/admin/page/music/upload_admin";
	}

	// --------------------------------------------------------------------------------------------
	// 음원 삭제
	@ApiOperation(value = "id에 해당하는 곡을 삭제", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}")
	@RequestMapping(value = "/tracks/delete_tracks", method = {RequestMethod.POST,RequestMethod.GET})
	public ResponseEntity<APIResponse> deleteTracks(@RequestBody String params, Principal principal, Model model) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		Long lastTrackId = 0L;
		for (JsonElement trackIdElement : trackIds) {
			lastTrackId = trackIdElement.getAsLong();
			MusicTrack  track = trackService.getTrackById(lastTrackId);
			if (track != null) {
				MusicAlbum album = track.getMusicAlbum();
				trackService.deleteTrack(lastTrackId);
				album.removeTrack(track);
				if (album.getTracks().size() == 0) albumService.deleteAlbum(album.getId());
			}
		}
		response = new APIResponse(true, "true", lastTrackId);
		return ResponseEntity.ok(response);
	}

	// --------------------------------------------------------------------------------------------
	// 관리자 음원 업로드
	@ApiOperation(value = "앨범 업로드 템플릿을 반환", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
			@ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
	}
	)
	@GetMapping("/music")
	public String musicAlbum(Principal principal, Model model) {
		super.setSubMenu(model, "/admin/music/upload_admin");
		model.addAttribute("musicform", new MusicAlbum());
		model.addAttribute("albumTypeMap", super.getMusicAlbumTypeMap());
		model.addAttribute("genreMap", super.getGenreMap());
		return "fragments/admin/page/music/music";
	}

	// --------------------------------------------------------------------------------------------
	// 관리자 영상 업로드
	@ApiOperation(value = "영상 업로드 템플릿을 반환", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
			@ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
	}
	)
	@GetMapping("/video")
	public String video(Principal principal, Model model) {
		super.setSubMenu(model, "/admin/music/upload_admin");
		model.addAttribute("videoform", new Video());
		model.addAttribute("videoTypeMap", super.getVideoTypeMap());
		return "fragments/admin/page/music/video";
	}

	// --------------------------------------------------------------------------------------------
	// MUSIC TRACK
	@ApiOperation(value = "트랙 업로드 템플릿을 반환", response = String.class)
	@GetMapping("/music/{id}/track")
	public String musicTrack(@PathVariable Long id, Principal principal, Model model) {
		MusicAlbum album = albumService.getAlbumById(id);
		if (album == null) {
			return "redirect:admin/music/music";
		}
		super.setSubMenu(model, "/admin/music/upload_admin");
		MusicTrack track = new MusicTrack();
		track.setMusicAlbum(album);
		model.addAttribute("trackform", track);
		Member member = super.getCurrentUser(principal);
		model.addAttribute("trackTypeMap", super.getMusicTrackTypeMap());
		model.addAttribute("genreMap", super.getGenreMap());
		model.addAttribute("themeMap", super.getThemeMap());
		model.addAttribute("currentUser", member);
		model.addAttribute("cooperatorRoleMap", super.getCooperatorRoleMap());
		model.addAttribute("isSA", UPMusicConstants.MusicAlbumType.SA.equals(album.getAlbumType()));
		model.addAttribute("tracks", album.getTracks());
		return "fragments/admin/page/music/track";
	}

	// --------------------------------------------------------------------------------------------
	// Excel 파일을 이용한 음원 업로드
	@ApiOperation(value = "Execl 업로드 템플릿을 반환", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
			@ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
			}
	)
	@GetMapping("/excelTrack")
	public String uploadByExcel(Principal principal, Model model) {
		super.setSubMenu(model, "/admin/music/upload_admin");
		return "fragments/admin/page/music/excelTrack";
	}

	// --------------------------------------------------------------------------------------------
	// Excel 파일을 이용한 영상 업로드
	@ApiOperation(value = "Execl 뮤직 업로드 템플릿을 반환", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
			@ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
	}
	)
	@GetMapping("/excelVideo")
	public String uploadVideoByExcel(Principal principal, Model model) {
		super.setSubMenu(model, "/admin/music/upload_admin");
		return "fragments/admin/page/music/excelVideo";
	}


}

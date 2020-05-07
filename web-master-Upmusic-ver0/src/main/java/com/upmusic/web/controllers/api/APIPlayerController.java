package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.LeagueSeasonTrack;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicAlbum;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.Video;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.LeagueSeasonService;
import com.upmusic.web.services.MusicAlbumService;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.VideoService;


@RestController
@RequestMapping("/api/player")
@Api(value="api.player", description="뮤직 플레이어와 관련된 API 컨트롤러")
public class APIPlayerController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MusicTrackService trackService;
	
	@Autowired
    private VideoService videoService;
	
	@Autowired
	private LeagueSeasonService leagueSeasonService;
	
	@Autowired
    private MusicAlbumService albumService;
	
	@ApiOperation(value = "플레이어 페이지의 메인 템플릿을 반환", response = Iterable.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @GetMapping()
    public Iterable<MusicTrack> page(@RequestParam(required = false, value = "params") String params, Principal principal, HttpServletRequest request, Model model){
		JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) return trackService.findPlaylistWithHeartByMember(member.getId());
    	return null;
    }
	
	@ApiOperation(value = "재생목록을 반환", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/playlist", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> getPlaylist(@RequestBody(required = false) String params, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			response = new APIResponse(true, "true", trackService.findPlaylistWithHeartByMember(member.getId()));
		}
		return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "id에 해당하는 곡을 재생목록에 추가", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/add_tracks", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addTracksToPlaylist(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			boolean addedTrack = false;
			boolean overflow = false;

			if (!trackService.addTrackToPlayerList(trackIds, member)) {
				overflow = true;
			} else {
				addedTrack = true;
			}

			if (overflow) {
				response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", addedTrack ? "true" : "false");
			} else if (0 < trackIds.size()) {
				// trackService.removeOverCountTracksOfPlayer(member.getId()); // 정책 변경으로 불필요
				response = new APIResponse(true, "true", null);
			} else {
				response = new APIResponse(true, "선택된 곡이 없습니다.", null);
			}
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "top50에 해당하는 곡을 재생목록에 추가", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/add_top50_tracks", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addTop50TracksToPlaylist(@RequestBody(required = false) String params, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			// 리그 순위 top 50 (전체)
			boolean addedTrack = false;
			boolean overflow = false;
			List<LeagueSeasonTrack> tracks = leagueSeasonService.listTop50ByCurrentSeason(member != null ? member.getId() : 0);
			if (0 < tracks.size()) {
				Collections.reverse(tracks);
				for (LeagueSeasonTrack leagueSeasonTrack : tracks) {
					if (!trackService.addTrackToPlayer(leagueSeasonTrack.getMusicTrack().getId(), member)) {
						overflow = true;
						break;
					} else {
						addedTrack = true;
					}
	    		}
				if (overflow) {
					response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", addedTrack ? "true" : "false");
				} else {
					response = new APIResponse(true, "true", null);
				}
			} else {
				response = new APIResponse(true, "선택된 곡이 없습니다.", null);
			}
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "최근 플레이 리스트에 해당하는 곡을 재생목록에 추가. 없을 경우 가장 많이 재생된 음원 목록을 추가", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/add_recent_played_tracks", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addRecentPlayedTracksToPlaylist(@RequestBody(required = false) String params, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			List<MusicTrack> tracks = trackService.findAllPlayedTrackByMemberId(member.getId());
			Page<Video> recentPlayedVideos = videoService.findPlayedVideoByMemberId(member.getId(), super.getPageMediaListInHome(0));
			if (0 < tracks.size() && 0 < recentPlayedVideos.getTotalElements()) {
				boolean addedTrack = false;
				boolean overflow = false;
				Collections.reverse(tracks);
				for (MusicTrack track : tracks) {
					logger.debug("addRecentPlayedTracksToPlaylist : track is {}", track.getSubject());
					if (!trackService.addTrackToPlayer(track.getId(), member)) {
						overflow = true;
						break;
					} else {
						addedTrack = true;
					}
	    		}
				if (overflow) {
					response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", addedTrack ? "true" : "false");
				} else {
					// trackService.removeOverCountTracksOfPlayer(member.getId());
					response = new APIResponse(true, "true", null);
				}
			} else {
				boolean addedTrack = false;
				boolean overflow = false;
				tracks = trackService.findTop10(super.get10TracksOrderByPlayCnt());
				if (0 < tracks.size()) {
					Collections.reverse(tracks);
					for (MusicTrack track : tracks) {
						if (!trackService.addTrackToPlayer(track.getId(), member)) {
							overflow = true;
							break;
						} else {
							addedTrack = true;
						}
		    		}
					if (overflow) {
						response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", addedTrack ? "true" : "false");
					} else {
						// trackService.removeOverCountTracksOfPlayer(member.getId());
						response = new APIResponse(true, "true", null);
					}
				} else {
					response = new APIResponse(true, "선택된 곡이 없습니다.", null);
				}
			}
		}
		return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "좋아요에 해당하는 곡을 재생목록에 추가. 없을 경우 가장 많이 좋아요를 얻은 음원 목록을 추가", response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/add_liked_tracks", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addLikedTracksToPlaylist(@RequestBody(required = false) String params, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			List<MusicTrack> tracks = trackService.findAllHeartTrackByMemberId(member.getId());
			Page<Video> likedVideos = videoService.findHeartVideoByMemberId(member.getId(), super.getPageMediaListInHome(0));
			if (0 < tracks.size() && 0 < likedVideos.getTotalElements()) {
				boolean addedTrack = false;
				boolean overflow = false;
				Collections.reverse(tracks);
				for (MusicTrack track : tracks) {
					if (!trackService.addTrackToPlayer(track.getId(), member)) {
						overflow = true;
						break;
					} else {
						addedTrack = true;
					}
	    		}
				if (overflow) {
					response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", addedTrack ? "true" : "false");
				} else {
					// trackService.removeOverCountTracksOfPlayer(member.getId());
					response = new APIResponse(true, "true", null);
				}
			} else {
				boolean addedTrack = false;
				boolean overflow = false;
				tracks = trackService.findTop10(super.get10TracksOrderByHeartCnt());
				if (0 < tracks.size()) {
					Collections.reverse(tracks);
					for (MusicTrack track : tracks) {
						if (!trackService.addTrackToPlayer(track.getId(), member)) {
							overflow = true;
							break;
						} else {
							addedTrack = true;
						}
		    		}
					if (overflow) {
						response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", addedTrack ? "true" : "false");
					} else {
						// trackService.removeOverCountTracksOfPlayer(member.getId());
						response = new APIResponse(true, "true", null);
					}
				} else {
					response = new APIResponse(true, "선택된 곡이 없습니다.", null);
				}
			}
		}
		return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "id에 해당하는 곡에 재생 건수 추가",response = ResponseEntity.class, notes="token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/{id}/add_play_cnt", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addPlayCnt(@PathVariable Long id, @RequestBody(required = false) String params, Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) {
    	APIResponse apiResponse = new APIResponse(true, super.getMessage("common.logged_out"), null);
    	JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			// 쿠키 - 재생 건수 증가
			boolean existsCookie = false;
			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(UPMusicConstants.COOKIE_TRACK + id)) existsCookie = true;
				}
			}

	        if (!existsCookie) {
	    		Cookie cookie = new Cookie(UPMusicConstants.COOKIE_TRACK + id, "hit");
	            cookie.setMaxAge(60 * 60 * 24);
	            response.addCookie(cookie);
	        }
	    	trackService.increasePlayCnt(id, existsCookie, member);
	    	apiResponse = new APIResponse(true, "true", id);
		}
		return ResponseEntity.ok(apiResponse);
    }
	
	@ApiOperation(value = "id에 해당하는 곡을 재생목록에서 제거", response = ResponseEntity.class, notes="params의 예 {\"ids\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/remove_tracks", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> removeTracksFromPlaylist(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			JsonArray trackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
			for (JsonElement trackIdElement : trackIds) {
				if (!StringUtils.isEmpty(trackIdElement.getAsString())) trackService.removeTrackFromPlayer(trackIdElement.getAsLong(), member);
    		}
    		response = new APIResponse(true, "true", params);
		}
		return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "재생목록을 업데이트", response = ResponseEntity.class, notes="params의 예 {\\\"ids\\\":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 \\\"token\\\":\\\"발급받은 토큰 문자열\\\"을 추가")
    @RequestMapping(value = "/update_list", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> updatePlaylist(@RequestBody String params, Principal principal, HttpServletRequest request) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			// 전체 재생 목록을 제거 
			trackService.removeAllTrackFromPlayer(member);
			JsonArray jsonTrackIds = element.getAsJsonObject().get("ids").getAsJsonArray();
			List<Long> trackIds = new ArrayList<Long>();
			for (JsonElement trackIdElement : jsonTrackIds) {
				if (!StringUtils.isEmpty(trackIdElement.getAsString())) trackIds.add(trackIdElement.getAsLong());
    		}
			Collections.reverse(trackIds);
			for (Long trackId : trackIds) {
				trackService.addTrackToPlayer(trackId, member);
			}
			response = new APIResponse(true, "true", trackService.findPlaylistWithHeartByMember(member.getId()));
		}
		return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "id에 해당하는 앨범의 곡들을 재생목록에 추가", response = ResponseEntity.class, notes="params의 예 {\"ids\":123456}, token을 이용한 회원인증이 필요한 경우엔 params에 \"token\":\"발급받은 토큰 문자열\"을 추가")
    @RequestMapping(value = "/add_album", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> addAlbumToPlaylist(@RequestBody String params, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		List<String> trackIds = new ArrayList<String>();
		MusicAlbum album = albumService.getAlbumById(element.getAsJsonObject().get("id").getAsLong());
		for(MusicTrack musicTrack : album.getTracks()){
			trackIds.add(musicTrack.getId().toString());
		}
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if (member != null) {
			boolean addedTrack = false;
			boolean overflow = false;
			for (String trackIdElement : trackIds) {
				if (!StringUtils.isEmpty(trackIdElement) && !trackService.addTrackToPlayer(Long.parseLong(trackIdElement), member)) {
					overflow = true;
					break;
				} else {
					addedTrack = true;
				}
    		}
			if (overflow) {
				response = new APIResponse(true, "재생목록에는 최대 200곡까지 등록가능 합니다.", addedTrack ? "true" : "false");
			} else if (0 < trackIds.size()) {
				// trackService.removeOverCountTracksOfPlayer(member.getId()); // 정책 변경으로 불필요
				response = new APIResponse(true, "true", null);
			} else {
				response = new APIResponse(true, "선택된 곡이 없습니다.", null);
			}
		}
		return ResponseEntity.ok(response);
    }

}

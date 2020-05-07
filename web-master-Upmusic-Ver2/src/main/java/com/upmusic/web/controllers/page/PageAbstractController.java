package com.upmusic.web.controllers.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.upmusic.web.config.UPMusicConstants.Gender;
import com.upmusic.web.config.UPMusicConstants.LeagueSeasonPeriod;
import com.upmusic.web.config.UPMusicConstants.MusicAlbumType;
import com.upmusic.web.config.UPMusicConstants.MusicCooperatorRole;
import com.upmusic.web.config.UPMusicConstants.MusicTrackType;
import com.upmusic.web.config.UPMusicConstants.VideoType;
import com.upmusic.web.controllers.AbstractController;
import com.upmusic.web.domain.Genre;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.Theme;
import com.upmusic.web.services.GenreService;
import com.upmusic.web.services.StoreOrderLicenseService;
import com.upmusic.web.services.ThemeService;

import javax.servlet.http.HttpServletRequest;

/**
 * 페이지 컨트롤러에 필수적인 기능들을 처리
 */
public abstract class PageAbstractController extends AbstractController {

	@Autowired
	private GenreService genreService;
	
	@Autowired
	private ThemeService themeService;
	
	@Autowired
	private StoreOrderLicenseService storeOrderLicenseService;
	
	
	protected void setMetaTag(Model model, Map<String, String> metaTags) {
		model.addAttribute("og_url", metaTags.get("og_url"));
		model.addAttribute("og_type", metaTags.get("og_type"));
		model.addAttribute("og_title", metaTags.get("og_title"));
		model.addAttribute("og_description", metaTags.get("og_description"));
		model.addAttribute("og_image", metaTags.get("og_image"));
	}
	
	/*
	 * @deprecated : 사이드 메뉴의 유저 정보가 필요하여 setSubMenu(Model model, String currentPath, Member currentUser) 사용
	 * 레이아웃 좌측 메뉴를 설정
	 
	protected void setSubMenu(Model model, String currentPath) {
		model.addAttribute("selectedSubMenu", currentPath);
		// 구매기능이 전체 페이지로 확장되어 여기에서 라이센스 종류 반환 
		model.addAttribute("licenseObj", storeOrderLicenseService.listAllStoreLicense());
	}
	*/

	/*
	 * 레이아웃 좌측 메뉴를 설정
	 */
	protected void setSubMenu(Model model, String currentPath, Member currentUser, HttpServletRequest request) {
		model.addAttribute("selectedSubMenu", currentPath);
		// 로그인한 유저 정보
		model.addAttribute("currentUser", currentUser);
		// 구매기능이 전체 페이지로 확장되어 여기에서 라이센스 종류 반환 
		model.addAttribute("licenseObj", storeOrderLicenseService.listAllStoreLicense());

		// 사용자 운영체제 정보 ios = iPhone, aos = Android
		String userAgent = request.getHeader("user-agent");
		String os = null;
		if(userAgent.contains("iPhone")){
			os = "ios";
		} else if (userAgent.contains("Android")){
			os = "aos";
		}
		model.addAttribute("os", os);
	}
	
	/*
	 * enum 맵
	 */
	
	protected Map<Gender, String> getGenderMap() {
		Map<Gender, String> genderMap = new HashMap<Gender, String>();
		for (Gender gender : Gender.values()) {
			genderMap.put(gender, getMessage("enum.gender."+ gender.name()));
		}
		return genderMap;
	}
	
	protected Map<Integer, String> getGenreMap() {
		Map<Integer, String> genreMap = new LinkedHashMap<Integer, String>();
		for (Genre genre : genreService.listAllGenres()) {
			genreMap.put(genre.getId(), getMessage("enum.genre."+ genre.getName()));
		}
		return genreMap;
	}
	
	protected Map<Integer, String> getThemeMap() {
		Map<Integer, String> themeMap = new LinkedHashMap<Integer, String>();
		for (Theme theme : themeService.listAllThemes()) {
			themeMap.put(theme.getId(), getMessage("enum.theme."+ theme.getName()));
		}
		return themeMap;
	}
	
	protected List<Theme> convertStringIdsToThemes(String[] ids) {
		List<Theme> themes = new ArrayList<Theme>();
		for (String id : ids) {
			themes.add(themeService.findById(Integer.valueOf(id)));
		}
		return themes;
	}
	
	protected List<String> convertThemesToStringIds(List<Theme> themes) {
		List<String> ids = new ArrayList<String>();
		for (Theme theme : themes) {
			ids.add(String.valueOf(theme.getId()));
		}
		return ids;
	}
	
	protected Map<MusicAlbumType, String> getMusicAlbumTypeMap() {
		Map<MusicAlbumType, String> albumTypeMap = new LinkedHashMap<MusicAlbumType, String>();
		for (MusicAlbumType albumType : MusicAlbumType.values()) {
			albumTypeMap.put(albumType, getMessage("enum.albumtype."+ albumType.name()));
		}
		return albumTypeMap;
	}
	
	protected Map<MusicTrackType, String> getMusicTrackTypeMap() {
		Map<MusicTrackType, String> trackTypeMap = new LinkedHashMap<MusicTrackType, String>();
		for (MusicTrackType trackType : MusicTrackType.values()) {
			trackTypeMap.put(trackType, getMessage("enum.tracktype."+ trackType.name()));
		}
		return trackTypeMap;
	}
	
	protected Map<MusicCooperatorRole, String> getCooperatorRoleMap() {
		Map<MusicCooperatorRole, String> cooperatorRoleMap = new LinkedHashMap<MusicCooperatorRole, String>();
		for (MusicCooperatorRole role : MusicCooperatorRole.values()) {
			cooperatorRoleMap.put(role, getMessage("enum.track.cooperator.role."+ role.name()));
		}
		return cooperatorRoleMap;
	}
	
	protected Map<VideoType, String> getVideoTypeMap() {
		Map<VideoType, String> videoTypeMap = new LinkedHashMap<VideoType, String>();
		for (VideoType videoType : VideoType.values()) {
			videoTypeMap.put(videoType, getMessage("enum.videotype."+ videoType.name()));
		}
		return videoTypeMap;
	}
	
	protected Map<LeagueSeasonPeriod, String> getLeagueSeasonPeriodMap() {
		Map<LeagueSeasonPeriod, String> periodMap = new LinkedHashMap<LeagueSeasonPeriod, String>();
		for (LeagueSeasonPeriod period : LeagueSeasonPeriod.values()) {
			periodMap.put(period, getMessage("enum.league_season_period."+ period.name()));
		}
		return periodMap;
	}

}

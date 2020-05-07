package com.upmusic.web.controllers.admin;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.Genre;
import com.upmusic.web.domain.Theme;
import com.upmusic.web.services.GenreService;
import com.upmusic.web.services.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.upmusic.web.controllers.AbstractController;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 페이지 컨트롤러에 필수적인 기능들을 처리
 */
public abstract class AdminAbstractController extends AbstractController {

	@Autowired
	private GenreService genreService;

	@Autowired
	private ThemeService themeService;

	/*
	 * 레이아웃 좌측 메뉴를 설정
	 */
	protected void setSubMenu(Model model, String currentPath) {
		model.addAttribute("selectedSubMenu", currentPath);
	}

	protected Map<UPMusicConstants.MusicAlbumType, String> getMusicAlbumTypeMap() {
		Map<UPMusicConstants.MusicAlbumType, String> albumTypeMap = new LinkedHashMap<UPMusicConstants.MusicAlbumType, String>();
		for (UPMusicConstants.MusicAlbumType albumType : UPMusicConstants.MusicAlbumType.values()) {
			albumTypeMap.put(albumType, getMessage("enum.albumtype."+ albumType.name()));
		}
		return albumTypeMap;
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

	protected Map<UPMusicConstants.MusicTrackType, String> getMusicTrackTypeMap() {
		Map<UPMusicConstants.MusicTrackType, String> trackTypeMap = new LinkedHashMap<UPMusicConstants.MusicTrackType, String>();
		for (UPMusicConstants.MusicTrackType trackType : UPMusicConstants.MusicTrackType.values()) {
			trackTypeMap.put(trackType, getMessage("enum.tracktype."+ trackType.name()));
		}
		return trackTypeMap;
	}

	protected Map<UPMusicConstants.MusicCooperatorRole, String> getCooperatorRoleMap() {
		Map<UPMusicConstants.MusicCooperatorRole, String> cooperatorRoleMap = new LinkedHashMap<UPMusicConstants.MusicCooperatorRole, String>();
		for (UPMusicConstants.MusicCooperatorRole role : UPMusicConstants.MusicCooperatorRole.values()) {
			cooperatorRoleMap.put(role, getMessage("enum.track.cooperator.role."+ role.name()));
		}
		return cooperatorRoleMap;
	}

	protected Map<UPMusicConstants.VideoType, String> getVideoTypeMap() {
		Map<UPMusicConstants.VideoType, String> videoTypeMap = new LinkedHashMap<UPMusicConstants.VideoType, String>();
		for (UPMusicConstants.VideoType videoType : UPMusicConstants.VideoType.values()) {
			videoTypeMap.put(videoType, getMessage("enum.videotype."+ videoType.name()));
		}
		return videoTypeMap;
	}
	
}

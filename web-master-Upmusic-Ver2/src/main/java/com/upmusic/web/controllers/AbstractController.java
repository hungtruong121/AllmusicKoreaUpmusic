package com.upmusic.web.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.Member;
import com.upmusic.web.services.MemberService;


/**
 * 전체 컨트롤러에 공통적인 기능들을 처리 
 */
public abstract class AbstractController {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
    private MemberService memberService;
	
	
	/**
	 * i18n 메시지 변환 후 반환
	 * @param code
	 * @return message
	 */
	protected String getMessage(String code) {
	    return messageSource.getMessage(code, null, null);
	}
	
	/**
	 * 현재 회원을 반환
	 * @param principal
	 * @return Member
	 */
	protected Member getCurrentUser(Principal principal) {
		if (principal != null ) {
        	return memberService.findByEmail(principal.getName());
    	}
   		return null;
	}
	
	/**
	 * 현재 회원을 반환
	 * @param token
	 * @return Member
	 */
	protected Member getCurrentUser(String token) {
		if (token != null ) {
        	return memberService.findByToken(token);
    	}
   		return null;
	}
	
	/**
	 * 뮤직 회원 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageMemberOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_MEMBER_DEFAULT_LIMIT, Sort.Direction.DESC, "id", "trackCnt");
	}

	/**
	 * 비디오 인기순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageVideoOrderByHot(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_VIDEO_DEFAULT_LIMIT, Sort.Direction.DESC, "hotPoint");
	}
	
	/**
	 * 비디오 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageVideoOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_VIDEO_DEFAULT_LIMIT, Sort.Direction.DESC, "id");
	}
	
	/**
	 * 앨범 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageMusicAlbumOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_MUSIC_ALBUM_LIMIT, Sort.Direction.DESC, "id");
	}
	
	/**
	 * 뮤직 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageMusicOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_MUSIC_DEFAULT_LIMIT, Sort.Direction.DESC, "id");
	}
	
	/**
	 * 뮤직 인기순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageMusicOrderByHot(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_MUSIC_DEFAULT_LIMIT, Sort.Direction.DESC, "hotPoint");
	}
	
	/**
	 * 제작의뢰 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageRequestOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_REQUEST_DEFAULT_LIMIT, Sort.Direction.DESC, "id");
	}
	
	/**
	 * 아티스트 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageArtistOrderByHot(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_ARTIST_DEFAULT_LIMIT, Sort.Direction.DESC, "heartCnt");
	}
	
	/**
	 * 아티스트 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageArtistOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_ARTIST_NEW_LIMIT, Sort.Direction.DESC, "id");
	}
	
	/**
	 * 보컬 캐스팅 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageVocalCastingOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_VOCAL_CASTING_DEFAULT_LIMIT, Sort.Direction.DESC, "id");
	}
	
	/**
	 * 모음 리스트 최신순 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageCollectionOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_COLLECTION_DEFAULT_LIMIT, Sort.Direction.DESC, "id");
	}
	
	/**
	 * 모음 리스트 내 곡 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageCollectionTrack(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_COLLECTION_TRACK_LIMIT);
	}
	
	/**
	 * 플레이 리스트 곡 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPagePlaylistTrack(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_MUSIC_DEFAULT_LIMIT);
	}
	
	/**
	 * 플레이 리스트 영상 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPagePlaylistVideo(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_VIDEO_DEFAULT_LIMIT);
	}
	
	/**
	 * 홈페이지 리스트 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getPageMediaListInHome(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_HOME_MEDIA_LIST_LIMIT);
	}
	
	protected PageRequest getPageMediaListInHomeOrderByPlayCnt(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_HOME_MEDIA_LIST_LIMIT, Sort.Direction.DESC, "playCnt");
	}
	
	protected PageRequest getPageMediaListInHomeOrderByHitCnt(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_HOME_MEDIA_LIST_LIMIT, Sort.Direction.DESC, "hitCnt");
	}
	
	protected PageRequest getPageMediaListInHomeOrderByHeartCnt(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_HOME_MEDIA_LIST_LIMIT, Sort.Direction.DESC, "heartCnt");
	}
	
	/**
	 * 댓글 리스트 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getCommentsOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_COMMENT_DEFAULT_LIMIT, Sort.Direction.DESC, "id");
	}
	
	
	/**
	 * 전체 리스트 반환
	 * limited 200
	 * @param page
	 * @return
	 */
	protected PageRequest getAllMediaListOrderByHeartCnt(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_HOME_MEDIA_LIST_LIMIT, Sort.Direction.DESC, "heartCnt");
	}
	
	protected PageRequest get10TracksOrderByPlayCnt() {
		return PageRequest.of(0, UPMusicConstants.PAGE_HOME_MEDIA_LIST_LIMIT, Sort.Direction.DESC, "playCnt");
	}
	
	protected PageRequest get10TracksOrderByHeartCnt() {
		return PageRequest.of(0, UPMusicConstants.PAGE_HOME_MEDIA_LIST_LIMIT, Sort.Direction.DESC, "heartCnt");
	}
	
	/**
	 * 정산 리스트 쿼리 반환
	 * @param page
	 * @return pageRequest
	 */
	protected PageRequest getTransactionsOrderByNew(int page) {
		return PageRequest.of(page, UPMusicConstants.PAGE_TRANSACTION_DEFAULT_LIMIT, Sort.Direction.DESC, "id");
	}
	
}

package com.upmusic.web.services;

import java.util.List;

import com.google.gson.JsonArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.MusicTrackCooperator;
import org.springframework.data.domain.Pageable;


public interface MusicTrackService {
	
	Iterable<MusicTrack> listAllTracks();
	
	List<MusicTrack> findTop10(PageRequest pageOrder);
	
	Page<MusicTrack> findAllWithHeartByMember(Long memberId, PageRequest pageOrderNew);
	
	Page<MusicTrack> findAllByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
	
	Page<MusicTrack> findAllByThemeWithHeartByMember(int themeId, Long memberId, PageRequest pageOrderNew);
	
	Page<MusicTrack> findAllByGenreAndInStoreTrueWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
	
	Page<MusicTrack> findByStatusIsAccepted(PageRequest pageOrderNew);
	
	Page<MusicTrack> findByStatusIsUnderExam(PageRequest pageOrderNew);
	
	MusicTrack findBySubejct(String subject);

	MusicTrack getTrackById(Long id);

	MusicTrack saveTrack(MusicTrack track);
	
	void deleteTrack(Long id, Long memberId);

    void deleteTrack(Long id);
    
    int increasePlayCnt(Long id, boolean existsCookie, Member member);
    
    boolean likedTrack(Long id, Long memberId);
    
    int increaseHeartCnt(Long id, Member member);
    
    int decreaseHeartCnt(Long id, Member member);
    
    // Cooperator
    MusicTrackCooperator saveTrackCooperator(MusicTrackCooperator cooperator);
    void deleteTrackCooperator(MusicTrackCooperator cooperator);

    // Player
    Iterable<MusicTrack> findPlaylistWithHeartByMember(Long memberId);
    boolean addTrackToPlayer(Long id, Member member);

	boolean addTrackToPlayerList(JsonArray ids, Member member);

    void removeAllTrackFromPlayer(Member member);
 	void removeTrackFromPlayer(Long id, Member member);
 	void removeOverCountTracksOfPlayer(Long memberId);
 	
    // Play list
	Page<MusicTrack> findPlayedTrackByMemberId(Long memberId, PageRequest pageOrderRequest);
	List<MusicTrack> findAllPlayedTrackByMemberId(Long memberId);
	void removeTrackFromPlaylist(Long id, Member member);
	
	// Like list
	Page<MusicTrack> findHeartTrackByMemberId(Long memberId, PageRequest pageOrderRequest);
	List<MusicTrack> findAllHeartTrackByMemberId(Long memberId);
	void removeTrackFromHeartlist(Long id, Member member);
	
	// Video 페이지의 아티스트의 음원 top5
	List<MusicTrack> findTop5ByArtistIdWithHeartByMember(Long artistId, Long memberId);

	// 업로드 내역
	Page<MusicTrack> findUploadedByArtistIdWithHeartByMember(Long artistId, Long memberId, PageRequest pageRequest);
	
	// 아티스트 음원
	Long findCountByArtistId(Long artistId);
	Long findCountByArtistIdAndInStoreTrue(Long artistId);
	List<MusicTrack> findTop10ByArtistIdWithHeartByMember(Long artistId, Long memberId);
	Page<MusicTrack> findAllByArtistIdWithHeartByMember(Long artistId, Long memberId, PageRequest pageOrderRequest);
	List<MusicTrack> findByArtistIdWithHeartByMember(Long artistId, Long memberId);
	List<MusicTrack> findTop10ByArtistIdAndInStoreTrueWithHeartByMember(Long artistId, Long memberId);
	Page<MusicTrack> findAllByArtistIdAndInStoreTrueWithHeartByMember(Long artistId, Long memberId, PageRequest pageOrderRequest);
    List<MusicTrack> findByArtistIdAndInStoreTrueWithHeartByMember(Long artistId, Long memberId);

	String setTitleTrack(Long id, Long memberId);
	
	void deleteTracksByMusicAlbumId(Long id);
	
	//
	// 관리자 영역
	//
	
	MusicTrack setTrackStatus(Long id, MusicTrackStatus status);
	
	void setTrackRejectedReason(Long id, String reason);

	Page<MusicTrack> findUploadedByUsers(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findUploadedByUsersByUpleague(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findUploadedByUsersByRecent(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findUploadedByUsersByMusicStore(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByUpleague(PageRequest pageRequest);

	Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByRecent(PageRequest pageRequest);

	Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByMusicStore(PageRequest pageRequest);

	Page<MusicTrack> findUploadedByAdmin(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findUploadedByAdminByUpleague(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findUploadedByAdminByRecent(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findUploadedByAdminByMusicStore(Long memberId, PageRequest pageRequest);

	Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByUpleague(PageRequest pageRequest);

	Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByRecent(PageRequest pageRequest);

	Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByMusicStore(PageRequest pageRequest);
	
	void deleteTrackRejectReason(Long id);
	
	Page<MusicTrack> findAllInStoreTrueWithHeartByMember(Long memberId, PageRequest pageOrderNew);
}

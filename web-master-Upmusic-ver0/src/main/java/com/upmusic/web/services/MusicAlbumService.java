package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicAlbum;
import com.upmusic.web.domain.MusicAlbumComment;


public interface MusicAlbumService {
	
	Iterable<MusicAlbum> listAllAlbums();
	
	Page<MusicAlbum> findAllSAWithHeartByMember(Long memberId, PageRequest pageOrderNew);
	
	Page<MusicAlbum> findAllSAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
	
//	Page<MusicAlbum> findHomeSAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
//	
//	Page<MusicAlbum> findAbroadSAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
	
	Page<MusicAlbum> findAllGAWithHeartByMember(Long memberId, PageRequest pageOrderNew);
	
	Page<MusicAlbum> findAllGAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
	
//	Page<MusicAlbum> findHomeGAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
//	
//	Page<MusicAlbum> findAbroadGAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew);
	
	Long findCountByArtistId(Long artistId);
	
	List<MusicAlbum> findTop4ByArtistIdWithHeartByMember(Long artistId, Long memberId);

	Page<MusicAlbum> findAllByArtistIdWithHeartByMember(Long artistId, Long memberId, PageRequest pageOrderNew);
	
	List<MusicAlbum> findByArtistIdWithHeartByMember(Long artistId, Long memberId);

	Iterable<MusicAlbum> getTop5ByMemberId(Long memberId);
	
	MusicAlbum getAlbumById(Long id);

	MusicAlbum saveAlbum(MusicAlbum album);

	boolean completeAlbum(Long id);
	
    void deleteAlbum(Long id);
    
    int increaseHitCnt(Long id);
    
    boolean likedAlbum(Long id, Long memberId);
    
    int increaseHeartCnt(Long id, Member member);
    
    int decreaseHeartCnt(Long id, Member member);

    Page<MusicAlbumComment> getComments(Long albumId, PageRequest pageOrderRequest);
    
    MusicAlbumComment getCommentById(Long commentId);
    
    MusicAlbumComment getCommentById(Long albumId, Long commentId);
    
    MusicAlbumComment addComment(Long id, Member member, String content, String ip);
	
    MusicAlbumComment updateComment(Long id, Long commentId, Member member, String content, String ip);

	Long deleteComment(Long id, Long commentId, Long memberId);

	// 회원 탈퇴에 따른 음원 제거
	void deleteAlbumsByMemberId(Long memberId);

}

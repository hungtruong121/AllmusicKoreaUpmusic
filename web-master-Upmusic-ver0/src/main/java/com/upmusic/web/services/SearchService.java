package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.domain.*;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;


public interface SearchService {

	Page<MusicTrack> searchTop10Tracks(String query, Long memberId);
	List<MusicTrack> searchTracks(String query, Long memberId);
	
	Page<MusicAlbum> searchTop4Albums(String query, Long memberId);
	List<MusicAlbum> searchAlbums(String query, Long memberId);
	
	Page<MusicTrack> searchTop10StoreTracks(String query, Long memberId);
	List<MusicTrack> searchStoreTracks(String query, Long memberId);
	
	Page<Member> searchTop4Artists(String query, Long memberId);
	List<Member> searchArtists(String query, Long memberId);

	Page<VocalCasting> searchTop10Castings(String query);
	List<VocalCasting> searchCastings(String query);

	Page<Video> searchTop4Videos(String query);
	List<Video> searchVideos(String query);
	
	Page<BandRecruit> searchTop4BandRecruits(String query);
	List<BandRecruit> searchBandRecruits(String query);

	void saveSearchKeyword(String query ,Member member);
	Page<Search> findTop10ByMemberId(@Param("memberId") Long memberId);

	void deleteById(Long id);
	void deleteByMemberId(Long memberId);

	Page<Object[]> findTop10ByPopularKeyword();
}

package com.upmusic.web.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.domain.Collection;
import com.upmusic.web.domain.CollectionTrack;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;


public interface CollectionService {
	
	Iterable<Collection> listAllCollections();
	
	Iterable<Collection> listAllCollectionsByMemberId(Long id);
	
	Page<Collection> findByMemberId(Long memberId, PageRequest pageOrderNew);

	Collection getCollectionById(Long id);

	Collection saveCollection(Collection collection);

    void deleteCollection(Long id);
    

    Page<MusicTrack> listTrackByCollection(Long collectionId, Long memberId, PageRequest pageMusicOrderByNew);
    
    int countTracksByCollection(Long collectionId);
    
    CollectionTrack findByCollectionIdAndMusicTrackId(Long collectionId, Long trackId);

    boolean saveCollectionTrack(Collection collection, Long musicTrackId);
    
    void removeOverCountTracks(Long collectionId);
    
	void deleteCollectionTrack(Long collectionId, Long musicTrackId);

	// play
	boolean sendToPlaylist(Long collectionId, Member member);

}

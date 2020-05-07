package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.Collection;
import com.upmusic.web.domain.CollectionTrack;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.MusicTrackPlayer;
import com.upmusic.web.repositories.CollectionRepository;
import com.upmusic.web.repositories.CollectionTrackRepository;
import com.upmusic.web.repositories.MusicTrackPlayerRepository;
import com.upmusic.web.repositories.MusicTrackRepository;


@Service
public class CollectionServiceImpl implements CollectionService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private CollectionRepository collectionRepository;
	
	@Autowired
	private CollectionTrackRepository collectionTrackRepository;
	
	@Autowired
    private MusicTrackRepository trackRepository;
	
	 @Autowired
	private MusicTrackPlayerRepository trackPlayerRepository;


    @Override
    public Iterable<Collection> listAllCollections() {
        logger.debug("listAllCollections called");
        return collectionRepository.findAll();
    }
    
    @Override
    public Iterable<Collection> listAllCollectionsByMemberId(Long id) {
    	logger.debug("listAllCollectionsByMemberId called");
        return collectionRepository.findAllByMemberIdOrderByCreatedAtDesc(id);
    }
    
    @Override
    public Page<Collection> findByMemberId(Long memberId, PageRequest pageOrderNew) {
    	logger.debug("findByMember called");
        return collectionRepository.findByMemberId(memberId, pageOrderNew);
    }

    @Override
    public Collection getCollectionById(Long id) {
        logger.debug("getCollectionById called");
        return collectionRepository.findById(id).orElse(null);
    }

    @Override
    public Collection saveCollection(Collection collection) {
        logger.debug("saveCollection called");
        return collectionRepository.save(collection);
    }

    @Override
    public void deleteCollection(Long id) {
        logger.debug("deleteCollection called");
        Iterable<CollectionTrack> collectionTracks = collectionTrackRepository.findByCollectionId(id);
        for (CollectionTrack collectionTrack : collectionTracks) {
        	collectionTrackRepository.delete(collectionTrack);
        }
        collectionRepository.deleteById(id);
    }
    
    
    @Override
    public Page<MusicTrack> listTrackByCollection(Long collectionId, Long memberId, PageRequest pageMusicOrderByNew) {
    	logger.debug("listTrackByCollection called");
    	return trackRepository.findByCollectionIdWithHeartByMember(collectionId, memberId, pageMusicOrderByNew);
    }
    
    @Override
    public int countTracksByCollection(Long collectionId) {
    	logger.debug("countTracksByCollection called");
    	return collectionTrackRepository.countByCollectionId(collectionId);
    }
    
    @Override
    public CollectionTrack findByCollectionIdAndMusicTrackId(Long collectionId, Long trackId) {
    	logger.debug("findByCollectionIdAndMusicTrackId called");
    	return collectionTrackRepository.findByCollectionIdAndMusicTrackId(collectionId, trackId);
    }
    
    @Override
    public boolean saveCollectionTrack(Collection collection, Long musicTrackId) {
    	logger.debug("saveCollectionTrack called");
    	// 중복 검사
    	CollectionTrack collectionTrack = findByCollectionIdAndMusicTrackId(collection.getId(), musicTrackId);
    	if (collectionTrack == null && UPMusicConstants.TRACK_IN_LIST_LIMIT <= collectionTrackRepository.countByCollectionId(collection.getId())) {
    		return false;
    	} else if (collectionTrack == null) {
    		MusicTrack musicTrack = trackRepository.findById(musicTrackId).orElse(null);
    		if (musicTrack != null) {
    			collectionTrack = new CollectionTrack();
    			collectionTrack.setMusicTrack(musicTrack);
    			collectionTrack.setCollection(collection);
    			collectionTrackRepository.save(collectionTrack);
    		}
    	}
    	return true;
    }
    
    @Override
    public void removeOverCountTracks(Long collectionId) {
        Page<Long> page = collectionTrackRepository.findLatestId(collectionId, PageRequest.of(0, UPMusicConstants.TRACK_IN_LIST_LIMIT));
        collectionTrackRepository.deleteByExcludedId(collectionId, page.getContent());
    }
    
    @Override
    public void deleteCollectionTrack(Long collectionId, Long musicTrackId) {
    	logger.debug("deleteCollectionTrack called");
    	CollectionTrack collectionTrack = findByCollectionIdAndMusicTrackId(collectionId, musicTrackId);
    	if (collectionTrack != null) {
    		Collection collection = collectionTrack.getCollection();
    		collection.setTrackCnt(collection.getTrackCnt() - 1);
    		collectionRepository.save(collection);
    		collectionTrackRepository.delete(collectionTrack);
    	}
    }
    
    /**
     * 내가 담은 리스트의 곡을 플레이 리스트로 복사
     */
    @Override
    public boolean sendToPlaylist(Long collectionId, Member member) {
    	logger.debug("sendToPlaylist called");
    	Page<MusicTrack> collectionTracks = listTrackByCollection(collectionId, member.getId(), PageRequest.of(0, UPMusicConstants.TRACK_IN_LIST_LIMIT, Sort.Direction.DESC, "id"));
    	boolean overflow = false;
    	for (MusicTrack track : collectionTracks) {
    		Long listSize = trackPlayerRepository.countByMemberId(member.getId());
    		logger.debug("sendToPlaylist : listSize is {}", listSize);
    		// 중복 검사
	    	MusicTrackPlayer record = trackPlayerRepository.findByMusicTrackIdAndMemberId(track.getId(), member.getId());
	    	if (record != null) { // 중복된 곡은 삭제 후 생성하므로 한도와 무관함
	    		logger.debug("sendToPlaylist : duplicated");
	    		trackPlayerRepository.delete(record);
    		// 현재 목록의 한도 검사
	    	} else if (UPMusicConstants.TRACK_IN_LIST_LIMIT <= listSize) {
	    		logger.debug("sendToPlaylist : overflow");
	    		overflow = true;
	    		break;
	    	}
			record = new MusicTrackPlayer();
			record.setMusicTrack(track);
			record.setMember(member);
			trackPlayerRepository.save(record);
    	}
    	return !overflow;
    }
}

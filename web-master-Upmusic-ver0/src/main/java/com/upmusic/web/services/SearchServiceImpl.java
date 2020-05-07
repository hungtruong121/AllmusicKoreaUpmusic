package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.*;
import com.upmusic.web.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;


@Service
public class SearchServiceImpl implements SearchService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MusicTrackRepository trackRepository;
	
	@Autowired
    private MusicAlbumRepository albumRepository;
	
	@Autowired
    private MemberRepository memberRepository;
	
	@Autowired
    private VocalCastingRepository castingRepository;
	
	@Autowired
    private VocalCastingCommentRepository castingCommentRepository;
	
	@Autowired
    private VideoRepository videoRepository;
	
	@Autowired
	private BandRecruitRepository bandRecruitRepository;
	
	@Autowired
	private BandRecruitCommentRepository bandRecruitCommentRepository;

	@Autowired
	private SearchRepository searchRepository;
	
	@Override
    public Page<MusicTrack> searchTop10Tracks(String query, Long memberId) {
    	logger.debug("searchTop10Tracks called");
    	return trackRepository.findTop10ByQueryWithHeartByMember(query, memberId, PageRequest.of(0, 10));
    }
	
	@Override
    public List<MusicTrack> searchTracks(String query, Long memberId) {
    	logger.debug("searchTracks called");
    	return trackRepository.findByQueryWithHeartByMember(query, memberId);
    }
	
	@Override
    public Page<MusicAlbum> searchTop4Albums(String query, Long memberId) {
    	logger.debug("searchTop4Albums called");
    	return albumRepository.findTop4ByQueryWithHeartByMember(query, memberId, PageRequest.of(0, 4));
    }
    
    @Override
    public List<MusicAlbum> searchAlbums(String query, Long memberId) {
    	logger.debug("searchAlbums called");
    	return albumRepository.findByQueryWithHeartByMember(query, memberId);
    }
    
    @Override
    public Page<MusicTrack> searchTop10StoreTracks(String query, Long memberId) {
    	logger.debug("searchTop10StoreTracks called");
    	return trackRepository.findTop10StoreTracksByQueryWithHeartByMember(query, memberId, PageRequest.of(0, 10));
    }
    
    @Override
    public List<MusicTrack> searchStoreTracks(String query, Long memberId) {
    	logger.debug("searchTracks called");
    	return trackRepository.findStoreTracksByQueryWithHeartByMember(query, memberId);
    }
    
    @Override
    public Page<Member> searchTop4Artists(String query, Long memberId) {
    	logger.debug("searchTop4Artists called");
    	return memberRepository.findTop4ArtistByQueryWithHeartByMember(query, memberId, PageRequest.of(0, 4));
    }
    
    @Override
    public List<Member> searchArtists(String query, Long memberId) {
    	logger.debug("searchArtists called");
    	return memberRepository.findArtistByQueryWithHeartByMember(query, memberId);
    }
    
    @Override
    public Page<VocalCasting> searchTop10Castings(String query) {
    	logger.debug("searchTop10Castings called");
    	Page<VocalCasting> requests = castingRepository.findTop10ByQuery(query, PageRequest.of(0, 10));
    	for (VocalCasting request : requests) {
    		request.setCommentCnt(castingCommentRepository.countByVocalCasting(request));
    	}
    	return requests;
    }
    
    @Override
    public List<VocalCasting> searchCastings(String query) {
    	logger.debug("searchCastings called");
    	List<VocalCasting> requests = castingRepository.findByQuery(query);
    	for (VocalCasting request : requests) {
    		request.setCommentCnt(castingCommentRepository.countByVocalCasting(request));
    	}
    	return requests;
    }
    
    @Override
    public Page<Video> searchTop4Videos(String query) {
    	logger.debug("searchTop4Videos called");
    	return videoRepository.findTop4ByQuery(query, PageRequest.of(0, 4));
    }
    
    @Override
    public List<Video> searchVideos(String query) {
    	logger.debug("searchVideos called");
    	return videoRepository.findByQuery(query);
    }
    
    /**
     * TODO : add search result
     */
    @Override
    public Page<BandRecruit> searchTop4BandRecruits(String query) {
    	logger.debug("searchTop4BandRecruits called");
    	Page<BandRecruit> result = bandRecruitRepository.searchTop4BandRecruits(query, PageRequest.of(0, 4));
		
		for(BandRecruit br : result.getContent()) {
			br.setBandCommentCnt(bandRecruitCommentRepository.findCountByBandRecruitCommentId(br.getBandRecruitId()));
		}
		
		return result;
    }
    
    /**
     * TODO : add search result
     */
    @Override
    public List<BandRecruit> searchBandRecruits(String query) {
    	logger.debug("searchBandRecruits called");
    	List<BandRecruit> result = bandRecruitRepository.searchBandRecruits(query);
		for(BandRecruit br : result) {
			br.setBandCommentCnt(bandRecruitCommentRepository.findCountByBandRecruitCommentId(br.getBandRecruitId()));
		}
		
		return result;
    }

    @Override
	public void saveSearchKeyword(String query ,Member member) {
		Search search = new Search();
		search.setSearchText(query);
		search.setMember(member);

		if (member != null) {
			searchRepository.save(search);
		}
	}

	@Override
	public Page<Search> findTop10ByMemberId(@Param("memberId") Long memberId) {
    	Page<Search> list = searchRepository.findTop10ByMemberId(memberId, PageRequest.of(0, 10));

    	return list;
	}

	@Override
	public void deleteById(Long id) {
    	searchRepository.deleteById(id);
	}

	@Override
	public void deleteByMemberId(Long memberId) {
    	logger.debug("deleteByMemberId start");
    	searchRepository.deleteByMemberId(memberId);
	}

	@Override
	public Page<Object[]> findTop10ByPopularKeyword() {
		Page<Object[]> list = searchRepository.findTop10ByPopularKeyword(PageRequest.of(0, 10));

		for (int i=0; i<list.getSize(); i++) {


		}



		return list;
	}
}

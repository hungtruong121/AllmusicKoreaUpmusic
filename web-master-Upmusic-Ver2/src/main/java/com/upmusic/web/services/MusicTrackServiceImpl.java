package com.upmusic.web.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.domain.*;
import com.upmusic.web.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class MusicTrackServiceImpl implements MusicTrackService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private MusicTrackRepository trackRepository;
	
	@Autowired
	private MusicTrackHeartRecordRepository trackHeartRecordRepository;

    @Autowired
    private MusicTrackCooperatorRepository trackCooperatorRepository;
    
    @Autowired
	private MusicTrackPlayerRepository trackPlayerRepository;
    
    @Autowired
	private MusicTrackPlayRecordRepository trackPlayRecordRepository;
    
    @Autowired
	private MusicTrackRejectedReasonRepository trackRejectedReasonRepository;
    
    @Autowired
	private CollectionRepository collectionRepository;
    
    @Autowired
	private CollectionTrackRepository collectionTrackRepository;
    
    @Autowired
	private MusicAlbumRepository albumRepository;
    
    @Autowired
    private LeagueSeasonRepository leagueSeasonRepository;
	
	@Autowired
    private LeagueSeasonTrackRepository leagueSeasonTrackRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private RecommendedMusicTrackRepository recommendedTrackRepository;
	
	@Autowired
	private LeagueSeasonService leagueSeasonService;
	

    @Override
    public Iterable<MusicTrack> listAllTracks() {
        logger.debug("listAllTracks called");
        return trackRepository.findAll();
    }
    
    @Override
    public List<MusicTrack> findTop10(PageRequest pageOrder) {
    	logger.debug("findTop10 called");
        return trackRepository.findTop10(pageOrder);
    }
    
    @Override
    public Page<MusicTrack> findAllWithHeartByMember(Long memberId, PageRequest pageOrderByNew) {
    	logger.debug("findAllWithHeartByMember called");
    	return trackRepository.findAllWithHeartByMember(memberId, pageOrderByNew);
    }
    
    @Override
    public Page<MusicTrack> findAllByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
    	return trackRepository.findByGenreIdWithHeartByMember(genreId, memberId, pageOrderNew);
    }
    
    @Override
    public Page<MusicTrack> findAllByThemeWithHeartByMember(int themeId, Long memberId, PageRequest pageOrderNew) {
    	return trackRepository.findByThemeIdWithHeartByMember(themeId, memberId, pageOrderNew);
    }
    
    @Override
    public Page<MusicTrack> findAllByGenreAndInStoreTrueWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
    	return trackRepository.findByGenreIdAndInStoreTrueWithHeartByMember(genreId, memberId, pageOrderNew);
    }
    
    @Override
    public Page<MusicTrack> findByStatusIsAccepted(PageRequest pageOrderNew) {
    	return trackRepository.findByTrackStatusAndPublishedTrue(MusicTrackStatus.ACCEPTED, pageOrderNew);
    }
    
    @Override
    public Page<MusicTrack> findByStatusIsUnderExam(PageRequest pageOrderNew) {
    	return trackRepository.findByTrackStatusAndPublishedTrue(MusicTrackStatus.BEFORE_EXAM, pageOrderNew);
    }
    
    @Override
    public MusicTrack findBySubejct(String subject) {
    	return trackRepository.findBySubject(subject);
    }
	
    @Override
    public MusicTrack getTrackById(Long id) {
        logger.debug("getTrackById called");
        return trackRepository.findById(id).orElse(null);
    }

    @Override
    public MusicTrack saveTrack(MusicTrack track) {
        logger.debug("saveTrack called");
        return trackRepository.save(track);
    }
    
    @Override
    public void deleteTrack(Long id, Long memberId) {
        logger.debug("deleteTrack called");
        MusicTrack track = getTrackById(id);
        if (track != null && 0 == (track.getArtistId().compareTo(memberId))) {
        	// 좋아요 삭제
        	List<MusicTrackHeartRecord> records = trackHeartRecordRepository.findByMusicTrackId(id);
            for (MusicTrackHeartRecord record : records) {
            	trackHeartRecordRepository.delete(record);
            }
        	// 플레이어 삭제
            List<MusicTrackPlayer>  players = trackPlayerRepository.findByMusicTrackId(id);
            for (MusicTrackPlayer record : players) {
            	trackPlayerRepository.delete(record);
            }
            // 플레이 리스트 삭제
            List<MusicTrackPlayRecord>  playRecords = trackPlayRecordRepository.findByMusicTrackId(id);
            for (MusicTrackPlayRecord record : playRecords) {
            	trackPlayRecordRepository.delete(record);
            }
            // 콜렉션 삭제
            Iterable<CollectionTrack> collectionTracks = collectionTrackRepository.findByMusicTrackId(id);
            for (CollectionTrack collectionTrack : collectionTracks) {
            	Collection collection = collectionTrack.getCollection();
            	collectionTrackRepository.delete(collectionTrack);
            	collection.setTrackCnt(collectionTrackRepository.countByCollectionId(collection.getId()));
            	collectionRepository.save(collection);
            }
            // 거절 사유 삭제
            MusicTrackRejectedReason reason = trackRejectedReasonRepository.findByMusicTrackId(id);
            if (reason != null) trackRejectedReasonRepository.delete(reason);
            
            // 추천음원 삭제
            RecommendedMusicTrack recommendedTrack = recommendedTrackRepository.findByMusicTrackId(id);
            if (recommendedTrack != null) {
            	recommendedTrackRepository.delete(recommendedTrack);
            }
            
            // 차트 삭제
            leagueSeasonService.deleteTrack(track);
            
            
            if (track.getTrackStatus() == MusicTrackStatus.ACCEPTED) {
        		// 앨범의 acceptedTrackCnt 업데이트
        		MusicAlbum album = track.getMusicAlbum();
        		if (0 < album.getAcceptedTrackCnt()) album.setAcceptedTrackCnt(album.getAcceptedTrackCnt() - 1);
        		albumRepository.save(album);
        		// 회원의 음원수 차감
                Member member = track.getMusicAlbum().getMember();
                if (0 < member.getTrackCnt()) member.setTrackCnt(member.getTrackCnt() - 1);
                memberRepository.save(member);
            }
            trackRepository.deleteById(id);
        }
    }

    @Override
    public void deleteTrack(Long id) {
        logger.debug("deleteTrack called");
        MusicTrack track = getTrackById(id);
        if (track != null) {
        	// 좋아요 삭제
        	List<MusicTrackHeartRecord> records = trackHeartRecordRepository.findByMusicTrackId(id);
            for (MusicTrackHeartRecord record : records) {
            	trackHeartRecordRepository.delete(record);
            }
            // 플레이어 삭제
            List<MusicTrackPlayer>  players = trackPlayerRepository.findByMusicTrackId(id);
            for (MusicTrackPlayer record : players) {
            	trackPlayerRepository.delete(record);
            }
            // 플레이 리스트 삭제
            List<MusicTrackPlayRecord>  playRecords = trackPlayRecordRepository.findByMusicTrackId(id);
            for (MusicTrackPlayRecord record : playRecords) {
            	trackPlayRecordRepository.delete(record);
            }
            // 콜렉션 삭제
            Iterable<CollectionTrack> collectionTracks = collectionTrackRepository.findByMusicTrackId(id);
            for (CollectionTrack collectionTrack : collectionTracks) {
            	Collection collection = collectionTrack.getCollection();
            	collectionTrackRepository.delete(collectionTrack);
            	collection.setTrackCnt(collectionTrackRepository.countByCollectionId(collection.getId()));
            	collectionRepository.save(collection);
            }
            // 거절 사유 삭제
            MusicTrackRejectedReason reason = trackRejectedReasonRepository.findByMusicTrackId(id);
            if (reason != null) trackRejectedReasonRepository.delete(reason);
            
            // 추천음원 삭제
            RecommendedMusicTrack recommendedTrack = recommendedTrackRepository.findByMusicTrackId(id);
            if (recommendedTrack != null) {
            	recommendedTrackRepository.delete(recommendedTrack);
            }
            
            // 차트 삭제
            leagueSeasonService.deleteTrack(track);
            
            
            if (track.getTrackStatus() == MusicTrackStatus.ACCEPTED) {
        		// 앨범의 acceptedTrackCnt 업데이트
        		MusicAlbum album = track.getMusicAlbum();
        		if (0 < album.getAcceptedTrackCnt()) album.setAcceptedTrackCnt(album.getAcceptedTrackCnt() - 1);
        		albumRepository.save(album);
        		// 회원의 음원수 차감
                Member member = track.getMusicAlbum().getMember();
                if (0 < member.getTrackCnt()) member.setTrackCnt(member.getTrackCnt() - 1);
                memberRepository.save(member);
            }
            trackRepository.deleteById(id);
        }
    }
    
    /**
     * 재생수 추가
     */
    @Override
    public int increasePlayCnt(Long id, boolean existsCookie, Member member) {
    	logger.debug("increaseHitCnt called");
    	MusicTrack track = trackRepository.findById(id).orElse(null);
        if (track != null) {
        	// 쿠키가 없을 경우에만 재생 건수 증가
        	if (!existsCookie) {
        		track.setPlayCnt(track.getPlayCnt() + 1);
            	track = trackRepository.save(track);
        	}
        	if (member != null) {
        		MusicTrackPlayRecord playRecord = trackPlayRecordRepository.findByMusicTrackIdAndMemberId(id, member.getId());
        		if (playRecord != null) {
        			logger.debug("increaseHitCnt called : playRecord is not null");
        			playRecord.setPlayedAt(new Date());
        			trackPlayRecordRepository.save(playRecord);
        		} else {
        			logger.debug("increaseHitCnt called : create playRecord");
        			playRecord = new MusicTrackPlayRecord();
        			playRecord.setMusicTrack(track);
        			playRecord.setMember(member);
        			playRecord.setPlayedAt(new Date());
        			trackPlayRecordRepository.save(playRecord);
        		}
        	}
        	if (!existsCookie) {
	        	// 현재 리그에 참여중인 곡일 경우 해당 데이터 업데이트
	        	LeagueSeason currentSeason = getCurrentLeagueSeason();
	        	if (currentSeason != null) {
	        		LeagueSeasonTrack seasonTrack = leagueSeasonTrackRepository.findOneByMusicTrack(track);
	        		if (seasonTrack != null) {
	        			seasonTrack.setPlayCnt(seasonTrack.getPlayCnt() + 1);
	            		seasonTrack = leagueSeasonTrackRepository.save(seasonTrack);
	        		}
	        	}
        	}
        	return track.getPlayCnt();
        }
        return 0;
    }
    
    /**
     * 해당 비디오와 회원의 좋아요 여부
     */
    @Override
    public boolean likedTrack(Long id, Long memberId) {
    	logger.debug("likedTrack called : id is {} and member is {}", id, memberId);
    	MusicTrackHeartRecord record = trackHeartRecordRepository.findByMusicTrackIdAndMemberId(id, memberId);
    	return record != null;
    }
    
    /**
     * 좋아요 추가
     */
    @Override
    public int increaseHeartCnt(Long id, Member member) {
    	logger.debug("increaseHeartCnt called");
    	MusicTrack track = trackRepository.findById(id).orElse(null);
        if (track != null) {
        	MusicTrackHeartRecord record = new MusicTrackHeartRecord();
        	record.setMusicTrack(track);
    		record.setMember(member);
    		trackHeartRecordRepository.save(record);
        	track.setHeartCnt(track.getHeartCnt() + 1);
        	track = trackRepository.save(track);
        	// 현재 리그에 참여중인 곡일 경우 해당 데이터 업데이트
        	LeagueSeason currentSeason = getCurrentLeagueSeason();
        	if (currentSeason != null) {
        		LeagueSeasonTrack seasonTrack = leagueSeasonTrackRepository.findOneByMusicTrack(track);
        		if (seasonTrack != null) {
        			seasonTrack.setHeartCnt(seasonTrack.getHeartCnt() + 1);
            		seasonTrack = leagueSeasonTrackRepository.save(seasonTrack);
        		}
        	}
        	return track.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 좋아요 취소
     */
    @Override
    public int decreaseHeartCnt(Long id, Member member) {
    	logger.debug("decreaseHeartCnt called");
    	MusicTrack track = trackRepository.findById(id).orElse(null);
        if (track != null) {
        	MusicTrackHeartRecord record = trackHeartRecordRepository.findByMusicTrackIdAndMemberId(id, member.getId());
        	if (record != null) {
        		trackHeartRecordRepository.delete(record);
        		if (0 < track.getHeartCnt()) {
            		track.setHeartCnt(track.getHeartCnt() - 1);
            		track = trackRepository.save(track);
            		// 현재 리그에 참여중인 곡일 경우 해당 데이터 업데이트
                	LeagueSeason currentSeason = getCurrentLeagueSeason();
                	if (currentSeason != null) {
                		LeagueSeasonTrack seasonTrack = leagueSeasonTrackRepository.findOneByMusicTrack(track);
                		if (seasonTrack != null) {
                			seasonTrack.setHeartCnt(seasonTrack.getHeartCnt() - 1);
                    		seasonTrack = leagueSeasonTrackRepository.save(seasonTrack);
                		}
                	}
            	}
        	}
        	return track.getHeartCnt();
        }
        return 0;
    }
    
    /*
     * Cooperator
     */
    
    @Override
    public MusicTrackCooperator saveTrackCooperator(MusicTrackCooperator cooperator) {
    	logger.debug("saveTrackCooperator called");
    	return trackCooperatorRepository.save(cooperator);
    }
    
    @Override
    public void deleteTrackCooperator(MusicTrackCooperator cooperator) {
    	logger.debug("saveTrackCooperator called");
    	trackCooperatorRepository.delete(cooperator);
    }
    
    /*
     * Player
     */
    
    @Override
    public Iterable<MusicTrack> findPlaylistWithHeartByMember(Long memberId) {
    	logger.debug("findPlaylistWithHeartByMember called");
        return trackRepository.findPlaylistWithHeartByMember(memberId);
    }
    
    @Override
    public boolean addTrackToPlayer(Long id, Member member) {
    	logger.debug("addTrackToPlayer called");
		MusicTrack musicTrack = trackRepository.findById(id).orElse(null);
		if (musicTrack != null) {
			Long listCount = trackPlayerRepository.countByMemberId(member.getId());
			// 중복 검사
	    	MusicTrackPlayer record = trackPlayerRepository.findByMusicTrackIdAndMemberId(id, member.getId());
	    	if (record != null) { // 중복된 곡은 삭제 후 생성하므로 한도와 무관함
	    		trackPlayerRepository.delete(record);
    		// 현재 목록의 한도 검사
	    	} else if (UPMusicConstants.TRACK_IN_LIST_LIMIT <= listCount) {
	    		return false;
	    	}
			record = new MusicTrackPlayer();
			record.setMusicTrack(musicTrack);
			record.setMember(member);
			trackPlayerRepository.save(record);
			return true;
		}
		return false;
    }

    public boolean addTrackToPlayerList(JsonArray ids, Member member) {
        logger.debug("addTrackToPlayerList called");

        Long listCount = trackPlayerRepository.countByMemberId(member.getId());

        List<Long> idList = new ArrayList<>();
        for (int i=0; i<ids.size(); i++) {
            idList.add(i, ids.get(i).getAsLong());
        }

        //가져온 음원트랙아이디 리스트
        List<MusicTrack> musicTrackList = trackRepository.findAllByMusicTrackId(idList);

        //유저가 가지고있는 플레이리스트
        List<MusicTrackPlayer> musicTrackPlayerList = trackPlayerRepository.findByMemberId(member.getId());

        List<MusicTrackPlayer> newMusicTrackPlayerList = new ArrayList<>();

        //삭제할 아이디리스트
        List<Long> deleteIds = new ArrayList<>();

        if (musicTrackList != null && musicTrackList.size() > 0) {
            for (MusicTrack musicTrack : musicTrackList) {
                // 중복 검사
                if (musicTrack != null && musicTrackPlayerList.size() > 0) {
                    if (UPMusicConstants.TRACK_IN_LIST_LIMIT <= listCount) {
                        return false;
                    }

                    for (MusicTrackPlayer musicTrackPlayer : musicTrackPlayerList) {
                        if (musicTrackPlayer != null && musicTrackPlayer.getMusicTrack().getId().equals(musicTrack.getId())) {
                            deleteIds.add(musicTrack.getId());

                        }
                    }
                }

                MusicTrackPlayer record = new MusicTrackPlayer();
                record.setMusicTrack(musicTrack);
                record.setMember(member);
                newMusicTrackPlayerList.add(record);

            }

            //중복된 음원리스트 삭제
            if (deleteIds != null && deleteIds.size() > 0) {
                trackPlayerRepository.deleteByMusicTrackPlayerIds(member.getId() ,deleteIds);
            }

            //음원리스트 저장
            trackPlayerRepository.saveAll(newMusicTrackPlayerList);
            return true;
        }

        return false;
    }
    
    @Override
    public void removeAllTrackFromPlayer(Member member) {
    	logger.debug("removeAllTrackFromPlayer called");
    	trackPlayerRepository.deleteByMemberId(member.getId());
    }
    
    @Override
    public void removeTrackFromPlayer(Long id, Member member) {
    	logger.debug("removeTrackFromPlayer called");
    	MusicTrackPlayer record = trackPlayerRepository.findByMusicTrackIdAndMemberId(id, member.getId());
    	if (record != null) {
    		trackPlayerRepository.delete(record);
    	}
    }
    
    @Override
    public void removeOverCountTracksOfPlayer(Long memberId) {
    	Page<Long> page = trackPlayerRepository.findLatestId(memberId, PageRequest.of(0, UPMusicConstants.TRACK_IN_LIST_LIMIT));
    	trackPlayerRepository.deleteByExcludedId(memberId, page.getContent());
    }
    
    /*
     * Play list
     */
    
    @Override
    public Page<MusicTrack> findPlayedTrackByMemberId(Long memberId, PageRequest pageOrderRequest) {
    	logger.debug("findPlayedTrackByMemberId called");
    	return trackRepository.findAllByPlayWithHeartByMember(memberId, pageOrderRequest);
    }
    
    @Override
    public List<MusicTrack> findAllPlayedTrackByMemberId(Long memberId) {
    	logger.debug("findAllPlayedTrackByMemberId called");
    	return trackRepository.findAllByPlayWithHeartByMember(memberId);
    }
    
    @Override
    public void removeTrackFromPlaylist(Long id, Member member) {
    	logger.debug("removeTrackFromPlaylist called");
    	MusicTrackPlayRecord record = trackPlayRecordRepository.findByMusicTrackIdAndMemberId(id, member.getId());
    	if (record != null) {
    		trackPlayRecordRepository.delete(record);
    	}
    }
    
    /*
     * Like list
     */
    
    @Override
    public Page<MusicTrack> findHeartTrackByMemberId(Long memberId, PageRequest pageOrderRequest) {
    	logger.debug("findHeartTrackByMemberId called");
    	return trackRepository.findAllByMemberHeart(memberId, pageOrderRequest);
    }
    
    @Override
    public List<MusicTrack> findAllHeartTrackByMemberId(Long memberId) {
    	logger.debug("findAllHeartTrackByMemberId called");
    	return trackRepository.findAllByMemberHeart(memberId);
    }
    
    @Override
    public void removeTrackFromHeartlist(Long id, Member member) {
    	logger.debug("removeTrackFromHeartlist called");
    	decreaseHeartCnt(id, member);
    }
    
    /*
     * 아티스트의 업로드 내역
     */
    
    @Override
    public Long findCountByArtistId(Long artistId) {
    	logger.debug("findCountByArtistId called");
    	return trackRepository.findCountByArtistId(artistId);
    }
    
    @Override
    public Long findCountByArtistIdAndInStoreTrue(Long artistId) {
    	logger.debug("findCountByArtistIdAndInStoreTrue called");
    	return trackRepository.findCountByArtistIdAndInStoreTrue(artistId);
    }
    
    @Override
    public Page<MusicTrack> findUploadedByArtistIdWithHeartByMember(Long artistId, Long memberId, PageRequest pageRequest) {
    	logger.debug("findUploadedByArtistIdWithHeartByMember called");
    	return trackRepository.findUploadedByArtistIdWithHeartByMember(artistId, memberId, pageRequest);
    }
    
    /*
     * 아티스트의 음원
     */
    
    @Override
    public List<MusicTrack> findTop5ByArtistIdWithHeartByMember(Long artistId, Long memberId) {
    	logger.debug("findTop5ByArtistIdWithHeartByMember called");
    	return trackRepository.findTop5ByArtistIdWithHeartByMember(artistId, memberId, PageRequest.of(0, 5, Sort.Direction.DESC, "hotPoint"));
    }
    
    @Override
    public List<MusicTrack> findTop10ByArtistIdWithHeartByMember(Long artistId, Long memberId) {
    	logger.debug("findTop10ByArtistIdWithHeartByMember called");
    	return trackRepository.findTop10ByArtistIdWithHeartByMember(artistId, memberId, PageRequest.of(0, 10, Sort.Direction.DESC, "id"));
    }
    
    @Override
    public List<MusicTrack> findByArtistIdWithHeartByMember(Long artistId, Long memberId) {
    	logger.debug("findByArtistIdWithHeartByMember called");
    	return trackRepository.findByArtistIdWithHeartByMember(artistId, memberId);
    }
    
    @Override
    public List<MusicTrack> findTop10ByArtistIdAndInStoreTrueWithHeartByMember(Long artistId, Long memberId) {
    	logger.debug("findTop10ByArtistIdAndInStoreTrueWithHeartByMember called");
    	return trackRepository.findTop10ByArtistIdAndInStoreTrueWithHeartByMember(artistId, memberId, PageRequest.of(0, 10, Sort.Direction.DESC, "id"));
    }

    @Override
    public Page<MusicTrack> findAllByArtistIdWithHeartByMember(Long artistId, Long memberId, PageRequest pageRequest) {
        logger.debug("findAllByArtistIdWithHeartByMember called");
        return trackRepository.findAllByArtistIdWithHeartByMember(artistId, memberId, pageRequest);
    }
    
    @Override
    public List<MusicTrack> findByArtistIdAndInStoreTrueWithHeartByMember(Long artistId, Long memberId) {
    	logger.debug("findByArtistIdAndInStoreTrueWithHeartByMember called");
    	return trackRepository.findByArtistIdAndInStoreTrueWithHeartByMember(artistId, memberId);
    }

    @Override
    public Page<MusicTrack> findAllByArtistIdAndInStoreTrueWithHeartByMember(Long artistId, Long memberId, PageRequest pageRequest) {
        logger.debug("findAllByArtistIdAndInStoreTrueWithHeartByMember called");
        return trackRepository.findAllByArtistIdAndInStoreTrueWithHeartByMember(artistId, memberId, pageRequest);
    }
    
    @Override
    public void deleteTracksByMusicAlbumId(Long albumId) {
    	logger.debug("deleteTracksByMusicAlbumId called");
    	List<MusicTrack> tracks = trackRepository.findAllByMusicAlbumId(albumId);
    	for (MusicTrack track : tracks) {
    		this.deleteTrack(track.getId());
    	}
    }

    /**
     * 관리자용
     */

    //사용자 전체 음원
    @Override
    public Page<MusicTrack> findUploadedByUsers(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByUsers called");
        return trackRepository.findUploadedByUsers(memberId, pageRequest);
    }

    //사용자 업리그 음원
    @Override
    public Page<MusicTrack> findUploadedByUsersByUpleague(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByUsersByUpleague called");
        return trackRepository.findUploadedByUsersByUpleague(memberId, pageRequest);
    }

    //사용자 최신곡 음원
    @Override
    public Page<MusicTrack> findUploadedByUsersByRecent(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByUsersByRecent called");
        return trackRepository.findUploadedByUsersByRecent(memberId, pageRequest);
    }

    //사용자 뮤직스토어 음원
    @Override
    public Page<MusicTrack> findUploadedByUsersByMusicStore(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByUsersByMusicStore called");
        return trackRepository.findUploadedByUsersByMusicStore(memberId, pageRequest);
    }

    //사용자 업리그 심사 대기 음원
    @Override
    public Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByUpleague(PageRequest pageRequest) {
        logger.debug("findByTrackStatusAndPublishedTrueByUserByUpleague called");
        return trackRepository.findByTrackStatusAndPublishedTrueByUserByUpleague(pageRequest);
    }

    //사용자 최신곡 심사 대기 음원
    @Override
    public Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByRecent(PageRequest pageRequest) {
        logger.debug("findByTrackStatusAndPublishedTrueByUserByRecent called");
        return trackRepository.findByTrackStatusAndPublishedTrueByUserByRecent(pageRequest);
    }

    //사용자 뮤직스토어 심사 대기 음원
    @Override
    public Page<MusicTrack> findByTrackStatusAndPublishedTrueByUserByMusicStore(PageRequest pageRequest) {
        logger.debug("findByTrackStatusAndPublishedTrueByUserByMusicStore called");
        return trackRepository.findByTrackStatusAndPublishedTrueByUserByMusicStore(pageRequest);
    }

    //관리자 전체 음원
    @Override
    public Page<MusicTrack> findUploadedByAdmin(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByAdmin called");
        return trackRepository.findUploadedByAdmin(memberId, pageRequest);
    }

    //관리자 업리그 음원
    @Override
    public Page<MusicTrack> findUploadedByAdminByUpleague(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByAdminByUpleague called");
        return trackRepository.findUploadedByAdminByUpleague(memberId, pageRequest);
    }

    //관리자 최신곡 음원
    @Override
    public Page<MusicTrack> findUploadedByAdminByRecent(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByAdminByRecent called");
        return trackRepository.findUploadedByAdminByRecent(memberId, pageRequest);
    }

    //관리자 뮤직스토어 음원
    @Override
    public Page<MusicTrack> findUploadedByAdminByMusicStore(Long memberId, PageRequest pageRequest) {
        logger.debug("findUploadedByAdminByMusicStore called");
        return trackRepository.findUploadedByAdminByMusicStore(memberId, pageRequest);
    }

    //관리자 업리그 심사대기 음원
    @Override
    public Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByUpleague(PageRequest pageRequest) {
        logger.debug("findByTrackStatusAndPublishedTrueByAdmin called");
        return trackRepository.findByTrackStatusAndPublishedTrueByAdminByUpleague(pageRequest);
    }

    //관리자 최신곡 심사대기 음원
    @Override
    public Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByRecent(PageRequest pageRequest) {
        logger.debug("findByTrackStatusAndPublishedTrueByRecent called");
        return trackRepository.findByTrackStatusAndPublishedTrueByAdminByRecent(pageRequest);
    }

    //관리자 뮤직스토어 심사대기 음원
    @Override
    public Page<MusicTrack> findByTrackStatusAndPublishedTrueByAdminByMusicStore(PageRequest pageRequest) {
        logger.debug("findByTrackStatusAndPublishedTrueByAdminByMusicStore called");
        return trackRepository.findByTrackStatusAndPublishedTrueByAdminByMusicStore(pageRequest);
    }

    /**
     * 타이틀곡 지정
     */
    @Override
    public String setTitleTrack(Long id, Long memberId) {
    	logger.debug("setTitleTrack called : id is {}", id);
    	String message = "곡을 찾을 수 없습니다.";
    	MusicTrack track = trackRepository.findById(id).orElse(null);
    	if (track != null && 0 == track.getArtistId().compareTo(memberId)) {
    		if (track.isTitleTrack()) {
    			message = "이미 타이틀곡으로 지정되었습니다.";
    		} else {
    			MusicAlbum album = track.getMusicAlbum();
        		for (MusicTrack musicTrack : album.getTracks()) {
    				musicTrack.setTitleTrack(musicTrack.getId().compareTo(id) == 0);
    				trackRepository.save(musicTrack);
        		}
        		message = "true";
    		}
    	}
    	return message;
    }
    
    /**
     * 곡의 심사 후 상태 변경
     */
    @Override
    public MusicTrack setTrackStatus(Long id, MusicTrackStatus status) {
    	MusicTrack track = getTrackById(id);
    	track.setTrackStatus(status);
    	saveTrack(track);
    	
    	if (status == MusicTrackStatus.ACCEPTED) {
    		// 앨범의 acceptedTrackCnt 업데이트
    		MusicAlbum album = track.getMusicAlbum();
    		album.setAcceptedTrackCnt(album.getAcceptedTrackCnt() + 1);
    		albumRepository.save(album);
    		// 회원의 음원수 증가
            Member member = track.getMusicAlbum().getMember();
            member.setTrackCnt(member.getTrackCnt() + 1);
            memberRepository.save(member);
    		// 업리그 등록
    		if (track.isInLeague()) registerTrack(track);
    	}
    	
    	return track;
    }
    
    /**
     * 곡 심사반려 사유를 저장
     */
    @Override
    public void setTrackRejectedReason(Long id, String reason) {
    	MusicTrack track = getTrackById(id);
    	MusicTrackRejectedReason rejectedReason = new MusicTrackRejectedReason();
    	rejectedReason.setMusicTrack(track);
    	rejectedReason.setReason(reason);
    	trackRejectedReasonRepository.save(rejectedReason);
    }
    
    /**
     * 현재 리그 시즌이 존재할 경우 반환
     * @return leagueSeason
     */
    private LeagueSeason getCurrentLeagueSeason() {
    	logger.debug("getCurrentLeagueSeason called");
        return leagueSeasonRepository.findOneByOpenDateLessThanEqualAndCloseDateGreaterThanEqual();
    }
    
    /**
     * 곡을 현재 리그 시즌에 등록 
     */
    private void registerTrack(MusicTrack track) {
    	logger.debug("registerTrack called");
    	LeagueSeason currentSeason = getCurrentLeagueSeason();
    	if (currentSeason != null) {
    		LeagueSeasonTrack seasonTrack = leagueSeasonTrackRepository.findOneByMusicTrack(track);
    		if (seasonTrack == null) {
    			seasonTrack = new LeagueSeasonTrack();
        		seasonTrack.setMusicTrack(track);
        		seasonTrack.setGenre(track.getGenre());
        		leagueSeasonTrackRepository.save(seasonTrack);
    		}
    	}
    }

	@Override
	public void deleteTrackRejectReason(Long id) {
		MusicTrackRejectedReason musicTrackRejectedReason = trackRejectedReasonRepository.findByMusicTrackId(id);
		if(musicTrackRejectedReason != null){
            trackRejectedReasonRepository.delete(musicTrackRejectedReason);
        }
	}

	@Override
	public Page<MusicTrack> findAllInStoreTrueWithHeartByMember(Long memberId, PageRequest pageOrderNew) {
		return trackRepository.findByStoreTrueWithHeartByMember(memberId, pageOrderNew);
	}
    
}

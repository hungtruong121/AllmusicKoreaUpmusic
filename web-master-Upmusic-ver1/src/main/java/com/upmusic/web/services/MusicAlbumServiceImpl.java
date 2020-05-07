package com.upmusic.web.services;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicAlbum;
import com.upmusic.web.domain.MusicAlbumComment;
import com.upmusic.web.domain.MusicAlbumHeartRecord;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.repositories.MusicAlbumCommentRepository;
import com.upmusic.web.repositories.MusicAlbumHeartRecordRepository;
import com.upmusic.web.repositories.MusicAlbumRepository;
import com.upmusic.web.repositories.MusicTrackRepository;


@Service
public class MusicAlbumServiceImpl implements MusicAlbumService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private MusicAlbumRepository albumRepository;
    
	@Autowired
	private MusicAlbumCommentRepository albumCommentRepository;
    
	@Autowired
	private MusicAlbumHeartRecordRepository albumHeartRecordRepository;
	
	@Autowired
	private MusicTrackRepository trackRepository;
	
	@Autowired
	private MusicTrackService trackService;


    @Override
    public Iterable<MusicAlbum> listAllAlbums() {
        logger.debug("listAllAlbums called");
        return albumRepository.findAll();
    }
    
    @Override
    public Page<MusicAlbum> findAllSAWithHeartByMember(Long memberId, PageRequest pageOrderByNew) {
    	logger.debug("findAllWithHeartByMember called");
    	return albumRepository.findAllSAWithHeartByMember(memberId, pageOrderByNew);
    }
    
    @Override
    public Page<MusicAlbum> findAllSAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
    	return albumRepository.findAllSAByGenreIdWithHeartByMember(genreId, memberId, pageOrderNew);
    }
    
//    @Override
//    public Page<MusicAlbum> findHomeSAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
//    	return albumRepository.findHomeSAByGenreIdWithHeartByMember(genreId, memberId, pageOrderNew);
//    }
//    
//    @Override
//    public Page<MusicAlbum> findAbroadSAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
//    	return albumRepository.findAbroadSAByGenreIdWithHeartByMember(genreId, memberId, pageOrderNew);
//    }
    
    @Override
    public Page<MusicAlbum> findAllGAWithHeartByMember(Long memberId, PageRequest pageOrderByNew) {
    	logger.debug("findAllGAWithHeartByMember called");
    	return albumRepository.findAllGAWithHeartByMember(memberId, pageOrderByNew);
    }
    
    @Override
    public Page<MusicAlbum> findAllGAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
    	return albumRepository.findAllGAByGenreIdWithHeartByMember(genreId, memberId, pageOrderNew);
    }
    
//    @Override
//    public Page<MusicAlbum> findHomeGAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
//    	return albumRepository.findHomeGAByGenreIdWithHeartByMember(genreId, memberId, pageOrderNew);
//    }
//    
//    @Override
//    public Page<MusicAlbum> findAbroadGAByGenreWithHeartByMember(int genreId, Long memberId, PageRequest pageOrderNew) {
//    	return albumRepository.findAbroadGAByGenreIdWithHeartByMember(genreId, memberId, pageOrderNew);
//    }
    
    @Override
    public Long findCountByArtistId(Long artistId) {
    	logger.debug("findCountByArtistId called");
    	return albumRepository.findCountByMemberId(artistId);
    }
    
    @Override
    public List<MusicAlbum> findTop4ByArtistIdWithHeartByMember(Long artistId, Long memberId) {
    	logger.debug("findTop4ByArtistIdWithHeartByMember called");
    	return albumRepository.findTop4ByArtistIdWithHeartByMember(artistId, memberId, PageRequest.of(0, 4, Sort.Direction.DESC, "id"));
    }

    @Override
    public Page<MusicAlbum> findAllByArtistIdWithHeartByMember(Long artistId, Long memberId, PageRequest pageOrderNew) {
        logger.debug("findTop4ByArtistIdWithHeartByMember called");
        return albumRepository.findAllByArtistIdWithHeartByMember(artistId, memberId, pageOrderNew);
    }
    
    @Override
    public List<MusicAlbum> findByArtistIdWithHeartByMember(Long artistId, Long memberId) {
    	logger.debug("findByArtistIdWithHeartByMember called");
    	return albumRepository.findByArtistIdWithHeartByMember(artistId, memberId);
    }
    
    @Override
    public Iterable<MusicAlbum> getTop5ByMemberId(Long memberId) {
    	logger.debug("getTop5ByMember called");
        return albumRepository.findTop5ByMemberId(memberId, PageRequest.of(0, 5, Sort.Direction.DESC, "hotPoint"));
    }

    @Override
    public MusicAlbum getAlbumById(Long id) {
        logger.debug("getAlbumById called");
        return albumRepository.findById(id).orElse(null);
    }

    @Override
    public MusicAlbum saveAlbum(MusicAlbum album) {
        logger.debug("saveAlbum called");
        return albumRepository.save(album);
    }
    
    @Override
    public boolean completeAlbum(Long id) {
    	logger.debug("completeAlbum called");
    	MusicAlbum album = albumRepository.findById(id).orElse(null);
    	if (album != null) {
    		album.setPublished(true);
    		albumRepository.save(album);
    		List<MusicTrack> tracks = album.getTracks();
    		for (MusicTrack track : tracks) {
    			track.setPublished(true);
    			trackRepository.save(track);
    		}
    		return true;
    	}
    	return false;
    }

    @Override
    public void deleteAlbum(Long id) {
        logger.debug("deleteAlbum called");
        // TODO delete track, comment, heart too
        albumRepository.deleteById(id);
    }
    
    @Override
    public void deleteAlbumsByMemberId(Long memberId) {
    	logger.debug("deleteAlbumsByMemberId called");
    	List<MusicAlbum> albums = albumRepository.findAllByMemberId(memberId);
		for (MusicAlbum album : albums) {
			this.deleteAlbum(album);
		}

		//다른 회원의 게시물에 내가 남긴 댓글 삭제
		List<MusicAlbumComment> albumComments = albumCommentRepository.findAllByMemberId(memberId);
        for (MusicAlbumComment albumComment : albumComments) {
            albumCommentRepository.deleteById(albumComment.getId());
        }

    }
    
    private void deleteAlbum(MusicAlbum album) {
    	trackService.deleteTracksByMusicAlbumId(album.getId());
    	
    	// 좋아요 삭제
    	List<MusicAlbumHeartRecord> records = albumHeartRecordRepository.findAllByMusicAlbumId(album.getId());
        for (MusicAlbumHeartRecord record : records) {
        	albumHeartRecordRepository.delete(record);
        }
        // 댓글 삭제
        List<MusicAlbumComment> comments = albumCommentRepository.findByMusicAlbumId(album.getId());
        for (MusicAlbumComment comment : comments) {
        	albumCommentRepository.delete(comment);
        }
    }
    
    /**
     * 조회수 추가
     */
    @Override
    public int increaseHitCnt(Long id) {
    	logger.debug("increaseHitCnt called");
    	MusicAlbum album = albumRepository.findById(id).orElse(null);
        if (album != null) {
        	album.setHitCnt(album.getHitCnt() + 1);
        	album = albumRepository.save(album);
        	return album.getHitCnt();
        }
        return 0;
    }
    
    /**
     * 해당 앨범과 회원의 좋아요 여부
     */
    @Override
    public boolean likedAlbum(Long id, Long memberId) {
    	logger.debug("likedAlbum called : id is {} and member is {}", id, memberId);
    	List<MusicAlbumHeartRecord> records = albumHeartRecordRepository.findAllByMusicAlbumIdAndMemberId(id, memberId);
    	return 0 < records.size();
    }
    
    /**
     * 좋아요 추가
     */
    @Override
    public int increaseHeartCnt(Long id, Member member) {
    	logger.debug("increaseHeartCnt called");
    	MusicAlbum album = albumRepository.findById(id).orElse(null);
        if (album != null) {
        	MusicAlbumHeartRecord record = new MusicAlbumHeartRecord();
        	record.setMusicAlbum(album);
    		record.setMember(member);
    		albumHeartRecordRepository.save(record);
        	album.setHeartCnt(album.getHeartCnt() + 1);
        	album = albumRepository.save(album);
        	return album.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 좋아요 취소
     */
    @Override
    public int decreaseHeartCnt(Long id, Member member) {
    	logger.debug("decreaseHeartCnt called");
    	MusicAlbum album = albumRepository.findById(id).orElse(null);
        if (album != null) {
        	List<MusicAlbumHeartRecord> records = albumHeartRecordRepository.findAllByMusicAlbumIdAndMemberId(id, member.getId());
        	if (0 < records.size()) {
        		albumHeartRecordRepository.delete(records.get(0));
        		if (0 < album.getHeartCnt()) {
            		album.setHeartCnt(album.getHeartCnt() - 1);
            		album = albumRepository.save(album);
            	}
        	} else {
        		// something wrong
        	}
        	return album.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 댓글 목록 반환
     */
    @Override
    public Page<MusicAlbumComment> getComments(Long albumId, PageRequest pageOrderRequest) {
    	logger.debug("getComments called");
    	return albumCommentRepository.findByMusicAlbumId(albumId, pageOrderRequest);
    }
    
    /**
     * 댓글 반환
     */
    @Override
    public MusicAlbumComment getCommentById(Long commentId) {
    	logger.debug("getCommentById called");
    	return albumCommentRepository.findById(commentId).orElse(null);
    }
    @Override
    public MusicAlbumComment getCommentById(Long albumId, Long commentId) {
    	logger.debug("getCommentById called");
    	return albumCommentRepository.findById(commentId).orElse(null);
    }
    
    /**
     * 댓글 추가
     */
    @Override
    public MusicAlbumComment addComment(Long id, Member member, String content, String ip) {
    	logger.debug("addComment called");
    	MusicAlbum album = albumRepository.findById(id).orElse(null);
        if (album != null) {
        	MusicAlbumComment comment = new MusicAlbumComment();
        	comment.setIp(ip);
        	comment.setMember(member);
        	comment.setContent(content);
        	comment.setMusicAlbum(album);
        	comment = albumCommentRepository.save(comment);
        	return comment;
        }
        return null;
    }
    
    /**
     * 댓글 업데이트
     */
    @Override
    public MusicAlbumComment updateComment(Long id, Long commentId, Member member, String content, String ip) {
    	logger.debug("updateComment called");
    	MusicAlbum album = albumRepository.findById(id).orElse(null);
        if (album != null) {
        	MusicAlbumComment comment = albumCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(member.getId()))) {
    			comment.setIp(ip);
            	comment.setMember(member);
            	comment.setContent(content);
            	comment.setMusicAlbum(album);
    			comment = albumCommentRepository.save(comment);
    			return comment;
    		}
        }
        return null;
    }
    
    /**
     * 댓글 삭제
     */
    @Override
    public Long deleteComment(Long id, Long commentId, Long memberId) {
    	logger.debug("deleteComment called");
    	MusicAlbum album = albumRepository.findById(id).orElse(null);
        if (album != null) {
        	MusicAlbumComment comment = albumCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(memberId))) {
    			albumCommentRepository.delete(comment);
    		}
        	return commentId;
        }
        return null;
    }
    
    class MusicAlbumCommentComparator implements Comparator<MusicAlbumComment> {
    	@Override
    	public int compare(MusicAlbumComment o1, MusicAlbumComment o2) {
    		return o1.getId().compareTo(o2.getId());
    	}
    }
}

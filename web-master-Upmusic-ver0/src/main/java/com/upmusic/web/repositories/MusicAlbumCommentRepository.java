package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicAlbumComment;


@Repository
public interface MusicAlbumCommentRepository extends JpaRepository<MusicAlbumComment, Long> {
	
	public Page<MusicAlbumComment> findByMusicAlbumId(Long albumId, Pageable pageOrderRequest);
	
	public List<MusicAlbumComment> findByMusicAlbumId(Long albumId);

	public List<MusicAlbumComment> findAllByMemberId(Long memberId);
	
}

package com.upmusic.web.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.CollectionTrack;


@Repository
public interface CollectionTrackRepository extends CrudRepository<CollectionTrack, Long> {

	Page<CollectionTrack> findByCollectionId(Long collectionId, Pageable pageMusicOrderByNew);
	
	@Query("SELECT t.id FROM CollectionTrack t WHERE t.collection.id = :collectionId ORDER BY t.id DESC")
    public Page<Long> findLatestId(@Param("collectionId") Long collectionId, Pageable pageMusicOrderByNew);

    @Modifying
    @Transactional
    @Query("DELETE FROM CollectionTrack t WHERE t.collection.id = :collectionId AND t.id NOT IN (:idList)")
    public void deleteByExcludedId(@Param("collectionId") Long collectionId, @Param("idList") List<Long> idList);

	CollectionTrack findByCollectionIdAndMusicTrackId(Long collectionId, Long trackId);

	int countByCollectionId(Long collectionId);

	Iterable<CollectionTrack> findByCollectionId(Long collectionId);

	Iterable<CollectionTrack> findByMusicTrackId(Long musicTrackId);
}

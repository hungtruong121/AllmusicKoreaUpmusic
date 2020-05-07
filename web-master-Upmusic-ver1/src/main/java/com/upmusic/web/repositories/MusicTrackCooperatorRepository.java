package com.upmusic.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicTrackCooperator;


@Repository
public interface MusicTrackCooperatorRepository extends JpaRepository<MusicTrackCooperator, Long> {
}

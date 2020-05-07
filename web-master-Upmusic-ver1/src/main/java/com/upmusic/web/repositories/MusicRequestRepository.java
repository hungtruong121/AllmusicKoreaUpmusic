package com.upmusic.web.repositories;


import com.upmusic.web.domain.MusicRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MusicRequestRepository extends JpaRepository<MusicRequest, Long> {
    public List<MusicRequest> findAllByMemberId(Long memberId);
}

package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MemberDetail;


@Repository
public interface MemberDetailRepository extends CrudRepository<MemberDetail, Long> {
}

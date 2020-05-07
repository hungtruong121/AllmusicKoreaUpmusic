package com.upmusic.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.StoreOrderLicense;


@Repository
public interface StoreOrderLicenseRepository extends JpaRepository<StoreOrderLicense, Long> {

	StoreOrderLicense findOneByMusicTrackIdAndBuyerId(Long musicTrackId, Long buyerId);
}

package com.upmusic.web.services;

import com.upmusic.web.domain.StoreLicense;
import com.upmusic.web.domain.StoreOrderLicense;

public interface StoreOrderLicenseService {
	
	Iterable<StoreOrderLicense> listAllStoreOrderLicense();

	StoreOrderLicense getStoreOrderLicenseById(Long id);

	StoreOrderLicense saveStoreOrderLicense(StoreOrderLicense collection);

    void deleteStoreOrderLicense(Long id);
    
    StoreOrderLicense findOrderLicenseByMusicTrackIdAndBuyerId(Long musicTrackId, Long memberId);
    
    Iterable<StoreLicense> listAllStoreLicense();

}

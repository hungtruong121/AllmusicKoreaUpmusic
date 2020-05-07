package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.StoreLicense;
import com.upmusic.web.domain.StoreOrderLicense;
import com.upmusic.web.repositories.StoreLicenseRepository;
import com.upmusic.web.repositories.StoreOrderLicenseRepository;


@Service
public class StoreOrderLicenseServiceImpl implements StoreOrderLicenseService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private StoreLicenseRepository storeLicenseRepository;
	
	@Autowired
    private StoreOrderLicenseRepository storeOrderLicenseRepository;


    @Override
    public Iterable<StoreOrderLicense> listAllStoreOrderLicense() {
        logger.debug("listAllStoreOrderLicenses called");
        return storeOrderLicenseRepository.findAll();
    }

    @Override
    public StoreOrderLicense getStoreOrderLicenseById(Long id) {
        logger.debug("getStoreOrderLicenseById called");
        return storeOrderLicenseRepository.findById(id).orElse(null);
    }
    
    @Override
    public StoreOrderLicense findOrderLicenseByMusicTrackIdAndBuyerId(Long musicTrackId, Long buyerId) {
    	logger.debug("findOrderLicenseByMusicTrackIdAndBuyerId called");
        return storeOrderLicenseRepository.findOneByMusicTrackIdAndBuyerId(musicTrackId, buyerId);
    }

    @Override
    public StoreOrderLicense saveStoreOrderLicense(StoreOrderLicense storeOrderLicense) {
        logger.debug("saveStoreOrderLicense called");
        return storeOrderLicenseRepository.save(storeOrderLicense);
    }

    @Override
    public void deleteStoreOrderLicense(Long id) {
        logger.debug("deleteStoreOrderLicense called");
        storeOrderLicenseRepository.deleteById(id);
    }
    
    
    @Override
    public Iterable<StoreLicense> listAllStoreLicense() {
    	logger.debug("listAllStoreLicense called");
        return storeLicenseRepository.findAll();
    }
}

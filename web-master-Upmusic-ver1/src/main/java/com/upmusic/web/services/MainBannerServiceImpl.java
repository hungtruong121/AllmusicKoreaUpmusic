package com.upmusic.web.services;

import com.upmusic.web.domain.MainBanner;
import com.upmusic.web.repositories.MainBannerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Size;
import java.util.List;

@Service
public class MainBannerServiceImpl implements MainBannerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MainBannerRepository mainBannerRepository;

    @Override
    public MainBanner saveBanner(MainBanner banner){
        logger.debug("saveBanner called..");
        return mainBannerRepository.save(banner);
    }

    @Override
    public void deleteById(Long id){
        logger.debug("deleteBanner called..");
        mainBannerRepository.deleteById(id);
    }

    @Override
    public List<MainBanner> findAllEventBanner(){
        logger.debug("findAllEventBanner called..");
        return mainBannerRepository.findAllEventBanner();
    }

    @Override
    public List<MainBanner> findAllArtistBanner(){
        logger.debug("findAllArtistBanner called..");
        return mainBannerRepository.findAllArtistBanner();
    }

    @Override
    public List<MainBanner> findAllShownEventBanner(){
        logger.debug("findAllEventBanner called..");
        return mainBannerRepository.findAllShownEventBanner();
    }

    @Override
    public List<MainBanner> findAllShownArtistBanner(){
        logger.debug("findAllArtistBanner called..");
        return mainBannerRepository.findAllShownArtistBanner();
    }
}

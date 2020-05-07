package com.upmusic.web.services;

import com.upmusic.web.domain.MainBanner;

import java.util.List;

public interface MainBannerService {

    MainBanner saveBanner(MainBanner banner);

    List<MainBanner> findAllEventBanner();

    List<MainBanner> findAllArtistBanner();

    List<MainBanner> findAllShownEventBanner();

    List<MainBanner> findAllShownArtistBanner();

    void deleteById(Long id);
}

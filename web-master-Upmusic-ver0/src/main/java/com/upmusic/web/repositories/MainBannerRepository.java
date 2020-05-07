package com.upmusic.web.repositories;

import com.upmusic.web.domain.MainBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MainBannerRepository extends JpaRepository<MainBanner, Long>, JpaSpecificationExecutor<MainBanner> {

    @Query(value = "SELECT * FROM main_banner WHERE banner_type = 0 ORDER BY order_no", nativeQuery = true)
    List<MainBanner> findAllEventBanner();

    @Query(value = "SELECT * FROM main_banner WHERE banner_type = 1", nativeQuery = true)
    List<MainBanner> findAllArtistBanner();

    @Query(value = "SELECT * FROM main_banner WHERE banner_type = 0 AND shown = 1 ORDER BY order_no", nativeQuery = true)
    List<MainBanner> findAllShownEventBanner();

    @Query(value = "SELECT * FROM main_banner WHERE banner_type = 1 AND shown = 1 ", nativeQuery = true)
    List<MainBanner> findAllShownArtistBanner();

//    void deleteByMainBannerId(Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM main_banner WHERE main_banner_id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);
}

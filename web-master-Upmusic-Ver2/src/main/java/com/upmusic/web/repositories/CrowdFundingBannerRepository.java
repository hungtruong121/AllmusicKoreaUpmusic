package com.upmusic.web.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.CrowdFundingBanner;

@Repository
public interface CrowdFundingBannerRepository extends JpaRepository<CrowdFundingBanner, Long>{

	@Query("SELECT CFB FROM CrowdFundingBanner CFB WHERE CFB.crowdFundingBannerId = :crowdFundingBannerId")
	CrowdFundingBanner findByCrowdFundingBannerId(@Param("crowdFundingBannerId") Long crowdFundingBannerId);
	
	@Query("SELECT CFB FROM CrowdFundingBanner CFB WHERE CFB.showYn = 1")
	Page<CrowdFundingBanner> findByCrowdFundingBannerShowYn(Pageable pageable);
	
}

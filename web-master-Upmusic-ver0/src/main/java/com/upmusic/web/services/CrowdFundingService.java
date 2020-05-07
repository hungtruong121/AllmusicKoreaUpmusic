package com.upmusic.web.services;

import java.util.Date;
import java.util.List;

import com.google.api.client.util.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.CrowdFunding;

public interface CrowdFundingService {
	public CrowdFunding save(CrowdFunding crowdFunding);
	public Page<CrowdFunding> findAllByCreatedAt(Date standardDate, Pageable pageable);
	public Page<CrowdFunding> findAll(Date today, Pageable pageable);
	public Page<CrowdFunding> findAllByCreateAt(Pageable pageable);
	public CrowdFunding findByCrowdFundingId(Long crowdFundingId);
	public void deleteByCrowdFundingId(Long crowdFundingId);
	public List<CrowdFunding> findAll();
	public Page<CrowdFunding> findAllByCloseAt(Pageable pageable);
	int countAllSuccessFunding();
	int countAllFailFunding();
	int countAllFunding();
	List<CrowdFunding> findAllByStartProject();
	
	List<CrowdFunding> findAllByNewProject(Date today);
	List<CrowdFunding> findAllByHotProject(Date today);

	public Page<CrowdFunding> findAllByArtistMemberId(Pageable pageable, String member_email);
	public Page<CrowdFunding> findAllByArtistNick(Pageable pageable, String artistNick);
	public Page<CrowdFunding> findAllBySubject(Pageable pageable, String subject);

	public Page<CrowdFunding> findAllByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable);
	public Page<CrowdFunding> findAllByArtistMemberIdByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable, String member_email);
	public Page<CrowdFunding> findAllByArtistNickByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable, String artistNick);
	public Page<CrowdFunding> findAllBySubjectByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable, String subject);
}

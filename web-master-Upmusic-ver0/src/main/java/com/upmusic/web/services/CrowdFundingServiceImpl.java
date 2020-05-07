package com.upmusic.web.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.util.DateTime;
import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.CrowdFunding;
import com.upmusic.web.repositories.CrowdFundingRepository;
import com.upmusic.web.repositories.MemberRepository;

@Service
public class CrowdFundingServiceImpl implements CrowdFundingService{

	@Autowired
	private CrowdFundingRepository crowdFundingRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	
	@Override
	public CrowdFunding save(CrowdFunding crowdFunding) {
		return crowdFundingRepository.save(crowdFunding);
	}


	@Override
	public Page<CrowdFunding> findAllByCreatedAt(Date standardDate, Pageable pageable) {
		return crowdFundingRepository.findAllByCreatedAt(standardDate, pageable);
	}


	@Override
	public Page<CrowdFunding> findAll(Date today, Pageable pageable) {
		return crowdFundingRepository.findAllByNow(today, pageable);
	}


	@Override
	public Page<CrowdFunding> findAllByCreateAt(Pageable pageable) {
		Page<CrowdFunding> result = crowdFundingRepository.findAllByCreateAt(pageable);
		
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(null));
		}
		return result;
	}


	@Override
	public CrowdFunding findByCrowdFundingId(Long crowdFundingId) {
		CrowdFunding result = crowdFundingRepository.findByCrowdFundingId(crowdFundingId);
		
		result.setArtistMember(memberRepository.findById(result.getArtistMemberId()).orElse(null));
		
		return result;
	}


	@Override
	public void deleteByCrowdFundingId(Long crowdFundingId) {
		crowdFundingRepository.deleteById(crowdFundingId);
	}


	@Override
	public List<CrowdFunding> findAll() {
		return crowdFundingRepository.findAll();
	}


	@Override
	public Page<CrowdFunding> findAllByCloseAt(Pageable pageable) {
		Page<CrowdFunding> result = crowdFundingRepository.findAllByCloseAt(pageable);
		
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(null));
		}
		return result;
	}


	@Override
	public int countAllSuccessFunding() {
		return crowdFundingRepository.countAllSuccessFunding();
	}


	@Override
	public int countAllFailFunding() {
		return crowdFundingRepository.countAllFailFunding();
	}


	@Override
	public List<CrowdFunding> findAllByStartProject() {
		return crowdFundingRepository.findAllByStartProject();
	}

	@Override
	public List<CrowdFunding> findAllByHotProject(Date today) {
		return crowdFundingRepository.findAllByHotProject(today);
	}


	@Override
	public List<CrowdFunding> findAllByNewProject(Date today) {
		return crowdFundingRepository.findAllByNewProject(today);
	}

	@Override
	public Page<CrowdFunding> findAllByArtistMemberId(Pageable pageable, String member_email){
		Page<CrowdFunding> result = crowdFundingRepository.findAllByArtistMemberId(member_email, pageable);
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(null));
		}
		return result;
	}

	@Override
	public Page<CrowdFunding> findAllByArtistNick(Pageable pageable, String artist_nick){
		Page<CrowdFunding> result = crowdFundingRepository.findAllByArtistNick(artist_nick, pageable);
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(null));
		}
		return result;
	}

	@Override
	public Page<CrowdFunding> findAllBySubject(Pageable pageable, String subject){
		Page<CrowdFunding> result =  crowdFundingRepository.findAllBySubject(subject, pageable);
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(null));
		}
		return result;
	}

	@Override
	public int countAllFunding() {
		return crowdFundingRepository.countAllFunding();
	}

	@Override
	public Page<CrowdFunding> findAllByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable){
		Page<CrowdFunding> result = crowdFundingRepository.findAllByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, pageable);
		for(CrowdFunding cf : result.getContent()){
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(memberRepository.findByEmail("admin@admin.com")));
		}
		return result;
	}

	@Override
	public Page<CrowdFunding> findAllByArtistMemberIdByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable, String member_email){
		Page<CrowdFunding> result = crowdFundingRepository.findAllByArtistMemberIdByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, member_email, pageable);
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(memberRepository.findByEmail("admin@admin.com")));
		}
		return result;
	}

	@Override
	public Page<CrowdFunding> findAllByArtistNickByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable, String artist_nick){
		Page<CrowdFunding> result = crowdFundingRepository.findAllByArtistNickByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, artist_nick, pageable);
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(memberRepository.findByEmail("admin@admin.com")));
		}
		return result;
	}

	@Override
	public Page<CrowdFunding> findAllBySubjectByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(Date openAt, Date closeAt, Pageable pageable, String subject){
		Page<CrowdFunding> result =  crowdFundingRepository.findAllBySubjectByOpenAtGreaterThanEqualAndCloseAtLessThanEqualOrderByOpenAt(openAt, closeAt, subject, pageable);
		for(CrowdFunding cf : result.getContent()) {
			cf.setArtistMember(memberRepository.findById(cf.getArtistMemberId()).orElse(memberRepository.findByEmail("admin@admin.com")));
		}
		return result;
	}
}

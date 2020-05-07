package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.domain.BandRecruitComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.BandRecruit;
import com.upmusic.web.repositories.BandRecruitCommentRepository;
import com.upmusic.web.repositories.BandRecruitRepository;
import com.upmusic.web.repositories.MemberRepository;

@Service
public class BandRecruitServiceImpl implements BandRecruitService{

	@Autowired
	private BandRecruitRepository bandRecruitRepository;
	
	@Autowired
	private BandRecruitCommentRepository bandRecruitCommentRepository;
	
	@Override
	public Page<BandRecruit> findBandRecruitList(Pageable pageable) {
		Page<BandRecruit> result = bandRecruitRepository.findAllWithMember(pageable);
		
		for(BandRecruit br : result.getContent()) {
			br.setBandCommentCnt(bandRecruitCommentRepository.findCountByBandRecruitCommentId(br.getBandRecruitId()));
		}
		
		return result;
	}

	@Override
	public BandRecruit findByBandRecruitId(Long bandRecruitId) {
		BandRecruit result = bandRecruitRepository.findByBandRecruitId(bandRecruitId);
		result.setCommentCnt(bandRecruitCommentRepository.findCountByBandRecruitCommentId(bandRecruitId));
		
		return result;
	}

	@Override
	public BandRecruit insertBandRecruit(BandRecruit bandRecruit) {
		return bandRecruitRepository.save(bandRecruit);
	}

	@Override
	public void deleteBandRecruit(BandRecruit bandRecruit) {
		bandRecruitRepository.delete(bandRecruit);
	}

	@Override
	public void deleteBandRecruitByMemberId(Long memberId) {
		List<BandRecruit> bandRecruits = bandRecruitRepository.findAllByMemberId(memberId);

		for (BandRecruit bandRecruit : bandRecruits) {
			bandRecruitCommentRepository.deleteByBandRecruitId(bandRecruit.getBandRecruitId());
			bandRecruitRepository.deleteById(bandRecruit.getBandRecruitId());
		}

		//다른 회원의 게시물에 코멘트 달았던 부분 삭제
		List<BandRecruitComment> bandRecruitComments = bandRecruitCommentRepository.findAllByMemberId(memberId);

		for (BandRecruitComment bandRecruitComment : bandRecruitComments) {
			bandRecruitCommentRepository.deleteById(bandRecruitComment.getBandRecruitCommentId());
		}

	}

	@Override
	public List<BandRecruit> findBandRecruitList() {
		List<BandRecruit> result = bandRecruitRepository.findAllWithMember();
		
		for(BandRecruit br : result) {
			br.setBandCommentCnt(bandRecruitCommentRepository.findCountByBandRecruitCommentId(br.getBandRecruitId()));
		}
		
		return result;
	}

}

package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.BandRecruit;
import com.upmusic.web.domain.BandRecruitComment;
import com.upmusic.web.domain.Member;
import com.upmusic.web.repositories.BandRecruitCommentRepository;

@Service
public class BandRecruitCommentServiceImpl implements BandRecruitCommentService{

	@Autowired
	private BandRecruitCommentRepository bandRecruitCommentRepository;
	

	@Override
	public void deleteBandRecruitComment(BandRecruit bandRecruit) {
		bandRecruitCommentRepository.deleteByBandRecruitId(bandRecruit.getBandRecruitId());
	}

	@Override
	public Page<BandRecruitComment> findAllByBandRecruitId(Long bandRecruitId, Pageable pageable, Member member) {
		Page<BandRecruitComment> result = bandRecruitCommentRepository.findAllByBandRecruitId(bandRecruitId, pageable);
		
		if(member != null) {
			for(BandRecruitComment vo : result) {
				if(member.getId().equals(vo.getMember().getId())) {
					vo.setRegistrantFlag(true);
				}
			}
		}
		
		
		return result;
	}

	@Override
	public BandRecruitComment insertBandRecruitComment(BandRecruitComment bandRecruitComment) {
		return bandRecruitCommentRepository.save(bandRecruitComment);
	}

	@Override
	public void deleteBandRecruitCommetByBandRecruitCommentId(BandRecruitComment bandRecruitComment) {
		bandRecruitCommentRepository.deleteById(bandRecruitComment.getBandRecruitCommentId());
	}

	@Override
	public List<BandRecruitComment> findAllByBandRecruitId(Long bandRecruitId, Member member) {
		List<BandRecruitComment> result = bandRecruitCommentRepository.findAllByBandRecruitId(bandRecruitId);
		
		if(member != null) {
			for(BandRecruitComment vo : result) {
				if(member.getId().equals(vo.getMember().getId())) {
					vo.setRegistrantFlag(true);
				}
			}
		}
		
		return result;
	}

	@Override
	public BandRecruitComment findByBandRecruitCommentId(Long bandRecruitCommentId) {
		BandRecruitComment result = bandRecruitCommentRepository.findByBandRecruitCommentId(bandRecruitCommentId);
		
		return result;
	}


}

package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.BandRecruit;
import com.upmusic.web.domain.BandRecruitComment;
import com.upmusic.web.domain.Member;

public interface BandRecruitCommentService {
	public Page<BandRecruitComment> findAllByBandRecruitId(Long bandRecruitId, Pageable pageable, Member member);
	public void deleteBandRecruitComment(BandRecruit bandRecruit);
	public BandRecruitComment insertBandRecruitComment(BandRecruitComment bandRecruitComment);
	public void deleteBandRecruitCommetByBandRecruitCommentId(BandRecruitComment bandRecruitComment);
	public List<BandRecruitComment> findAllByBandRecruitId(Long bandRecruitId, Member member);
	public BandRecruitComment findByBandRecruitCommentId(Long bandRecruitCommentId);
}

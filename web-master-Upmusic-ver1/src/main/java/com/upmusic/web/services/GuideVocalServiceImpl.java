package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.domain.GuideVocal;
import com.upmusic.web.domain.Member;
import com.upmusic.web.repositories.GuideVocalRepository;
import com.upmusic.web.repositories.MemberRepository;
import com.upmusic.web.repositories.RoleRepository;


@Service
public class GuideVocalServiceImpl implements GuideVocalService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private GuideVocalRepository guideRepository;
	
	@Autowired
    private RoleRepository roleRepository;
	
	@Autowired
    private MemberRepository memberRepository;
	

    @Override
    public Iterable<GuideVocal> listAllGuideVocals() {
        logger.debug("listAllGuideVocals called");
        return guideRepository.findAll();
    }
    
    @Override
    public Page<GuideVocal> findAll(PageRequest pageOrderByNew) {
    	logger.debug("findAll called");
    	return guideRepository.findAll(pageOrderByNew);
    }
    
    @Override
    public Page<GuideVocal> findByStatusIsUnderExam(PageRequest pageOrderNew) {
    	return guideRepository.findByGuideStatusIsUnderExamOrBeforeExam(pageOrderNew);
    }
    
    @Override
    public GuideVocal getGuideVocalById(Long id) {
        logger.debug("getGuideVocalById called");
        return guideRepository.findById(id).orElse(null);
    }
    
    @Override
    public GuideVocal findOneByMember(Member member) {
    	logger.debug("findOneByMember called");
        return guideRepository.findOneByMember(member);
    }

    @Override
    public GuideVocal saveGuideVocal(GuideVocal request) {
        logger.debug("saveGuideVocal called");
        return guideRepository.save(request);
    }

    @Override
    public void deleteGuideVocal(Long id) {
        logger.debug("deleteGuideVocal called");
        guideRepository.deleteById(id);
    }
    
    /**
     * 심사 후 상태 변경
     */
    @Override
    public GuideVocal setGuideStatus(Long id, MusicTrackStatus status) {
    	GuideVocal guide = getGuideVocalById(id);
    	guide.setGuideStatus(status);
    	saveGuideVocal(guide);
    	
    	if (status == MusicTrackStatus.ACCEPTED) {
    		// 회원의 권한 추가
    		Member member = guide.getMember();
            member.addRole(roleRepository.findByName("ROLE_GUIDE"));
            memberRepository.save(member);
    	}
    	
    	return guide;
    }

}

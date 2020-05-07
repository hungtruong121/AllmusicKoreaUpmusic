package com.upmusic.web.services;

import java.util.*;

import com.upmusic.web.helper.SpecificationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upmusic.web.domain.Member;
import com.upmusic.web.repositories.MemberRepository;


@Service
public class MemberServiceImpl implements MemberService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private MusicAlbumService albumService;
	
	@Autowired
	private VideoService videoService;

	@Autowired
	private VocalCastingService vocalCastingService;

	@Autowired
	private BandRecruitService bandRecruitService;

	@Autowired
	private MusicRequestService musicRequestService;

	@Autowired
	private FeedService feedService;

    @Override
    public Iterable<Member> listAllMembers() {
        logger.debug("listAllMembers called");
        return memberRepository.findAll();
    }
    
    @Override
    public Iterable<Member> listAllArtists() {
        logger.debug("listAllArtists called");
        return memberRepository.findAllArtists();
    }
    
    @Override
    public Page<Member> findAll(PageRequest pageRequest) {
        logger.debug("findAll called");
        return memberRepository.findAllInAdminPage(pageRequest);
    }
    
    @Override
    public List<Member> findTop2Artist() {
    	logger.debug("findTop2Artist called");
        return memberRepository.findTop2Artist(PageRequest.of(0, 2, Sort.Direction.DESC, "heartCnt"));
    }
    
    @Override
    public Page<Member> findAllArtistWithLikeByMember(Long likerId, PageRequest pageRequest) {
    	logger.debug("findAllArtistWithFollowByMember called");
    	return memberRepository.findAllArtistWithLikeByMember(likerId, pageRequest);
    }
    
    @Override
    public Page<Member> findAllFamilyArtistWithLikeByMember(Long likerId, PageRequest pageRequest) {
    	logger.debug("findAllFamilyArtistWithLikeByMember called");
    	return memberRepository.findAllFamilyArtistWithLikeByMember(likerId, pageRequest);
    }
    
    @Override
    public Page<Member> findAllNewArtistWithLikeByMember(Long likerId, PageRequest pageRequest) {
    	logger.debug("findAllNewArtistWithLikeByMember called");
    	Date date = new Date();
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -6);
        Date lastMonth = calendar.getTime();
    	return memberRepository.findAllNewArtistWithLikeByMember(likerId, lastMonth, pageRequest);
    }
    
    @Override
    public Page<Member> findAllGuideVocalWithLikeByMember(Long likerId, PageRequest pageRequest) {
    	logger.debug("findAllGuideVocalWithLikeByMember called");
    	return memberRepository.findAllGuideVocalWithLikeByMember(likerId, pageRequest);
    }
    
    @Override
    public Member getMemberById(Long id) {
        logger.debug("getMemberById called");
        return memberRepository.findById(id).orElse(null);
    }
    
    @Override
    public Member findByEmail(String email) {
    	logger.debug("findByEmail called");
        return memberRepository.findByEmail(email);
    }

    /**
     * 더미 유저를 생성하기 위해 임시 사용
     * @param member
     * @return member
     */
    @Override
    public Member saveMember(Member member) {
        logger.debug("saveMember called");
        return memberRepository.save(member);
    }

    @Override
    public void deleteMember(Long id) {
        logger.debug("deleteMember called");
        // TODO delete memberRole and memeberDetail too
        memberRepository.deleteById(id);
    }
    
    /**
     * 해당 회원의 좋아요 여부
     */
    @Override
    public boolean likedMember(Long id, Long likerId) {
    	logger.debug("likedMember called : id is {} and likerId is {}", id, likerId);
    	Member member = memberRepository.findById(id).orElse(null);
    	if (member != null && member.hasLiker(likerId)) return true;
    	return false;
    }
    
    /**
     * 좋아요 추가
     */
    @Override
    public int increaseHeartCnt(Long id, Member liker) {
    	logger.debug("increaseHeartCnt called");
    	Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
        	member.addLiker(liker);
        	member.setHeartCnt(member.getHeartCnt() + 1);
        	memberRepository.save(member);
        	return member.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 좋아요 취소
     */
    @Override
    public int decreaseHeartCnt(Long id, Member liker) {
    	logger.debug("decreaseHeartCnt called");
    	Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
        	member.removeLiker(liker);
        	if (0 < member.getHeartCnt()) member.setHeartCnt(member.getHeartCnt() - 1);
        	memberRepository.save(member);
        	return member.getHeartCnt();
        }
        return 0;
    }
    
    @Override
    public Member findByToken(String token) {
    	return memberRepository.findByToken(token);
    }
    
	@Override
	public List<Member> findAllByLikerId(Long likerId, Pageable pageable) {
		Page<Map<String, Object>> temp = memberRepository.findAllByLikerId(likerId, pageable);
		List<Member> result = new ArrayList<Member>();
		
		for(Map<String, Object> map : temp.getContent()) {
			result.add(new Member(map));
		}
		
		return result;
	}

	@Override
	public List<Member> findAllByEmail(String email, Long likerId) {
		List<Member> result = memberRepository.findAllByEmail(email);
		
		for(Member member : result) {
			int count = memberRepository.countIsLiker(member.getId(), likerId);
			if(count > 0) {
				member.setLiked(true);
			}
			member.setMemberUrl(member.getUrl());
		}
		
		return result;
	}

	public List<Member> findAllByNickName(String nickName, Long likerId) {
		List<Member> result = memberRepository.findAllByNickName(nickName);

		for(Member member : result) {
			int count = memberRepository.countIsLiker(member.getId(), likerId);
			if(count > 0) {
				member.setLiked(true);
			}
			member.setMemberUrl(member.getUrl());
		}

		return result;
	}

	@Override
	public int countIsLiker(Long memberId, Long likerId) {
		return memberRepository.countIsLiker(memberId, likerId);
	}

	@Override
	public void saveMemberLike(Long memberId, Long likerId) {
		memberRepository.saveMemberLike(memberId, likerId);
	}

	@Override
	public void deleteMemberLike(Long memberId, Long likerId) {
		memberRepository.deleteMemberLike(memberId, likerId);
	}
	
	@Override
	@Transactional
	public void withdraw(Member member) {
		logger.debug("withdraw called");
		
		// 영상 및 음원 삭제
		videoService.deleteVideosByMemberId(member.getId());
		albumService.deleteAlbumsByMemberId(member.getId());

		//보컬캐스팅 삭제
		vocalCastingService.deleteVocalCastingByMemberId(member.getId());

		//함께해요 삭제
		bandRecruitService.deleteBandRecruitByMemberId(member.getId());

		//제작의뢰 삭제
		musicRequestService.deleteRequestByMemberId(member.getId());

		//피드 삭제
		feedService.deleteFeedByMemberId(member.getId());

        // 회원 탈퇴처리
		this.withdrawMember(member);
	}
	
	private void withdrawMember(Member member) {
		member.setEmail("withdrawal_" + UUID.randomUUID().toString().substring(0,5) + "_" + member.getEmail());
		member.setPassword("withdrawal");
		member.setNick("탈퇴회원");
		member.setBirthday("00000000");
		member.setPhoneNumber("00000000000");
		member.setReceiveEmail(false);
		member.setProfileImage("1");
		member.setToken(null);
		memberRepository.save(member);
	}

	@Override
	public List<Member> findAllByLikerIdMobile(Long likerId) {
		List<Map<String, Object>> temp = memberRepository.findAllByLikerIdMobile(likerId);
		List<Member> result = new ArrayList<Member>();
		
		for(Map<String, Object> map : temp) {
			result.add(new Member(map));
		}
		
		return result;
	}

	public Page<Member> findAll(PageRequest pageRequest, String column, String value){
		Specification<Member> spec = SpecificationHelper.searchMember(column, value);
		return memberRepository.findAll(spec, pageRequest);
	}
}

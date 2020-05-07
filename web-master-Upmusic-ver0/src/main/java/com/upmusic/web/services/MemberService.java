package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.helper.SpecificationHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.Member;
import org.springframework.data.jpa.domain.Specification;


public interface MemberService {
	
	Iterable<Member> listAllMembers();
	
	Iterable<Member> listAllArtists();
	
	Page<Member> findAll(PageRequest pageRequest);
	
	List<Member> findTop2Artist();
	
	Page<Member> findAllArtistWithLikeByMember(Long likerId, PageRequest pageRequest);
	
	Page<Member> findAllFamilyArtistWithLikeByMember(Long likerId, PageRequest pageRequest);
	
	Page<Member> findAllNewArtistWithLikeByMember(Long likerId, PageRequest pageRequest);
	
	Page<Member> findAllGuideVocalWithLikeByMember(Long likerId, PageRequest pageRequest);

	Member getMemberById(Long id);
	
	Member findByEmail(String email);

	Member saveMember(Member member);

    void deleteMember(Long id);
    
	
	boolean likedMember(Long id, Long likerId);
    
    int increaseHeartCnt(Long id, Member liker);
    
    int decreaseHeartCnt(Long id, Member liker);
    

	Member findByToken(String token);
	
	List<Member> findAllByLikerId(Long likerId, Pageable pageable);
	
	List<Member> findAllByEmail(String email, Long likerId);

	List<Member> findAllByNickName(String nickName, Long likerId);
	
	int countIsLiker(Long memberId, Long likerId);
	
	void saveMemberLike(Long memberId, Long likerId);
	
	void deleteMemberLike(Long memberId, Long likerId);

	void withdraw(Member member);

	List<Member> findAllByLikerIdMobile(Long likerId);

	Page<Member> findAll(PageRequest pageRequest, String column, String value);
}

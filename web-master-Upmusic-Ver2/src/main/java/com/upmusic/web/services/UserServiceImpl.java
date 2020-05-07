package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.AuthenticationEmail;
import com.upmusic.web.domain.AuthenticationPhone;
import com.upmusic.web.repositories.MemberRepository;
import com.upmusic.web.repositories.AuthenticationEmailRepository;
import com.upmusic.web.repositories.AuthenticationPhoneRepository;
import com.upmusic.web.repositories.RoleRepository;


@Service
public class UserServiceImpl implements UserService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MemberRepository memberRepository;
	
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private AuthenticationPhoneRepository authenticationPhoneRepository;
    
    @Autowired
    private AuthenticationEmailRepository authenticationEmailRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    
    @Override
    public Member save(Member user) {
    	logger.debug("save called");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.addRole(roleRepository.findByName("ROLE_MEMBER"));
        }
        return memberRepository.save(user);
    }
    
    @Override
    public void updatePassword(String email, String newPassword) {
    	logger.debug("updatePassword called");
    	Member user = memberRepository.findByEmail(email);
    	user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    	memberRepository.save(user);
    	// 인증코드 삭제
    	AuthenticationEmail authenticationEmail = findAuthenticationEmailByEmail(email);
		if (authenticationEmail != null) authenticationEmailRepository.delete(authenticationEmail);
    }
    
    @Override
    public Member findByEmail(String email) {
    	logger.debug("findByEmail called");
        return memberRepository.findByEmail(email);
    }
    
    @Override
    public Member findByPhoneNumber(String phoneNumber) {
    	logger.debug("findByPhoneNumber called");
        return memberRepository.findByPhoneNumber(phoneNumber);
    }
    
    @Override
    public AuthenticationPhone findAuthenticationPhoneByPhoneNumber(String phoneNumber) {
    	logger.debug("findPhoneAuthenticationByPhoneNumber called");
        return authenticationPhoneRepository.findByPhoneNumber(phoneNumber);
    }
    
    @Override
    public AuthenticationEmail findAuthenticationEmailByEmail(String email) {
    	logger.debug("findAuthenticationEmailByEmail called");
        return authenticationEmailRepository.findByEmail(email);
    }

	@Override
	public boolean checkPassword(long id, String password) {
		if(bCryptPasswordEncoder.matches(password, memberRepository.getOne(id).getPassword())) return true;
		return false;
	}
    
}

package com.upmusic.web.services;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.AuthenticationEmail;
import com.upmusic.web.repositories.AuthenticationEmailRepository;


@Service
public class AuthenticationEmailServiceImpl implements AuthenticationEmailService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private AuthenticationEmailRepository authenticationEmailRepository;

    @Autowired
    public void setEmailAuthenticationRepository(AuthenticationEmailRepository authenticationEmailRepository) {
        this.authenticationEmailRepository = authenticationEmailRepository;
    }
    
    /**
     * 이메일주소와 인증코드를 임시 저장하여 유효성 검사에 이용
     */
    @Override
    public AuthenticationEmail saveAuthenticationEmail(String email) {
    	logger.debug("saveEmailAuthentication called");
    	AuthenticationEmail authenticationEmail = authenticationEmailRepository.findByEmail(email);
    	if (authenticationEmail != null) authenticationEmailRepository.delete(authenticationEmail);
    	try {
    		authenticationEmail = new AuthenticationEmail();
    		authenticationEmail.setEmail(email);
    		authenticationEmail.setAuthenticationCode(createAuthenticationCode());
    		return authenticationEmailRepository.save(authenticationEmail);
    	} catch (Exception e) {
    		logger.error("saveEmailAuthentication error : {}", e.toString());
            return null;
    	}
    }

    /**
     * 임시 저장된 인증코드 삭제
     */
    @Override
    public void deleteAuthenticationEmail(Long id) {
        logger.debug("deleteEmailAuthentication called");
        authenticationEmailRepository.deleteById(id);
    }
    
    /**
     * 6자리 랜덤 코드 생성
     * @return code
     */
    private String createAuthenticationCode() {
    	Random random = new Random();
    	int intCode = 100000 + random.nextInt(900000);
    	return String.valueOf(intCode);
    }

    /**
     * 2018.12.20 - ngoclh
     * delete all authentication email
     */
	@Override
	public void deleteAllAuthenticationEmail() {
		authenticationEmailRepository.deleteAll();
		
	}
}

package com.upmusic.web.services;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.AuthenticationPhone;
import com.upmusic.web.repositories.AuthenticationPhoneRepository;


@Service
public class AuthenticationPhoneServiceImpl implements AuthenticationPhoneService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private AuthenticationPhoneRepository authenticationPhoneRepository;

    @Autowired
    public void setAuthenticationPhoneRepository(AuthenticationPhoneRepository authenticationPhoneRepository) {
        this.authenticationPhoneRepository = authenticationPhoneRepository;
    }
    
    /**
     * 휴대폰 번호와 인증코드를 임시 저장하여 유효성 검사에 이용
     */
    @Override
    public AuthenticationPhone saveAuthenticationPhone(String phoneNumber) {
    	logger.debug("savePhoneAuthentication called");
    	AuthenticationPhone authenticationPhone = authenticationPhoneRepository.findByPhoneNumber(phoneNumber);
    	if (authenticationPhone != null) return authenticationPhone;
    	try {
    		authenticationPhone = new AuthenticationPhone();
    		authenticationPhone.setPhoneNumber(phoneNumber);
    		authenticationPhone.setAuthenticationCode(createAuthenticationCode());
    		return authenticationPhoneRepository.save(authenticationPhone);
    	} catch (Exception e) {
    		logger.error("savePhoneAuthentication error : {}", e.toString());
            return null;
    	}
    }

    /**
     * 임시 저장된 인증코드 삭제
     */
    @Override
    public void deleteAuthenticationPhone(Long id) {
        logger.debug("deletePhoneAuthentication called");
        authenticationPhoneRepository.deleteById(id);
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
}

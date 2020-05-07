package com.upmusic.web.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * 비밀번호 찾기 모델
 */
@Data
public class PasswordForgot {

	@Getter
	@Setter
	private boolean phoneAuthentication = true;
	
	@Getter
	@Setter
	private String email;
	
	@Getter
	@Setter
	private String nick;
	
	@Getter
	@Setter
	private String phoneNumber;
	
	@Getter
	@Setter
	private String authenticationCode;
	
	@Getter
	@Setter
	private boolean authenticated = false;
	
	@Getter
	@Setter
	private String newPassword;
	
	@Getter
	@Setter
	private String newPasswordConfirm;
	
}
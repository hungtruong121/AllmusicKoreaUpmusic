package com.upmusic.web.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 로그인 모델
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {

	private String email;
	
	private String password;
	
	private boolean remember_me;
}
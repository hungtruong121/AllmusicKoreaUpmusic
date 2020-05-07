package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


/**
 * 이메일 인증 엔티티
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationEmail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "인증 ID")
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	@ApiModelProperty(notes = "인증에 사용되는 이메일주소")
	@Getter
	private String email;
	
	@Column(nullable = false, length = 6)
	@ApiModelProperty(notes = "인증에 사용될 코드")
	@Getter
	@Setter
	private String authenticationCode;

}
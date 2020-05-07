package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 회원권한 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Role {
	
	@Id
	@ApiModelProperty(notes = "회원권한 ID")
    private int id;
	
	@Column
	@ApiModelProperty(notes = "회원권한 명칭")
	@Getter
    private String name;

}
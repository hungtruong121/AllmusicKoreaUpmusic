package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 리워드 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
	
	@Id
	@ApiModelProperty(notes = "리워드 ID")
    private int id;
	
	@Column
	@ApiModelProperty(notes = "리워드 명칭")
	@Getter
    private String name;
	
	@Column
	@ApiModelProperty(notes = "리워드 조건")
	@Getter
    private Long playtime;
	
	@Column
	@ApiModelProperty(notes = "리워드 지급액")
	@Getter
    private Long payments;

}
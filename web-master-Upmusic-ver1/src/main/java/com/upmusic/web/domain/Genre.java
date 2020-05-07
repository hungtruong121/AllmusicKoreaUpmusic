package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 장르 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
	
	@Id
	@ApiModelProperty(notes = "장르 ID")
    private int id;
	
	@Column
	@ApiModelProperty(notes = "장르 명칭")
	@Getter
    private String name;

}
package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 테마 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Theme {
	
	@Id
	@ApiModelProperty(notes = "테마 ID")
    private int id;
	
	@Column
	@ApiModelProperty(notes = "테마 명칭")
	@Getter
    private String name;

}
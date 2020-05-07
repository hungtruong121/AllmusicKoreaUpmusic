package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 가이드 보컬 범위 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GuideVocalScope {
	
	@Id
	@ApiModelProperty(notes = "가이드 보컬 범위 ID")
    private int id;
	
	@Column
	@ApiModelProperty(notes = "가이드 보컬 범위 명칭")
	@Getter
    private String name;
	
	
	public String getNameString() {
		if (name.equals("GUIDE")) return "가이드";
		if (name.equals("CHORUS")) return "코러스";
		if (name.equals("RAP")) return "랩";
		return "";
	}

}
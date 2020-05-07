package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 라이센스 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StoreLicense {
	
	@Id
	@ApiModelProperty(notes = "라이센스 ID")
    private int id;
	
	@Column
	@ApiModelProperty(notes = "라이센스 명칭")
	@Getter
    private String name;
	
	@Column
    @ApiModelProperty(notes = "판매가격")
    @Getter
    private int price;
	
	@Column
	@ApiModelProperty(notes = "제공하는 파일 형식")
	@Getter
    private String trackType;
	
	@Column
	@ApiModelProperty(notes = "라이센스 설명")
    private String description;

}
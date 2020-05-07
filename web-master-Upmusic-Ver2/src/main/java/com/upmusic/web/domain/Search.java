package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 검색어 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Search {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	@ApiModelProperty(notes = "검색어")
	@Getter
    private String searchText;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "회원")
	private Member member;

	@Transient
	private int sc;
}
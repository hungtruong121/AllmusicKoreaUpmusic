package com.upmusic.web.domain;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.config.UPMusicConstants.MusicCooperatorRole;
import com.upmusic.web.domain.converter.MusicAlbumCooperatorRoleAttributeConverter;


/**
 * 트랙 협력자 엔티티
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MusicTrackCooperator {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_track_id", nullable = false)
	@ApiModelProperty(notes = "트랙")
    @Getter
	@Setter
	@JsonIgnore
    private MusicTrack musicTrack;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
	@ApiModelProperty(notes = "협력자")
	@Getter
	@Setter
    private Member member;
	
	@Column
	@ApiModelProperty(notes = "수익 배분")
	@Getter
	private int distribution;
    
	@Column
	@Convert(converter = MusicAlbumCooperatorRoleAttributeConverter.class)
	@ApiModelProperty(notes = "역할")
	@Getter
    private MusicCooperatorRole cooperatorRole;
    
	/**
	 * 트랙 입력폼에서 받은 id로 멤버를 대체하기 위해 필요한 임시 항목
	 */
	@Transient
	@Getter
	@JsonIgnore
	private Long cooperatorId;
	
	/**
	 * 역할명을 반환
	 * @return cooperatorRole
	 */
	public String getCooperatorRoleName() {
		if (!StringUtils.isEmpty(cooperatorRole)) return "enum.track.cooperator.role."+ cooperatorRole.name();
		return null;
	}
}
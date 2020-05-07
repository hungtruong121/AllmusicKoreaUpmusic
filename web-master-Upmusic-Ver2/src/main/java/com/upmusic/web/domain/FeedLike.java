package com.upmusic.web.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 피드 파일 모델
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FeedLike {

	@EmbeddedId
	FeedLikePK id;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "생성 일시")
	@JsonIgnore
	private Date createdAt;
	
	public void setFeedLikeId(Long feedId, Long memberId) {
		this.id = new FeedLikePK(feedId, memberId);
	}

}

@Embeddable
class FeedLikePK implements Serializable  {

	private Long feedId;
	private Long memberId;
	
	FeedLikePK(Long feedId, Long memberId){
		this.feedId = feedId;
		this.memberId = memberId;
	}
	
	FeedLikePK(){
		
	}

}
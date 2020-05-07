package com.upmusic.web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upmusic.web.helper.AzureHelper;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FeedFile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "피드 파일 ID")
	private Long feedFileId;

	@Column(length=50)
	@ApiModelProperty(notes = "파일명")
    private String filename;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
	@ApiModelProperty(notes = "피드 ID")
    private Feed feed;
	
	@Transient
	private String imgSrcText;
	
	public String getImgUrl() {
		String result = "";
		if(filename != null)
			result = String.format("%s/feed/%d/%s", AzureHelper.getStorageResourceUrl(), feedFileId, filename);
		
		return result;
	}
	
	public FeedFile(FeedFile feedFile, Feed feed, Member member) {
		this.feedFileId = feedFile.getFeedFileId();
		this.filename = feedFile.getFilename();
		this.feed = feed;
		this.feed.setMember(member);
	}

}
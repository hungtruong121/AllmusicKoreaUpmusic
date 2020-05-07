package com.upmusic.web.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketTrackPlayingMessage {

	@Getter
	@Setter
	private Long playtime;
	
	@Getter
	@Setter
	private Long musicTrackId;
	
	@Getter
	@Setter
	private int streamingRewardStep;

	@Getter
	@Setter
	private String upPoint;

	@Getter
	@Setter
	private int firstLimit;

	@Getter
	@Setter
	private int secondLimit;

	@Getter
	@Setter
	private int thirdLimit;

}

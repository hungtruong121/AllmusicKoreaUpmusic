package com.upmusic.web.controllers.websocket;

import java.security.Principal;

import com.upmusic.web.domain.PointRewardCondition;
import com.upmusic.web.repositories.PointRewardConditionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MemberPlaytime;
import com.upmusic.web.message.WebsocketTrackPlayingMessage;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.PointTransactionService;


@Controller
public class WebsocketPlayerController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MemberService memberService;
	
	@Autowired
    private PointTransactionService pointTransactionService;

	@Autowired
	private PointRewardConditionRepository pointRewardConditionRepository;
    
	@MessageMapping("/playtime")
    @SendToUser("/topic/player")
    public WebsocketTrackPlayingMessage playtime(WebsocketTrackPlayingMessage message, Principal principal) {
		if (principal != null ) {
        	Member member = memberService.findByEmail(principal.getName());
			PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
        	if (member != null) {
        		logger.debug("playtime : member is {}", member.getId());
        		MemberPlaytime playtime = pointTransactionService.findOrCreatePlaytime(member.getId());
        		message.setPlaytime(playtime.getPlaytime());
        		message.setStreamingRewardStep(pointTransactionService.getStreamingRewardStep(playtime.getPlaytime()));
				message.setUpPoint(member.getUpmPointString());
				message.setFirstLimit(condition.getListenFirstStepLimit());
				message.setSecondLimit(condition.getListenSecondStepLimit());
				message.setThirdLimit(condition.getListenThirdStepLimit());
        	}
    	}
		return message; 
    }
	
	@MessageMapping("/increase_playtime")
    @SendToUser("/topic/player")
    public WebsocketTrackPlayingMessage increasePlaytime(WebsocketTrackPlayingMessage message, Principal principal) {
		if (principal != null ) {
        	Member member = memberService.findByEmail(principal.getName());
			PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
        	if (member != null) {
        		logger.debug("playtime : member is {} and track is {}", member.getId(), message.getMusicTrackId());
				MemberPlaytime playtime = pointTransactionService.findOrCreatePlaytime(member.getId());
        		message = pointTransactionService.createOrUpdateRewardListen(member.getId(), message.getMusicTrackId());
        		message.setPlaytime(playtime.getPlaytime());
				message.setFirstLimit(condition.getListenFirstStepLimit());
				message.setSecondLimit(condition.getListenSecondStepLimit());
				message.setThirdLimit(condition.getListenThirdStepLimit());
				message.setUpPoint(member.getUpmPointString());
        	}
    	}
		return message;
    }

}

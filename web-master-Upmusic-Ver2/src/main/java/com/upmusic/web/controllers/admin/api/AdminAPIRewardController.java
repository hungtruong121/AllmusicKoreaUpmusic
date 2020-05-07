package com.upmusic.web.controllers.admin.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.PointRewardCondition;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.PointRewardConditionService;
import com.upmusic.web.validator.RewardSharePropertiesValidator;
import com.upmusic.web.validator.RewardStreamingPropertiesValidator;
import com.upmusic.web.validator.RewardUploadPropertiesValidator;


@RestController
@RequestMapping("/admin/api/reward")
@Api(value="admin.api.reward", description="관리자페이지의 리워드와 관련된 API 컨트롤러")
public class AdminAPIRewardController extends AdminAPIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PointRewardConditionService pointRewardConditionService;
	
	@Autowired
    private RewardStreamingPropertiesValidator streamingValidator;
	
	@Autowired
    private RewardUploadPropertiesValidator uploadValidator;
	
    @Autowired
    private RewardSharePropertiesValidator shareValidator;
	
	@Autowired
	private Gson gson;
	

	@ApiOperation(value = "리워드 설정 페이지의 스트리밍 설정 폼을 처리", response = ResponseEntity.class)
	@RequestMapping(value = "/streaming_properties", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> updateStreamingProperties(@ModelAttribute("conditionform") PointRewardCondition conditionform, BindingResult bindingResult, HttpServletRequest request) {
		logger.debug("updateStreamingProperties : conditionform is {}", conditionform);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		if (request.isUserInRole(UPMusicConstants.ROLE_ADMIN)) {
			streamingValidator.validate(conditionform, bindingResult);
	        if (bindingResult.hasErrors()) {
	        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
	    	} else {
	    		PointRewardCondition condition = pointRewardConditionService.getCondition();
				condition.setListenFirstStepPoint(conditionform.getListenFirstStepPoint());
				condition.setListenFirstStepLimit(conditionform.getListenFirstStepLimit());
				condition.setListenSecondStepPoint(conditionform.getListenSecondStepPoint());
				condition.setListenSecondStepLimit(conditionform.getListenSecondStepLimit());
				condition.setListenThirdStepPoint(conditionform.getListenThirdStepPoint());
				condition.setListenThirdStepLimit(conditionform.getListenThirdStepLimit());
				condition.setListenArtistPoint(conditionform.getListenArtistPoint());
				condition.setListenArtistSelfPoint(conditionform.getListenArtistSelfPoint());
				condition.setListenArtistSelfLimit(conditionform.getListenArtistSelfLimit());
				condition = pointRewardConditionService.saveCondition(condition);
				response = new APIResponse(true, "true", condition);
	    	}
		}
    	return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "리워드 설정 페이지의 업로드 설정 폼을 처리", response = ResponseEntity.class)
	@RequestMapping(value = "/upload_properties", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> updateUploadProperties(@ModelAttribute("conditionform") PointRewardCondition conditionform, BindingResult bindingResult, HttpServletRequest request) {
		logger.debug("updateUploadProperties :  conditionform is {}", conditionform);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		if (request.isUserInRole(UPMusicConstants.ROLE_ADMIN)) {
			uploadValidator.validate(conditionform, bindingResult);
	        if (bindingResult.hasErrors()) {
	        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
	    	} else {
	    		PointRewardCondition condition = pointRewardConditionService.getCondition();
				condition.setUploadPoint(conditionform.getUploadPoint());
				condition = pointRewardConditionService.saveCondition(condition);
				response = new APIResponse(true, "true", condition);
	    	}
		}
    	return ResponseEntity.ok(response);
    }
	
	@ApiOperation(value = "리워드 설정 페이지의 공유 설정 폼을 처리", response = ResponseEntity.class)
	@RequestMapping(value = "/share_properties", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> updateShareProperties(@ModelAttribute("conditionform") PointRewardCondition conditionform, BindingResult bindingResult, HttpServletRequest request) {
		logger.debug("updateShareProperties :  conditionform is {}", conditionform);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		if (request.isUserInRole(UPMusicConstants.ROLE_ADMIN)) {
			shareValidator.validate(conditionform, bindingResult);
	        if (bindingResult.hasErrors()) {
	        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
	    	} else {
	    		PointRewardCondition condition = pointRewardConditionService.getCondition();
				condition.setSharePoint(conditionform.getSharePoint());
				condition.setShareLimit(conditionform.getShareLimit());
				condition = pointRewardConditionService.saveCondition(condition);
				response = new APIResponse(true, "true", condition);
	    	}
		}
    	return ResponseEntity.ok(response);
    }
    
}

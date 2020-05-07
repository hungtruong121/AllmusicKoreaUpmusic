package com.upmusic.web.controllers.admin.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.upmusic.web.domain.Terms;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.TermsService;
import com.upmusic.web.validator.TermsValidator;


@RestController
@RequestMapping("/admin/api/terms")
@Api(value="admin.api.terms", description="관리자페이지의 정책과 관련된 API 컨트롤러")
public class AdminAPITermsController extends AdminAPIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TermsService termsService;
	
	@Autowired
    private TermsValidator termsValidator;
	
	@Autowired
	private Gson gson;
	
	
	@ApiOperation(value = "id에 해당하는 정책을 업데이트", response = ResponseEntity.class)
	@RequestMapping(value = "/{id}", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> updateTerms(@ModelAttribute("termsform") Terms termsform, @PathVariable int id, BindingResult bindingResult, Model model) {
		logger.debug("updateTerms :  termsform is {}", termsform);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		termsValidator.validate(termsform, bindingResult);
        if (bindingResult.hasErrors()) {
        	response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
    	} else {
    		Terms terms = termsService.findById(id);
    		terms.setSubject(termsform.getSubject());
    		terms.setContent(termsform.getContent());
    		terms.setUpdatedAt(new Date()); // annotation 적용 안됨
    		termsService.saveTerms(terms);
    		response = new APIResponse(true, "true", "저장되었습니다.");
    	}
    	return ResponseEntity.ok(response);
    }
}

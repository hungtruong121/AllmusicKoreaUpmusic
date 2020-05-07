package com.upmusic.web.controllers.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.services.PointRewardConditionService;


@Controller
@RequestMapping("/admin/reward")
@Api(value = "admin.reward", description = "관리자 리워드 관리 페이지를 담당하는 컨트롤러")
public class AdminRewardController extends AdminAbstractController {

	@Autowired
	private PointRewardConditionService pointRewardConditionService;
	

	// --------------------------------------------------------------------------------------------
	// 리워드 설정
	
	@ApiOperation(value = "리워드 설정 페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/properties")
	public String properties(HttpServletRequest request, Model model) {
		if (!request.isUserInRole(UPMusicConstants.ROLE_ADMIN)) {
			return "redirect:/auth/login";
		}
		super.setSubMenu(model, "/admin/reward/properties");
		model.addAttribute("conditionform", pointRewardConditionService.getCondition());
		return "fragments/admin/page/reward/properties";
	}
	
}

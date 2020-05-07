package com.upmusic.web.controllers.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.upmusic.web.services.TermsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Controller
@RequestMapping("/admin/terms")
@Api(value = "admin.terms", description = "관리자 약관 등의 정책 페이지를 담당하는 컨트롤러")
public class AdminTermsController extends AdminAbstractController {

	@Autowired
	private TermsService termsService;
	

	@ApiOperation(value = "정책 페이지의 템플릿을 반환", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"), @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@GetMapping()
	public String terms(HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/terms");
		model.addAttribute("termsList", termsService.findList());
		
		return "fragments/admin/page/terms/index";
	}

	/**
	 * 정책 수정 화면 이동
	 */
	@GetMapping("/{id}/edit")
	public String editTerms(@PathVariable int id, Model model) {
		super.setSubMenu(model, "/admin/terms");
		model.addAttribute("termsform", termsService.findById(id));

		return "fragments/admin/page/terms/edit";
	}
	
}

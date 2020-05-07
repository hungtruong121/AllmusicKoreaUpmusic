package com.upmusic.web.controllers.page;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Controller
@Api(value = "error", description = "서비스의 에러 페이지를 담당하는 컨트롤러")
public class PageErrorController extends PageAbstractController implements ErrorController {

	@ApiOperation(value = "에러페이지의 템플릿을 반환", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
			@ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@RequestMapping("/error")
	public String page(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				return "fragments/page/error/error-404";
			} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				return "fragments/page/error/error-403";
			} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				return "fragments/page/error/error-500";
			}
		}
		return "fragments/page/error/index";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}

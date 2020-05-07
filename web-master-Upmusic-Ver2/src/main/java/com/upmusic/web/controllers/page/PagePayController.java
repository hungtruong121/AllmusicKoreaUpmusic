package com.upmusic.web.controllers.page;

import com.galaxia.api.crypto.GalaxiaCipher;
import com.galaxia.api.crypto.Seed;
import com.galaxia.api.merchant.Message;
import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.domain.Member;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.PointTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Calendar;
import com.galaxia.api.util.*;
import com.galaxia.api.*;

@Controller
@RequestMapping("/pay")
@Api(value="pay", description="결제 페이지를 담당하는 컨트롤러")
public class PagePayController extends PageAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MemberService memberService;

	@Autowired
	private PointTransactionService pointTransactionService;

	@ApiOperation(value = "결제 페이지의 메인 템플릿을 반환",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )

    @GetMapping("")
    public String page(Principal principal, Model model) {
        Member member = super.getCurrentUser(principal);

		model.addAttribute("member", member);

        return "fragments/page/pay/pay";
    }

    @PostMapping("/pay")
	public String pay(Principal principal, HttpServletRequest request, Model model, HttpServletResponse response) {
    	Member member = super.getCurrentUser(principal);

		try {
			request.setCharacterEncoding("euc-kr");

			//만료된 페이지 설정
			response.setHeader("cache-control", "no-cache");
			response.setHeader("pragma", "no-cache");
			response.setHeader("expire", "0");

			//날짜변수 선언
			Calendar today = Calendar.getInstance();
			String year = Integer.toString(today.get(Calendar.YEAR));
			String month = Integer.toString(today.get(Calendar.MONTH)+1);
			String date = Integer.toString(today.get(Calendar.DATE));
			String hour = Integer.toString(today.get(Calendar.HOUR));
			String minute = Integer.toString(today.get(Calendar.MINUTE));
			String second = Integer.toString(today.get(Calendar.SECOND));

			if(today.get(Calendar.MONTH)+1 < 10) month = "0" + month ;
			if(today.get(Calendar.DATE) < 10) date = "0" + date ;
			if(today.get(Calendar.HOUR) < 10) hour = "0" + hour ;
			if(today.get(Calendar.MINUTE) < 10) minute = "0" + minute ;
			if(today.get(Calendar.SECOND) < 10) second = "0" + second ;

			//결제 요청 파라메터
			String serviceId = "glx_api" ; //테스트 아이디 일반결제 : glx_api
			String orderDate = year + month + date + hour + minute + second ; //주문일시
			String orderId = "test_" + orderDate ;  //주문번호
            String userId = String.valueOf(member.getId());
			String userName = "홍길동";
			String itemName = "TEST" ;
			String itemCode = "TEST_CD1";
			String amount = request.getParameter("AMOUNT");
			String userIp = "127.0.0.1";
			//returnUrl = "http://50.10.10.161:8080/credit-jsp-link/return.jsp";
			String payment = request.getParameter("PAYMENT");

			String requestURL = request.getRequestURL().toString();

			String returnUrl;
			//개발
			if (requestURL.contains("upmusic.azurewebsites.net")) {
                returnUrl = "https://upmusic.azurewebsites.net/pay/return";
            } else if (requestURL.contains("www.hyupmusic.com")) {
                returnUrl = "https://www.hyupmusic.com/pay/return";
            } else {
                returnUrl = "http://localhost:8080/pay/return";
            }

			String temp = serviceId + orderId + amount;
			String checkSum = ChecksumUtil.genCheckSum(temp);

			model.addAttribute("serviceId", serviceId);
			model.addAttribute("orderDate", orderDate);
			model.addAttribute("orderId", orderId);
			model.addAttribute("userId", userId);
			model.addAttribute("userName", userName);
			model.addAttribute("itemName", itemName);
			model.addAttribute("itemCode", itemCode);
			model.addAttribute("amount", amount);
			model.addAttribute("userIp", userIp);
			model.addAttribute("returnUrl", returnUrl);
			model.addAttribute("checkSum", checkSum);
			model.addAttribute("payment", payment);

		} catch (Exception e) {
			e.printStackTrace();
		}

    	return "fragments/page/pay/pay_step";
    }

	@ApiOperation(value = "결제 리턴페이지의 템플릿을 반환",response = String.class)
	@PostMapping("/return")
	public String payReturn(Principal principal, HttpServletRequest request, HttpServletResponse response, Model model, HttpSession session) {
		logger.debug("return called");
		Member member = super.getCurrentUser(principal);

		//String authNumber = "";
		//String authDate = null ;
		String authAmount = null;
		String chargeType = null;

		try {
			request.setCharacterEncoding("euc-kr");

			//만료된 페이지 설정
			response.setHeader("cache-control", "no-cache");
			response.setHeader("pragma", "no-cache");
			response.setHeader("expire", "0");

			//결제 정보 받아오기
			String serviceId = request.getParameter("SERVICE_ID");
			String serviceCode = request.getParameter("SERVICE_CODE");
			String orderId = request.getParameter("ORDER_ID");
			String orderDate = request.getParameter("ORDER_DATE");
			String message = request.getParameter("MESSAGE");
			String responseCode = request.getParameter("RESPONSE_CODE");
			String responseMessage = request.getParameter("RESPONSE_MESSAGE");
			//String detailResponseCode = request.getParameter("DETAIL_RESPONSE_CODE");
			//String detailResponseMessage = request.getParameter("DETAIL_RESPONSE_MESSAGE");
			//String checkSum = request.getParameter("CHECK_SUM");
			String transactionId = request.getParameter("TRANSACTION_ID");
			String bankCode = request.getParameter("BANK_CODE");
			String accountNumber = request.getParameter("ACCOUNT_NUMBER");

			model.addAttribute("serviceId",serviceId);
			model.addAttribute("serviceCode",serviceCode);
			model.addAttribute("orderId",orderId);
			model.addAttribute("orderDate",orderDate);
			model.addAttribute("message",message);
			model.addAttribute("responseCode",responseCode);
			model.addAttribute("responseMessage",responseMessage);

			if (serviceCode != null && serviceCode.equals("1100")) {
				chargeType = "핸드폰";
			} else if (serviceCode != null && serviceCode.equals("0900")) {
				chargeType = "신용카드";
			} else if (serviceCode != null && serviceCode.equals("1000")) {
				chargeType = "계좌이체";
			}

			model.addAttribute("chargeType",chargeType);

			//결제 정보 session에 저장
			session.setAttribute("serviceId", serviceId);
			session.setAttribute("serviceCode", serviceCode);
			session.setAttribute("orderId", orderId);
			session.setAttribute("orderDate", orderDate);
			session.setAttribute("message", message);
			session.setAttribute("responseCode", responseCode);
			//session.setAttribute("responseMessage", responseMessage);
			session.setAttribute("transactionId", transactionId);
			session.setAttribute("bankCode", bankCode);
			session.setAttribute("accountNumber", accountNumber);

			logger.debug("responseCode:"+responseCode);
			//인증 성공 시 승인 요청
			if(responseCode.equals("0000")) {
				//checksum
				//String temp = serviceId + orderId + orderDate;
				logger.debug("diffCheckSum");
//				if(ChecksumUtil.diffCheckSum(checkSum, temp) != true){
//
//				}

				String requestURL = request.getRequestURL().toString();
				String configLoad = "src/main/webapp/.ebextensions/config.ini";
				if (requestURL.contains("upmusic.azurewebsites.net") || requestURL.contains("www.hyupmusic.com")) {
					configLoad = "D:\\home\\site\\wwwroot\\webapps\\ROOT\\.ebextensions\\config.ini";
				}

				//승인요청
				Message respMsg = linkAuthProcess(session, configLoad);

				logger.debug("respMsg:"+respMsg);

				//승인요청에 대한 응답 결과 설정
				responseCode = respMsg.get(MessageTag.RESPONSE_CODE);
				//responseMessage = respMsg.get(MessageTag.RESPONSE_MESSAGE);
				//detailResponseCode = respMsg.get(MessageTag.DETAIL_RESPONSE_CODE);
				//detailResponseMessage = respMsg.get(MessageTag.DETAIL_RESPONSE_MESSAGE);
				transactionId = respMsg.get(MessageTag.TRANSACTION_ID);

				logger.debug("responseCode: {}",responseCode);
				logger.debug("transactionId: {}", transactionId);

				//승인 성공인 경우 승인번호/승인일시 처리
				if(responseCode.equals("0000")) {
					//authNumber = respMsg.get(MessageTag.AUTH_NUMBER);
					//authDate = respMsg.get(MessageTag.AUTH_DATE);
					authAmount = respMsg.get(MessageTag.AUTH_AMOUNT);

					model.addAttribute("authAmount",authAmount);
					logger.debug("authAmount:"+authAmount);
				}
			}

			//가맹점 수정부분
			if(responseCode.equals("0000")) {
				//포인트 적립
				Member artist = memberService.getMemberById(member.getId());

				BigDecimal authAmountAdd = new BigDecimal(authAmount);

				artist.setChargePoint(artist.getChargePointDecimal().add(authAmountAdd));
				memberService.saveMember(artist);

				//포인트 트랜잭션 테이블에 저장
				pointTransactionService.addChargePoint(member.getId(), PointTransactionType.CHARGE, authAmountAdd, serviceCode, transactionId, "charge");
			} else {
				//응답실패했을때
				logger.debug("응답실패");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "fragments/page/pay/return";
	}

	public static final String VERSION = "0100" ;

	private GalaxiaCipher getCipher(String configLoad, HttpSession session) {
		logger.debug("getCipher called");
		GalaxiaCipher cipher = null;

		String key = null;
		String iv = null;
		try {
			//config.ini 파일 경로 지정
			logger.debug("configLoad : {}",configLoad);

			ConfigInfo config = new ConfigInfo(configLoad,session.getAttribute("serviceCode").toString());

			key = config.getKey();
			iv = config.getIv();

			logger.debug("key : {}", key);
			logger.debug("iv : {}", iv);

			cipher = new Seed();
			cipher.setKey(key.getBytes());
			cipher.setIV(iv.getBytes());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public Message linkAuthProcess(HttpSession session, String configLoad) throws Exception {
		logger.debug("linkAuthProcess called");
		String serviceId = (String)session.getAttribute("serviceId");
		String msg = (String)session.getAttribute("message");

		logger.debug("serviceId : {}", serviceId);
		logger.debug("msg : {}", msg);

		//메시지 Length 제거
		byte[] b = new byte[msg.getBytes().length - 4] ;
		System.arraycopy(msg.getBytes(), 4, b, 0, b.length);

		Message requestMsg = new Message(b, getCipher(configLoad, session)) ;

		Message responseMsg = null;

		ServiceBroker sb = new ServiceBroker(configLoad, session.getAttribute("serviceCode").toString());

		responseMsg = sb.invoke(requestMsg);

		return responseMsg;
	}

//	public Message networkCancelProcess(HttpSession session, String configLoad) throws Exception {
//		String serviceId = (String)session.getAttribute("serviceId");
//		String orderId = (String)session.getAttribute("orderId");
//		String orderDate = (String)session.getAttribute("orderDate");
//
//		Message requestMsg = new Message(VERSION, serviceId,
//				ServiceCode.CREDIT_CARD,
//				Command.NETWORK_CANCEL_REQUEST,
//				orderId,
//				orderDate,
//				getCipher(configLoad, session)) ;
//		Message responseMsg = null ;
//
//		//ServiceBroker sb = new ServiceBroker(ServiceCode.CREDIT_CARD);
//		ServiceBroker sb = new ServiceBroker(configLoad, ServiceCode.CREDIT_CARD);
//
//		responseMsg = sb.invoke(requestMsg);
//
//		return responseMsg;
//	}

}

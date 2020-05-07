package com.upmusic.web.controllers.api;

import com.galaxia.api.*;
import com.galaxia.api.crypto.GalaxiaCipher;
import com.galaxia.api.crypto.Seed;
import com.galaxia.api.merchant.Message;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.PointRewardCondition;
import com.upmusic.web.domain.PointTransaction;
import com.upmusic.web.message.PointTransactionResponse;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.PointRewardConditionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonObject;
import com.upmusic.web.domain.Member;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.PointTransactionService;


@RestController
@RequestMapping("/api/point_transaction")
@Api(value="api.store", description="리워드 정산과 관련된 API 컨트롤러")
public class APIPointTransactionController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    private PointTransactionService pointTransactionService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PointRewardConditionService pointRewardConditionService;

    
    @ApiOperation(value = "회원의 해당일 정산내역 반환",response = ResponseEntity.class)
    @RequestMapping(value = "/daily/{date}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<APIResponse> dayTransaction(@PathVariable String date, Principal principal) throws ParseException {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
    		JsonObject jsonObject = pointTransactionService.getDailyTransaction(member.getId(), date);
    		response = new APIResponse(true, "true", jsonObject.toString());
    	}
    	return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 정산내역 삭제", response = ResponseEntity.class)
    @RequestMapping(value = "/{id}/remove", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteVideos(@PathVariable Long id, Principal principal, HttpServletRequest request) {
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
    		boolean result = pointTransactionService.removeFromMember(id, member.getId());
    		response = new APIResponse(true, result ? "true" : "false", null);
    	}
		return ResponseEntity.ok(response);
    }

	@ApiOperation(value = "포인트 환불", response = ResponseEntity.class)
	@RequestMapping(value = "/refund", method= RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> pointRefund(@RequestBody String params, Principal principal, HttpServletRequest request, HttpServletResponse resp, HttpSession session) {
    	logger.debug("refund called");

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String chargeTransactionId = element.getAsJsonObject().get("chargeTransactionId").getAsString();
		String authAmount = element.getAsJsonObject().get("chargePoint").getAsString();
		String chargeType = element.getAsJsonObject().get("chargeType").getAsString();
		String updateTransactionId = element.getAsJsonObject().get("updateTransactionId").getAsString();

		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		Member member = super.getCurrentUser(principal);
		if (member != null) {
			String serviceId = null;
			String orderId = null;
			String orderDate = null;
			String transactionId = null;
			String responseCode = null;
			String responseMessage = null;
			String detailResponseCode = null;
			String detailResponseMessage = null;
			String serviceCode = null;
			//String chargeTransactionId = request.getParameter("chargeTransactionId");
			//String authAmount = request.getParameter("chargePoint");

			String requestURL = request.getRequestURL().toString();
			String configLoad = "src/main/webapp/.ebextensions/config.ini";
			if (requestURL.contains("upmusic.azurewebsites.net") || requestURL.contains("www.hyupmusic.com")) {
				configLoad = "D:\\home\\site\\wwwroot\\webapps\\ROOT\\.ebextensions\\config.ini";
			}

			try {
				request.setCharacterEncoding("euc-kr");

				//만료된 페이지 설정
				resp.setHeader("cache-control", "no-cache");
				resp.setHeader("pragma", "no-cache");
				resp.setHeader("expire", "0");

				//날짜변수 선언
				Calendar today = Calendar.getInstance();
				String year = Integer.toString(today.get(Calendar.YEAR));
				String month = Integer.toString(today.get(Calendar.MONTH) + 1);
				String date = Integer.toString(today.get(Calendar.DATE));
				String hour = Integer.toString(today.get(Calendar.HOUR));
				String minute = Integer.toString(today.get(Calendar.MINUTE));
				String second = Integer.toString(today.get(Calendar.SECOND));

				if (today.get(Calendar.MONTH) + 1 < 10) month = "0" + month;
				if (today.get(Calendar.DATE) < 10) date = "0" + date;
				if (today.get(Calendar.HOUR) < 10) hour = "0" + hour;
				if (today.get(Calendar.MINUTE) < 10) minute = "0" + minute;
				if (today.get(Calendar.SECOND) < 10) second = "0" + second;

				//취소 요청 파라메터
				serviceId = "glx_api"; //테스트 아이디 일반결제 : glx_api
				orderDate = year + month + date + hour + minute + second; //취소 요청일시
				orderId = "cancel_" + orderDate;    //취소 요청번호
				transactionId = chargeTransactionId;                        // 취소건의 빌게이트 거래번호

				if (chargeType != null && chargeType.equals("MOBILE")) {
					serviceCode = "1100";
				} else if (chargeType != null && chargeType.equals("CREDIT")) {
					serviceCode = "0900";
				} else if (chargeType != null && chargeType.equals("ACCOUNT")) {
					serviceCode = "1000";
				}

				//결제 정보 session에 저장
				session.setAttribute("serviceId", serviceId);
				session.setAttribute("orderId", orderId);
				session.setAttribute("orderDate", orderDate);
				session.setAttribute("transactionId", transactionId);
				session.setAttribute("serviceCode", serviceCode);

				Message respMsg = cancelProcess(session, configLoad);

				//session.setAttribute("responseMessage", respMsg);

				//취소요청에 대한 응답 결과 설정
				responseCode = respMsg.get(MessageTag.RESPONSE_CODE);
				responseMessage = respMsg.get(MessageTag.RESPONSE_MESSAGE);
				detailResponseCode = respMsg.get(MessageTag.DETAIL_RESPONSE_CODE);
				detailResponseMessage = respMsg.get(MessageTag.DETAIL_RESPONSE_MESSAGE);
				transactionId = respMsg.get(MessageTag.TRANSACTION_ID);

				//환불 응답처리
				if (responseCode != null && responseCode.equals("0000")) {

					//포인트 적립
					Member artist = memberService.getMemberById(member.getId());

					//금액 받아와야함.
					//String authAmount = "1000";

					BigDecimal authAmountAdd = new BigDecimal(authAmount);

					//환불처리
					artist.setChargePoint(artist.getChargePointDecimal().subtract(authAmountAdd));

					memberService.saveMember(artist);

					//이전 충전내역에 충전상태 변경
					pointTransactionService.updateChargePoint(Long.parseLong(updateTransactionId));

					//포인트 트랜잭션 테이블에 insert
					pointTransactionService.addChargePoint(member.getId(), UPMusicConstants.PointTransactionType.CHARGE, authAmountAdd.multiply(new BigDecimal("-1")), serviceCode, transactionId,"refund");

					response = new APIResponse(true, "true" , null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = new APIResponse(false, "false" , null);
			}
		}
		return ResponseEntity.ok(response);
	}

	public Message cancelProcess(HttpSession session, String configLoad) throws Exception {
    	logger.debug("cancelProcess called");
		logger.debug("serviceCode:"+session.getAttribute("serviceCode").toString());

		String serviceId = (String)session.getAttribute("serviceId");
		String orderId = (String)session.getAttribute("orderId");
		String orderDate = (String)session.getAttribute("orderDate");
		String transactionId = (String)session.getAttribute("transactionId");

		String VERSION = "0100";
		String command = "";

		//신용카드
		if (session.getAttribute("serviceCode").toString().equals("0900")) {
			command = Command.CANCEL_SMS_REQUEST;
		} else if (session.getAttribute("serviceCode").toString().equals("1100") || session.getAttribute("serviceCode").toString().equals("1000")) {
			command = Command.CANCEL_REQUEST;
		}

		Message requestMsg = new Message(VERSION, serviceId,
				session.getAttribute("serviceCode").toString(),
				command,
				orderId,
				orderDate,
				getCipher(serviceId, configLoad, session)) ;
		Message responseMsg = null ;

		logger.debug("requestMsg:"+requestMsg);

		if(transactionId != null) requestMsg.put(MessageTag.TRANSACTION_ID, transactionId);

		ServiceBroker sb = new ServiceBroker(configLoad, session.getAttribute("serviceCode").toString());

		responseMsg = sb.invoke(requestMsg);

		logger.debug("cancelProcess End");
		return responseMsg;
	}

	private GalaxiaCipher getCipher(String serviceId, String configLoad, HttpSession session) throws Exception {
		logger.debug("getCipher Start");

		logger.debug("serviceCode:"+session.getAttribute("serviceCode").toString());

		GalaxiaCipher cipher = null ;

		String key = null ;
		String iv = null ;
		try {
			//config.ini 파일 경로 지정

			logger.debug("configLoad:"+configLoad);

			ConfigInfo config = new ConfigInfo(configLoad, session.getAttribute("serviceCode").toString());

			key = config.getKey();
			iv = config.getIv();

			logger.debug("key:"+key);
			logger.debug("iv:"+iv);

			cipher = new Seed();
			cipher.setKey(key.getBytes());
			cipher.setIV(iv.getBytes());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return cipher;
	}

	@ApiOperation(value = "공유 리워드 포인트 적립", response = ResponseEntity.class)
	@RequestMapping(value = "/shareReward", method= RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> shareReward(Principal principal) {
		logger.debug("shareReward start");

		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		Member member = super.getCurrentUser(principal);
		if (member != null) {
			try {
				//공유 리워드
				PointRewardCondition condition = pointRewardConditionService.getCondition();

				int shareLimit = condition.getShareLimit();

				//현재날짜
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
				Date time = new Date();
				String sysDate = date.format(time);

				//하루에 shareLimit만큼만 리워드 증가
				if (pointTransactionService.getDailyShareTransaction(member.getId(),sysDate).size() <= shareLimit) {
					PointTransactionResponse pointTransactionResponse = pointTransactionService.accumulatePoints(member.getId(), UPMusicConstants.PointTransactionType.SHARE, null, condition.getSharePoint());
					response = new APIResponse(pointTransactionResponse.isStatus(), pointTransactionResponse.getMessage(), null);
				} else {
					response = new APIResponse(true, "true", null);
				}

			} catch (Exception e) {
				e.printStackTrace();
				response = new APIResponse(false, "false" , null);
			}

			logger.debug("shareReward End");
		}
		return ResponseEntity.ok(response);
	}
    
}

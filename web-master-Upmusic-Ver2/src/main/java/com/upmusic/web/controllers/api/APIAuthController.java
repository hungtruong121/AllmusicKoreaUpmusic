package com.upmusic.web.controllers.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.Gender;
import com.upmusic.web.domain.AuthenticationEmail;
import com.upmusic.web.domain.AuthenticationPhone;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.Role;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.*;
import com.upmusic.web.validator.UserValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@Api(value = "api.auth", description = "로그인과 회원가입 관련된 API 컨트롤러")
public class APIAuthController extends APIAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private UserService userService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private AuthenticationPhoneService authenticationPhoneService;

	@Autowired
	private AuthenticationEmailService authenticationEmailService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AzureStorageService azureStorageService;

	@Autowired
	private UserValidator userValidator;

	@Value("${google.client.client-id}")
	private String googleClientId;


	/**
	 * CSRF 토큰 반환
	 *
	 * @Depricated : api 이하 경로에 csrf 제외
	 * @param request
	 * @return map
	 */
	@ApiOperation(value = "CSRF 토큰")
	@RequestMapping(value = "/token", method = RequestMethod.GET)
	public Map<String, String> getLoginApiJson(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		logger.debug("csrf: {}", csrf.getToken());
		map.put("_csrf.token", csrf.getToken());
		map.put("_csrf.header", csrf.getHeaderName());
		map.put("_csrf.parameterName", csrf.getParameterName());
		return map;
	}

	@ApiOperation(value = "재생목록을 반환", response = ResponseEntity.class)
	@RequestMapping(value = "/check_session", method= RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> getPlaylist(Principal principal, HttpServletRequest request) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member != null) {
			//만약 탈퇴회원일시
			if (!member.getPassword().equals("withdrawal")) {
				response = new APIResponse(true, "true", null);
			}
		}
		return ResponseEntity.ok(response);
	}

	/**
	 * 앱에서 로그인 할 경우에 사용
	 * @param params
	 * @return json
	 */
	@ApiOperation(value = "로그인", notes = "params의 예 {\"email\":\"이메일 계정\",\"password\":\"비밀번호\"}")
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> login(@RequestBody String params) {
		logger.debug("login called: params is {}", params);
		APIResponse response = new APIResponse(false, super.getMessage("common.login.error"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String email = element.getAsJsonObject().get("email").getAsString();
		String password = element.getAsJsonObject().get("password").getAsString();
		if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) response = authLogin(email, password);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "회원가입 처리", response = String.class)
	@RequestMapping(value = "/registration", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> registration(@ModelAttribute("memberform") Member memberform, BindingResult bindingResult, HttpServletRequest request, Model model) {
		logger.debug("registration called: memberform is {}", memberform);
		APIResponse response = new APIResponse(false, "회원가입에 실패하였습니다. 다시 시도하여 주십시오.", null);
		String originalPassword = memberform.getPassword();
		userValidator.validate(memberform, bindingResult);
		if (!bindingResult.hasErrors()) {
			if (memberform.getGenderNo().equals("0")) {
				memberform.setGender(Gender.FEMALE);
			} else {
				memberform.setGender(Gender.MALE);
			}
			memberform.setUpmPoint(BigDecimal.ZERO);
			memberform.setFundingPoint(BigDecimal.ZERO);
			memberform.setChargePoint(BigDecimal.ZERO);
			memberform = userService.save(memberform);
			response = authLogin(memberform.getEmail(), originalPassword);
		}
		return ResponseEntity.ok(response);
	}

	/**
	 * 앱에서 소셜 로그인 또는 회원가입할 경우에 사용
	 * @param params
	 * @return json
	 */
	@ApiOperation(value = "소셜 로그인 또는 회원가입 - Facebook", notes = "params의 예 {\"access_token\":\"EAAEXeEqujzUBANGR32FwZA89huLk81IZAe2ZCJwT3IZA5ZCE9RcAL0KanJIOcyZCycZAfQo9Y8Ey2AA6vwuhxG0Bv6E0ZADd3q6ljrnrmWYG2amK729n5YZBZCIZA4NZBrM7vRZCgZBG20sYsmdb8cyxkAZBq3yK0RWmepuMGpaeyzu8DbS32Q67r3lyTNwh0CCNsNpZBVgZD\"}")
	@RequestMapping(value = "/login_or_register_with_facebook", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> loginOrRegisterWithFacebook(@RequestBody String params) {
		logger.debug("loginOrRegisterWithFacebook called: params is {}", params);
		APIResponse response = new APIResponse(false, "access_token이 유효하지 않습니다", null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String accessToken = element.getAsJsonObject().get("access_token").getAsString();
		if (accessToken != null && !accessToken.isEmpty()) {
			try {
				HttpClient client = HttpClientBuilder.create().build();
				String newUrl = "https://graph.facebook.com/me?access_token=" + accessToken + "&fields=id,name,email,picture.type(large)";
				HttpGet httpget = new HttpGet(newUrl);
				System.out.println("Get from face --> executing request: " + httpget.getURI());
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = client.execute(httpget, responseHandler);
				logger.info("loginOrRegisterWithFacebook from FB: " + responseBody);

				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser.parse(responseBody);
				logger.debug("loginOrRegisterWithFacebook / raw json :{}", jsonObject);

				//String email = !StringUtils.isEmpty(jsonObject.get("email")) ? jsonObject.get("email").getAsString() : jsonObject.get("id").getAsString() + "@facebook.com";
				String email = jsonObject.get("id").getAsString() + "@facebook.com";
				Member member = userService.findByEmail(email);

				logger.debug("email :{}", email);
				logger.debug("member :{}", member);

				// Not a member
				if (member == null) {
					member = saveFromFacebook(jsonObject);
					// Member
				} else {
					member.setPassword(UPMusicConstants.TOKEN_AES128);
					member = userService.save(member);
				}
				if (member != null) {
					response = authLogin(email, UPMusicConstants.TOKEN_AES128);
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = new APIResponse(false, e.toString(), null);
			}
		}
		return ResponseEntity.ok(response);
	}

	/**
	 * 앱에서 소셜 로그인 또는 회원가입할 경우에 사용
	 * @param params
	 * @return json
	 */
	@ApiOperation(value = "소셜 로그인 또는 회원가입 - Google", notes = "params의 예 {\"id_token\":\"XYZ123\"}")
	@RequestMapping(value = "/login_or_register_with_google", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> loginOrRegisterWithGoogle(@RequestBody String params) {
		logger.debug("loginOrRegisterWithGoogle called: params is {}", params);
		APIResponse response = new APIResponse(false, "id_token이 유효하지 않습니다", null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String idTokenString = element.getAsJsonObject().get("id_token").getAsString();
		try {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory()).build();
			GoogleIdToken idToken = verifier.verify(idTokenString);
			logger.debug("loginOrRegisterWithGoogle : idToken is {}", idToken);
			if (idToken != null) {
				Payload payload = idToken.getPayload();

				String email = UPMusicConstants.SOCIAL_GOOGLE+"_"+payload.getEmail();

				//Member member = userService.findByEmail(payload.getEmail());
				Member member = userService.findByEmail(email);

				// Not a member
				if (member == null) {
					member = saveFromGoogle(email,payload);
					// Member
				} else {
					member.setPassword(UPMusicConstants.TOKEN_AES128);
					member = userService.save(member);
				}
				if (member != null) {
					response = authLogin(member.getEmail(), UPMusicConstants.TOKEN_AES128);
				}
			}
		} catch (Exception e) {
			logger.error("loginOrRegisterWithGoogle faild! : error is {}", e.toString());
			response = new APIResponse(false, super.getMessage("common.login.error"), null);
		}
		return ResponseEntity.ok(response);
	}

	/**
	 * 앱에서 소셜 로그인 또는 회원가입할 경우에 사용
	 * @param params
	 * @return json
	 */
	@ApiOperation(value = "소셜 로그인 또는 회원가입 - Kakao", notes = "params의 예 {\"access_token\":\"XYZ123\"}")
	@RequestMapping(value = "/login_or_register_with_kakao", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> loginOrRegisterWithKakao(@RequestBody String params) {
		logger.debug("loginOrRegisterWithKakao called: params is {}", params);
		APIResponse response = new APIResponse(false, "access_token이 유효하지 않습니다", null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String accessToken = element.getAsJsonObject().get("access_token").getAsString();
		try {
			String kakaoProfile = this.requestKakaoProfile(accessToken);
			logger.debug("loginOrRegisterWithKakao : kakaoProfile is {}", kakaoProfile);

			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = (JsonObject) jsonParser.parse(kakaoProfile);
			logger.debug("loginOrRegisterWithKakao / raw json :{}", jsonObject);

//		    String email = jsonObject.get("nickName").getAsString() + "@kakao.com";
//		    if (!StringUtils.isEmpty(jsonObject.get("email")) || !StringUtils.isEmpty(jsonObject.get("id"))) {
//		    	email = !StringUtils.isEmpty(jsonObject.get("email")) ? jsonObject.get("email").getAsString() : jsonObject.get("id").getAsString() + "@kakao.com";
//		    }

			JsonObject propsObject = (JsonObject) jsonObject.get("properties");

			String email = jsonObject.get("id").getAsInt() + "@kakao.com";

			Member member = userService.findByEmail(email);
			// Not a member
			if (member == null) {
				member = saveFromKakao(email, propsObject);
				// Member
			} else {
				member.setPassword(UPMusicConstants.TOKEN_AES128);
				member = userService.save(member);
			}
			if (member != null) {
				response = authLogin(email, UPMusicConstants.TOKEN_AES128);
			}
		} catch (Exception e) {
			logger.error("loginOrRegisterWithKakao faild! : error is {}", e.toString());
			response = new APIResponse(false, e.toString(), null);
		}
		return ResponseEntity.ok(response);
	}

	/**
	 * 앱에서 소셜 로그인 또는 회원가입할 경우에 사용
	 * @param params
	 * @return json
	 */
	@ApiOperation(value = "소셜 로그인 또는 회원가입 - Naver", notes = "params의 예 {\"access_token\":\"XYZ123\"}")
	@RequestMapping(value = "/login_or_register_with_naver", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> loginOrRegisterWithNaver(@RequestBody String params) {
		logger.debug("loginOrRegisterWithNaver called: params is {}", params);
		APIResponse response = new APIResponse(false, "access_token이 유효하지 않습니다", null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String accessToken = element.getAsJsonObject().get("access_token").getAsString();
		try {
			String naverProfile = this.requestNaverProfile(accessToken);
			logger.debug("loginOrRegisterWithNaver : naverProfile is {}", naverProfile);

			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = (JsonObject) jsonParser.parse(naverProfile);
			logger.debug("loginOrRegisterWithNaver / raw json :{}", jsonObject);
			if (jsonObject.has("response")) {
				JsonObject naverResponseObject = jsonObject.getAsJsonObject("response");
				//String email = !StringUtils.isEmpty(naverResponseObject.get("email")) ? naverResponseObject.get("email").getAsString() : naverResponseObject.get("id").getAsString() + "@naver.com";

				String email = UPMusicConstants.SOCIAL_NAVER+"_"+naverResponseObject.get("email").getAsString();

				Member member = userService.findByEmail(email);
				// Not a member
				if (member == null) {
					member = saveFromNaver(email, naverResponseObject);
					// Member
				} else {
					member.setPassword(UPMusicConstants.TOKEN_AES128);
					member = userService.save(member);
				}
				if (member != null) {
					response = authLogin(email, UPMusicConstants.TOKEN_AES128);
				}
			} else {
				response = new APIResponse(false, jsonObject.getAsString(), null);
			}

		} catch (Exception e) {
			logger.error("loginOrRegisterWithNaver faild! : error is {}", e.toString());
			response = new APIResponse(false, e.toString(), null);
		}
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "이메일 중복검사", notes = "params의 예 {\"email\":\"tester0@gmail.com\"}")
	@RequestMapping(value = "/check_email", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> checkEmail(@RequestBody String params) {
		logger.debug("checkEmail called: params is {}", params);
		APIResponse response = new APIResponse(false, super.getMessage("model.member.duplicated.email"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String email = element.getAsJsonObject().get("email").getAsString();
		logger.debug("checkEmail called: email is {}", email);
		if (!UPMusicHelper.validateEmail(email)) {
			response = new APIResponse(false, super.getMessage("model.member.notmatch.email_regex"), null);
		} else if (userService.findByEmail(email) == null) {
			response = new APIResponse(true, super.getMessage("model.member.checked.email"), null);
		}
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "회원가입 휴대폰 인증번호 요청", notes = "params의 예 {\"phoneNumber\":\"010-1234-5678\"}")
	@RequestMapping(value = "/request_phone_authentication_code", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> requestPhoneAuthenticationCode(@RequestBody String params) {
		logger.debug("requestPhoneAuthenticationCode called: params is {}", params);
		APIResponse response = new APIResponse(false, super.getMessage("model.member.duplicated.phone_number"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String phoneNumber = element.getAsJsonObject().get("phoneNumber").getAsString();
		phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
		logger.debug("requestPhoneAuthenticationCode called: phoneNumber is {}", phoneNumber);
		if (phoneNumber.length() < 10 || phoneNumber.length() > 13) {
			response.setMessage(super.getMessage("model.member.size.phone_number"));
		} else if (userService.findByPhoneNumber(phoneNumber) == null) {
			AuthenticationPhone phoneAuthentication = authenticationPhoneService.saveAuthenticationPhone(phoneNumber);
			if (phoneAuthentication != null) {
				sendAuthenticationPhoneCode(phoneAuthentication);
				response = new APIResponse(true, super.getMessage("model.member.sended.phone_authentication"), null);
			} else {
				response.setMessage(super.getMessage("common.problem.server"));
			}
		}
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "비밀번호 찾기 휴대폰 인증번호 요청", notes = "params의 예 {\"phoneNumber\":\"010-1234-5678\"}")
	@RequestMapping(value = "/request_pw_phone_authentication_code", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> requestPwPhoneAuthenticationCode(@RequestBody String params) {
		logger.debug("requestPwPhoneAuthenticationCode called: params is {}", params);
		APIResponse response = new APIResponse(false, super.getMessage("model.member.notmatch.phone_number"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String phoneNumber = element.getAsJsonObject().get("phoneNumber").getAsString();
		phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
		logger.debug("requestPwPhoneAuthenticationCode called: phoneNumber is {}", phoneNumber);
		if (phoneNumber.length() < 10 || phoneNumber.length() > 13) {
			response.setMessage(super.getMessage("model.member.size.phone_number"));
		} else if (userService.findByPhoneNumber(phoneNumber) != null) {
			AuthenticationPhone phoneAuthentication = authenticationPhoneService.saveAuthenticationPhone(phoneNumber);
			if (phoneAuthentication != null) {
				sendAuthenticationPhoneCode(phoneAuthentication);
				response = new APIResponse(true, super.getMessage("model.member.sended.phone_authentication"), null);
			} else {
				response.setMessage(super.getMessage("common.problem.server"));
			}
		}
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "비밀번호 찾기 이메일 인증번호 요청", notes = "params의 예 {\"email\":\"tester0@gmail.com\"}")
	@RequestMapping(value = "/request_pw_email_authentication_code", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> requestPwEmailAuthenticationCode(@RequestBody String params) {
		logger.debug("requestPwEmailAuthenticationCode called: params is {}", params);
		APIResponse response = new APIResponse(false, super.getMessage("common.problem.server"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(params);
		String email = element.getAsJsonObject().get("email").getAsString();
		if (!UPMusicHelper.validateEmail(email)) {
			response.setMessage(super.getMessage("model.member.notmatch.email_regex"));
		} else if (userService.findByEmail(email) == null) {
			response.setMessage(super.getMessage("model.member.notmatch.email"));
		} else {
			logger.debug("requestPwEmailAuthenticationCode called: email is {}", email);
			AuthenticationEmail authenticationEmail = authenticationEmailService.saveAuthenticationEmail(email);
			if (authenticationEmail != null) {
				sendAuthenticationEmailCode(authenticationEmail);
				response = new APIResponse(true, super.getMessage("model.member.sended.email_authentication"), null);
			} else {
				response.setMessage(super.getMessage("common.problem.server"));
			}
		}
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "회원탈퇴 처리", notes = "params의 예 {\"email\":\"tester0@gmail.com\"}")
	@RequestMapping(value = "/withdraw", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<APIResponse> withdraw (@RequestBody(required = false) String params, Principal principal) {
		logger.debug("withdraw called: params is {}", params);
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		JsonParser parser = new JsonParser();
		JsonElement element = null;
		if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		Set<Role> currentRole = member.getRoles();
		Iterator<Role> iter = currentRole.iterator();
		boolean roleAdmin = false;
		while (iter.hasNext()){
			if(iter.next().getId() == 1){
				roleAdmin = true;
			}
		}
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if(member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
		if(roleAdmin && element.getAsJsonObject().has("id")){
			Member targetMember = memberService.getMemberById(Long.parseLong(element.getAsJsonObject().get("id").getAsString()));
			memberService.withdraw(targetMember);
			response = new APIResponse(true, "true", null);
		} else if (member != null) {
			memberService.withdraw(member);
			response = new APIResponse(true, "true", null);
		}
		return ResponseEntity.ok(response);
	}

	/**
	 * 서비스 결정 후 작업 휴대폰 번호로 인증코드 전송
	 * @param authenticationPhone
	 */
	private void sendAuthenticationPhoneCode(AuthenticationPhone authenticationPhone) {
		logger.debug("sendAuthenticationCode called: authenticationPhone is {}", authenticationPhone);
	}

	/**
	 * 이메일주소로 인증코드 전송
	 * @param authenticationEmail
	 */
	private void sendAuthenticationEmailCode(AuthenticationEmail authenticationEmail) {
		logger.debug("sendAuthenticationCode called: authenticationEmail is {}", authenticationEmail);
		emailService.sendEmail(authenticationEmail.getEmail(), "[UPMusic] 이메일 인증", authenticationEmail.getAuthenticationCode());
	}

	private Member saveFromFacebook(JsonObject jsonObject) {
		Member member = new Member();
		member.setNick(jsonObject.get("name").getAsString());
//    	member.setEmail(!StringUtils.isEmpty(jsonObject.get("email")) ? jsonObject.get("email").getAsString() : jsonObject.get("id").getAsString() + "@facebook.com");
		member.setEmail(jsonObject.get("id").getAsString() + "@facebook.com");
		member.setPassword(UPMusicConstants.TOKEN_AES128);
		member.setSocial(UPMusicConstants.SOCIAL_FACEBOOK);
		member.setBirthday("00000000");
		member.setPhoneNumber("00000000000");
		//member.setGender(!StringUtils.isEmpty(jsonObject.get("email")) && jsonObject.get("gender").getAsString().equals("male") ? Gender.MALE : Gender.FEMALE);

		member.setGender(jsonObject.get("gender") != null && jsonObject.get("gender").getAsString().equals("male") ? Gender.MALE : Gender.FEMALE);

		member.setUpmPoint(BigDecimal.ZERO);
		member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
		logger.debug("facebookAuth : member is {}", member);
		member = userService.save(member);

		// 회원 id가 필요하여 1차 저장 후 프로필이미지 저장
		if (!StringUtils.isEmpty(jsonObject.get("picture")) && !StringUtils.isEmpty(jsonObject.get("picture").getAsJsonObject().get("data")) && !StringUtils.isEmpty(jsonObject.get("picture").getAsJsonObject().get("data").getAsJsonObject().get("url"))) {
			String filename = azureStorageService.uploadResource(jsonObject.get("picture").getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString(), "profiles/" + member.getId() + "/");
			member.setProfileImage(filename);
			member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
			member = userService.save(member);
		}
		return member;
	}

	private Member saveFromGoogle(String email,Payload payload) {
		Member member = new Member();
		member.setNick((String) payload.get("name"));
		member.setEmail(email);
		member.setPassword(UPMusicConstants.TOKEN_AES128);
		member.setSocial(UPMusicConstants.SOCIAL_GOOGLE);
		member.setBirthday("00000000");
		member.setPhoneNumber("00000000000");
		member.setGender(payload.get("gender") != null && payload.get("gender").equals("male") ? Gender.MALE : Gender.FEMALE);
		member.setUpmPoint(BigDecimal.ZERO);
		member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
		member = userService.save(member);

		if (!StringUtils.isEmpty(payload.get("picture"))) {
			String filename = azureStorageService.uploadResource((String) payload.get("picture"), "profiles/" + member.getId() + "/");
			member.setProfileImage(filename);
			member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
			member = userService.save(member);
		}
		return member;
	}

	private Member saveFromKakao(String email, JsonObject jsonObject) {
		Member member = new Member();
//    	member.setNick(jsonObject.get("nickName").getAsString());
		member.setNick(jsonObject.get("nickname").getAsString());
		member.setEmail(email);
		member.setPassword(UPMusicConstants.TOKEN_AES128);
		member.setSocial(UPMusicConstants.SOCIAL_KAKAO);
		member.setBirthday("00000000");
		member.setPhoneNumber("00000000000");
		member.setGender(jsonObject.get("gender") != null && jsonObject.get("gender").getAsString().equals("male") ? Gender.MALE : Gender.FEMALE);
		member.setUpmPoint(BigDecimal.ZERO);
		member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
		member = userService.save(member);

		if (!StringUtils.isEmpty(jsonObject.get("profile_image"))) {
			String filename = azureStorageService.uploadResource(jsonObject.get("profile_image").getAsString(), "profiles/" + member.getId() + "/");
			member.setProfileImage(filename);
			member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
			member = userService.save(member);
		} else if (!StringUtils.isEmpty(jsonObject.get("thumbnail_image"))) {
			String filename = azureStorageService.uploadResource(jsonObject.get("thumbnail_image").getAsString(), "profiles/" + member.getId() + "/");
			member.setProfileImage(filename);
			member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
			member = userService.save(member);
		}
		return member;
	}

	private Member saveFromNaver(String email, JsonObject jsonObject) {
		Member member = new Member();
		member.setNick(jsonObject.get("nickname") != null ? jsonObject.get("nickname").getAsString() : email.split("@")[0]);
		member.setEmail(email);
		member.setPassword(UPMusicConstants.TOKEN_AES128);
		member.setSocial(UPMusicConstants.SOCIAL_NAVER);
		member.setBirthday(jsonObject.get("birthday") != null ? jsonObject.get("birthday").getAsString() : "00000000");
		member.setPhoneNumber("00000000000");
		member.setGender(jsonObject.get("gender") != null && jsonObject.get("gender").getAsString().equals("M") ? Gender.MALE : Gender.FEMALE);
		member.setUpmPoint(BigDecimal.ZERO);
		member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
		member = userService.save(member);

		if (!StringUtils.isEmpty(jsonObject.get("profile_image"))) {
			String filename = azureStorageService.uploadResource(jsonObject.get("profile_image").getAsString(), "profiles/" + member.getId() + "/");
			member.setProfileImage(filename);
			member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
			member = userService.save(member);
		}
		return member;
	}

	private String requestKakaoProfile(final String accessToken) {
//        String requestUrl = "https://kapi.kakao.com/v1/api/talk/profile";
		String requestUrl = "https://kapi.kakao.com/v2/user/me";
		HttpsURLConnection conn;
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		InputStreamReader isr = null;
		try {
			final URL url = new URL(requestUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");

			final int responseCode = conn.getResponseCode();
			if (responseCode == 200)
				isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
			else
				isr = new InputStreamReader(conn.getErrorStream());

			reader = new BufferedReader(isr);
			final StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			return buffer.toString();

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("requestKakaoProfile faild! : error is {}", e.toString());
		} finally {
			if (writer != null) try { writer.close(); } catch (Exception ignore) { }
			if (reader != null) try { reader.close(); } catch (Exception ignore) { }
			if (isr != null) try { isr.close(); } catch (Exception ignore) { }
		}

		return null;
	}

	private String requestNaverProfile(final String accessToken) {
		String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가
		try {
			String apiURL = "https://openapi.naver.com/v1/nid/me";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", header);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if(responseCode==200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {  // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("requestNaverProfile faild! : error is {}", e.toString());
		}
		return null;
	}

	private APIResponse authLogin(String email, String password) {

		APIResponse response = new APIResponse(false, super.getMessage("common.login.error"), null);
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
			authenticationManager.authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(authToken);
			httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
			Member member = memberService.findByEmail(email);
			String token = UPMusicHelper.encryptAES(UPMusicHelper.getTimeStamp(), UPMusicConstants.TOKEN_AES128);
			member.setToken(token);
			memberService.saveMember(member);

			String encodeToString = Base64.getEncoder().encodeToString(httpSession.getId().getBytes());

			response = new APIResponse(true, encodeToString , member);
			logger.debug("autologin successfully! session id is {}", httpSession.getId());
		} catch (Exception e) {
			logger.debug("autologin faild! : error is {}", e.toString());
			response = new APIResponse(false, super.getMessage("common.login.error"), null);
		}
		return response;
	}
}

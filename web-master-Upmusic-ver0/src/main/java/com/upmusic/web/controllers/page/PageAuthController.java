package com.upmusic.web.controllers.page;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.upmusic.web.services.MemberService;
import com.upmusic.web.validator.MemberValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.Gender;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.PasswordForgot;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.SecurityService;
import com.upmusic.web.services.UserService;
import com.upmusic.web.validator.PasswordForgotValidator;
import com.upmusic.web.validator.UserValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/auth")
@Api(value="auth", description="로그인과 회원가입 페이지를 담당하는 컨트롤러")
public class PageAuthController extends PageAbstractController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HttpSession httpSession;

	@Autowired
	private MemberService memberService;
	
	@Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private PasswordForgotValidator passwordForgotValidator;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

	@Autowired
	private MemberValidator memberValidator;
    
    @Autowired
	private AzureStorageService azureStorageService;
    
    
	@ApiOperation(value = "로그인 템플릿을 반환", response = String.class)
	@GetMapping("/login")
    public String login(HttpServletRequest request, Model model, String error, String logout) {
		super.setSubMenu(model, "/auth/login", null, request);
		// 로그인 후 리다이렉트 할 url 저장
		String referrer = request.getHeader("Referer");
		if (referrer != null && !referrer.contains("auth")) {
			logger.debug("login : save referrer to session. referrer is {}", referrer);
			request.getSession().setAttribute("redirectTo", referrer); // 로그인 및 회원가입 페이지는 리다이렉트 url에서 제외
		}
		if (error != null) model.addAttribute("loginError", "아이디 또는 비밀번호가 정확하지 않습니다.");
        if (logout != null) model.addAttribute("loginMessage", "정상적으로 로그인 되었습니다.");
        String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/auth/login_page_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/auth/login_page_mobile";
		}
        if (request.getParameter("modal") != null) return "fragments/page/auth/login";
        return "fragments/page/auth/login_page";
    }
	
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/facebook/complete")
    public String facebookAuth(Principal principal) {
		logger.debug("facebookAuth : principal is {}", principal.getName());
		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
        logger.debug("facebookAuth : map is {}", map);
//        if (StringUtils.isEmpty(map.get("email")) && StringUtils.isEmpty(map.get("id"))) return "redirect:/auth/logout";
//
//        String email = !StringUtils.isEmpty(map.get("email")) ? map.get("email") : map.get("id") + "@facebook.com";
		String email = map.get("id") + "@facebook.com";
        Member member = userService.findByEmail(email);
        // Not a member
        if (member == null) {
        	member = saveFromFacebook(map);
        // Member
        } else {
        	member.setPassword(UPMusicConstants.TOKEN_AES128);
        	member = userService.save(member);
        }
        logger.debug("facebookAuth : member is {}", member);
        if (member == null) return "redirect:/auth/logout";
        
        authLogin(member);
        return "redirect:/";
    }
	
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/google/complete")
    public String googleAuth(Principal principal, HttpServletRequest request) {
		logger.debug("googleAuth : principal is {}", principal.getName());
		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
        logger.debug("googleAuth : map is {}", map);
        if (StringUtils.isEmpty(map.get("email"))) return "redirect:/auth/logout";

        String email = UPMusicConstants.SOCIAL_GOOGLE+"_"+map.get("email");

        //Member member = userService.findByEmail(map.get("email"));
		Member member = userService.findByEmail(email);

        // Not a member
        if (member == null) {
        	member = saveFromGoogle(email,map);
        // Member
        } else {
        	member.setPassword(UPMusicConstants.TOKEN_AES128);
        	member = userService.save(member);
        }
        logger.debug("googleAuth : member is {}", member);
        if (member == null) return "redirect:/auth/logout";
        
        authLogin(member);
        return "redirect:/";
    }
	
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/kakao/complete")
    public String kakaoAuth(Principal principal, HttpServletRequest request) {
		logger.debug("kakaoAuth : principal is {}", principal.getName());
		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
        logger.debug("kakaoAuth : map is {}", map);
        
        // 이메일 제공 거부한 경우에 대처
//        String email = map.get("kaccount_email");
//        if (StringUtils.isEmpty(email)) email = String.valueOf(map.get("id")) + "@kakao.com";

		//중복 메일가입 이슈로 고정
		String email = map.get("kaccount_email");

		if(!StringUtils.isEmpty(map.get("id"))) {
			email = String.valueOf(map.get("id")) + "@kakao.com";
		}
        
        Member member = userService.findByEmail(email);
        // Not a member
        if (member == null) {
        	member = saveFromKakao(email, map);
        // Member
        } else {
        	member.setPassword(UPMusicConstants.TOKEN_AES128);
        	member = userService.save(member);
        }
        logger.debug("kakaoAuth : member is {}", member);
        if (member == null) return "redirect:/auth/logout";
        
        authLogin(member);
        return "redirect:/";
    }
	
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/naver/complete")
    public String naverAuth(Principal principal, HttpServletRequest request) {
		logger.debug("naverAuth : principal is {}", principal.getName());
		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
        logger.debug("naverAuth : map is {}", map);
        if (StringUtils.isEmpty(map.get("response"))) return "redirect:/auth/logout";
        
        HashMap<String, String> responseMap = (HashMap<String, String>)(Object) map.get("response");

		String email = UPMusicConstants.SOCIAL_NAVER+"_"+responseMap.get("email");

        //Member member = userService.findByEmail(responseMap.get("email"));
		Member member = userService.findByEmail(email);

        //logger.debug("findByEmail : {}", member.toString());

        // Not a member
        if (member == null) {
        	member = saveFromNaver(email,responseMap);
        // Member
        } else {
        	member.setPassword(UPMusicConstants.TOKEN_AES128);
        	member = userService.save(member);
        }
        logger.debug("naverAuth : member is {}", member);
        if (member == null) return "redirect:/auth/logout";
        
        authLogin(member);
        return "redirect:/";
    }
	
	@ApiOperation(value = "비밀번호 찾기 템플릿을 반환", response = String.class)
    @GetMapping("/forgot_password")
    public String forgotPassword(HttpServletRequest request, Model model) throws MalformedURLException {
		super.setSubMenu(model, "/auth/forgot_password", null, request);
		initNiceID(request, model);
		model.addAttribute("forgotpwform", new PasswordForgot());
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/auth/forgot_password_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/auth/forgot_password_mobile";
		}
        return "fragments/page/auth/forgot_password";
    }
	
	@ApiOperation(value = "비밀번호 찾기 템플릿을 처리", response = String.class)
    @PostMapping("/forgot_password")
    public String forgotPassword(@ModelAttribute("forgotpwform") PasswordForgot forgotpwform, BindingResult bindingResult, HttpServletRequest request, Model model) throws MalformedURLException {
		String userAgent = request.getHeader("user-agent");
		passwordForgotValidator.validate(forgotpwform, bindingResult);
        if (bindingResult.hasErrors()) {
        	super.setSubMenu(model, "/auth/forgot_password", null, request);
        	initNiceID(request, model);
    		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
    			model.addAttribute("userAgent", userAgent);
    			return "fragments/page/auth/forgot_password_mobile";
    		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
    			return "fragments/page/auth/forgot_password_mobile";
    		}
        	return "fragments/page/auth/forgot_password";
        }
        super.setSubMenu(model, "/auth/new_password", null, request);
        forgotpwform.setAuthenticated(true);
        model.addAttribute("forgotpwform", forgotpwform);
        if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/auth/new_password_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/auth/new_password_mobile";
		}
        return "fragments/page/auth/new_password";
    }
	
	@ApiOperation(value = "비밀번호 업데이트", response = String.class)
    @PostMapping("/update_password")
    public String updatePassword(@ModelAttribute("forgotpwform") PasswordForgot forgotpwform, BindingResult bindingResult, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/auth/new_password", null, request);
		String userAgent = request.getHeader("user-agent");
		model.addAttribute("forgotpwform", forgotpwform);
		passwordForgotValidator.validate(forgotpwform, bindingResult);
        if (bindingResult.hasErrors()) {
        	if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
    			model.addAttribute("userAgent", userAgent);
    			return "fragments/page/auth/new_password_mobile";
    		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
    			return "fragments/page/auth/new_password_mobile";
    		}
        	return "fragments/page/auth/new_password";
        }
        super.setSubMenu(model, "/auth/new_password", null, request);
        userService.updatePassword(forgotpwform.getEmail(), forgotpwform.getNewPassword());
        // if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
        if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			model.addAttribute("passwordChangeCompleted", true);
			return "fragments/page/auth/new_password_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			model.addAttribute("passwordChangeCompleted", true);
			return "fragments/page/auth/new_password_mobile";
		}
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(forgotpwform.getEmail());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), forgotpwform.getNewPassword(), userDetails.getAuthorities());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
        return "redirect:/";
    }
	
	@ApiOperation(value = "회원가입 템플릿을 반환", response = String.class)
    @GetMapping("/registration")
    public String registration(HttpServletRequest request, Model model) throws MalformedURLException {
		super.setSubMenu(model, "/auth/registration", null, request);
		initNiceID(request, model);
		model.addAttribute("memberform", new Member());
		String userAgent = request.getHeader("user-agent");
		logger.debug("page : userAgent is {}", userAgent);
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/auth/registration_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/auth/registration_mobile";
		}
        return "fragments/page/auth/registration";
    }
	
	@ApiOperation(value = "본인인증 성공 템플릿을 반환", response = String.class)
	@PostMapping("/cellphoneSuccess")
    public String cellphoneSuccess(HttpServletRequest request, Model model) throws MalformedURLException{
		NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
	    String sEncodeData = requestReplace(request.getParameter("EncodeData"), "encodeData");

	    String sSiteCode = "BH326";				// NICE로부터 부여받은 사이트 코드
	    String sSitePassword = "y1Eqj2m6lbWT";			// NICE로부터 부여받은 사이트 패스워드

	    String sCipherTime = "";			// 복호화한 시간
	    String sRequestNumber = "";			// 요청 번호
	    String sResponseNumber = "";		// 인증 고유번호
	    String sAuthType = "";				// 인증 수단
	    String sName = "";					// 성명
	    String sDupInfo = "";				// 중복가입 확인값 (DI_64 byte)
	    String sConnInfo = "";				// 연계정보 확인값 (CI_88 byte)
	    String sBirthDate = "";				// 생년월일(YYYYMMDD)
	    String sGender = "";				// 성별
	    String sNationalInfo = "";			// 내/외국인정보 (개발가이드 참조)
		String sMobileNo = "";				// 휴대폰번호
		String sMobileCo = "";				// 통신사
	    String sMessage = "";
	    String sPlainData = "";
	    
	    int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);

	    if( iReturn == 0 )
	    {
	        sPlainData = niceCheck.getPlainData();
	        sCipherTime = niceCheck.getCipherDateTime();
	        
	        // 데이타를 추출합니다.
			HashMap mapresult = niceCheck.fnParse(sPlainData);
	        
	        sRequestNumber  = (String)mapresult.get("REQ_SEQ");
	        sResponseNumber = (String)mapresult.get("RES_SEQ");
	        sAuthType		= (String)mapresult.get("AUTH_TYPE");
	        sName			= (String)mapresult.get("NAME");
			//sName			= (String)mapresult.get("UTF8_NAME"); //charset utf8 사용시 주석 해제 후 사용
	        sBirthDate		= (String)mapresult.get("BIRTHDATE");
	        sGender			= (String)mapresult.get("GENDER");
	        sNationalInfo  	= (String)mapresult.get("NATIONALINFO");
	        sDupInfo		= (String)mapresult.get("DI");
	        sConnInfo		= (String)mapresult.get("CI");
	        sMobileNo		= (String)mapresult.get("MOBILE_NO");
	        sMobileCo		= (String)mapresult.get("MOBILE_CO");
	        
	        String session_sRequestNumber = (String) httpSession.getAttribute("REQ_SEQ");
	        if(!sRequestNumber.equals(session_sRequestNumber)) {
	            sMessage = "세션값이 다릅니다. 올바른 경로로 접근하시기 바랍니다.";
	            sResponseNumber = "";
	            sAuthType = "";
	        }
	    }
	    else if( iReturn == -1)
	    {
	        sMessage = "복호화 시스템 에러입니다.";
	    }    
	    else if( iReturn == -4)
	    {
	        sMessage = "복호화 처리오류입니다.";
	    }    
	    else if( iReturn == -5)
	    {
	        sMessage = "복호화 해쉬 오류입니다.";
	    }    
	    else if( iReturn == -6)
	    {
	        sMessage = "복호화 데이터 오류입니다.";
	    }    
	    else if( iReturn == -9)
	    {
	        sMessage = "입력 데이터 오류입니다.";
	    }    
	    else if( iReturn == -12)
	    {
	        sMessage = "사이트 패스워드 오류입니다.";
	    }    
	    else
	    {
	        sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
	    }
	    logger.debug("cellphoneSuccess : sMessage is {}", sMessage);
	    logger.debug("cellphoneSuccess : sBirthDate is {}", sBirthDate);
	    logger.debug("cellphoneSuccess : sMobileNo is {}", sMobileNo);
	    logger.debug("cellphoneSuccess : sGender is {}", sGender);
	    model.addAttribute("sMessage", sMessage);
	    model.addAttribute("sBirthDate", sBirthDate);
	    model.addAttribute("sMobileNo", sMobileNo);
	    model.addAttribute("sGender", sGender);
	    
        return "fragments/page/auth/cellphoneSuccess";
    }
	
	@ApiOperation(value = "본인인증 실패 템플릿을 반환", response = String.class)
	@PostMapping("/cellphoneFail")
    public String cellphoneFail(HttpServletRequest request, Model model) throws MalformedURLException{
		NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();

	    String sEncodeData = requestReplace(request.getParameter("EncodeData"), "encodeData");

	    String sSiteCode = "BH326";				// NICE로부터 부여받은 사이트 코드
	    String sSitePassword = "y1Eqj2m6lbWT";			// NICE로부터 부여받은 사이트 패스워드

	    String sCipherTime = "";			// 복호화한 시간
	    String sRequestNumber = "";			// 요청 번호
	    String sErrorCode = "";				// 인증 결과코드
	    String sAuthType = "";				// 인증 수단
	    String sMessage = "";
	    String sPlainData = "";
	    
	    int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);

	    if( iReturn == 0 )
	    {
	        sPlainData = niceCheck.getPlainData();
	        sCipherTime = niceCheck.getCipherDateTime();
	        
	        // 데이타를 추출합니다.
	        HashMap mapresult = niceCheck.fnParse(sPlainData);
	        
	        sRequestNumber 	= (String)mapresult.get("REQ_SEQ");
	        sErrorCode 		= (String)mapresult.get("ERR_CODE");
	        sAuthType 		= (String)mapresult.get("AUTH_TYPE");
	    }
	    else if( iReturn == -1)
	    {
	        sMessage = "복호화 시스템 에러입니다.";
	    }    
	    else if( iReturn == -4)
	    {
	        sMessage = "복호화 처리오류입니다.";
	    }    
	    else if( iReturn == -5)
	    {
	        sMessage = "복호화 해쉬 오류입니다.";
	    }    
	    else if( iReturn == -6)
	    {
	        sMessage = "복호화 데이터 오류입니다.";
	    }    
	    else if( iReturn == -9)
	    {
	        sMessage = "입력 데이터 오류입니다.";
	    }    
	    else if( iReturn == -12)
	    {
	        sMessage = "사이트 패스워드 오류입니다.";
	    }    
	    else
	    {
	        sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
	    }
	    
	    model.addAttribute("sMessage", sMessage);
	    
        return "fragments/page/auth/cellphoneSuccess";
    }
	
	@ApiOperation(value = "회원가입 처리", response = String.class)
	@PostMapping("/registration")
	public String registration(@ModelAttribute("memberform") Member memberform, BindingResult bindingResult, HttpServletRequest request, Model model) throws MalformedURLException {
		String originalPassword = memberform.getPassword();
        userValidator.validate(memberform, bindingResult);
        if (bindingResult.hasErrors()) {
        	initNiceID(request, model);
        	String userAgent = request.getHeader("user-agent");
        	logger.debug("page : userAgent is {}", userAgent);
    		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
    			model.addAttribute("userAgent", userAgent);
    			return "fragments/page/auth/registration_mobile";
    		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
    			return "fragments/page/auth/registration_mobile";
    		}
        	return "fragments/page/auth/registration";
        }
        if (memberform.getGenderNo().equals("0")) {
        	memberform.setGender(Gender.FEMALE);
        } else {
        	memberform.setGender(Gender.MALE);
        }
        memberform.setUpmPoint(BigDecimal.ZERO);
        memberform.setFundingPoint(BigDecimal.ZERO);
        memberform.setChargePoint(BigDecimal.ZERO);
        userService.save(memberform);
        
        // 모바일의 경우엔 로그인 처리 제외
        String userAgent = request.getHeader("user-agent");
		logger.debug("page : userAgent is {}", userAgent);
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			memberform.setPassword(originalPassword);
			model.addAttribute("userAgent", userAgent);
			model.addAttribute("registeredCompleted", true);
			return "fragments/page/auth/registration_mobile";
		}
		return securityService.autologin(memberform.getEmail(), originalPassword, request);
    }

    /*
	@ApiOperation(value = "아티스트 편집페이지 템플릿을 반환", response = String.class)
	@GetMapping("/artist/{id}/edit")
	public String artistEdit(@PathVariable Long id, Principal principal, HttpServletRequest request, Model model) throws MalformedURLException {
		Member member = super.getCurrentUser(principal);
		initNiceID(request, model);
		super.setSubMenu(model, "/music/artist", member, request);
		Member artist = memberService.getMemberById(id);
		if (artist != null && member != null && 0 == (artist.getId().compareTo(member.getId()))) {
			model.addAttribute("artistform", artist);
			String userAgent = request.getHeader("user-agent");
			if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
				model.addAttribute("userAgent", userAgent);
				return "fragments/page/music/artist_edit_mobile";
			} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
				return "fragments/page/music/artist_edit_mobile";
			}
			return "fragments/page/music/artist_edit";

		}

		return "redirect:/";
	}

	@ApiOperation(value = "아티스트 편집페이지 템플릿을 처리", response = String.class)
	@PostMapping("/artist/{id}")
	public String artistUpdate(@ModelAttribute("artistform") Member artistform, @PathVariable Long id, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		Member artist = memberService.getMemberById(id);
		Member member = super.getCurrentUser(principal);
		if (artist != null && member != null && 0 == (artist.getId().compareTo(member.getId()))) {
			memberValidator.validate(artistform, bindingResult);
			if (bindingResult.hasErrors()) {
				artistform.setEmail(artist.getEmail());
				artistform.setTrackCnt(artist.getTrackCnt());
				artistform.setPhoneNumber(artist.getPhoneNumber());
				super.setSubMenu(model, "/music/artist", member, request);
				return "fragments/page/music/artist_edit";
			}
			// 아티스트 저장
			logger.debug("Save artist");
			artist.setNick(artistform.getNick());
			artist.setProfileImage(UPMusicHelper.makeReadableUrl(artistform.getProfileImage()));
			artist = memberService.saveMember(artist);
			if (artistform.getProfileImageMultipart() != null) azureStorageService.uploadResource(artistform.getProfileImageMultipart(), "profiles/" + artist.getId() + "/");
			return "redirect:/music/artist/" + artist.getId();
		}
		return "redirect:/auth/login";
	}
	*/
	
	private void initNiceID(HttpServletRequest request, Model model) throws MalformedURLException {
		NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
	    String sSiteCode = "BH326";			// NICE로부터 부여받은 사이트 코드
	    String sSitePassword = "y1Eqj2m6lbWT";		// NICE로부터 부여받은 사이트 패스워드
	    String sRequestNumber = "REQ0000000001";        	// 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로 
	    // 업체에서 적절하게 변경하여 쓰거나, 아래와 같이 생성한다.
	    sRequestNumber = niceCheck.getRequestNO(sSiteCode);
	    httpSession.setAttribute("REQ_SEQ" , sRequestNumber);	// 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.
	   	String sAuthType = "M";      	// 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서
	   	String popgubun 	= "N";		//Y : 취소버튼 있음 / N : 취소버튼 없음
		String customize 	= "";		//없으면 기본 웹페이지 / Mobile : 모바일페이지
		String sGender = ""; 			//없으면 기본 선택 값, 0 : 여자, 1 : 남자 
		
	    // CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
		//리턴url은 인증 전 인증페이지를 호출하기 전 url과 동일해야 합니다. ex) 인증 전 url : http://www.~ 리턴 url : http://www.~
	    String sReturnUrl = UPMusicHelper.baseUrl(request) + "/auth/cellphoneSuccess";      // 성공시 이동될 URL
	    String sErrorUrl = UPMusicHelper.baseUrl(request) + "/auth/cellphoneFail";          // 실패시 이동될 URL

	    // 입력될 plain 데이타를 만든다.
	    String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
	                        "8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
	                        "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
	                        "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
	                        "7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
	                        "11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
	                        "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize + 
							"6:GENDER" + sGender.getBytes().length + ":" + sGender;
	    
	    String sMessage = "";
	    String sEncData = "";
	    
	    int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
	    if( iReturn == 0 ) {
	        sEncData = niceCheck.getCipherData();
	    } else if( iReturn == -1) {
	        sMessage = "암호화 시스템 에러입니다.";
	    } else if( iReturn == -2) {
	        sMessage = "암호화 처리오류입니다.";
	    } else if( iReturn == -3) {
	        sMessage = "암호화 데이터 오류입니다.";
	    } else if( iReturn == -9) {
	        sMessage = "입력 데이터 오류입니다.";
	    } else {
	        sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
	    }
	    model.addAttribute("sMessage", sMessage);
	    model.addAttribute("sEncData", sEncData);
	}
	
	private String requestReplace (String paramValue, String gubun) {
        String result = "";
        if (paramValue != null) {
        	paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        	paramValue = paramValue.replaceAll("\\*", "");
        	paramValue = paramValue.replaceAll("\\?", "");
        	paramValue = paramValue.replaceAll("\\[", "");
        	paramValue = paramValue.replaceAll("\\{", "");
        	paramValue = paramValue.replaceAll("\\(", "");
        	paramValue = paramValue.replaceAll("\\)", "");
        	paramValue = paramValue.replaceAll("\\^", "");
        	paramValue = paramValue.replaceAll("\\$", "");
        	paramValue = paramValue.replaceAll("'", "");
        	paramValue = paramValue.replaceAll("@", "");
        	paramValue = paramValue.replaceAll("%", "");
        	paramValue = paramValue.replaceAll(";", "");
        	paramValue = paramValue.replaceAll(":", "");
        	paramValue = paramValue.replaceAll("-", "");
        	paramValue = paramValue.replaceAll("#", "");
        	paramValue = paramValue.replaceAll("--", "");
        	paramValue = paramValue.replaceAll("-", "");
        	paramValue = paramValue.replaceAll(",", "");
        	if (gubun != "encodeData") {
        		paramValue = paramValue.replaceAll("\\+", "");
        		paramValue = paramValue.replaceAll("/", "");
        		paramValue = paramValue.replaceAll("=", "");
        	}
        	result = paramValue;
        }
        return result;
	}

    private Member saveFromFacebook(Map<String, String> map) {
    	Member member = new Member();
    	member.setNick(map.get("name"));
    	member.setEmail(!StringUtils.isEmpty(map.get("email")) ? map.get("email") : map.get("id") + "@facebook.com");
    	member.setPassword(UPMusicConstants.TOKEN_AES128);
    	member.setSocial(UPMusicConstants.SOCIAL_FACEBOOK);
    	member.setBirthday("00000000");
    	member.setPhoneNumber("00000000000");
    	member.setGender(map.get("gender") != null && map.get("gender").equals("male") ? Gender.MALE : Gender.FEMALE);
    	member.setUpmPoint(BigDecimal.ZERO);
    	member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
		member = userService.save(member);
    	
		// 회원 id가 필요하여 1차 저장 후 프로필이미지 저장
		String filename = azureStorageService.uploadResource("http://graph.facebook.com/" + map.get("id") + "/picture?type=large", "profiles/" + member.getId() + "/");
		member.setProfileImage(filename);
		member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
		member = userService.save(member);
    	return member;
    }

    private Member saveFromGoogle(String email,Map<String, String> map) {
    	Member member = new Member();
    	member.setNick(map.get("name"));
    	member.setEmail(email);
    	member.setPassword(UPMusicConstants.TOKEN_AES128);
    	member.setSocial(UPMusicConstants.SOCIAL_GOOGLE);
    	member.setBirthday("00000000");
    	member.setPhoneNumber("00000000000");
    	member.setGender(map.get("gender") != null && map.get("gender").equals("male") ? Gender.MALE : Gender.FEMALE);
    	member.setUpmPoint(BigDecimal.ZERO);
    	member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
    	member = userService.save(member);
    	
    	if (!StringUtils.isEmpty(map.get("picture"))) {
    		String filename = azureStorageService.uploadResource(map.get("picture"), "profiles/" + member.getId() + "/");
    		member.setProfileImage(filename);
    		member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
    		member = userService.save(member);
    	}
    	return member;
    }

    @SuppressWarnings("unchecked")
	private Member saveFromKakao(String email, Map<String, String> map) {
    	HashMap<String, String> propertyMap = (HashMap<String, String>)(Object) map.get("properties");
    	Member member = new Member();
    	member.setNick(propertyMap.get("nickname"));
    	member.setEmail(email);
    	member.setPassword(UPMusicConstants.TOKEN_AES128);
    	member.setSocial(UPMusicConstants.SOCIAL_KAKAO);
    	member.setBirthday("00000000");
    	member.setPhoneNumber("00000000000");
    	member.setGender(propertyMap.get("gender") != null && propertyMap.get("gender").equals("male") ? Gender.MALE : Gender.FEMALE);
    	member.setUpmPoint(BigDecimal.ZERO);
    	member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
    	member = userService.save(member);
    	
    	if (!StringUtils.isEmpty(propertyMap.get("profile_image"))) {
    		String filename = azureStorageService.uploadResource(propertyMap.get("profile_image"), "profiles/" + member.getId() + "/");
    		member.setProfileImage(filename);
    		member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
    		member = userService.save(member);
    	} else if (!StringUtils.isEmpty(propertyMap.get("thumbnail_image"))) {
    		String filename = azureStorageService.uploadResource(propertyMap.get("thumbnail_image"), "profiles/" + member.getId() + "/");
    		member.setProfileImage(filename);
    		member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
    		member = userService.save(member);
    	}
    	return member;
    }
    
	private Member saveFromNaver(String email,Map<String, String> responseMap) {
    	Member member = new Member();
    	member.setNick(responseMap.get("nickname"));
    	member.setEmail(email);
    	member.setPassword(UPMusicConstants.TOKEN_AES128);
    	member.setSocial(UPMusicConstants.SOCIAL_NAVER);
    	member.setBirthday(responseMap.get("birthday") != null ? responseMap.get("birthday") : "00000000");
    	member.setPhoneNumber("00000000000");
    	member.setGender(responseMap.get("gender") != null && responseMap.get("gender").equals("M") ? Gender.MALE : Gender.FEMALE);
    	member.setUpmPoint(BigDecimal.ZERO);
    	member.setFundingPoint(BigDecimal.ZERO);
		member.setChargePoint(BigDecimal.ZERO);
    	member = userService.save(member);
    	
    	if (!StringUtils.isEmpty(responseMap.get("profile_image"))) {
    		String filename = azureStorageService.uploadResource(responseMap.get("profile_image"), "profiles/" + member.getId() + "/");
    		member.setProfileImage(filename);
    		member.setPassword(UPMusicConstants.TOKEN_AES128); // userService의 save 메소드는 비밀번호를 매번 암호화
    		member = userService.save(member);
    	}
    	return member;
    }
    
    private void authLogin(Member member) {
    	UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), UPMusicConstants.TOKEN_AES128, userDetails.getAuthorities());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
    }

}

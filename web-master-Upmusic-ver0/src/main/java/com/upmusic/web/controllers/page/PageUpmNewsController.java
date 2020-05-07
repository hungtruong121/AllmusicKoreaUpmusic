package com.upmusic.web.controllers.page;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.BandRecruit;
import com.upmusic.web.domain.BandRecruitComment;
import com.upmusic.web.domain.Event;
import com.upmusic.web.domain.EventBanner;
import com.upmusic.web.domain.FAQ;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.Notice;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.BandRecruitCommentService;
import com.upmusic.web.services.BandRecruitService;
import com.upmusic.web.services.EventBannerService;
import com.upmusic.web.services.EventService;
import com.upmusic.web.services.FAQService;
import com.upmusic.web.services.NoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Controller
@RequestMapping("/upm_news")
@Api(value="upm_news", description="UPM 소식 페이지를 담당하는 컨트롤러")
public class PageUpmNewsController extends PageAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private EventService eventService;

	@Autowired
	private EventBannerService eventBannerService;

	@Autowired
	private BandRecruitService bandRecruitService;

	@Autowired
	private BandRecruitCommentService bandRecruitCommentService;

	@Autowired
	private FAQService FAQService;

	@Autowired
	private AzureStorageService azureStorageService;

	Gson gson = new Gson();

    @ApiOperation(value = "UPM 소식 페이지의 메인 템플릿을 반환",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @GetMapping()
    public String page(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/upm_news", super.getCurrentUser(principal), request);
    	return "fragments/page/upm_news/index";
    }

    @SuppressWarnings("deprecation")
	@ApiOperation(value = "공지사항 템플릿을 반환", response = String.class)
    @GetMapping("/notice")
    public String notice(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/upm_news/notice", super.getCurrentUser(principal), request);

    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 7, new Sort(Direction.DESC, "createdAt"));

    	Page<Notice> noticeList = noticeService.findList(pageable);

    	model.addAttribute("noticeList", noticeList);
    	model.addAttribute("menuFlag", "전체");
    	
    	List<Notice> allNoticeList = noticeService.findByCategory("");
    	List<Notice> infoNoticeList = noticeService.findByCategory("안내");
    	List<Notice> updateNoticeList = noticeService.findByCategory("업데이트");
    	List<Notice> serviceNoticeList = noticeService.findByCategory("시스템 점검");
    	
    	model.addAttribute("allNoticeList", allNoticeList);
    	model.addAttribute("infoNoticeList", infoNoticeList);
    	model.addAttribute("updateNoticeList", updateNoticeList);
    	model.addAttribute("serviceNoticeList", serviceNoticeList);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/upm_news/notice_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/upm_news/notice_mobile";
		}
    	
        return "fragments/page/upm_news/notice";
    }

    @SuppressWarnings("deprecation")
	@ApiOperation(value = "이벤트 템플릿을 반환", response = String.class)
    @GetMapping("/event")
    public String event(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/upm_news/event", super.getCurrentUser(principal), request);

    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 8, new Sort(Direction.DESC, "createdAt"));

    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
    	String today = sdf.format(date) + " 23:59:59";

    	Date today2 = null;
		try {
			today2 = sdf.parse(today);
		} catch (ParseException e) {
			e.printStackTrace();
		}

    	// 진행중인 이벤트 조회
    	Page<Event> eventList = eventService.findAllNewEvent(today2, pageable);
    	Page<Event> endEventList = eventService.findByShowYnAndCloseAt(today2, pageable);
    	PageRequest pageableEventBanner = new PageRequest(0, 5, new Sort(Direction.ASC, "orderNo"));
    	Page<EventBanner> eventBannerList = eventBannerService.findUseEventList(pageableEventBanner);

    	model.addAttribute("endEventList", endEventList);
    	model.addAttribute("eventList", eventList);
    	model.addAttribute("eventBannerList", eventBannerList);
    	
    	// 모바일 
    	List<Event> eventListMobile = eventService.findAllByCloseAtAndShowYn(today2);
    	List<Event> endEventListMobile = eventService.findByCloseAt(today2);
    	
    	model.addAttribute("eventListMobile", eventListMobile);
    	model.addAttribute("endEventListMobile", endEventListMobile);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/upm_news/event_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/upm_news/event_mobile";
		}

        return "fragments/page/upm_news/event";
    }

    @SuppressWarnings("deprecation")
	@ApiOperation(value = "업뮤직 밴드 템플릿을 반환", response = String.class)
    @GetMapping("/upm_band")
    public String upMusicBande(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/upm_news/upm_band", super.getCurrentUser(principal), request);

    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 10, new Sort(Direction.DESC, "createdAt"));

    	Page<BandRecruit> bandRecruitList = bandRecruitService.findBandRecruitList(pageable);

    	model.addAttribute("bandRecruitList", bandRecruitList);
    	
    	// 모바일
    	List<BandRecruit> bandRecruitListMobile = bandRecruitService.findBandRecruitList();

    	model.addAttribute("bandRecruitListMobile", bandRecruitListMobile);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/upm_news/upm_band_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/upm_news/upm_band_mobile";
		}
    	
        return "fragments/page/upm_news/upm_band";
    }

    /**
     * 공지사항 상세보기
     * @param model
     * @return
     */
    @GetMapping("/notice/detail")
    public String noticeDetail(Principal principal, Model model, HttpServletRequest request, HttpServletResponse response, 
    		@ModelAttribute(value = "paramData") Notice paramData) {
    	super.setSubMenu(model, "/upm_news/notice", super.getCurrentUser(principal), request);
    	Notice result = noticeService.findByNoticeId(paramData.getNoticeId());

    	// 쿠키 - 조회수 증가
		boolean existsCookie = false;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(UPMusicConstants.COOKIE_NOTICE + paramData.getNoticeId())) existsCookie = true;
			}
		}

        if (!existsCookie) {
        	if(result.getHitCnt() == null)
        		result.setHitCnt(1);
        	else
        		result.setHitCnt(result.getHitCnt()+1);
    		Cookie cookie = new Cookie(UPMusicConstants.COOKIE_NOTICE + paramData.getNoticeId(), "hit");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
        }

    	noticeService.updateHitCnt(result);

    	model.addAttribute("result", result);
    	//model.addAttribute("page", page);
    	return "fragments/page/upm_news/notice_detail";
    }

    /**
     * 공지사항 메뉴선택 이동
     * @return
     */
    @SuppressWarnings("deprecation")
	@GetMapping("/notice/menuSelect")
    public String noticeMenuSelect(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Notice paramData) {
    	super.setSubMenu(model, "/upm_news/notice", super.getCurrentUser(principal), request);

    	if("1".equals(paramData.getCategory())) {
    		paramData.setCategory("안내");
    	}else if("2".equals(paramData.getCategory())) {
    		paramData.setCategory("업데이트");
    	}else if("3".equals(paramData.getCategory())) {
    		paramData.setCategory("시스템 점검");
    	}
    	
    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 7, new Sort(Direction.DESC, "createdAt"));

    	Page<Notice> result = noticeService.findByCategory(paramData.getCategory(), pageable);

    	model.addAttribute("noticeList", result);
    	model.addAttribute("menuFlag", paramData.getCategory());
    	return "fragments/page/upm_news/notice";
    }

    /**
     * 이벤트 상세보기 페이지 이동
     * @return
     */
    @GetMapping("/event/detail")
    public String eventDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Event paramData) {
    	super.setSubMenu(model, "/upm_news/event", super.getCurrentUser(principal), request);

    	model.addAttribute("result", eventService.findByEventId(paramData.getEventId()));
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/upm_news/event_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/upm_news/event_detail_mobile";
		}
		
    	return "fragments/page/upm_news/event_detail";
    }


    /**
     * 업뮤직 밴드 등록 화면 이동
     */
    @GetMapping("/upm_band/insertForm")
    public String upmBandInsertForm(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/upm_news/upm_band", super.getCurrentUser(principal), request);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/upm_news/upm_band_insert_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/upm_news/upm_band_insert_mobile";
		}
    	
    	return "fragments/page/upm_news/upm_band_insert";
    }

    /**
     * 업뮤직 밴드 등록
     */
    @PostMapping("/upm_band/insert")
    public String upmBandInsert(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") BandRecruit paramData,
			Principal principal) {
    	super.setSubMenu(model, "/upm_news/upm_band", super.getCurrentUser(principal), request);

    	Member member = super.getCurrentUser(principal);
		paramData.setMember(member);

		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));

    	BandRecruit result = bandRecruitService.insertBandRecruit(paramData);

    	if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "upm_band/" + result.getBandRecruitId() + "/");
		}

    	return "redirect:/upm_news/upm_band";
    }

    /**
     * 업뮤직 밴드 상세보기
     */
    @SuppressWarnings("deprecation")
	@GetMapping("/upm_band/detail")
    public String upmBandDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") BandRecruit paramData) {
    	super.setSubMenu(model, "/upm_news/upm_band/detail", super.getCurrentUser(principal), request);

    	Member member = super.getCurrentUser(principal);

    	BandRecruit result = bandRecruitService.findByBandRecruitId(paramData.getBandRecruitId());

    	if(member != null && member.getId().equals(result.getMember().getId())) {
    		result.setRegistrantFlag(true);
    	}

    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(0, pageIndex + 5, new Sort(Direction.DESC, "createdAt"));

    	Page<BandRecruitComment> commentList = bandRecruitCommentService.findAllByBandRecruitId(result.getBandRecruitId(), pageable, member);

    	String path = request.getServletPath();
    	String query = request.getQueryString();
    	
    	if(query == null)
    		query = "";
    	
    	// 공유 
    	try {
			super.setMetaTag(model, result.getMetaTag(request));
			/*model.addAttribute("shareUrl", String.format("%s/music/artist/%d", UPMusicHelper.baseUrl(request), member.getId()));*/
			model.addAttribute("shareUrl", String.format("%s/upm_news/upm_band/detail?bandRecruitId=" + paramData.getBandRecruitId(), UPMusicHelper.baseUrl(request)));
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		}

		if (member != null) {
			model.addAttribute("currentUserId", member.getId());
			model.addAttribute("isOwner", 0 == (result.getMember().getId().compareTo(member.getId())));
		} else {
			model.addAttribute("currentUserId", 0);
			model.addAttribute("isOwner", false);
		}
		
    	
    	model.addAttribute("result", result);
    	model.addAttribute("commentList", commentList);
    	
    	List<BandRecruitComment> commentListMobile = bandRecruitCommentService.findAllByBandRecruitId(result.getBandRecruitId(), member);
    	model.addAttribute("commentListMobile", commentListMobile);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/upm_news/upm_band_detail_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/upm_news/upm_band_detail_mobile";
		}
    	
    	return "fragments/page/upm_news/upm_band_detail";
    }
    

    /**
     * 업뮤직 밴드 삭제
     */
    @GetMapping("/upm_band/delete")
    public String upmBandDelete(Principal principal, Model model, @ModelAttribute(value = "paramData") BandRecruit paramData) {

    	bandRecruitCommentService.deleteBandRecruitComment(paramData);
    	bandRecruitService.deleteBandRecruit(paramData);

    	return "redirect:/upm_news/upm_band";
    }

    /**
     * 업뮤직 밴드 수정 폼 이동
     */
    @GetMapping("/upm_band/updateForm")
    public String upmBandUpdateForm(Principal principal, Model model, @ModelAttribute(value = "paramData") BandRecruit paramData, HttpServletRequest request) {
    	super.setSubMenu(model, "/upm_news/upm_band", super.getCurrentUser(principal), request);

    	BandRecruit result = bandRecruitService.findByBandRecruitId(paramData.getBandRecruitId());
    	model.addAttribute("result", result);

		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/upm_news/upm_band_update_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			return "fragments/page/upm_news/upm_band_update_mobile";
		}

		return "fragments/page/upm_news/upm_band_update";
    }

    /**
     * 업뮤직 밴드 수정
     */
    @PostMapping("/upm_band/update")
    public String upmBandUpdate(Principal principal, Model model, @ModelAttribute(value = "paramData") BandRecruit paramData, HttpServletRequest request) {
    	super.setSubMenu(model, "/upm_news/upm_band", super.getCurrentUser(principal), request);

    	Member member = super.getCurrentUser(principal);
		paramData.setMember(member);
    	paramData.setUpdatedAt(new Date());

    	BandRecruit result = bandRecruitService.findByBandRecruitId(paramData.getBandRecruitId());
    	paramData.setFilename(result.getFilename());

    	if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
    		paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
    		azureStorageService.uploadResource(paramData.getThumbnailImg(), "upm_band/" + paramData.getBandRecruitId() + "/");
    	}

    	bandRecruitService.insertBandRecruit(paramData);

    	return "redirect:/upm_news/upm_band";
    }
    


    /**
     * 업뮤직 밴드 코멘트 작성
     */
    @PostMapping("/upm_band/comment_insert")
    public String upmBandCommentInsertAjax(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") BandRecruit paramData) {
    	Member member = super.getCurrentUser(principal);
    	BandRecruitComment paramVO = new BandRecruitComment();
    	paramVO.setBandRecruitId(paramData);
    	paramVO.setMember(member);
    	paramVO.setContent(paramData.getContent());

    	paramVO.setCreatedAt(new Date());
    	paramVO.setUpdatedAt(new Date());

		String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = request.getRemoteAddr();

        paramVO.setIp(ip);

        bandRecruitCommentService.insertBandRecruitComment(paramVO);

    	return "redirect:/upm_news/upm_band/detail?bandRecruitId=" + paramVO.getBandRecruitId().getBandRecruitId();
    }

    /**
     * 업뮤직 밴드 코멘트 삭제
     */
    @PostMapping("/upm_band/comment_delete")
    public String upmBandCommentDelete(Principal principal, Model model, HttpServletRequest request) {

    	Long bandRecruitId = Long.parseLong(String.valueOf(request.getParameter("bandRecruitId_sub")));
    	Long bandRecruitCommentId = Long.parseLong(String.valueOf(request.getParameter("bandRecruitCommentId")));

    	BandRecruitComment paramData = new BandRecruitComment();
    	paramData.setBandRecruitCommentId(bandRecruitCommentId);

    	bandRecruitCommentService.deleteBandRecruitCommetByBandRecruitCommentId(paramData);

    	return "redirect:/upm_news/upm_band/detail?bandRecruitId=" + bandRecruitId;
    }

    /**
     * 업뮤직 밴드 코멘트 수정
     */
    @PostMapping("/upm_band/comment_update")
    public String upmBandCommentUpdateAjax(Principal principal, Model model, HttpServletRequest request, @RequestParam(value="content") String content,
    		@RequestParam(value = "bandRecruitId") String bandRecruitId, @RequestParam(value="bandRecruitCommentId") String bandRecruitCommentId) {
    	Long bandRecruitValue = Long.parseLong(bandRecruitId);
    	Member member = super.getCurrentUser(principal);
    	BandRecruit bandRecruit = new BandRecruit();
    	bandRecruit.setBandRecruitId(bandRecruitValue);
    	BandRecruitComment paramData = new BandRecruitComment();
    	paramData.setUpdatedAt(new Date());
    	paramData.setContent(content);
    	paramData.setBandRecruitCommentId(Long.parseLong(bandRecruitCommentId));
    	paramData.setBandRecruitId(bandRecruit);
    	paramData.setMember(member);

		String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = request.getRemoteAddr();

        paramData.setIp(ip);

        bandRecruitCommentService.insertBandRecruitComment(paramData);

    	return "redirect:/upm_news/upm_band/detail?bandRecruitId=" + bandRecruitId;
    }
    
    /**
     * 댓글 등록 화면 이동 (모바일)
     */
    @GetMapping("/upm_band/commentInsertForm")
    public String upmBandCommentInsertFormMobile(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/comment", super.getCurrentUser(principal), request);
    	model.addAttribute("bandRecruitId", request.getParameter("bandRecruitId"));
    	
    	return "fragments/page/upm_news/upm_band_comment_insert_mobile";
    }
    
    /**
     * 업뮤직 밴드 댓글 수정 화면 이동 (모바일)
     */
    @GetMapping("/upm_band/commentUpdateForm")
    public String upmBandCommentUpdateForm(Principal principal, Model model, HttpServletRequest request, @RequestParam(value="bandRecruitCommentId") Long bandRecruitCommentId) {
    	super.setSubMenu(model, "/upm_news/upm_band", super.getCurrentUser(principal), request);
    	
    	BandRecruitComment result = bandRecruitCommentService.findByBandRecruitCommentId(bandRecruitCommentId);
    	
    	model.addAttribute("result", result);
    	return "fragments/page/upm_news/upm_band_comment_update_mobile";
    }
    

	@SuppressWarnings("deprecation")
	@ApiOperation(value = "FAQ 템플릿을 반환", response = String.class)
    @GetMapping("/faq")
    public String faq(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") FAQ paramData){
    	super.setSubMenu(model, "/upm_news/faq", super.getCurrentUser(principal), request);

    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 8, new Sort(Direction.DESC, "createdAt"));

    	if(paramData.getSearchText() == null)
    		paramData.setSearchText("");

    	if(paramData.getCategory() == null)
    		paramData.setCategory("");

    	Page<FAQ> faqList = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), paramData.getCategory(), pageable);

    	model.addAttribute("faqList", faqList);
    	model.addAttribute("searchText", paramData.getSearchText());
    	model.addAttribute("category", paramData.getCategory());
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			model.addAttribute("userAgent", userAgent);
			
			// 모바일용 조회
			List<FAQ> faqListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), paramData.getCategory());
			
			List<FAQ> uploadListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_UPLOAD);
			List<FAQ> copyrightListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_COPYRIGHT);
			List<FAQ> upleagueListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_UPLEAGUE);
			List<FAQ> musicstoreListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_MUSICSTORE);
			List<FAQ> artistListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_ARTIST);
			List<FAQ> paymentListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_PAYMENT);
			List<FAQ> calculateListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_CALCULATE);
			List<FAQ> rewardListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_REWARD);
			List<FAQ> pointListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_POINT);
			List<FAQ> otherListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_OTHER);
			
			
			model.addAttribute("faqListMobile", faqListMobile);
			model.addAttribute("uploadListMobile", uploadListMobile);
			model.addAttribute("copyrightListMobile", copyrightListMobile);
			model.addAttribute("upleagueListMobile", upleagueListMobile);
			model.addAttribute("musicstoreListMobile", musicstoreListMobile);
			model.addAttribute("artistListMobile", artistListMobile);
			model.addAttribute("paymentListMobile", paymentListMobile);
			model.addAttribute("calculateListMobile", calculateListMobile);
			model.addAttribute("rewardListMobile", rewardListMobile);
			model.addAttribute("pointListMobile", pointListMobile);
			model.addAttribute("otherListMobile", otherListMobile);
			
			return "fragments/page/upm_news/faq_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			
			// 모바일용 조회
			List<FAQ> faqListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), paramData.getCategory());
			
			List<FAQ> uploadListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_UPLOAD);
			List<FAQ> copyrightListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_COPYRIGHT);
			List<FAQ> upleagueListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_UPLEAGUE);
			List<FAQ> musicstoreListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_MUSICSTORE);
			List<FAQ> artistListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_ARTIST);
			List<FAQ> paymentListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_PAYMENT);
			List<FAQ> calculateListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_CALCULATE);
			List<FAQ> rewardListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_REWARD);
			List<FAQ> pointListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_POINT);
			List<FAQ> otherListMobile = FAQService.findByParentFaqIdIsNull(paramData.getSearchText(), UPMusicConstants.FAQ_OTHER);
			
			
			model.addAttribute("faqListMobile", faqListMobile);
			model.addAttribute("uploadListMobile", uploadListMobile);
			model.addAttribute("copyrightListMobile", copyrightListMobile);
			model.addAttribute("upleagueListMobile", upleagueListMobile);
			model.addAttribute("musicstoreListMobile", musicstoreListMobile);
			model.addAttribute("artistListMobile", artistListMobile);
			model.addAttribute("paymentListMobile", paymentListMobile);
			model.addAttribute("calculateListMobile", calculateListMobile);
			model.addAttribute("rewardListMobile", rewardListMobile);
			model.addAttribute("pointListMobile", pointListMobile);
			model.addAttribute("otherListMobile", otherListMobile);
			
			return "fragments/page/upm_news/faq_mobile";
		}
    	
        return "fragments/page/upm_news/faq";
    }
}

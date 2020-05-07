package com.upmusic.web.controllers.admin;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.upmusic.web.controllers.page.PageUpmNewsController;
import com.upmusic.web.domain.Event;
import com.upmusic.web.domain.EventBanner;
import com.upmusic.web.domain.FAQ;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.Notice;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.EventBannerService;
import com.upmusic.web.services.EventService;
import com.upmusic.web.services.FAQService;
import com.upmusic.web.services.NoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.util.StringUtils;


@Controller
@RequestMapping("/admin")
@Api(value = "admin", description = "관리자 영상 페이지를 담당하는 컨트롤러")
public class AdminHomeController extends AdminAbstractController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PageUpmNewsController pageUpmNewsController;

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private EventService eventService;

	@Autowired
	private EventBannerService eventBannerService;
	
	@Autowired
	private FAQService FAQService;

	@Autowired
	private AzureStorageService azureStorageService;

	@ApiOperation(value = "관리자 페이지의 메인 템플릿을 반환", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"), @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@GetMapping()
	public String admin(HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin");

		return "fragments/admin/page/home/index";
	}


	@GetMapping("/upm_news/notice")
	public String adminNotice(Principal principal, Model model, HttpServletRequest request) {
		super.setSubMenu(model, "/admin/upm_news/notice");

		pageUpmNewsController.notice(principal, model, request);

		return "fragments/admin/page/upm_news/notice";
	}


	/**
     * 공지사항 상세보기
     * @param model
     * @return
     */
    @GetMapping("/upm_news/notice/detail")
    public String adminNoticeDetail(Principal principal, Model model, HttpServletRequest request, HttpServletResponse response,@ModelAttribute(value = "paramData") Notice paramData) {
    	super.setSubMenu(model, "/admin/upm_news/notice");

    	pageUpmNewsController.noticeDetail(principal, model, request, response, paramData);

    	return "fragments/admin/page/upm_news/notice_detail";
    }

    /**
     * 공지사항 메뉴선택 이동
     * @return
     */
	@GetMapping("/upm_news/notice/menuSelect")
    public String adminNoticeMenuSelect(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Notice paramData) {
    	super.setSubMenu(model, "/admin/upm_news/notice");

    	pageUpmNewsController.noticeMenuSelect(principal, model, request, paramData);

    	return "fragments/admin/page/upm_news/notice";
    }


	/**
	 * 공지사항 등록 화면 이동
	 */
	@GetMapping("/upm_news/notice/insertForm")
	public String adminNoticeInsertForm(Model model) {
		super.setSubMenu(model, "/admin/upm_news/notice");

		return "fragments/admin/page/upm_news/notice_insert";
	}

	/**
	 * 공지사항 등록
	 */
	@PostMapping("/upm_news/notice/insert")
	public String adminNoticeInsert(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Notice paramData,
			Principal principal) {
		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);

		// createDate
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		paramData.setHitCnt(0);

		noticeService.insertNotice(paramData);

		return "redirect:/admin/upm_news/notice";
	}

	/**
	 * 공지사항 수정 화면 이동
	 */
	@GetMapping("/upm_news/notice/editForm")
	public String adminNoticeEditForm(Principal principal, Model model, @ModelAttribute(value = "paramData") Notice paramData) {
		super.setSubMenu(model, "/admin/upm_news/notice");

		logger.debug("# adminNoticeEditForm ");

		Notice result = noticeService.findByNoticeId(paramData.getNoticeId());

		model.addAttribute("result", result);

		return "fragments/admin/page/upm_news/notice_edit";
	}

	/**
	 * 공지사항 수정
	 */
	@PostMapping("/upm_news/notice/edit")
	public String adminNoticeEdit(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Notice paramData, Principal principal) {
		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);


		// createDate
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());

		noticeService.saveNotice(paramData);

		return "redirect:/admin/upm_news/notice";
	}

	/**
	 * 공지사항 삭제
	 */
	@GetMapping("/upm_news/notice/delete")
	public String adminNoticeDelete(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Notice paramData, Principal principal) {

		logger.debug("# adminNoticeDelete");
		noticeService.deleteNotice(paramData.getNoticeId());

		return "redirect:/admin/upm_news/notice";
	}




	/**
	 * 이벤트 리스트
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/upm_news/event")
	public String adminEvent(Principal principal, Model model, HttpServletRequest request) {

		//pageUpmNewsController.event(principal, model, request);
		
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
    	Page<Event> eventList = eventService.findAllNewEventForAdmin(today2, pageable);
    	Page<Event> endEventList = eventService.findByShowYnAndCloseAt(today2, pageable);

    	model.addAttribute("endEventList", endEventList);
    	model.addAttribute("eventList", eventList);

		return "fragments/admin/page/upm_news/event";
	}


	/**
	 * 이벤트 상세
	 */
	@GetMapping("/upm_news/event/detail")
	public String adminEventDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Event paramData) {

		pageUpmNewsController.eventDetail(principal, model, request, paramData);

		return "fragments/admin/page/upm_news/event_detail";
	}

	/**
	 * 이벤트 등록 화면 이동
	 */
	@GetMapping("/upm_news/event/eventInsertForm")
	public String adminEventInsertForm() {
		return "fragments/admin/page/upm_news/event_insert";
	}

	/**
	 * 이벤트 등록
	 */
	@PostMapping("/upm_news/event/insert")
	public String adminEventInsert(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Event paramData,
			Principal principal) {

		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			paramData.setOpenAt(sdf.parse(paramData.getEventStartDate()));
			paramData.setCloseAt(sdf.parse(paramData.getEventEndDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));

		Event saveResult = eventService.saveEvent(paramData);

		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "event/" + saveResult.getEventId() + "/");
		}

		return "redirect:/admin/upm_news/event";
	}

	/**
	 * 이벤트 수정 화면 이동
	 */
	@GetMapping("/upm_news/event/editForm")
	public String adminEventEditForm(Principal principal, Model model, @ModelAttribute(value = "paramData") Event paramData) {
		super.setSubMenu(model, "/admin/upm_news/event");

		logger.debug("# adminEventEditForm ");

		model.addAttribute("result", eventService.findByEventId(paramData.getEventId()));

		return "fragments/admin/page/upm_news/event_edit";
	}

	/**
	 * 이벤트 수정
	 */
	@PostMapping("/upm_news/event/edit")
	public String adminEventEdit(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Event paramData, Principal principal) {

		logger.debug("# adminEventEdit: "+paramData.toString());
		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			paramData.setOpenAt(sdf.parse(paramData.getEventStartDate()));
			paramData.setCloseAt(sdf.parse(paramData.getEventEndDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());


		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "event/" + paramData.getEventId() + "/");
		}
		//logger.debug("# adminEventEdit: "+paramData.toString());
		Event saveResult = eventService.saveEvent(paramData);

		return "redirect:/admin/upm_news/event";
	}

	/**
	 * 이벤트 삭제
	 */
	@GetMapping("/upm_news/event/delete")
	public String adminEventDelete(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Event paramData, Principal principal) {

		logger.debug("# adminEventDelete");
		eventService.deleteEvent(paramData.getEventId());

		return "redirect:/admin/upm_news/event";
	}

	/**
	 * 이벤트 베너 리스트
	 */
	@GetMapping("/upm_news/event_banner")
	public String adminEventBanner(Principal principal, Model model, HttpServletRequest request) {
		super.setSubMenu(model, "/admin/upm_news/event_banner");
		//pageUpmNewsController.event(principal, model, request);

    	int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 5, new Sort(Direction.ASC, "orderNo"));

    	Page<EventBanner> eventBannerList = eventBannerService.findEventList(pageable);

    	model.addAttribute("eventBannerList", eventBannerList);

		return "fragments/admin/page/upm_news/event_banner";
	}

	/**
	 * 이벤트 베너 등록 화면 이동
	 */
	@GetMapping("/upm_news/event_banner/insertForm")
	public String adminEventBannerInsertForm() {
		return "fragments/admin/page/upm_news/event_banner_insert";
	}

	/**
	 * 이벤트 베너 등록
	 */
	@PostMapping("/upm_news/event_banner/insert")
	public String adminEventBannerInsert(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") EventBanner paramData, Principal principal) {

		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);

		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));

		EventBanner saveResult = eventBannerService.save(paramData);

		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "event_banner/" + saveResult.getEventBannerId() + "/");
		}


		return "redirect:/admin/upm_news/event_banner";
	}

	/**
	 * 이벤트 베너 수정 화면 이동
	 */
	@GetMapping("/upm_news/event_banner/editForm")
	public String adminEventBannerEditForm(Principal principal, Model model, @ModelAttribute(value = "paramData") EventBanner paramData) {
		super.setSubMenu(model, "/admin/upm_news/event_banner");

		logger.debug("# adminEventBannerEditForm ");

		EventBanner result = eventBannerService.findByEventBannerId(paramData.getEventBannerId());

		model.addAttribute("result", result);

		return "fragments/admin/page/upm_news/event_banner_edit";
	}

	/**
	 * 이벤트 베너 수정
	 */
	@PostMapping("/upm_news/event_banner/edit")
	public String adminEventBannerEdit(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") EventBanner paramData, Principal principal) {

		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);

		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());

		logger.debug("# adminEventBannerEdit :"+paramData.getFilename());

		if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
			paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
			azureStorageService.uploadResource(paramData.getThumbnailImg(), "event_banner/" + paramData.getEventBannerId() + "/");
		}

		logger.debug("# adminEventBannerEdit :"+paramData.getFilename());
		EventBanner saveResult = eventBannerService.save(paramData);


		return "redirect:/admin/upm_news/event_banner";
	}

	/**
	 * 이벤트 베너 삭제
	 */
	@GetMapping("/upm_news/event_banner/delete")
	public String adminEventBannerDelete(Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") EventBanner paramData, Principal principal) {

		logger.debug("# adminEventBannerDelete");
		eventBannerService.delete(paramData.getEventBannerId());

		return "redirect:/admin/upm_news/event_banner";
	}
	
	/**
	 * faq 화면 이동
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/upm_news/faq")
	public String adminFaq(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") FAQ paramData) {
		super.setSubMenu(model, "/admin/upm_news/faq");
		
		int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(pageIndex, 8, new Sort(Direction.DESC, "createdAt"));
    	
    	if(paramData.getSearchText() == null)
    		paramData.setSearchText("");
    	
    	if(paramData.getCategory() == null)
    		paramData.setCategory("");
    	
    	Page<FAQ> faqList = FAQService.findByParentFaqIdIsNullForAdmin(paramData.getSearchText(), paramData.getCategory(), pageable);
    	
    	model.addAttribute("faqList", faqList);
    	model.addAttribute("searchText", paramData.getSearchText());
    	model.addAttribute("category", paramData.getCategory());
		
		return "fragments/admin/page/upm_news/faq";
	}
	
	/**
	 * faq 등록 화면 이동
	 */
	@GetMapping("/upm_news/faq/insertForm")
	public String adminFaqInsertForm(Principal principal, Model model, HttpServletRequest request) {
		super.setSubMenu(model, "/admin/upm_news/faq");
		
		return "fragments/admin/page/upm_news/faq_insert";
	}
	
	/**
	 * faq 등록
	 */
	@PostMapping("/upm_news/faq/insert")
	public String adminFaqInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") FAQ paramData) {
		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);
		paramData.setCreatedAt(new Date());
		paramData.setUpdatedAt(new Date());
		
		FAQ result = FAQService.save(paramData);
		
		// 답변 등록
		FAQ answer = new FAQ();
		answer.setContent(paramData.getAnswerContent());
		answer.setMember(member);
		answer.setCreatedAt(new Date());
		answer.setUpdatedAt(new Date());
		answer.setParentFaqId(result.getFaqId());
		
		FAQService.save(answer);
		
		return "redirect:/admin/upm_news/faq";
	}
	
	/**
	 * faq 상세 보기
	 */
	@GetMapping("/upm_news/faq/detail")
	public String adminFaqDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") FAQ paramData) {
		FAQ result = FAQService.findByFaqId(paramData.getFaqId());
		
		model.addAttribute("result", result);
		return "fragments/admin/page/upm_news/faq_detail";
	}

	/**
	 * faq 수정
	 */
	@PostMapping("/upm_news/faq/update")
	public String adminFaqUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") FAQ paramData) {
		Member member = super.getCurrentUser(principal);
		paramData.setMember(member);
		paramData.setUpdatedAt(new Date());
		
		FAQService.save(paramData);
		
		// 답변 수정
		FAQ answer = FAQService.findByParentFaqId(paramData.getFaqId());
		answer.setContent(paramData.getAnswerContent());
		answer.setMember(member);
		answer.setUpdatedAt(new Date());
		
		FAQService.save(answer);
		
		return "redirect:/admin/upm_news/faq";
	}
	
	/**
	 * faq 삭제
	 */
	@GetMapping("/upm_news/faq/delete")
	public String adminFaqDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") FAQ paramData) {
		
		// 답변 삭제
		FAQ answer = FAQService.findByParentFaqId(paramData.getFaqId());
		FAQService.delete(answer.getFaqId());
		
		// faq 삭제
		FAQService.delete(paramData.getFaqId());
		
		return "redirect:/admin/upm_news/faq";
	}

}

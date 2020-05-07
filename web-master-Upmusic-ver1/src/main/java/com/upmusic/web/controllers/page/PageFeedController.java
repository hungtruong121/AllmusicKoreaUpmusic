package com.upmusic.web.controllers.page;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.*;
import com.upmusic.web.message.PointTransactionResponse;
import com.upmusic.web.services.*;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.upmusic.web.helper.UPMusicHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Controller
@RequestMapping("/feed")
@Api(value="feed", description="피드 페이지를 담당하는 컨트롤러")
public class PageFeedController extends PageAbstractController {
	
	@Autowired
	private FeedService feedService;
	
	@Autowired
	private FeedFileService feedFileService;
	
	@Autowired
	private FeedCommentService feedCommentService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private FeedLikeService feedLikeService;
	
	@Autowired
	private AzureStorageService azureStorageService;

	@Autowired
	private PointRewardConditionService pointRewardConditionService;

	@Autowired
	private PointTransactionService pointTransactionService;

    @SuppressWarnings("deprecation")
	@ApiOperation(value = "피드 페이지의 메인 템플릿을 반환",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
    @GetMapping()
    public String page(Principal principal, Model model, HttpServletRequest request){
    	super.setSubMenu(model, "/feed", super.getCurrentUser(principal), request);
    	Member member = super.getCurrentUser(principal);
    	
    	int feedHomePage = 0;
    	int myFeedPage = 0;
    	int allFeedPage = 0;
    	if (!StringUtils.isEmpty(request.getParameter("feedHomePage"))) feedHomePage = Integer.valueOf(request.getParameter("feedHomePage"));
    	if (!StringUtils.isEmpty(request.getParameter("myFeedPage"))) myFeedPage = Integer.valueOf(request.getParameter("myFeedPage"));
    	if (!StringUtils.isEmpty(request.getParameter("allFeedPage"))) allFeedPage = Integer.valueOf(request.getParameter("allFeedPage"));
    	PageRequest feedHomePageable = new PageRequest(0, 5 + feedHomePage, new Sort(Direction.DESC, "created_at"));
    	PageRequest myFeedPageable = new PageRequest(0, 5 + myFeedPage, new Sort(Direction.DESC, "createdAt"));
    	PageRequest allFeedPageable = new PageRequest(0, 5 + allFeedPage, new Sort(Direction.DESC, "created_at"));
    	PageRequest commentPageable = new PageRequest(0, 5, new Sort(Direction.DESC, "createdAt"));
    	PageRequest likerPageable = new PageRequest(0, 8, new Sort(Direction.DESC, "created_at"));
    	
    	// 피드 홈 조회 (좋아요 표시한 아티스트의 피드만 조회)
    	Page<Feed> feedHomeList = feedService.findByMemberIdAndLiker(member.getId(), feedHomePageable, commentPageable);
    	feedLikeStr(feedHomeList, member);
    	
    	// 나의 피드 조회
    	Page<Feed> myFeedList = feedService.findByMemberId(member.getId(), myFeedPageable, commentPageable);
    	feedLikeStr(myFeedList, member);
    	
    	// 전체 피드 조회
    	Page<Feed> allFeedList = feedService.findAll(member.getId(), allFeedPageable, commentPageable);
    	feedLikeStr(allFeedList, member);
    	
    	// 좋아요 리스트
    	List<Member> likeList = memberService.findAllByLikerId(member.getId(), likerPageable);
    	
    	String path = request.getServletPath();
    	String query = request.getQueryString();
    	
    	if(query == null)
    		query = "";
    	
    	// 공유
		try {
			super.setMetaTag(model, member.getMetaTag(request));
			//model.addAttribute("shareUrl", String.format("%s" + path + query + "%d", UPMusicHelper.baseUrl(request), member.getId()));
			model.addAttribute("shareUrl", String.format("%s/feed/share", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	model.addAttribute("feedHomeList", feedHomeList);
    	model.addAttribute("myFeedList", myFeedList);
    	model.addAttribute("allFeedList", allFeedList);
    	model.addAttribute("likeList", likeList);
    	model.addAttribute("id", member.getId());
    	
    	// mobile
    	List<Feed> feedHomeListMobile = feedService.findByMemberIdAndLiker(member.getId());
    	feedLikeStrMobile(feedHomeListMobile, member);
    	List<Feed> myFeedListMobile = feedService.findByMemberId(member.getId());
    	feedLikeStrMobile(myFeedListMobile, member);
    	List<Feed> allFeedListMobile = feedService.findAll(member.getId());
    	feedLikeStrMobile(allFeedListMobile, member);
    	
    	model.addAttribute("feedHomeListMobile", feedHomeListMobile);
    	model.addAttribute("myFeedListMobile", myFeedListMobile);
    	model.addAttribute("allFeedListMobile", allFeedListMobile);
    	
    	String userAgent = request.getHeader("user-agent");
		if (userAgent != null && (userAgent.equals("UPMusicAndroid") || userAgent.equals("UPMusicIOS"))) {
			// 공유
			try {
				super.setMetaTag(model, member.getMetaTag(request));
				model.addAttribute("shareUrl", String.format("%s/feed/feedImgDetail", UPMusicHelper.baseUrl(request)));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			model.addAttribute("userAgent", userAgent);
			return "fragments/page/feed/feed_mobile";
		} else if (userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"))) {
			// 공유
			try {
				super.setMetaTag(model, member.getMetaTag(request));
				model.addAttribute("shareUrl", String.format("%s/feed/feedImgDetail", UPMusicHelper.baseUrl(request)));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "fragments/page/feed/feed_mobile";
		}
    	
    	return "fragments/page/feed/index";
    }
    
	@ApiOperation(value = "나의 피드 템플릿을 반환", response = String.class)
    @GetMapping("/me")
    public String me(Principal principal, Model model, HttpServletRequest request){
        return page(principal, model, request);
    }
    
    @ApiOperation(value = "전체 UPM 피드 템플릿을 반환", response = String.class)
    @GetMapping("/upm")
    public String upm(Principal principal, Model model, HttpServletRequest request){
        return page(principal, model, request);
    }
    
    /**
     * 피드 등록 
     * @return
     */
    @PostMapping("/feedInsert")
    public String feedInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData, 
    		@RequestParam(value = "feedImg") List<MultipartFile> feedImg) {
    	Member member = super.getCurrentUser(principal);
    	paramData.setMember(member);
    	paramData.setCreatedAt(new Date());
    	paramData.setUpdatedAt(new Date());
    	paramData.setPublicRangeForFormValue();
    	paramData.setCommentCnt(0);
    	paramData.setLikeCnt(0);

    	Feed result = feedService.insertFeed(paramData);
    	
    	for(MultipartFile multipartFile : feedImg) {
    		FeedFile feedFileVO = new FeedFile();
			feedFileVO.setFilename(UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
			feedFileVO.setFeed(result);
			
			FeedFile feedFileResult = feedFileService.insertFeedFile(feedFileVO);
			azureStorageService.uploadResource(multipartFile, "feed/" + feedFileResult.getFeedFileId() + "/");
    	}
    	
    	return "redirect:/feed/me#my_feed";
    }
    
    /**
     * 피드 삭제
     */
    @PostMapping("/feedDelete")
    public String feedDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData) {
    	
    	feedCommentService.deleteByFeedId(paramData.getFeedId());
    	feedFileService.deleteByFeedId(paramData.getFeedId());
    	feedLikeService.deleteByFeedId(paramData.getFeedId());
    	feedService.deleteFeed(paramData);
    	
    	return "redirect:/feed/me#my_feed";
    }
    
    /**
     * 피드 수정
     */
    @PostMapping("/feedUpdate")
    public String feedUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData, 
    		@RequestParam(value = "feedImg_sub") List<MultipartFile> feedImg) {
    	
    	Member member = super.getCurrentUser(principal);
    	paramData.setMember(member);
    	paramData.setUpdatedAt(new Date());
    	paramData.setPublicRangeForFormValue();
    	
    	int commentCnt = feedCommentService.countByFeedId(paramData.getFeedId());
        paramData.setCommentCnt(commentCnt);
        
    	Feed result = feedService.insertFeed(paramData);
    	
    	/*if(feedImg.size() > 0) {
    		feedFileService.deleteByFeedId(paramData.getFeedId());
    	}*/
    	
    	// 수정시 파일을 선택하지 않아도 데이터가없는 MultipartFile 객체가 하나 넘어오기 때문에 아래와 같은 방식으로 변경 (이유는 찾지 못함)
    	boolean flag = false;
    	for(MultipartFile multipartFile : feedImg) {
    		if(!multipartFile.getOriginalFilename().equals("")) {
    			flag = true;
    		}
    	}
    	
    	if(flag) {
    		feedFileService.deleteByFeedId(paramData.getFeedId());
    	}
    	
    	for(MultipartFile multipartFile : feedImg) {
    		FeedFile feedFileVO = new FeedFile();
			feedFileVO.setFilename(UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
			feedFileVO.setFeed(result);
			
			FeedFile feedFileResult = feedFileService.insertFeedFile(feedFileVO);
			azureStorageService.uploadResource(multipartFile, "feed/" + feedFileResult.getFeedFileId() + "/");
    	}
    	
    	
    	return "redirect:/feed/me#my_feed";
    }
    
    // 피드 공개 범위 수정
    @GetMapping("/feedPublicRangeUpdate")
    public String feedPublicRangeUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData) {
    	
    	Member member = super.getCurrentUser(principal);
    	Feed orgFeed = feedService.findByFeedId(paramData.getFeedId());
    	orgFeed.setMember(member);
    	orgFeed.setUpdatedAt(new Date());
    	orgFeed.setPublicRangeFormValue(paramData.getPublicRangeFormValue());
    	orgFeed.setPublicRangeForFormValue();
    	
    	int commentCnt = feedCommentService.countByFeedId(paramData.getFeedId());
    	orgFeed.setCommentCnt(commentCnt);
        
    	feedService.insertFeed(orgFeed);
    	
    	return "redirect:/feed/me#my_feed";
    }
    
    // 피드 댓글 등록 화면 이동 (모바일)
    @GetMapping("/feedCommentInsertForm")
    public String feedCommentInsertForm(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/comment", super.getCurrentUser(principal), request);
    	
    	model.addAttribute("feedId", request.getParameter("feedId"));
    	
    	return "fragments/page/feed/feed_comment_insert_mobile";
    }
    
    // 피드 댓글 등록 (모바일)
    @PostMapping("/feedCommentInsert")
    public String feedCommentInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Feed paramData) {
    	
    	Member member = super.getCurrentUser(principal);
		FeedComment feedComment = new FeedComment();
		feedComment.setMember(member);
		feedComment.setFeed(paramData);
		feedComment.setContent(paramData.getContent().replaceAll(System.getProperty("line.separator"), "<br>"));
		
		feedComment.setCreatedAt(new Date());
		feedComment.setUpdatedAt(new Date());
    	
		String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = request.getRemoteAddr();
        
        feedComment.setIp(ip);
        
        feedCommentService.insertFeedComment(feedComment);
        
        int commentCnt = feedCommentService.countByFeedId(paramData.getFeedId());
        paramData.setCommentCnt(commentCnt);
        paramData.setMember(member);
        paramData.setUpdatedAt(new Date());
        
        feedService.updateByFeedId(paramData.getFeedId(), commentCnt);
        
    	return "redirect:/feed";
    }
    
    // 피드 댓글 수정 화명 이동(모바일)
    @GetMapping("/feedCommentUpdateForm")
    public String feedCommentUpdateForm(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/feed/me", super.getCurrentUser(principal), request);
    	FeedComment result = feedCommentService.findByFeedCommentId(Long.parseLong(request.getParameter("feedCommentId")));
    	
    	model.addAttribute("result", result);
    	return "fragments/page/feed/feed_comment_update_mobile";
    }
    
    // 피드 댓글 수정 (모바일)
    @PostMapping("/feedCommentUpdate")
    public String feedCommentUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") FeedComment paramData,
    		@RequestParam(value = "feedId") Long feedId) {
    	Member member = super.getCurrentUser(principal);
		
		String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = request.getRemoteAddr();
        
        paramData.setIp(ip);
        paramData.setUpdatedAt(new Date());
        paramData.setMember(member);
        Feed feed = new Feed();
        feed.setFeedId(feedId);
        paramData.setFeed(feed);
        paramData.setContent(paramData.getContent().replaceAll(System.getProperty("line.separator"), "<br>"));
        
        feedCommentService.insertFeedComment(paramData);
		
    	return "redirect:/feed";
    }
    
    // 피드 이미지 상세보기 (모바일)
    @GetMapping("/feedImgDetail")
    public String feedImgDetail(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Feed paramData) {
    	super.setSubMenu(model, "/feed/me", super.getCurrentUser(principal), request);
    	Member member = super.getCurrentUser(principal);
    	
    	List<FeedFile> result = feedFileService.findAllByFeedIdDESC(paramData.getFeedId());
    	
    	for(FeedFile f : result) {
    		f.setFeed(null);
    		f.setImgSrcText(f.getImgUrl());
    	}
    	Feed feed = feedService.findByFeedId(paramData.getFeedId(), member.getId());
    	int cnt = feedLikeService.findByFeedIdAndMemberId(feed.getFeedId(), member.getId());
    	feed.setFeedLikeClassMobile(cnt);
    	
    	try {
			super.setMetaTag(model, member.getMetaTag(request));
			model.addAttribute("shareUrl", String.format("%s/feed/feedImgDetail", UPMusicHelper.baseUrl(request)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	model.addAttribute("result", result);
    	model.addAttribute("feed", feed);
    	return "fragments/page/feed/feed_img_detail_mobile";
    }
    
    // 피드 수정 페이지 (모바일)
    @GetMapping("/feedUpdateForm")
    public String feedUpdateForm(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Feed paramData) {
    	super.setSubMenu(model, "/feed/me", super.getCurrentUser(principal), request);
    	Feed result = feedService.findByFeedId(paramData.getFeedId());
    	
    	model.addAttribute("result", result);
    	return "fragments/page/feed/feed_update_mobile";
    }
    
    // 피드 공유 페이지
    /*@SuppressWarnings("deprecation")
	@GetMapping("/feedShare")
    public String feedShare(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData) {
    	Member member = super.getCurrentUser(principal);
    	Long memberId = 0L;
    	
    	if(member != null) {
    		memberId = member.getId();
    	}
    	
    	Feed result = feedService.findByFeedId(paramData.getFeedId());
    	PageRequest commentPageable = new PageRequest(0, 3, new Sort(Direction.DESC, "createdAt"));
    	
    	result.setFeedFileList(feedFileService.findAllByFeedId(result.getFeedId()));
    	result.setFeedCommentListDate(feedCommentService.findAllByFeedId(result.getFeedId(), commentPageable, memberId));
    	
    	model.addAttribute("result", result);
    	return "fragments/page/feed/feed_link";
    }*/
    
    // 공유 링크
    @SuppressWarnings("deprecation")
	@GetMapping("/share/{id}")
    public String feedShareLink(@PathVariable Long id, Principal principal, Model model, HttpServletRequest request) {
    	Member member = super.getCurrentUser(principal);
    	Long memberId = 0L;
    	super.setSubMenu(model, "/feed/share", member, request);
    	
    	if(member != null) {
    		memberId = member.getId();
    	}

		Feed result = feedService.findByFeedId(id);

    	try {
			super.setMetaTag(model, result.getMetaTag(request));
			//model.addAttribute("shareUrl", String.format("%s" + path + query + "%d", UPMusicHelper.baseUrl(request), member.getId()));
			model.addAttribute("shareUrl", String.format("%s/feed/share/%d", UPMusicHelper.baseUrl(request), id));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

    	PageRequest commentPageable = new PageRequest(0, 3, new Sort(Direction.DESC, "createdAt"));
    	
    	result.setFeedFileList(feedFileService.findAllByFeedId(result.getFeedId()));
    	result.setFeedCommentListDate(feedCommentService.findAllByFeedId(result.getFeedId(), commentPageable, memberId));
    	
    	model.addAttribute("result", result);
    	return "fragments/page/feed/feed_link";
    }
    
    // 업뮤직 피드 공유
    @PostMapping("/share/insert")
    public String feedShareInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData,
    		@RequestParam("shareUrl") String shareUrl) {

		String referrer = request.getHeader("Referer");

    	Member member = super.getCurrentUser(principal);
    	paramData.setMember(member);
    	paramData.setCreatedAt(new Date());
    	paramData.setUpdatedAt(new Date());
    	paramData.setPublicRangeForFormValue();
    	paramData.setCommentCnt(0);
    	paramData.setLikeCnt(0);
    	
    	paramData.setContent(paramData.getContent() + "<p><br><a style='text-decoration:underline;' href='" + shareUrl + "'>" + shareUrl + "</a>");
    	
    	feedService.insertFeed(paramData);

    	//공유리워드 적립
		PointRewardCondition condition = pointRewardConditionService.getCondition();
		//PointTransactionResponse pointTransactionResponse = pointTransactionService.accumulatePoints(member.getId(), UPMusicConstants.PointTransactionType.SHARE, null, condition.getSharePoint());

		int shareLimit = condition.getShareLimit();

		//현재날짜
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		Date time = new Date();
		String sysDate = date.format(time);

		//하루에 shareLimit만큼만 리워드 증가
		try {
			if (pointTransactionService.getDailyShareTransaction(member.getId(),sysDate).size() <= shareLimit) {
				PointTransactionResponse pointTransactionResponse = pointTransactionService.accumulatePoints(member.getId(), UPMusicConstants.PointTransactionType.SHARE, null, condition.getSharePoint());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

    	//return "redirect:/feed/me#my_feed";
		return "redirect:"+referrer;
    }
    
    // 피드 등록 화면 이동 (모바일)
    @GetMapping("/feedInsertForm")
    public String feedInsertForm(Model model, Principal principal, HttpServletRequest request) {
    	super.setSubMenu(model, "/feed/me", super.getCurrentUser(principal), request);
    	
    	return "fragments/page/feed/feed_insert_mobile";
    }
    
    // 좋아요 리스트 화면 이동 (모바일)
    @GetMapping("/feedLikeList")
    public String feedLikeList(Model model, Principal principal, HttpServletRequest request) {
    	super.setSubMenu(model, "/feed/me", super.getCurrentUser(principal), request);
    	Member member = super.getCurrentUser(principal);
    	
    	List<Member> likeList = memberService.findAllByLikerIdMobile(member.getId());
    	
    	model.addAttribute("likeList", likeList);
    	return "fragments/page/feed/feed_likeList_mobile";
    }
    
    public void feedLikeStr(Page<Feed> list, Member member) {
    	//feedLikeService
    	for(Feed f : list.getContent()) {
    		int cnt = feedLikeService.findByFeedIdAndMemberId(f.getFeedId(), member.getId());
    		f.setFeedLikeClass(cnt);
    		f.setFeedLikeImgUrl(cnt);
    	}
    }
    
    public void feedLikeStrMobile(List<Feed> list, Member member) {
    	for(Feed f : list) {
    		int cnt = feedLikeService.findByFeedIdAndMemberId(f.getFeedId(), member.getId());
    		f.setFeedLikeClassMobile(cnt);
    	}
    }
    
}

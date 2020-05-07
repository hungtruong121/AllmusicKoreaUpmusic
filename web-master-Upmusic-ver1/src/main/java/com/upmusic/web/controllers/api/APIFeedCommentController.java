package com.upmusic.web.controllers.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.upmusic.web.controllers.page.PageFeedController;
import com.upmusic.web.domain.Feed;
import com.upmusic.web.domain.FeedComment;
import com.upmusic.web.domain.FeedFile;
import com.upmusic.web.domain.FeedLike;
import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.FeedCommentService;
import com.upmusic.web.services.FeedFileService;
import com.upmusic.web.services.FeedLikeService;
import com.upmusic.web.services.FeedService;
import com.upmusic.web.services.MemberService;

@RestController
@RequestMapping("/api/feed")
public class APIFeedCommentController extends APIAbstractController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private FeedCommentService FeedCommentService;
	
	@Autowired
	private FeedService feedService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private FeedFileService feedFileService;
	
	@Autowired
	private FeedCommentService feedCommentService;
	
	@Autowired
	private FeedLikeService feedLikeService;
	
	@Autowired
	private PageFeedController pageFeedController;
	
	@Autowired
	private AzureStorageService azureStorageService;
	
	@Autowired
	private Gson gson;
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/comment")
	@ResponseBody
	public String findFeedComment(Principal principal, Model model, HttpServletRequest request, @RequestParam("feedId") Long feedId) {
		
		int pageIndex = 0;
    	if (!StringUtils.isEmpty(request.getParameter("page"))) pageIndex = Integer.valueOf(request.getParameter("page"));
    	PageRequest pageable = new PageRequest(0, 5, new Sort(Direction.DESC, "createdAt"));
    	
    	//Page<FeedComment> result = FeedCommentService.findAllByFeedId(feedId, pageable);
    	
		return gson.toJson("");
	}
	
	/**
     * 피드 댓글 등록
     * @param principal
     * @param model
     * @param request
     * @param paramData
     * @return
     */
    @SuppressWarnings("deprecation")
	@RequestMapping(value = "/comment_insert", method=RequestMethod.POST)
    public Map<String, Object> feedCommentInsertAjax(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") Feed paramData, 
    		@RequestParam("pageSize") int pageSize) {
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
        
        FeedCommentService.insertFeedComment(feedComment);
        
        PageRequest pageable = new PageRequest(0, 5 + pageSize, new Sort(Direction.DESC, "createdAt"));
        Page<FeedComment> result = FeedCommentService.findAllByFeedId(paramData.getFeedId(), pageable, member.getId());
        int commentCnt = FeedCommentService.countByFeedId(paramData.getFeedId());
        paramData.setCommentCnt(commentCnt);
        paramData.setMember(member);
        paramData.setUpdatedAt(new Date());
        
        feedService.updateByFeedId(paramData.getFeedId(), commentCnt);
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("commentList", result);
        resultMap.put("feedId", paramData.getFeedId());
        resultMap.put("commentCnt", commentCnt);
        resultMap.put("commentTotalPage", result.getTotalPages());
        
    	return resultMap;
    }
	
    /**
     * 피드 댓글 삭제
     * @param principal
     * @param model
     * @param request
     * @param paramData
     * @param feedId
     * @return
     */
	@RequestMapping(value = "/comment_delete", method=RequestMethod.POST)
    public Map<String, Object> feedCommentDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") FeedComment paramData,
    		@RequestParam(value = "feedId") Long feedId, @RequestParam("pageSize") int pageSize){
    	Member member = super.getCurrentUser(principal);
    	
    	FeedCommentService.deleteByFeedCommentId(paramData.getFeedCommentId());
    	
    	Map<String, Object> resultMap = feedCntUpdate(feedId, member, pageSize);
    	return resultMap;
    }
	
	@RequestMapping(value = "/comment_deleteMobile", method=RequestMethod.POST)
    public Map<String, Object> feedCommentDeleteMobile(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") FeedComment paramData,
    		@RequestParam(value = "feedId") Long feedId){
    	Member member = super.getCurrentUser(principal);
    	
    	FeedCommentService.deleteByFeedCommentId(paramData.getFeedCommentId());
    	
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	
   	 	System.out.println("######################### 3 " + feedId + "/" + member.getId());
   	
   	 	List<FeedComment> result = FeedCommentService.findAllByFeedId(feedId, member.getId());
   	 	int commentCnt = FeedCommentService.countByFeedId(feedId);
       
   	 	System.out.println("######################### 4 " + result);
   	 	System.out.println("######################### 5 " + commentCnt);
       
   	 	Feed feed = new Feed();
   	 	feed.setCommentCnt(commentCnt);
   	 	feed.setMember(member);
   	 	feed.setUpdatedAt(new Date());
       
   	 	feedService.updateByFeedId(feedId, commentCnt);
       
   	 	resultMap.put("commentList", result);
   	 	resultMap.put("feedId", feedId);
   	 	resultMap.put("commentCnt", commentCnt);
    	
    	return resultMap;
    }
	
	/**
	 * 피드 댓글 수정
	 * @param principal
	 * @param model
	 * @param request
	 * @param paramData
	 * @param feedId
	 * @return
	 */
	@RequestMapping(value = "/comment_update", method=RequestMethod.POST)
	public Map<String, Object> feedCommentUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute(value = "paramData") FeedComment paramData,
    		@RequestParam(value = "feedId") Long feedId){
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
        
        FeedCommentService.insertFeedComment(paramData);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("content", paramData.getContent());
    	return resultMap;
	}
	
	/**
	 * 피드 댓글 페이징 이동
	 * @param principal
	 * @param model
	 * @param request
	 * @param feedId
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/comment_paging")
	public Map<String, Object> feedCommentPaging(Principal principal, Model model, HttpServletRequest request, @RequestParam("feedId") Long feedId,
			@RequestParam("pageSize") int pageSize){
		Member member = super.getCurrentUser(principal);
		Long memberId = 0l;
		if(member != null) memberId = member.getId();
		
		PageRequest pageable = new PageRequest(0, 5 + pageSize, new Sort(Direction.DESC, "createdAt"));
    	Page<FeedComment> result = FeedCommentService.findAllByFeedId(feedId, pageable, memberId);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int commentCnt = FeedCommentService.countByFeedId(feedId);
		resultMap.put("commentList", result);
		resultMap.put("pageSize", 5 + pageSize);
		resultMap.put("feedId", feedId);
        resultMap.put("commentCnt", commentCnt);
        resultMap.put("hasNext", result.hasNext());
		return resultMap;
	}
    
	/**
	 * 아티스트 검색
	 */
	@RequestMapping(value = "/artist_find", method=RequestMethod.POST)
	public Map<String, Object> artistFind(Principal principal, @ModelAttribute("paramData") Member paramData){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Member member = super.getCurrentUser(principal);
		List<Member> result = memberService.findAllByNickName(paramData.getNick(), member.getId());
		for(Member m : result) {
			try {
				m.setImgUrl(m.getProfileImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		resultMap.put("result", result);
		return resultMap;
	}
	
	/**
	 * 아티스트 좋아요 적용 / 해제
	 */
	@RequestMapping(value = "/artist_like_toggle", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> artistLikeToggle(Principal principal, @RequestParam("artist_id") Long artist_id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		//int checkIsLike = memberService.countIsLiker(artist_id, member.getId());
		String result = "";
		
		/*if(checkIsLike > 0) {
			memberService.deleteMemberLike(artist_id, member.getId());
			result = "0";
		}else {
			memberService.saveMemberLike(artist_id, member.getId());
			result = "1";
		}*/
		
		JsonParser parser = new JsonParser();
    	JsonElement element = null;
    	String params = "{}";
    	if (params != null) element = parser.parse(params);
		Member member = super.getCurrentUser(principal);
		// 모바일앱에서 토큰을 이용한 인증처리를 요청할 경우
		if (member == null && element != null && element.getAsJsonObject().has("token")) member = super.getCurrentUser(element.getAsJsonObject().get("token").getAsString());
    	if (member != null) {
    		logger.debug("likeMemberFromList : userId is {}", member.getId());
    		if (0 == member.getId().compareTo(artist_id)) {
    			//response = new APIResponse(true, super.getMessage("common.yourself"), null);
    		} else {
    			List<Object> obj = new ArrayList<Object>();
	    		obj.add(artist_id);
	        	if (memberService.likedMember(artist_id, member.getId())) {
	        		obj.add(memberService.decreaseHeartCnt(artist_id, member));
	        		//response = new APIResponse(true, "false", obj);
	        		result = "0";
	        	} else {
	        		obj.add(memberService.increaseHeartCnt(artist_id, member));
	        		//response = new APIResponse(true, "true", obj);
	        		result = "1";
	        	}
    		}
    	}
		
		resultMap.put("result", result);
		return resultMap;
	}
	
    
    /**
     * 현재 피드 코멘트 조회 및 피드 댓글 수 수정
     */
    @SuppressWarnings("deprecation")
	public Map<String, Object> feedCntUpdate(Long feedId, Member member, int pageSize){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	PageRequest pageable = new PageRequest(0, 5 + pageSize, new Sort(Direction.DESC, "createdAt"));
    	Page<FeedComment> result = FeedCommentService.findAllByFeedId(feedId, pageable, member.getId());
        int commentCnt = FeedCommentService.countByFeedId(feedId);
        Feed feed = new Feed();
        feed.setCommentCnt(commentCnt);
        feed.setMember(member);
        feed.setUpdatedAt(new Date());
        
        feedService.updateByFeedId(feedId, commentCnt);
        
        resultMap.put("commentList", result);
        resultMap.put("feedId", feedId);
        resultMap.put("commentCnt", commentCnt);
        resultMap.put("totalpage", result.getTotalPages());
        return resultMap;
    }
    
    @RequestMapping(value = "/insert", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> feedInsert(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData, 
    		@RequestParam(value = "feedImg") List<MultipartFile> feedImg) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	Member member = super.getCurrentUser(principal);
    	paramData.setMember(member);
    	paramData.setCreatedAt(new Date());
    	paramData.setUpdatedAt(new Date());
    	paramData.setPublicRangeForFormValue();
    	paramData.setCommentCnt(0);
    	paramData.setLikeCnt(0);
    	
    	paramData.setContent(paramData.getContent().replaceAll(System.getProperty("line.separator"), "<br>"));
    	
    	Feed result = feedService.insertFeed(paramData);
    	
    	for(MultipartFile multipartFile : feedImg) {
    		FeedFile feedFileVO = new FeedFile();
			feedFileVO.setFilename(UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
			feedFileVO.setFeed(result);
			
			FeedFile feedFileResult = feedFileService.insertFeedFile(feedFileVO);
			azureStorageService.uploadResource(multipartFile, "feed/" + feedFileResult.getFeedFileId() + "/");
    	}
    	
    	return resultMap;
    }
    
    @RequestMapping(value = "/imgDetail", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> feedImgDetail(@ModelAttribute("paramData") Feed paramData){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	List<FeedFile> result = feedFileService.findAllByFeedIdDESC(paramData.getFeedId());
    	
    	for(FeedFile f : result) {
    		f.setFeed(null);
    		f.setImgSrcText(f.getImgUrl());
    	}
    	
    	resultMap.put("result", result);
    	return resultMap;
    }
    
    // 피드 수정
    @RequestMapping(value = "/feedUpdate", method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> feedUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData, 
    		@RequestParam(value = "feedImg") List<MultipartFile> feedImg) {
    	
    	Member member = super.getCurrentUser(principal);
    	Feed orgFeed = feedService.findByFeedId(paramData.getFeedId());
    	orgFeed.setUpdatedAt(new Date());
    	orgFeed.setMember(member);
    	orgFeed.setContent(paramData.getContent().replaceAll(System.getProperty("line.separator"), "<br>"));
    	
    	int commentCnt = feedCommentService.countByFeedId(paramData.getFeedId());
        paramData.setCommentCnt(commentCnt);
        
    	Feed result = feedService.insertFeed(orgFeed);
    	
    	if(feedImg.size() > 0) {
    		feedFileService.deleteByFeedId(paramData.getFeedId());
    	}
    	
    	for(MultipartFile multipartFile : feedImg) {
    		FeedFile feedFileVO = new FeedFile();
			feedFileVO.setFilename(UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
			feedFileVO.setFeed(result);
			
			FeedFile feedFileResult = feedFileService.insertFeedFile(feedFileVO);
			azureStorageService.uploadResource(multipartFile, "feed/" + feedFileResult.getFeedFileId() + "/");
    	}
    	
    	return myFeed(principal, model, request);
    }
    
    // 피드 수정 모바일
    @RequestMapping(value = "/feedUpdateMobile", method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> feedUpdateMobile(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData, 
    		@RequestParam(value = "feedImg") List<MultipartFile> feedImg) {
    	
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	Member member = super.getCurrentUser(principal);
    	Feed orgFeed = feedService.findByFeedId(paramData.getFeedId());
    	orgFeed.setUpdatedAt(new Date());
    	orgFeed.setMember(member);
    	orgFeed.setContent(paramData.getContent().replaceAll(System.getProperty("line.separator"), "<br>"));
    	orgFeed.setPublicRangeFormValue(paramData.getPublicRangeFormValue());
    	orgFeed.setPublicRangeForFormValue();
    	
    	int commentCnt = feedCommentService.countByFeedId(paramData.getFeedId());
        paramData.setCommentCnt(commentCnt);
        
    	Feed result = feedService.insertFeed(orgFeed);
    	
    	if(feedImg.size() > 0) {
    		feedFileService.deleteByFeedId(paramData.getFeedId());
    	}
    	
    	for(MultipartFile multipartFile : feedImg) {
    		FeedFile feedFileVO = new FeedFile();
			feedFileVO.setFilename(UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
			feedFileVO.setFeed(result);
			
			FeedFile feedFileResult = feedFileService.insertFeedFile(feedFileVO);
			azureStorageService.uploadResource(multipartFile, "feed/" + feedFileResult.getFeedFileId() + "/");
    	}
    	
    	return resultMap;
    }
    
    /**
     * 피드 좋아요 toggle
     */
    @RequestMapping(value = "/feedLike", method=RequestMethod.POST)
    @ResponseBody
    public String feedLike(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData) {
    	Member member = super.getCurrentUser(principal);
    	
    	Feed feed = feedService.findByFeedId(paramData.getFeedId());
    	int feedLikeResult = feedLikeService.findByFeedIdAndMemberId(paramData.getFeedId(), member.getId());
    	FeedLike likeVO = new FeedLike();
    	likeVO.setFeedLikeId(paramData.getFeedId(), member.getId());
    	likeVO.setCreatedAt(new Date());
    	int feedCnt = 0;
    	
    	if(feed.getLikeCnt() != null) {
    		feedCnt = feed.getLikeCnt();
    	}
    	
    	if(feedLikeResult > 0) {
    		feedLikeService.delete(likeVO);
    		feed.setLikeCnt(feedCnt - 1);
    	}else if(feedLikeResult <= 0) {
    		feedLikeService.save(likeVO);
    		feed.setLikeCnt(feedCnt + 1);
    	}
    	
    	feed.setUpdatedAt(new Date());
    	feedService.insertFeed(feed);
    	
    	return String.valueOf(feed.getLikeCnt());
    }
    
    
    /**
     * 피드 홈
     */
    @SuppressWarnings("deprecation")
	@RequestMapping(value = "/feedHome")
    @ResponseBody
    public Map<String, Object> feedHome(Principal principal, Model model, HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	
    	Member member = super.getCurrentUser(principal);
    	
    	int feedHomePage = 0;
    	if (!StringUtils.isEmpty(request.getParameter("feedHomePage"))) feedHomePage = Integer.valueOf(request.getParameter("feedHomePage"));
    	PageRequest feedHomePageable = new PageRequest(0, 5 + feedHomePage, new Sort(Direction.DESC, "created_at"));
    	PageRequest commentPageable = new PageRequest(0, 5, new Sort(Direction.DESC, "createdAt"));
    	
    	// 피드 홈 조회 (좋아요 표시한 아티스트의 피드만 조회)
    	Page<Feed> feedHomeList = feedService.findByMemberIdAndLiker(member.getId(), feedHomePageable, commentPageable);
    	pageFeedController.feedLikeStr(feedHomeList, member);
    	
    	resultMap.put("feedHomeList", feedHomeList);
    	resultMap.put("totalPage", feedHomeList.getTotalPages());
    	return resultMap;
    }
    
    /**
     * 피드 홈
     */
	@RequestMapping(value = "/feedHomeMobile")
    @ResponseBody
    public Map<String, Object> feedHomeMobile(Principal principal, Model model, HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	
    	Member member = super.getCurrentUser(principal);
    	
    	// 피드 홈 조회 (좋아요 표시한 아티스트의 피드만 조회)
    	List<Feed> feedHomeList = feedService.findByMemberIdAndLiker(member.getId());
    	pageFeedController.feedLikeStrMobile(feedHomeList, member);
    	
    	resultMap.put("feedHomeList", feedHomeList);
    	return resultMap;
    }
    
    /**
     * 나의 피드
     */
    @SuppressWarnings("deprecation")
	@RequestMapping(value = "/myFeed")
    @ResponseBody
    public Map<String, Object> myFeed(Principal principal, Model model, HttpServletRequest request){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	Member member = super.getCurrentUser(principal);
    	int myFeedPage = 0;
    	if (!StringUtils.isEmpty(request.getParameter("myFeedPage"))) myFeedPage = Integer.valueOf(request.getParameter("myFeedPage"));
    	PageRequest myFeedPageable = new PageRequest(0, 5 + myFeedPage, new Sort(Direction.DESC, "createdAt"));
    	PageRequest commentPageable = new PageRequest(0, 5, new Sort(Direction.DESC, "createdAt"));
    	
    	// 나의 피드 조회
    	Page<Feed> myFeedList = feedService.findByMemberId(member.getId(), myFeedPageable, commentPageable);
    	pageFeedController.feedLikeStr(myFeedList, member);
    	
    	resultMap.put("myFeedList", myFeedList);
    	resultMap.put("totalPage", myFeedList.getTotalPages());
    	return resultMap;
    }
    
    /**
     * 나의 피드 (모바일)
     */
	@RequestMapping(value = "/myFeedMobile")
    @ResponseBody
    public Map<String, Object> myFeedMobile(Principal principal, Model model, HttpServletRequest request){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	Member member = super.getCurrentUser(principal);
    	
    	// 나의 피드 조회
    	List<Feed> myFeedList = feedService.findByMemberId(member.getId());
    	pageFeedController.feedLikeStrMobile(myFeedList, member);
    	
    	resultMap.put("myFeedList", myFeedList);
    	return resultMap;
    }
	
    /**
     * 전체 피드
     */
    @SuppressWarnings("deprecation")
	@RequestMapping(value = "/allFeed")
    @ResponseBody
    public Map<String, Object> allFeed(Principal principal, Model model, HttpServletRequest request){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	Member member = super.getCurrentUser(principal);
    	
    	int allFeedPage = 0;
    	if (!StringUtils.isEmpty(request.getParameter("allFeedPage"))) allFeedPage = Integer.valueOf(request.getParameter("allFeedPage"));
    	PageRequest allFeedPageable = new PageRequest(0, 5 + allFeedPage, new Sort(Direction.DESC, "created_at"));
    	PageRequest commentPageable = new PageRequest(0, 5, new Sort(Direction.DESC, "createdAt"));
    	
    	// 전체 피드 조회
    	Page<Feed> allFeedList = feedService.findAll(member.getId(), allFeedPageable, commentPageable);
    	pageFeedController.feedLikeStr(allFeedList, member);
    	
    	resultMap.put("allFeedList", allFeedList);
    	resultMap.put("totalPage", allFeedList.getTotalPages());
    	return resultMap;
    }
    
    /**
     * 전체 피드 (모바일) 
     */
	@RequestMapping(value = "/allFeedMobile")
    @ResponseBody
    public Map<String, Object> allFeedMobile(Principal principal, Model model, HttpServletRequest request){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	Member member = super.getCurrentUser(principal);
    	
    	// 전체 피드 조회
    	List<Feed> allFeedList = feedService.findAll(member.getId());
    	pageFeedController.feedLikeStrMobile(allFeedList, member);
    	
    	resultMap.put("allFeedList", allFeedList);
    	return resultMap;
    }
    
    /**
     * 피드 삭제
     */
    @RequestMapping("/feedDelete")
    @ResponseBody
    public Map<String, Object> feedDelete(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData) {
    	
    	feedCommentService.deleteByFeedId(paramData.getFeedId());
    	feedFileService.deleteByFeedId(paramData.getFeedId());
    	feedLikeService.deleteByFeedId(paramData.getFeedId());
    	feedService.deleteFeed(paramData);
    	
    	return myFeed(principal, model, request);
    }
    
    // 피드 공개 범위 수정
    @RequestMapping("/feedPublicRangeUpdate")
    @ResponseBody
    public Map<String, Object> feedPublicRangeUpdate(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") Feed paramData) {
    	
    	Member member = super.getCurrentUser(principal);
    	Feed orgFeed = feedService.findByFeedId(paramData.getFeedId());
    	orgFeed.setMember(member);
    	orgFeed.setUpdatedAt(new Date());
    	orgFeed.setPublicRangeFormValue(paramData.getPublicRangeFormValue());
    	orgFeed.setPublicRangeForFormValue();
    	
    	int commentCnt = feedCommentService.countByFeedId(paramData.getFeedId());
    	orgFeed.setCommentCnt(commentCnt);
        
    	feedService.insertFeed(orgFeed);
    	
    	return myFeed(principal, model, request);
    }
    
}

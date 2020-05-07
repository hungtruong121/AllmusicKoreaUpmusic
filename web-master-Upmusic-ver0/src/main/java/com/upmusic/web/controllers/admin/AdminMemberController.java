package com.upmusic.web.controllers.admin;

import com.google.gson.Gson;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.*;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.repositories.RoleRepository;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.UserService;
import com.upmusic.web.services.VideoService;
import com.upmusic.web.validator.FamilyArtistValidator;
import com.upmusic.web.validator.UserValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.services.MemberService;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.security.Principal;


@Controller
@RequestMapping("/admin/member")
@Api(value = "admin.member", description = "관리자 회원관리 페이지를 담당하는 컨트롤러")
public class AdminMemberController extends AdminAbstractController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private UserService userService;

	@Autowired
	private FamilyArtistValidator familyArtistValidator;

	@Autowired
	private VideoService videoService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AzureStorageService azureStorageService;

	@Autowired
	private Gson gson;

	// --------------------------------------------------------------------------------------------
	// 전체 회원

	@ApiOperation(value = "관리자 회원관리 페이지의 메인 템플릿을 반환", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
			@ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"), @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
			@ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다") })
	@GetMapping()
	public String page(HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/member");

		int page = 0;

		//검색처리를 위한 항목들
		String column = request.getParameter("searchColumn"); //검색할 column (email || nick || phoneNumber)
		String value = request.getParameter("searchValue"); //검색어

    	if (!StringUtils.isEmpty(request.getParameter("page"))) page = Integer.valueOf(request.getParameter("page"));
    	if (value == null || value == "") { //검색어가 없다면 전체 회원 목록 반환
			model.addAttribute("members", memberService.findAll(super.getPageMemberOrderByNew(page)));
		} else {
    		model.addAttribute("members", memberService.findAll(super.getPageMemberOrderByNew(page), column, value));
		}

		return "fragments/admin/page/member/index";
	}
	
	@ApiOperation(value = "회원 상세페이지의 템플릿을 반환", response = String.class)
	@GetMapping("/{id}")
	public String memberDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
		super.setSubMenu(model, "/admin/video");
		Member member = memberService.getMemberById(id);
		model.addAttribute("member", member);
		
		return "fragments/admin/page/member/member_detail";
	}

	//----------------------------------------------------------------------
	// FAMILY ARTIST

	@ApiOperation(value = "패밀리 아티스트 목록을 반환", response = String.class)
	@GetMapping("/artist/family")
	public String familyArtistPage(Principal principal, HttpServletRequest request, Model model){
		super.setSubMenu(model, "/admin/member/artist/family");
		Member member = super.getCurrentUser(principal);
		Page<Member> familyArtists = memberService.findAllFamilyArtistWithLikeByMember(member != null ? member.getId() : 0, super.getPageArtistOrderByHot(0));
		model.addAttribute("familyArtistsFragmentId", "artist-family-fragment"); // ajax로 페이지를 로드할 때 사용할 아이디
		model.addAttribute("familyArtists", familyArtists);
		model.addAttribute("familyPaginationUrl", "/component/music/family_artist");
		model.addAttribute("pageNo", 0);
		return "fragments/admin/page/member/artist_family";
	}

	@ApiOperation(value = "패밀리 아티스트 등록 폼을 반환", response = String.class)
	@GetMapping("/artist/family/insert")
	public String familyArtistInsertForm(HttpServletRequest request, Model model){
		super.setSubMenu(model, "/admin/member/artist/family");
		model.addAttribute("familyArtistForm", new FamilyArtist());
		model.addAttribute("videoTypeMap", super.getVideoTypeMap());
		return "fragments/admin/page/member/family_artist_insertForm";
	}

//	@ApiOperation(value = "패밀리 아티스트 등록 폼을 처리", response = String.class)
//	@PostMapping("/artist/family/insert")
//	public ResponseEntity<APIResponse> registration(@ModelAttribute("memberform") Member memberform, BindingResult bindingResult, HttpServletRequest request, Model model) {
//		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
//		familyArtistValidator.validate(memberform, bindingResult);
//		if (bindingResult.hasErrors()) {
//			response = new APIResponse(true, "false", gson.toJson(bindingResult.getFieldErrors()));
//		}else {
//			if (memberform.getGenderNo().equals("0")) {
//				memberform.setGender(UPMusicConstants.Gender.FEMALE);
//			} else {
//				memberform.setGender(UPMusicConstants.Gender.MALE);
//			}
//
//			memberform.addRole(roleRepository.findByName("ROLE_FAMILY"));
//
//			memberform.setUpmPoint(BigDecimal.ZERO);
//			memberform.setFundingPoint(BigDecimal.ZERO);
//
//			Member member = userService.save(memberform);
//			member.setProfileImage(UPMusicHelper.makeReadableUrl(memberform.getProfileImage()));
//			if (memberform.getProfileImageMultipart() != null && !StringUtils.isEmpty(memberform.getProfileImageMultipart().getOriginalFilename())) {
//				azureStorageService.uploadResource(memberform.getProfileImageMultipart(), "profiles/" + member.getId() + "/");
//			}
//			response = new APIResponse(true, "true", member.getId());
//		}
//		return ResponseEntity.ok(response);
//	}

	@ApiOperation(value = "패밀리 아티스트 등록 폼을 처리", response = String.class)
	@PostMapping("/artist/family/insert")
	public ResponseEntity<APIResponse> registration(@ModelAttribute("familyArtistForm") FamilyArtist familyArtistForm, BindingResult bindingResult, HttpServletRequest request, Model model) {
		APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
		Member memberform = new Member();
		Video videoform = new Video();

		// member
		memberform.setEmail(familyArtistForm.getEmail());
		memberform.setNick(familyArtistForm.getNick());
		memberform.setPassword(familyArtistForm.getPassword());
		memberform.setBirthday(familyArtistForm.getBirthday());
		if (familyArtistForm.getGenderNo().equals("0")) {
			memberform.setGender(UPMusicConstants.Gender.FEMALE);
		} else {
			memberform.setGender(UPMusicConstants.Gender.MALE);
		}
		memberform.setPhoneNumber(familyArtistForm.getPhoneNumber());
		memberform.addRole(roleRepository.findByName("ROLE_FAMILY"));
		memberform.setUpmPoint(BigDecimal.ZERO);
		memberform.setFundingPoint(BigDecimal.ZERO);
		memberform.setProfileImageMultipart(familyArtistForm.getProfileImageMultipart());
		memberform.setChargePoint(BigDecimal.valueOf(0));
		memberform.setProfileImage(familyArtistForm.getProfileImage());
		Member member = userService.save(memberform);
		member.setProfileImage(UPMusicHelper.makeReadableUrl(memberform.getProfileImage()));
		if (memberform.getProfileImageMultipart() != null && !StringUtils.isEmpty(memberform.getProfileImageMultipart().getOriginalFilename())) {
			azureStorageService.uploadResource(memberform.getProfileImageMultipart(), "profiles/" + member.getId() + "/");
		}
		response = new APIResponse(true, "true", member.getId());
		if (familyArtistForm.getSubject() != null && !familyArtistForm.getSubject().equals("")) {
			//video
			videoform.setVideoService(familyArtistForm.getVideoService());
			videoform.setVideoServiceObjectId(familyArtistForm.getVideoServiceObjectId());
			videoform.setDuration(familyArtistForm.getDuration());
			videoform.setVideoType(familyArtistForm.getVideoType());
			videoform.setSubject(familyArtistForm.getSubject());
			videoform.setDescription(familyArtistForm.getDescription());
			videoform.setThumbnail(familyArtistForm.getThumbnail());
			videoform.setMember(member);
			Genre genre = new Genre();
			genre.setId(1);
			videoform.setGenre(genre);
			Video newVideo = videoService.saveVideo(videoform);
			// 회원의 비디오수 증가
			member.setVideoCnt(member.getVideoCnt() + 1);
			memberService.saveMember(member);
			response = new APIResponse(true, "true", member.getId());
		}
		return ResponseEntity.ok(response);
	}
}

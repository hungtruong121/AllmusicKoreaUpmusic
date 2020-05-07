package com.upmusic.web.controllers.page;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.*;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.upmusic.web.domain.*;
import com.upmusic.web.helper.AzureHelper;
import com.upmusic.web.helper.customClass.CustomMultipartFile;
import com.upmusic.web.message.APIResponse;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.MemberService;
import com.upmusic.web.services.MusicAlbumService;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.VideoService;
import com.upmusic.web.validator.MusicTrackValidator;
import com.upmusic.web.validator.VideoValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;
import org.tritonus.share.sampled.file.TAudioFileFormat;

@Controller
@RequestMapping("/upload")
@Api(value="upload", description="업로드 페이지를 담당하는 컨트롤러")
public class PageUploadController extends PageAbstractController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MusicAlbumService albumService;
	
	@Autowired
	private MusicTrackService trackService;
	
	@Autowired
    private VideoService videoService;
	
	@Autowired
	private AzureStorageService azureStorageService;
	
	@Autowired
    private MemberService memberService;

	@Autowired
    private MusicTrackValidator trackValidator;
	
    @Autowired
    private VideoValidator videoValidator;
    
    // --------------------------------------------------------------------------------------------
 	// MUSIC ALBUM
    
    @ApiOperation(value = "앨범 업로드 템플릿을 반환", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 대해 성공적으로 응답 처리되었습니다"),
            @ApiResponse(code = 401, message = "로그인 후 다시 시도하십시오"),
            @ApiResponse(code = 403, message = "접근이 불가한 페이지입니다"),
            @ApiResponse(code = 404, message = "해당 페이지를 찾을 수 없습니다")
    }
    )
	@GetMapping("/music")
    public String musicAlbum(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/upload", super.getCurrentUser(principal), request);
    	model.addAttribute("musicform", new MusicAlbum());
    	model.addAttribute("albumTypeMap", super.getMusicAlbumTypeMap());
    	model.addAttribute("genreMap", super.getGenreMap());
        return "fragments/page/upload/music";
    }
    
	// --------------------------------------------------------------------------------------------
 	// MUSIC TRACK
    
    @ApiOperation(value = "트랙 업로드 템플릿을 반환", response = String.class)
	@GetMapping("/music/{id}/track")
    public String musicTrack(@PathVariable Long id, Principal principal, Model model, HttpServletRequest request) {
    	MusicAlbum album = albumService.getAlbumById(id);
    	if (album == null) {
    		return "redirect:/upload/music";
    	}
    	super.setSubMenu(model, "/upload", super.getCurrentUser(principal), request);
    	MusicTrack track = new MusicTrack();
    	track.setMusicAlbum(album);
    	model.addAttribute("trackform", track);
    	Member member = super.getCurrentUser(principal);
    	model.addAttribute("trackTypeMap", super.getMusicTrackTypeMap());
    	model.addAttribute("genreMap", super.getGenreMap());
    	model.addAttribute("themeMap", super.getThemeMap());
    	model.addAttribute("currentUser", member);
    	model.addAttribute("cooperatorRoleMap", super.getCooperatorRoleMap());
    	model.addAttribute("isSA", UPMusicConstants.MusicAlbumType.SA.equals(album.getAlbumType()));
    	model.addAttribute("tracks", album.getTracks());
        return "fragments/page/upload/track";
    }
    
    /**
     * 트랙 업로드
     * @param id
     * @param trackform
     * @param request
     * @param bindingResult
     * @param principal
     * @param model
     * @return redirect
     * @throws UnsupportedEncodingException 
     */
	@ApiOperation(value = "트랙 업로드 템플릿을 처리", response = String.class)
    @PostMapping("/music/{id}/track")
    public String createMusicTrack(@PathVariable Long id, @ModelAttribute("trackform") MusicTrack trackform, HttpServletRequest request, BindingResult bindingResult, Principal principal, Model model) {
		logger.debug("createMusicTrack : trackform is {}", trackform);
		MusicAlbum album = albumService.getAlbumById(id);
    	if (album == null) {
    		return "redirect:/upload/music";
    	}
    	trackform.setMusicAlbum(album);
		Member member = super.getCurrentUser(principal);
		if (0 == (member.getId().compareTo(album.getMember().getId()))) {
			// 유효성 결과와 상관없이 협력자의 회원 정보가 필요함
			List<MusicTrackCooperator> cooperators = trackform.getCooperators();
		    cooperators.removeIf(x->x.getCooperatorId() == null);
			for (MusicTrackCooperator cooperator : cooperators) {
				Member cooperatorMember = memberService.getMemberById(cooperator.getCooperatorId());
				cooperator.setMember(cooperatorMember);
			}
			trackform.setCooperators(cooperators);
			
			if (trackform.getFileMultipart() == null || StringUtils.isEmpty(trackform.getFileMultipart().getOriginalFilename())) trackform.setFilename(null);
			if (trackform.getExtraFileMultipart() == null || StringUtils.isEmpty(trackform.getExtraFileMultipart().getOriginalFilename())) trackform.setExtraSource(null);
	    	model.addAttribute("trackform", trackform);
	    	trackValidator.validate(trackform, bindingResult);
	        if (bindingResult.hasErrors()) {
	        	super.setSubMenu(model, "/upload", super.getCurrentUser(principal), request);
	        	model.addAttribute("trackTypeMap", super.getMusicTrackTypeMap());
	        	model.addAttribute("genreMap", super.getGenreMap());
	        	model.addAttribute("themeMap", super.getThemeMap());
	        	model.addAttribute("currentUser", member);
	        	model.addAttribute("cooperatorRoleMap", super.getCooperatorRoleMap());
	        	model.addAttribute("isSA", UPMusicConstants.MusicAlbumType.SA.equals(album.getAlbumType()));
	        	model.addAttribute("tracks", album.getTracks());
	        	return "fragments/page/upload/track";
	        }
			trackform.setFilename(UPMusicHelper.makeReadableUrl(trackform.getFileMultipart().getOriginalFilename()));
			if (trackform.getExtraFileMultipart() != null && !StringUtils.isEmpty(trackform.getExtraFileMultipart().getOriginalFilename())) trackform.setExtraSource(UPMusicHelper.makeReadableUrl(trackform.getExtraFileMultipart().getOriginalFilename()));
			trackform.setInStore(false);
			trackform.setPrice(0);
			trackform.setRentalFee(0);
			trackform.setSortNo(album.getTracks().size() + 1);
			trackform.setTitleTrack(0 == album.getTracks().size()); // 기본값으로 첫 곡을 타이틀곡으로 지정
			MusicTrack newTrack = trackService.saveTrack(trackform);
			
			// 파일 azure에 업로드
			azureStorageService.uploadTrackToTranscode(trackform.getFileMultipart(), album.getId() + "/");
			if (trackform.getExtraFileMultipart() != null && !StringUtils.isEmpty(trackform.getExtraFileMultipart().getOriginalFilename())) azureStorageService.uploadTrackToTranscode(trackform.getExtraFileMultipart(), album.getId() + "/");
			
			// 협력자 저장
			for (MusicTrackCooperator cooperator : cooperators) {
				cooperator.setMusicTrack(newTrack);
				trackService.saveTrackCooperator(cooperator);
			}
			
			return "redirect:/upload/music/" + album.getId() + "/track";
		}
    	return "redirect:/upload/music";
    }
	
	// --------------------------------------------------------------------------------------------
 	// VIDEO
    
    @ApiOperation(value = "영상 업로드 템플릿을 반환", response = String.class)
    @GetMapping("/video")
    public String video(Principal principal, Model model, HttpServletRequest request) {
    	super.setSubMenu(model, "/upload", super.getCurrentUser(principal), request);
    	model.addAttribute("videoform", new Video());
    	model.addAttribute("videoTypeMap", super.getVideoTypeMap());
        return "fragments/page/upload/video";
    }
    
    /**
     * 영상 업로드 템플릿을 처리
     * @param videoform
     * @param bindingResult
     * @param principal
     * @param model
     * @return redirect
     * @throws UnsupportedEncodingException 
     */
	@ApiOperation(value = "영상 업로드 템플릿을 처리", response = String.class)
    @PostMapping("/video")
    public String createVideo(@ModelAttribute("videoform") Video videoform, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		videoValidator.validate(videoform, bindingResult);
        if (bindingResult.hasErrors()) {
        	super.setSubMenu(model, "/upload", super.getCurrentUser(principal), request);
        	model.addAttribute("videoTypeMap", super.getVideoTypeMap());
        	model.addAttribute("genreMap", super.getGenreMap());
        	return "fragments/page/upload/video";
        }
        Member member = super.getCurrentUser(principal);
    	videoform.setMember(member);
    	Video newVideo = videoService.saveVideo(videoform);
        // 회원의 비디오수 증가
        member.setVideoCnt(member.getVideoCnt() + 1);
        memberService.saveMember(member);
    	return "redirect:" + newVideo.getUrl();
    }

//	@ApiOperation(value = "음원 엑셀 업로드 템플릿을 처리", response = String.class)
	@RequestMapping(value = "/excelMusicUpload", method=RequestMethod.POST, produces = "application/json")
	public @ResponseBody String excelMusicUpload(@RequestBody String excelForm, BindingResult bindingResult, Principal principal, Model model) {
		logger.debug("music data from excel : {}", excelForm);
		int maxSize = 300000;
		JSONArray array = new JSONArray(excelForm);
		int list_cnt = array.length();
		long albumId = 0;
		Member member = super.getCurrentUser(principal);
		for(int i = 0; i < list_cnt; i++){
			JSONObject jsonObject = array.getJSONObject(i);
			String imgName = jsonObject.getString("imgName");
			String fileName = jsonObject.getString("fileName");
			Map<String, byte[]> map = azureStorageService.getBlob(imgName, fileName);
			MultipartFile image = new CustomMultipartFile(map.get("image"));
			MultipartFile mp3 = new CustomMultipartFile(map.get("music"));
			MultipartFile zip = new CustomMultipartFile(map.get("track"));

			try{
				if(i != 0 && jsonObject.getString("albumSubject").equals("sameAlbum")){
					//track
					MusicTrack trackform = new MusicTrack();
					MusicAlbum album = albumService.getAlbumById(albumId);
					trackform.setDuration(jsonObject.getInt("duration"));
					trackform.setMusicAlbum(album);
					if (mp3 == null) trackform.setFilename(null);
					if (zip == null) trackform.setExtraSource(null);
					trackform.setId(albumId);
					trackform.setSubject(jsonObject.getString("musicSubject"));
					int trackTypeKey = jsonObject.getInt("trackType");
					switch(trackTypeKey){
						case 1 : trackform.setTrackType(UPMusicConstants.MusicTrackType.MR); break;
						case 2 : trackform.setTrackType(UPMusicConstants.MusicTrackType.AR); break;
						case 3 : trackform.setTrackType(UPMusicConstants.MusicTrackType.AR_GUIDE); break;
					}
					trackform.setPrice(jsonObject.getInt("price"));
					trackform.setRentalFee(jsonObject.getInt("rentalFee"));
					int leagueTypeKey = jsonObject.getInt("leagueType");
					switch(leagueTypeKey){
						case 0 : trackform.setInStore(true); trackform.setInRecent(true); trackform.setInLeague(true); break;
						case 1 : trackform.setInStore(true); trackform.setInRecent(true); break;
						case 2 : trackform.setInLeague(true); break;
					}
					int titleTrackKey = jsonObject.getInt("titleTrack");
					trackform.setTitleTrack(titleTrackKey == 1 ? true : false);
					trackform.setTrackStatus(UPMusicConstants.MusicTrackStatus.BEFORE_EXAM);
					Theme theme = new Theme();
					theme.setId(jsonObject.getInt("theme"));
					trackform.setTheme(theme);
					Genre musicGenre = new Genre();
					musicGenre.setId(jsonObject.getInt("musicGenre"));
					trackform.setGenre(musicGenre);
					trackform.setPublished(true);
					trackform.setFilename(UPMusicHelper.makeReadableUrl(fileName + ".mp3"));
					if (zip != null) trackform.setExtraSource(UPMusicHelper.makeReadableUrl(fileName + ".zip"));
					trackform.setSortNo(album.getTracks().size() + 1);
					trackform.setTitleTrack(0 == album.getTracks().size()); // 기본값으로 첫 곡을 타이틀곡으로 지정
					MusicTrack newTrack = trackService.saveTrack(trackform);
					// 파일 azure에 업로드
					azureStorageService.uploadTrackToTranscode(mp3, fileName + ".mp3", album.getId() + "/");
					if (zip != null) azureStorageService.uploadTrackToTranscode(zip, fileName + ".zip", album.getId() + "/");
				} else {
					//album
					MusicAlbum musicform = new MusicAlbum();
					if (image == null) {
						musicform.setImageFilename(null);
					}
					musicform.setImageFilename(UPMusicHelper.makeReadableUrl(imgName + ".png"));
					Genre albumGenre = new Genre();
					albumGenre.setId(jsonObject.getInt("albumGenre"));
					musicform.setGenre(albumGenre);
					musicform.setPublished(true);
					int albumType = jsonObject.getInt("albumType");
					musicform.setAlbumType(albumType == 1 ? UPMusicConstants.MusicAlbumType.SA : UPMusicConstants.MusicAlbumType.GA);
					musicform.setSubject(jsonObject.getString("albumSubject"));
					musicform.setDescription(jsonObject.getString("albumDescription"));
					musicform.setMember(member);
					MusicAlbum newAlbum = albumService.saveAlbum(musicform);
					albumId = newAlbum.getId();
					if (image.getSize() >= maxSize) {
						InputStream is = AzureHelper.scale(image, 0.18, 0.18);
						azureStorageService.uploadResource(is, imgName + ".png", "albums/" + albumId + "/");
					} else {
						azureStorageService.uploadResource(image.getInputStream(), imgName + ".png", "albums/" + albumId + "/");
					}

					//track
					MusicTrack trackform = new MusicTrack();
					MusicAlbum album = albumService.getAlbumById(albumId);
					trackform.setDuration(jsonObject.getInt("duration"));
					trackform.setMusicAlbum(album);
					if (mp3 == null) trackform.setFilename(null);
					if (zip == null) trackform.setExtraSource(null);
					trackform.setId(albumId);
					trackform.setSubject(jsonObject.getString("musicSubject"));
					int trackTypeKey = jsonObject.getInt("trackType");
					switch (trackTypeKey) {
						case 1: trackform.setTrackType(UPMusicConstants.MusicTrackType.MR); break;
						case 2: trackform.setTrackType(UPMusicConstants.MusicTrackType.AR); break;
						case 3: trackform.setTrackType(UPMusicConstants.MusicTrackType.AR_GUIDE); break;
					}
					trackform.setPrice(jsonObject.getInt("price"));
					trackform.setRentalFee(jsonObject.getInt("rentalFee"));
					int leagueTypeKey = jsonObject.getInt("leagueType");
					switch (leagueTypeKey) {
						case 0 : trackform.setInStore(true); trackform.setInRecent(true); trackform.setInLeague(true); break;
						case 1 : trackform.setInLeague(true); trackform.setInRecent(true); break;
						case 2 : trackform.setInStore(true); break;
					}
					int titleTrackKey = jsonObject.getInt("titleTrack");
					trackform.setTitleTrack(titleTrackKey == 1 ? true : false);
					trackform.setTrackStatus(UPMusicConstants.MusicTrackStatus.BEFORE_EXAM);
					Theme theme = new Theme();
					theme.setId(jsonObject.getInt("theme"));
					trackform.setTheme(theme);
					Genre musicGenre = new Genre();
					musicGenre.setId(jsonObject.getInt("musicGenre"));
					trackform.setGenre(musicGenre);
					trackform.setPublished(true);
					trackform.setFilename(UPMusicHelper.makeReadableUrl(fileName + ".mp3"));
					if (zip != null) trackform.setExtraSource(UPMusicHelper.makeReadableUrl(fileName + ".zip"));
					trackform.setSortNo(album.getTracks().size() + 1);
					trackform.setTitleTrack(0 == album.getTracks().size()); // 기본값으로 첫 곡을 타이틀곡으로 지정
					MusicTrack newTrack = trackService.saveTrack(trackform);
					// 파일 azure에 업로드
					azureStorageService.uploadTrackToTranscode(mp3, fileName + ".mp3", album.getId() + "/");
					if (zip != null) azureStorageService.uploadTrackToTranscode(zip, fileName + ".zip", album.getId() + "/");
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return "fragments/page/music/index";
	}
}

package com.upmusic.web.controllers.admin;

import com.upmusic.web.domain.CrowdFunding;
import com.upmusic.web.domain.MainBanner;
import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.repositories.MainBannerRepository;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.MainBannerService;
import com.upmusic.web.services.MainBannerServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/admin/main")
public class AdminMainController extends AdminAbstractController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MainBannerService mainBannerService;

    @Autowired
    private AzureStorageService azureStorageService;

    @ApiOperation(value = "배너 리스트 반환", response = ResponseEntity.class)
    @GetMapping("/banner")
    public String adminMainBanner(Model model) {

        model.addAttribute("eventBannerCnt", mainBannerService.findAllShownEventBanner().size());
        model.addAttribute("artistBannerCnt", mainBannerService.findAllShownArtistBanner().size());
        model.addAttribute("eventBannerList", mainBannerService.findAllEventBanner());
        model.addAttribute("artistBannerList", mainBannerService.findAllArtistBanner());

        return "fragments/admin/page/main/main_banner";
    }

    @ApiOperation(value = "배너 생성", response = ResponseEntity.class)
    @RequestMapping(value = "/banner/insert", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> insertBanner(Principal principal, Model model, HttpServletRequest request, @ModelAttribute("paramData") MainBanner paramData){
        APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
        Member member = super.getCurrentUser(principal);
        paramData.setMember(member);
        paramData.setCreatedAt(new Date());
        paramData.setUpdatedAt(new Date());
        paramData.setFilename(UPMusicHelper.makeReadableUrl(paramData.getThumbnailImg().getOriginalFilename()));
        MainBanner banner = mainBannerService.saveBanner(paramData);
        if(paramData.getThumbnailImg() != null && !StringUtils.isEmpty(paramData.getThumbnailImg().getOriginalFilename())) {
            azureStorageService.uploadResource(paramData.getThumbnailImg(), "main_banner/" + banner.getMainBannerId() + "/");
        }
        model.addAttribute("eventBannerCnt", mainBannerService.findAllShownEventBanner().size());
        model.addAttribute("artistBannerCnt", mainBannerService.findAllShownArtistBanner().size());
        model.addAttribute("eventBannerList", mainBannerService.findAllEventBanner());
        model.addAttribute("artistBannerList", mainBannerService.findAllArtistBanner());
        response = new APIResponse(true, "true", banner.getMainBannerId());
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "id에 해당하는 배너를 삭제", response = ResponseEntity.class)
    @RequestMapping(value = "/banner/{id}/delete", method= RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<APIResponse> deleteBanner(@PathVariable Long id, HttpServletRequest request, Model model) {
        mainBannerService.deleteById(id);
        APIResponse response = new APIResponse(true, "true", id);
        model.addAttribute("eventBannerCnt", mainBannerService.findAllShownEventBanner().size());
        model.addAttribute("artistBannerCnt", mainBannerService.findAllShownArtistBanner().size());
        model.addAttribute("eventBannerList", mainBannerService.findAllEventBanner());
        model.addAttribute("artistBannerList", mainBannerService.findAllArtistBanner());
        return ResponseEntity.ok(response);
    }

}

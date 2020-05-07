package com.upmusic.web.controllers.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.StoreOrderLicense;
import com.upmusic.web.message.APIResponse;
import com.upmusic.web.services.AzureStorageService;
import com.upmusic.web.services.MusicTrackService;
import com.upmusic.web.services.StoreOrderLicenseService;


@RestController
@RequestMapping("/api/store")
@Api(value="api.store", description="스토어와 관련된 API 컨트롤러")
public class APIStoreController extends APIAbstractController {

	@Autowired
    private StoreOrderLicenseService storeOrderLicenseService;
	
	@Autowired
    private AzureStorageService azureStorageService;
	
	@Autowired
    private MusicTrackService trackService;

    
    @ApiOperation(value = "id에 해당하는 곡의 구매여부 반환",response = MusicTrack.class)
    @RequestMapping(value = "/is_buyer/{trackId}", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<APIResponse> isBuyer(@PathVariable Long trackId, Principal principal){
    	APIResponse response = new APIResponse(false, super.getMessage("common.request.auth"), null);
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
    		StoreOrderLicense licenseOrder = storeOrderLicenseService.findOrderLicenseByMusicTrackIdAndBuyerId(trackId, member.getId());
        	if (licenseOrder != null) {
        		if (UPMusicConstants.StoreOrderStatus.COMPLETED == licenseOrder.getOrderStatus()) {
        			response = new APIResponse(true, "true", licenseOrder.getStoreLicense().getTrackType());
        		} else {
        			response = new APIResponse(true, "false", licenseOrder.getOrderStatus().name());
        		}
        	} else {
        		response = new APIResponse(true, "false", null);
        	}
    	}
    	return ResponseEntity.ok(response);
    }
    
    @ApiOperation(value = "id에 해당하는 곡을 다운로드",response = MusicTrack.class)
    @RequestMapping(value = "/download/{trackId}/{trackFormat}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<byte[]> download(@PathVariable Long trackId, @PathVariable String trackFormat, Principal principal) throws IOException{
    	Member member = super.getCurrentUser(principal);
    	if (member != null) {
    		StoreOrderLicense licenseOrder = storeOrderLicenseService.findOrderLicenseByMusicTrackIdAndBuyerId(trackId, member.getId());
        	if (licenseOrder != null) {
        		if (UPMusicConstants.StoreOrderStatus.COMPLETED == licenseOrder.getOrderStatus()) {
        			if (licenseOrder.getStoreLicense().getTrackType().contains(trackFormat)) {
        				MusicTrack track = trackService.getTrackById(trackId);
        				String filename = track.getFilename();
        				if ("WAV".equals(trackFormat)) filename = filename.replaceAll(".mp3", ".wav");
        				if ("ZIP".equals(trackFormat)) filename = track.getExtraSource();
        				return azureStorageService.downloadMusicFile(track.getMusicAlbum().getId() + "/", filename);
        			}
        		}
        	}
    	}
    	return null;
    }
    
}

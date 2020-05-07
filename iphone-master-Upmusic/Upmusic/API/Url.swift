//
//  Url.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation

// 서버의 주소
let baseUrl = "http://up-music.ap-northeast-2.elasticbeanstalk.com/";
let baseUrl2 = "https://upmusic.azurewebsites.net";

let publicResourcesUrl = "https://upmresource.blob.core.windows.net/upm-container-resource";
let musicResourcesUrl = "https://upmalbum.blob.core.windows.net/upm-container-album";

let adminUrl = baseUrl + "/admin";
let apiUrl = baseUrl + "/api";
let componentUrl = baseUrl + "/component";

let artistUrl = apiUrl + "/artist";
let authUrl = apiUrl + "/auth";
let collectionUrl = apiUrl + "/collection";
let albumUrl = apiUrl + "/album";
let trackUrl = apiUrl + "/track";
let heartlistUrl = apiUrl + "/my_heartlist";
let playlistUrl = apiUrl + "/my_playlist";
let playerUrl = apiUrl + "/player";
let storeUrl = apiUrl + "/store";
let storeRequestUrl = storeUrl + "/request";
let videoUrl = baseUrl + "/video";
let vocalCastingUrl = apiUrl + "/vocal_casting";
let componentMusicUrl = componentUrl + "/music";
let componentMyPlaylistUrl = componentUrl + "/my_playlist";
let pageAuthUrl = baseUrl + "/auth";
let componentVideoUrl = componentUrl + "/video";
let pageCrowdFundingUrl = baseUrl + "/crowd_funding";
let pageUploadUrl = baseUrl + "/upload";
let pageVideoUrl = baseUrl + "/video";
let pageUpmNewsUrl = baseUrl + "/upm_news";
let pageMyUpmUrl = baseUrl + "/my_upm";
let pageFeedUrl = baseUrl + "/feed";
let pageMyPlayListUrl = baseUrl + "/my_playlist";
let pageMusicUrl = baseUrl + "/music";




// 서버주소 + " -- " : 상황에 맞도록 주소 설정
class Url {
    
    /**
     * 0. admin-home-controller : 관리자 홈
     * admin-music-controller : 관리자 홈
     * admin-video-controller : 관리자 홈
     * */
    
    let adminHomeUrl = adminUrl + "";
    
    let adminMusicUrl = adminUrl + "/music";
    let adminMusicJudgingUrl = adminUrl + "/music/judging";
    
    let adminMusicSeasonUrl = adminUrl + "/music/season";
    let adminMusicSeasonDetailUrl = adminUrl + "/music/season/{id}";
    let adminMusicSeasonUpdateUrl = adminUrl + "/music/season/{id}";
    let adminMusicSeasonDeleteUrl = adminUrl + "/music/season/{id}/delete";
    let adminMusicSeasonEditUrl = adminUrl + "/music/season/{id}/edit";
    let adminMusicSeasonNewUrl = adminUrl + "/music/season/{id}/new";
    
    let adminMusicTrackDetailUrl = adminUrl + "/music/track/{id}";
    let adminMusicTrackAcceptUrl = adminUrl + "/music/track/{id}/accept";
    let adminMusicTrackRejectUrl = adminUrl + "/music/track/{id}/accept";
    
    let adminVideoUrl = adminUrl + "/video";
    let adminVideoTrackDetailUrl = adminUrl + "/video/{id}";
    
    /**
     * 1. api-artist-controller : 아티스트 관련
     * */
    let artistAllUrl = artistUrl + "";
    let artistOneUrl = artistUrl + "/{id}";
    let artistSetLikeUrl = artistUrl + "{id}" + "/like_from_list";
    
    /**
     * 2. api-auth-controller : 인증관련
     * */
    
    let authCheckEmailUrl = authUrl + "/check_email";
    let authLoginUrl = authUrl + "/login";
    let authRequestPhoneUrl = authUrl + "/request_phone_authentication_code";
    let authPasswordEmailUrl = authUrl + "/request_pw_email_authentication_code";
    let authPhoneEmailUrl = authUrl + "/request_pw_phone_authentication_code";
    
    /**
     * 3. api-collection-controller : 리스트 관련
     * */
    
    let collectionAllUrl = collectionUrl + "";
    let collectionOneUrl = collectionUrl + "/{id}";
    let collectionDeleteUrl = collectionUrl + "/{id}";
    let collectionAddTrackUrl = collectionUrl + "/{id}" + "/add_tracks";
    let collectionAddPlaylistUrl = collectionUrl + "/{id}" + "/play";
    let collectionRemoveTrackUrl = collectionUrl + "/{id}" + "/remove_tracks";
    let collectionCreateUrl = collectionUrl + "/create";
    let collectionRemoveUrl = collectionUrl + "/delete";
    
    /**
     * 4. api-member-controller : 멤버 관련
     * */
    
    let memberUrl = apiUrl + "/member";
    let memberSearchUrl = apiUrl + "/member" + "/search";
    
    
    /**
     * api-music-album-controller : 앨범 관련
     * */
    
    let albumAllUrl = albumUrl;
    let albumOneUrl = albumUrl + "/{id}";
    let albumAddCommentUrl = albumUrl + "/{id}" + "/comment";
    let albumGetCommetUrl = albumUrl + "/{id}" + "/comment" + "/{commentId}";
    let albumRemoveCommetUrl = albumUrl + "/{id}" + "/comment" + "/{commentId}";
    let albumSetLikeUrl = albumUrl + "/{id}" + "/comment" + "/like";
    let albumSetLike2Url = albumUrl + "/{id}" + "/comment" + "/like_from_list";
    
    
    /**
     * 5. api-music-track-controller : 곡 관련
     * */
    
    let trackOneUrl = trackUrl;
    let trackSetLikeUrl = trackUrl + "/{id}" + "/like";
    let trackSetTitleUrl = trackUrl + "/{id}" + "/title";
    let trackDeleteUrl = trackUrl + "/delete_tracks";
    
    /**
     * 6. api-my-heartlist-controller : 좋아요 목록 관련
     * */
    
    let heartlistRemoveTrackUrl = heartlistUrl + "/remove_tracks";
    let heartlistRemoveVideoUrl = heartlistUrl + "/remove_videos";
    
    /**
     * 7. api-my-playlist-controller : 재생목록 관련
     * */
    
    let playlistAddTrackUrl = playlistUrl + "/add_tracks";
    let playlistAddVideoUrl = playlistUrl + "/add_video";
    let playlistRemoveTrackUrl = playlistUrl + "/remove_tracks";
    let playlistRemoveVideoUrl = playlistUrl + "/remove_videos";
    /**
     * 8. api-player-controller : 뮤직플레이어 관련
     * */
    let playerGetMainTemplateUrl = playerUrl;
    let playerAddPlayCountUrl = playerUrl + "/{id}" + "/add_play_cnt";
    let playerRemoveTrackUrl = playerUrl + "/remove_tracks";
    
    /**
     * 9. api-store-controller : 스토어 관련
     * */
    let storeDownloadUrl = storeUrl + "/download" + "/{trackId}" + "/{trackFormat}";
    let storeIsBuyerUrl = storeUrl + "/is_buyer" + "/{trackId}";
    
    /**
     * 10. api-store-request-controller : 제작의뢰 관련
     * */
    let storeRequestGetUrl = storeRequestUrl + "/{id}";
    let storeRequestDeleteUrl = storeRequestUrl + "/{id}";
    let storeRequestAddCommetUrl = storeRequestUrl + "/{id}" + "/comment";
    let storeRequestGetCommentUrl = storeRequestUrl + "/{id}" + "/comment" + "/{commentId}";
    let storeRequestUpdateCommentUrl = storeRequestUrl + "/{id}" + "/comment" + "/{commentId}";
    let storeRequestDeleteCommentUrl = storeRequestUrl + "/{id}" + "/comment" + "/{commentId}";
    
    /**
     * 11. api-video-controller : 영상 관련
     * */
    let videoGetAllUrl = videoUrl + "";
    let videoGetOneUrl = videoUrl + "/{id}";
    let videoAddCommentUrl = videoUrl + "/{id}" + "/comment";
    let videoGetCommentUrl = videoUrl + "/{id}" + "/comment" + "/{commentId}";
    let videoUpdateCommentUrl = videoUrl + "/{id}" + "/comment" + "/{commentId}";
    let videoDeleteCommentUrl = videoUrl + "/{id}" + "/comment" + "/{commentId}";
    let videoSetLikeUrl = videoUrl + "/like";
    let videoAddPlaylistUrl = videoUrl + "/play";
    let videoRemoveVideoUrl = videoUrl + "/delete_videos";
    
    
    /**
     * 12. api-vocal-casting-controller : 보컬 캐스팅 관련
     * */

    let vocalCastingGetAllUrl = vocalCastingUrl + "";
    let vocalCastingGetOneUrl = vocalCastingUrl + "/{id}";
    let vocalCastingRemoveUrl = vocalCastingUrl + "/{id}";
    let vocalCastingAddCommentUrl = vocalCastingUrl + "/{id}" + "/comment";
    let vocalCastingGetCommentUrl = vocalCastingUrl + "/{id}" + "/comment" + "/{commentId}";
    let vocalCastingUpdateCommentUrl = vocalCastingUrl + "/{id}" + "/comment" + "/{commentId}";
    let vocalCastingRemoveCommentUrl = vocalCastingUrl + "/{id}" + "/comment" + "/{commentId}";
    let vocalCastingSetLikeUrl = vocalCastingUrl + "/{id}" + "/like";
    

    /**
     * 13. component-music-controller : 뮤직 컴포넌트 담당
     * */
    let componentMusicAbroadAlbumUrl = componentMusicUrl + "/album_abroad/ga";
    let componentMusicAbroadSingleUrl = componentMusicUrl + "/album_abroad/sa";
    let componentMusicHomeAlbumUrl = componentMusicUrl + "/album_home/ga";
    let componentMusicHomeSingleUrl = componentMusicUrl + "/album_home/sa";
    
    let componentGetAlbumCommentsUrl = componentMusicUrl + "/album/{id}/comments";
    let componentGetAlbumUrl = componentMusicUrl + "/album/ga";
    let componentGetSingleUrl = componentMusicUrl + "/album/sa";
    
    let componentGetArtistUrl = componentMusicUrl + "/artist";
    let componentGetFamilyArtistUrl = componentMusicUrl + "/family_artist";
    let componentGetGuideVocalUrl = componentMusicUrl + "/guide_vocal";
    
    let componentGetLeagueGenreUrl = componentMusicUrl + "/league_genre";
    let componentGetLeagueSeasonUrl = componentMusicUrl + "/league_season";
    let componentGetLeagueTop50Url = componentMusicUrl + "/league_top50";
    let componentGetNewArtistUrl = componentMusicUrl + "/new_artist";
    
    let componentGetRewardGenreUrl = componentMusicUrl + "/reward_genre";
    let componentGetRewardRealtimeUrl = componentMusicUrl + "/reward_realtime";
    let componentGetRewardThemeUrl = componentMusicUrl + "/reward_theme";
    
    let componentGetStoreUrl = componentMusicUrl + "/store";
    let componentGetStoreRequestUrl = componentMusicUrl + "/store/request";
    
    let componentGetCommentsFromStoreRequestUrl = componentMusicUrl + "/store/request/{id}/comments";
    let componentGetCommentsFromVocalCastingUrl = componentMusicUrl + "/vocal_casting/{id}/comments";
    
    
    /**
     * 14. component-my-playlist-controller : 마이 플레이리스트 컴포넌트 담당
     * */
    
    let componentMyPlaylistGetUrl = componentMyPlaylistUrl + "/collection";
    let componentMyPlaylistGetModalUrl = componentMyPlaylistUrl + "/collection_modal";
    let componentMyPlaylistGetDetailUrl = componentMyPlaylistUrl + "/collection/{id}";
    let componentMyPlaylistGetLikeTrackUrl = componentMyPlaylistUrl + "/like_track";
    let componentMyPlaylistGetLikeVideoUrl = componentMyPlaylistUrl + "/like_video";
    let componentMyPlaylistGetRecentTrackUrl = componentMyPlaylistUrl + "/recent_track";
    let componentMyPlaylistGetRecentVideoUrl = componentMyPlaylistUrl + "/recent_video";
    
    /**
     * 15. component-video-controller : 마이 플레이리스트 컴포넌트 담당
     * */
    
    let componentVideoGetCommentsUrl = componentVideoUrl + "/{id}/comments";
    
    
    /**
     * 16. page-auth-controller : 로그인/ 회원가입 페이지 담당
     * */
    
    
    let pageAuthForgotPasswordUrl = pageAuthUrl + "/forgot_password";
    let pageAuthForgotPasswordHandleUrl = pageAuthUrl + "/forgot_password";
    let pageAuthLoginUrl = pageAuthUrl + "/login";
    let pageAuthRegistrationUrl = pageAuthUrl + "/registration";
    let pageAuthRegistrationHandleUrl = pageAuthUrl + "/registration";
    let pageAuthUpdatePasswordUrl = pageAuthUrl + "/update_password";
    
    
    /**
     * 17. page-crowd-funding-controller : 크라우드펀딩 페이지 담당
     * */
    
    let pageCrowdFundingGetUrl = pageCrowdFundingUrl + "";
    let pageCrowdFundingGetHotProjectUrl = pageCrowdFundingUrl + "/hot_project";
    let pageCrowdFundingGetInformationUrl = pageCrowdFundingUrl + "/information";
    let pageCrowdFundingGetNewProjectUrl = pageCrowdFundingUrl + "/new_project";
    let pageCrowdFundingGetParticipationUrl = pageCrowdFundingUrl + "/participation";
    
    
    /**
     * 18. page-error-controller : 서비스 에러 페이지 담당
     * */
    
    let pageErrorUrl = baseUrl + "/error";
    
    /**
     * 19. page-feed-controller : 피드 페이지 담당
     * */
    
    
    let pageFeedMeUrl = pageFeedUrl + "/me";
    let pageFeedUpmUrl = pageFeedUrl + "/upm";
    
    /**
     * 20. page-home-controller : 시작 페이지 담당
     * */
    
    let pageHomeUrl = baseUrl + "/";
    
    
    /**
     * 21. page-music-controller : 뮤직 페이지 담당
     * */
    
    let pageMusicAlbumUrl = pageMusicUrl + "/album";
    let pageMusicAlbumAbroadUrl = pageMusicUrl + "/album_abroad";
    let pageMusicAlbumHomeUrl = pageMusicUrl + "/album_home";
    let pageMusicAlbumDetailUrl = pageMusicUrl + "/album/{id}";
    let pageMusicAlbumEditUrl = pageMusicUrl + "/album/{id}/edit";
    let pageMusicArtistUrl = pageMusicUrl + "/artist";
    let pageMusicArtistOneUrl = pageMusicUrl + "/artist/{id}";
    let pageMusicLeagueUrl = pageMusicUrl + "/league";
    let pageMusicRewardUrl = pageMusicUrl + "/reward";
    let pageMusicStoreUrl = pageMusicUrl + "/store";
    let pageMusicStoreRequestUrl = pageMusicUrl + "/store/request";
    let pageMusicStoreRequestHandleUrl = pageMusicUrl + "/store/request";
    let pageMusicStoreRequestDetailUrl = pageMusicUrl + "/store/request/{id}";
    let pageMusicStoreRequestDetailHandleUrl = pageMusicUrl + "/store/request/{id}";
    let pageMusicStoreRequestEditUrl = pageMusicUrl + "/store/request/{id}/edit";
    let pageMusicTrackEditHandleUrl = pageMusicUrl + "/track/{id}";
    let pageMusicTrackEditUrl = pageMusicUrl  + "/track/{id}/edit";
    let pageMusicVocalCastingUrl = pageMusicUrl  + "/vocal_casting";
    let pageMusicVocalCastingDetailUrl = pageMusicUrl  + "/vocal_casting/{id}";
    let pageMusicVocalCastingDetailHandleUrl = pageMusicUrl  + "/vocal_casting/{id}";
    let pageMusicVocalCastingEditUrl = pageMusicUrl  + "/vocal_casting/{id}/edit";
    let pageMusicVocalCastingNewCastingUrl = pageMusicUrl  + "/vocal_casting/new/casting";
    let pageMusicVocalCastingNewCastingHandleUrl = pageMusicUrl  + "/vocal_casting/new/casting";
    

    /**
     * 22. page-my-playlist-controller : 나의 플레이 리스트 페이지 담당
     * */
    let pageMyPlayListGetUrl =  pageMyPlayListUrl + "/collection";
    let pageMyPlayListDetailUrl = pageMyPlayListUrl + "/collection/{id}";
    let pageMyPlayListLikeUrl = pageMyPlayListUrl + "/like";
    let pageMyPlayListRecentUrl = pageMyPlayListUrl + "/recent";
    
    
    /**
     * 23. page-my-upm-controller : MY UPM 페이지 담당
     * */
    
    let pageMyUpmPointUrl = pageMyUpmUrl + "/point";
    let pageMyUpmProfileUrl = pageMyUpmUrl + "/profile";
    let pageMyUpmRewardUrl = pageMyUpmUrl + "/reward";
    let pageMyUpmTransactionUrl = pageMyUpmUrl + "/transaction";
    let pageMyUpmUploadUrl = pageMyUpmUrl + "/upload";
    
    /**
     * 24. page-player-controller : 뮤직플레이어 페이지 담당
     * */
    
    let pagePlayerUrl = baseUrl + "/player";
    
    
    /**
     * 25. page-search-controller : 검색결과 페이지 담당
     * */
    
    let pageSearchUrl = baseUrl + "/search";
    
    
    /**
     * 26. page-upload-controller : 업로드 페이지 담당
     * */
    
    let pageUploadMusicUrl = pageUploadUrl + "/music";
    let pageUploadMusicHandleUrl = pageUploadUrl + "/music";
    let pageUploadTrackUrl = pageUploadUrl + "/music/{id}/track";
    let pageUploadTrackHandleUrl = pageUploadUrl + "/music/{id}/track";
    let pageUploadVideoURl = pageUploadUrl + "/video";
    let pageUploadVideoHandleURl = pageUploadUrl + "/video";
    
    
    /**
     * 27. page-upm-news-controller : UPM 소식 페이지 담당
     * */
    
    let pageUpmNewsEventUrl = pageUpmNewsUrl + "/event";
    let pageUpmNewsNoticeUrl = pageUpmNewsUrl + "/notice";
    let pageUpmNewsUpmBancUrl = pageUpmNewsUrl + "/upm_band";
    
    
    /**
     * 28. page-video-controller : 비디오 페이지 담당
     * */
    
    let pageVideoDetailUrl = pageVideoUrl + "/{id}";
    let pageVideoUpdateHandleUrl = pageVideoUrl + "/{id}";
    let pageVideoEditUrl = pageVideoUrl + "/{id}/edit";
    let pageVideoGVUrl = pageVideoUrl + "/gv";
    let pageVideoHOTUrl = pageVideoUrl + "/hot";
    let pageVideoMVUrl = pageVideoUrl + "/mv";
    

}

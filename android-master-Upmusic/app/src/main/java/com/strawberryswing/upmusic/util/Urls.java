package com.strawberryswing.upmusic.util;

public class Urls {

    public String baseUrl = "http://up-music.ap-northeast-2.elasticbeanstalk.com";
    public String adminUrl = baseUrl + "/admin";
    public String apiUrl = baseUrl + "/api";
    public String componentUrl = baseUrl + "/component";


    /**
     *
     *  
     * 0. admin-home-controller : 관리자 홈
     * admin-music-controller : 관리자 홈
     * admin-video-controller : 관리자 홈
     * */


    public String adminHomeUrl = adminUrl + "";

    public String adminMusicUrl = adminUrl + "/music";
    public String adminMusicJudgingUrl = adminUrl + "/music/judging";

    public String adminMusicSeasonUrl = adminUrl + "/music/season";
    public String adminMusicSeasonDetailUrl = adminUrl + "/music/season/{id}";
    public String adminMusicSeasonUpdateUrl = adminUrl + "/music/season/{id}";
    public String adminMusicSeasonDeleteUrl = adminUrl + "/music/season/{id}/delete";
    public String adminMusicSeasonEditUrl = adminUrl + "/music/season/{id}/edit";
    public String adminMusicSeasonNewUrl = adminUrl + "/music/season/{id}/new";

    public String adminMusicTrackDetailUrl = adminUrl + "/music/track/{id}";
    public String adminMusicTrackAcceptUrl = adminUrl + "/music/track/{id}/accept";
    public String adminMusicTrackRejectUrl = adminUrl + "/music/track/{id}/accept";

    public String adminVideoUrl = adminUrl + "/video";
    public String adminVideoTrackDetailUrl = adminUrl + "/video/{id}";

    /**
     * 1. api-artist-controller : 아티스트 관련
     * */
    public String artistUrl = apiUrl + "/artist";
    public String artistAllUrl = artistUrl + "";
    public String artistOneUrl = artistUrl + "/{id}";
    public String artistSetLikeUrl = artistUrl + "{id}" + "/like_from_list";

    /**
     * 2. api-auth-controller : 인증관련
     * */

    public String authUrl = apiUrl + "/auth";
    public String authCheckEmailUrl = authUrl + "/check_email";
    public String authLoginUrl = authUrl + "/login";
    public String authRequestPhoneUrl = authUrl + "/request_phone_authentication_code";
    public String authPasswordEmailUrl = authUrl + "/request_pw_email_authentication_code";
    public String authPhoneEmailUrl = authUrl + "/request_pw_phone_authentication_code";

    /**
     * 3. api-collection-controller : 리스트 관련
     * */

    public String collectionUrl = apiUrl + "/collection";
    public String collectionAllUrl = collectionUrl + "";
    public String collectionOneUrl = collectionUrl + "/{id}";
    public String collectionDeleteUrl = collectionUrl + "/{id}";
    public String collectionAddTrackUrl = collectionUrl + "/{id}" + "/add_tracks";
    public String collectionAddPlaylistUrl = collectionUrl + "/{id}" + "/play";
    public String collectionRemoveTrackUrl = collectionUrl + "/{id}" + "/remove_tracks";
    public String collectionCreateUrl = collectionUrl + "/create";
    public String collectionRemoveUrl = collectionUrl + "/delete";

    /**
     * 4. api-member-controller : 멤버 관련
     * */

    public String memberUrl = apiUrl + "/member";
    public String memberSearchUrl = apiUrl + "/member" +"/search";


    /**
     * api-music-album-controller : 앨범 관련
     * */

    public String albumUrl = apiUrl + "/album";
    public String albumAllUrl = albumUrl;
    public String albumOneUrl = albumUrl+ "/{id}";
    public String albumAddCommentUrl = albumUrl + "/{id}" + "/comment";
    public String albumGetCommetUrl = albumUrl+ "/{id}" + "/comment" + "/{commentId}";
    public String albumRemoveCommetUrl = albumUrl+ "/{id}" + "/comment" + "/{commentId}";
    public String albumSetLikeUrl = albumUrl+ "/{id}" + "/comment" + "/like";
    public String albumSetLike2Url = albumUrl+ "/{id}" + "/comment" + "/like_from_list";


    /**
     * 5. api-music-track-controller : 곡 관련
     * */

    public String trackUrl = apiUrl + "/track";
    public String trackOneUrl = trackUrl;
    public String trackSetLikeUrl = trackUrl+ "/{id}" + "/like";
    public String trackSetTitleUrl = trackUrl + "/{id}" + "/title";
    public String trackDeleteUrl= trackUrl + "/delete_tracks";

    /**
     * 6. api-my-heartlist-controller : 좋아요 목록 관련
     * */

    public String heartlistUrl = apiUrl + "/my_heartlist";
    public String heartlistRemoveTrackUrl = heartlistUrl + "/remove_tracks";
    public String heartlistRemoveVideoUrl = heartlistUrl + "/remove_videos";

    /**
     * 7. api-my-playlist-controller : 재생목록 관련
     * */


    public String playlistUrl = apiUrl + "/my_playlist";
    public String playlistAddTrackUrl = playlistUrl + "/add_tracks";
    public String playlistAddVideoUrl = playlistUrl + "/add_video";
    public String playlistRemoveTrackUrl = playlistUrl + "/remove_tracks";
    public String playlistRemoveVideoUrl = playlistUrl + "/remove_videos";
    /**
     * 8. api-player-controller : 뮤직플레이어 관련
     * */
    public String playerUrl = apiUrl + "/player";
    public String playerGetMainTemplateUrl = playerUrl;
    public String playerAddPlayCountUrl = playerUrl + "/{id}" + "/add_play_cnt";
    public String playerRemoveTrackUrl = playerUrl + "/remove_tracks";

    /**
     * 9. api-store-controller : 스토어 관련
     * */
    public String storeUrl = apiUrl + "/store";
    public String storeDownloadUrl = storeUrl + "/download" + "/{trackId}" + "/{trackFormat}";
    public String storeIsBuyerUrl = storeUrl + "/is_buyer" + "/{trackId}";

    /**
     * 10. api-store-request-controller : 제작의뢰 관련
     * */

    public String storeRequestUrl = storeUrl + "/request";
    public String storeRequestGetUrl = storeRequestUrl + "/{id}";
    public String storeRequestDeleteUrl = storeRequestUrl + "/{id}";
    public String storeRequestAddCommetUrl = storeRequestUrl + "/{id}" + "/comment";
    public String storeRequestGetCommentUrl = storeRequestUrl + "/{id}" + "/comment" + "/{commentId}";
    public String storeRequestUpdateCommentUrl = storeRequestUrl + "/{id}" + "/comment" + "/{commentId}";
    public String storeRequestDeleteCommentUrl = storeRequestUrl + "/{id}" + "/comment" + "/{commentId}";

    /**
     * 11. api-video-controller : 영상 관련
     * */


    public String videoUrl = baseUrl + "/video";
    public String videoGetAllUrl = videoUrl + "";
    public String videoGetOneUrl = videoUrl + "/{id}";
    public String videoAddCommentUrl = videoUrl + "/{id}" + "/comment";
    public String videoGetCommentUrl = videoUrl + "/{id}" + "/comment" + "/{commentId}";
    public String videoUpdateCommentUrl = videoUrl + "/{id}" + "/comment" + "/{commentId}";
    public String videoDeleteCommentUrl = videoUrl + "/{id}" + "/comment" + "/{commentId}";
    public String videoSetLikeUrl = videoUrl + "/like";
    public String videoAddPlaylistUrl = videoUrl + "/play";
    public String videoRemoveVideoUrl = videoUrl + "/delete_videos";


    /**
     * 12. api-vocal-casting-controller : 보컬 캐스팅 관련
     * */


    public String vocalCastingUrl = apiUrl + "/vocal_casting";
    public String vocalCastingGetAllUrl = vocalCastingUrl + "";
    public String vocalCastingGetOneUrl = vocalCastingUrl + "/{id}";
    public String vocalCastingRemoveUrl = vocalCastingUrl + "/{id}";
    public String vocalCastingAddCommentUrl = vocalCastingUrl + "/{id}" + "/comment";
    public String vocalCastingGetCommentUrl = vocalCastingUrl + "/{id}" + "/comment" + "/{commentId}";
    public String vocalCastingUpdateCommentUrl = vocalCastingUrl + "/{id}" + "/comment" + "/{commentId}";
    public String vocalCastingRemoveCommentUrl = vocalCastingUrl + "/{id}" + "/comment" + "/{commentId}";
    public String vocalCastingSetLikeUrl = vocalCastingUrl + "/{id}" + "/like";



    /**
     * 13. component-music-controller : 뮤직 컴포넌트 담당
     * */


    public String componentMusicUrl = componentUrl + "/music";
    public String componentMusicAbroadAlbumUrl = componentMusicUrl + "/album_abroad/ga";
    public String componentMusicAbroadSingleUrl = componentMusicUrl + "/album_abroad/sa";
    public String componentMusicHomeAlbumUrl = componentMusicUrl + "/album_home/ga";
    public String componentMusicHomeSingleUrl = componentMusicUrl + "/album_home/sa";

    public String componentGetAlbumCommentsUrl = componentMusicUrl + "/album/{id}/comments";
    public String componentGetAlbumUrl = componentMusicUrl + "/album/ga";
    public String componentGetSingleUrl = componentMusicUrl + "/album/sa";

    public String componentGetArtistUrl = componentMusicUrl + "/artist";
    public String componentGetFamilyArtistUrl = componentMusicUrl + "/family_artist";
    public String componentGetGuideVocalUrl = componentMusicUrl + "/guide_vocal";

    public String componentGetLeagueGenreUrl = componentMusicUrl + "/league_genre";
    public String componentGetLeagueSeasonUrl = componentMusicUrl + "/league_season";
    public String componentGetLeagueTop50Url = componentMusicUrl + "/league_top50";
    public String componentGetNewArtistUrl = componentMusicUrl + "/new_artist";

    public String componentGetRewardGenreUrl = componentMusicUrl + "/reward_genre";
    public String componentGetRewardRealtimeUrl = componentMusicUrl + "/reward_realtime";
    public String componentGetRewardThemeUrl = componentMusicUrl + "/reward_theme";

    public String componentGetStoreUrl = componentMusicUrl + "/store";
    public String componentGetStoreRequestUrl = componentMusicUrl + "/store/request";

    public String componentGetCommentsFromStoreRequestUrl = componentMusicUrl + "/store/request/{id}/comments";
    public String componentGetCommentsFromVocalCastingUrl = componentMusicUrl + "/vocal_casting/{id}/comments";


    /**
     * 14. component-my-playlist-controller : 마이 플레이리스트 컴포넌트 담당
     * */

    public String componentMyPlaylistUrl = componentUrl + "/my_playlist";
    public String componentMyPlaylistGetUrl = componentMyPlaylistUrl + "/collection";
    public String componentMyPlaylistGetModalUrl = componentMyPlaylistUrl + "/collection_modal";
    public String componentMyPlaylistGetDetailUrl = componentMyPlaylistUrl + "/collection/{id}";
    public String componentMyPlaylistGetLikeTrackUrl = componentMyPlaylistUrl + "/like_track";
    public String componentMyPlaylistGetLikeVideoUrl = componentMyPlaylistUrl + "/like_video";
    public String componentMyPlaylistGetRecentTrackUrl = componentMyPlaylistUrl + "/recent_track";
    public String componentMyPlaylistGetRecentVideoUrl = componentMyPlaylistUrl + "/recent_video";

    /**
     * 15. component-video-controller : 마이 플레이리스트 컴포넌트 담당
     * */

    public String componentVideoUrl = componentUrl + "/video";
    public String componentVideoGetCommentsUrl = componentVideoUrl + "/{id}/comments";


    /**
     * 16. page-auth-controller : 로그인/ 회원가입 페이지 담당
     * */


    public String pageAuthUrl = baseUrl + "/auth";
    public String pageAuthForgotPasswordUrl = pageAuthUrl + "/forgot_password";
    public String pageAuthForgotPasswordHandleUrl = pageAuthUrl + "/forgot_password";
    public String pageAuthLoginUrl = pageAuthUrl + "/login";
    public String pageAuthRegistrationUrl = pageAuthUrl + "/registration";
    public String pageAuthRegistrationHandleUrl = pageAuthUrl + "/registration";
    public String pageAuthUpdatePasswordUrl = pageAuthUrl + "/update_password";


    /**
     * 17. page-crowd-funding-controller : 크라우드펀딩 페이지 담당
     * */

    public String pageCrowdFundingUrl = baseUrl + "/crowd_funding";
    public String pageCrowdFundingGetUrl = pageCrowdFundingUrl + "";
    public String pageCrowdFundingGetHotProjectUrl = pageCrowdFundingUrl + "/hot_project";
    public String pageCrowdFundingGetInformationUrl = pageCrowdFundingUrl + "/information";
    public String pageCrowdFundingGetNewProjectUrl = pageCrowdFundingUrl + "/new_project";
    public String pageCrowdFundingGetParticipationUrl = pageCrowdFundingUrl + "/participation";


    /**
     * 18. page-error-controller : 서비스 에러 페이지 담당
     * */

    public String pageErrorUrl = baseUrl + "/error";

    /**
     * 19. page-feed-controller : 피드 페이지 담당
     * */


    public String pageFeedUrl = baseUrl + "/feed";
    public String pageFeedMeUrl = pageFeedUrl + "/me";
    public String pageFeedUpmUrl = pageFeedUrl + "/upm";

    /**
     * 20. page-home-controller : 시작 페이지 담당
     * */

    public String pageHomeUrl = baseUrl + "/";


    /**
     * 21. page-music-controller : 뮤직 페이지 담당
     * */


    public String pageMusicUrl = baseUrl + "/music";
    public String pageMusicAlbumUrl = pageMusicUrl + "/album";
    public String pageMusicAlbumAbroadUrl = pageMusicUrl + "/album_abroad";
    public String pageMusicAlbumHomeUrl = pageMusicUrl + "/album_home";
    public String pageMusicAlbumDetailUrl = pageMusicUrl + "/album/{id}";
    public String pageMusicAlbumEditUrl = pageMusicUrl + "/album/{id}/edit";
    public String pageMusicArtistUrl = pageMusicUrl + "/artist";
    public String pageMusicArtistOneUrl = pageMusicUrl + "/artist/{id}";
    public String pageMusicLeagueUrl = pageMusicUrl + "/league";
    public String pageMusicRewardUrl = pageMusicUrl + "/reward";
    public String pageMusicStoreUrl = pageMusicUrl + "/store";
    public String pageMusicStoreRequestUrl = pageMusicUrl + "/store/request";
    public String pageMusicStoreRequestHandleUrl = pageMusicUrl + "/store/request";
    public String pageMusicStoreRequestDetailUrl = pageMusicUrl + "/store/request/{id}";
    public String pageMusicStoreRequestDetailHandleUrl = pageMusicUrl + "/store/request/{id}";
    public String pageMusicStoreRequestEditUrl = pageMusicUrl + "/store/request/{id}/edit";
    public String pageMusicTrackEditHandleUrl = pageMusicUrl + "/track/{id}";
    public String pageMusicTrackEditUrl = pageMusicUrl  + "/track/{id}/edit";
    public String pageMusicVocalCastingUrl = pageMusicUrl  + "/vocal_casting";
    public String pageMusicVocalCastingDetailUrl = pageMusicUrl  + "/vocal_casting/{id}";
    public String pageMusicVocalCastingDetailHandleUrl = pageMusicUrl  + "/vocal_casting/{id}";
    public String pageMusicVocalCastingEditUrl = pageMusicUrl  + "/vocal_casting/{id}/edit";
    public String pageMusicVocalCastingNewCastingUrl = pageMusicUrl  + "/vocal_casting/new/casting";
    public String pageMusicVocalCastingNewCastingHandleUrl = pageMusicUrl  + "/vocal_casting/new/casting";



    /**
     * 22. page-my-playlist-controller : 나의 플레이 리스트 페이지 담당
     * */
    public String pageMyPlayListUrl = baseUrl + "/my_playlist";
    public String pageMyPlayListGetUrl =  pageMyPlayListUrl + "/collection";
    public String pageMyPlayListDetailUrl = pageMyPlayListUrl + "/collection/{id}";
    public String pageMyPlayListLikeUrl = pageMyPlayListUrl + "/like";
    public String pageMyPlayListRecentUrl = pageMyPlayListUrl + "/recent";


    /**
     * 23. page-my-upm-controller : MY UPM 페이지 담당
     * */

    public String pageMyUpmUrl = baseUrl + "/my_upm";
    public String pageMyUpmPointUrl = pageMyUpmUrl + "/point";
    public String pageMyUpmProfileUrl = pageMyUpmUrl + "/profile";
    public String pageMyUpmRewardUrl = pageMyUpmUrl + "/reward";
    public String pageMyUpmTransactionUrl = pageMyUpmUrl + "/transaction";
    public String pageMyUpmUploadUrl = pageMyUpmUrl + "/upload";

    /**
     * 24. page-player-controller : 뮤직플레이어 페이지 담당
     * */

    public String pagePlayerUrl = baseUrl + "/player";


    /**
     * 25. page-search-controller : 검색결과 페이지 담당
     * */

    public String pageSearchUrl = baseUrl + "/search";


    /**
     * 26. page-upload-controller : 업로드 페이지 담당
     * */

    public String pageUploadUrl = baseUrl + "/upload";
    public String pageUploadMusicUrl = pageUploadUrl + "/music";
    public String pageUploadMusicHandleUrl = pageUploadUrl + "/music";
    public String pageUploadTrackUrl = pageUploadUrl + "/music/{id}/track";
    public String pageUploadTrackHandleUrl = pageUploadUrl + "/music/{id}/track";
    public String pageUploadVideoURl = pageUploadUrl + "/video";
    public String pageUploadVideoHandleURl = pageUploadUrl + "/video";


    /**
     * 27. page-upm-news-controller : UPM 소식 페이지 담당
     * */



    public String pageUpmNewsUrl = baseUrl + "/upm_news";
    public String pageUpmNewsEventUrl = pageUpmNewsUrl + "/event";
    public String pageUpmNewsNoticeUrl = pageUpmNewsUrl + "/notice";
    public String pageUpmNewsUpmBancUrl = pageUpmNewsUrl + "/upm_band";


    /**
     * 28. page-video-controller : 비디오 페이지 담당
     * */


    public String pageVideoUrl = baseUrl + "/video";
    public String pageVideoDetailUrl = pageVideoUrl + "/{id}";
    public String pageVideoUpdateHandleUrl = pageVideoUrl + "/{id}";
    public String pageVideoEditUrl = pageVideoUrl + "/{id}/edit";
    public String pageVideoGVUrl = pageVideoUrl + "/gv";
    public String pageVideoHOTUrl = pageVideoUrl + "/hot";
    public String pageVideoMVUrl = pageVideoUrl + "/mv";


}

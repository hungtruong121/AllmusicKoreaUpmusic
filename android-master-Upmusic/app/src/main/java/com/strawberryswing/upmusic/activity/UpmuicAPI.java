package com.strawberryswing.upmusic.activity;

import com.google.gson.JsonObject;
import com.strawberryswing.upmusic.requestModel.EmailRequest;
import com.strawberryswing.upmusic.requestModel.FacebookRequest;
import com.strawberryswing.upmusic.requestModel.GoogleRequest;
import com.strawberryswing.upmusic.requestModel.KakaoRequest;
import com.strawberryswing.upmusic.requestModel.NaverRequest;
import com.strawberryswing.upmusic.model.APIResponse;
import com.strawberryswing.upmusic.model.MusicTrack;
import com.strawberryswing.upmusic.model.Video;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UpmuicAPI {

    public static String SHARED_PREFERENCE_NAME_COOKIE = "SHARED_PREFERENCE_NAME_COOKIE_OF_USER";
    String PREVIOUS_BASE_URL = "http://up-music.ap-northeast-2.elasticbeanstalk.com";
    String BASE_URL = "https://upmusic.azurewebsites.net";
    String SOCKET_URL = "upmusic.azurewebsites.net";

    String TEMP_URL = "http://172.20.10.6:8080";
    String TEMP_URL_EXCEPT_HEADER = "172.20.10.6:8080";


    /**
     * /search
     * value = "이메일 계정으로 회원 검색", notes="params의 예 {\"email\":\"tester0@gmail.com\"}"
     */

    // TODO add
    /**
     * api-music-album-controller
     * 곡과 관련된 API 컨트롤러
     * @return
     */

    // TODO add
    /**
     * api-video-controller
     * 곡과 관련된 API 컨트롤러
     * @return
     */


    /**
     * api-music-track-controller
     * 곡과 관련된 API 컨트롤러
     * @return
     */
    @GET("/api/track") // 전체 곡을 반환
    public Call<List<MusicTrack>> getAllTracks();

//    @Headers("Content-Type: application/json")
    @GET("/api/track/{id}") // id에 해당하는 곡을 반환
    public Call<MusicTrack> getTrackById(@Path("id") String id);
//    public Call<MusicTrack> getTrackById(@Path("id") String id, @Body Map<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("/api/track/{id}/like")
    public Call<JsonObject> sendLikeOrNotStatus(@Path("id") String id, @Body Map<String, String> body);

    @POST("/api/track/{id}/title")
    public Call<List<MusicTrack>> setTitleById();

    @POST("/api/track/check_subject") // 곡명 중복검사
    public Call<String> checkSubject();

    @DELETE("/api/track/delete_tracks")
    public Call<List<MusicTrack>> deleteTracksById();

    @DELETE("/api/track/delete_tracks_with_album")
    public Call<List<MusicTrack>> deleteTracksWithAlbumById();


    /**
     *
     * api-my-playlist-controller
     * 재생목록과 관련된 API 컨트롤러
     * @return
     */
    @POST("/api/my_playlist/add_top50_tracks")
    public Call<String> addTop50TracksToMyPlaylist();

    @POST("/api/my_playlist/add_tracks")
    public Call<String> addTracksToMyPlaylist();

    @POST("/api/my_playlist/add_video")
    public Call<String> addVideoToMyPlaylist();

    @DELETE("/api/my_playlist/remove_tracks")
    public Call<JsonObject> deleteTracksFromMyPlaylist();
    //params의 예 {"ids":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가


    @DELETE("/api/my_playlist/remove_videos")
    public Call<List<MusicTrack>> deleteVideosFromMyPlaylist();

    /**
     *
     * @return
     */
    @GET("/api/video")
    public Call<List<Video>> getAllVideos();

    @GET("/api/video/{id}")
    public Call<MusicTrack> getVideoById();

    @GET("/api/video/{id}/comment")
    public Call<MusicTrack> commentOnVideoId();

    /**
     *
     * api-auth-controller
     * 인증과 관련된 컨트롤러
     * @return
     */
    @POST("/api/auth/check_email")
    public Call<String> authEmailCheck();

    @POST("/api/auth/check_session")
    public Call<JsonObject> authCheckSession();

    @POST("/api/auth/login")
    public Call<APIResponse> authEmailLogin(@Body EmailRequest request);

    @POST("/api/auth/login_or_register_with_facebook")
    public Call<APIResponse> authFacebookLoginOrRegister(@Body FacebookRequest request);

    @POST("/api/auth/login_or_register_with_google")
    public Call<APIResponse> authGoogleLoginOrRegister(@Body GoogleRequest request);

    @POST("/api/auth/login_or_register_with_kakao")
    public Call<APIResponse> authKakaoLoginOrRegister(@Body KakaoRequest request);

    @POST("/api/auth/login_or_register_with_naver")
    public Call<APIResponse> authNaverLoginOrRegister(@Body NaverRequest request);

    @POST("/api/auth/request_phone_authentication_code")
    public Call<String> authRequestPhoneAuthenticationCode();

    @POST("/api/auth/request_pw_email_authentication_code")
    public Call<String> authRequestPWEmailAuthenticationCode();

    @POST("/api/auth/request_pw_email_authentication_code")
    public Call<String> authRequestPWPhoneAuthenticationCode();

    @GET("/api/auth/token")
    public Call<String> authToken();

    /**
     *
     * api-collection-controller
     * 내가 담은 리스트와 관련된 컨트롤러
     * @return
     */
    @GET("/api/collection")
    public Call<JsonObject> getCollection();
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @GET("/api/collection/{id}")
    public Call<JsonObject> getCollectionByID(@Path("id") String id);
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @DELETE("/api/collection/{id}")
    public Call<JsonObject> deleteCollectionByID(@Path("id") String id);
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @POST("/api/collection/{id}/add_tracks")
    public Call<JsonObject> addTracksOnCollectionByID(@Path("id") String id,@Body Map<String, Long[]> body );
    //params의 예 {"ids":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가


    @POST("/api/collection/create")
    public Call<JsonObject> createCollection(@Body Map<String, String> body);
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가


    @POST("/api/collection/{id}/play")
    public Call<JsonObject> addOnPlaylist(@Path("id") String id);
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @DELETE("/api/collection/{id}")
    public Call<JsonObject> removeTracksFromCollectionByID(@Path("id") String id);
    //params의 예 {"ids":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가






    // TODO Maybe fix.... PARAM (@Path("id") String id)
    @DELETE("/api/collection/delete")
    public Call<String> deleteList(@Path("id") String id);
    //params의 예 {"ids":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    /**
     *
     * api-player-controller
     * 뮤직플레이어와 관련된 컨트롤러
     * @return
     */
    @GET("/api/player")
    public Call<String> getPlayerMainPage();
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @POST("/api/player/{id}/add_play_cnt")
    public Call<JsonObject> addPlayCountOnTrackByID(@Path("id") String id);
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @POST("/api/player/add_liked_tracks")
    public Call<String> addLikedTracks();
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @POST("/api/player/add_recent_played_tracks")
    public Call<String> addRecentPlayedTracks();
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @POST("/api/player/add_top50_tracks")
    public Call<String> addTop50Tracks();
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    // TODO Maybe fix.... PARAM (@Path("id") String id)
    @POST("/api/player/add_tracks")
    public Call<String> addTracks(@Path("id") String id);
    //params의 예 {"ids":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    // TODO Maybe fix.... PARAM (@Path("id") String id)
    @DELETE("/api/player/add_tracks")
    public Call<String> removeTracks(@Path("id") String id);
    //params의 예 {"ids":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @Headers("Content-Type: application/json")
    @POST("/api/player/playlist")
    public Call<JsonObject> getPlayerPlaylist(@Body Map<String, String> body);
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/api/player/remove_tracks", hasBody = true)
    public Call<JsonObject> deleteTracksFromMyPlayer(@Body Map<String, String[]> body);
    //params의 예 {"ids":[123456,789012]}, token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    @Headers("Content-Type: application/json")
    @POST("/api/player/update_list")
    public Call<JsonObject> updatePlayerPlaylist(@Body Map<String, long[]> body);
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가

    //    https://upmusic.azurewebsites.net/
    @Headers("Content-Type: application/json")
    @POST("/api/point_transaction/shareReward") // @Body Map<String, String> body
    public Call<JsonObject> sharePointTransaction();
    //token을 이용한 회원인증이 필요한 경우엔 params에 "token":"발급받은 토큰 문자열"을 추가



}

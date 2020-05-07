package com.strawberryswing.upmusic.activity;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthTokenAPI {

    String BASE_URL = "https://upmusic-auth-token-server-3.herokuapp.com";
//
//    @FormUrlEncoded
//    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("/verifyToken")
    public Call<String> getVerifyToken(@Body AuthToken token);


    @POST("/verifyTokenAsNaver")
    public Call<String> getVerifyTokenAsNaver(@Body AuthToken token);
}

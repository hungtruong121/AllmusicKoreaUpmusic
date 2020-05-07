package com.strawberryswing.upmusic.requestModel;

public class KakaoRequest {
    private String access_token;

    public KakaoRequest(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}

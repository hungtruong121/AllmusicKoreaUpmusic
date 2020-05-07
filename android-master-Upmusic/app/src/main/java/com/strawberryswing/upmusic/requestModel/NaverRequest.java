package com.strawberryswing.upmusic.requestModel;

public class NaverRequest {

    private String access_token;

    public NaverRequest(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}

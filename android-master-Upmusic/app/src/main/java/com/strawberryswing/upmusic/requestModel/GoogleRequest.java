package com.strawberryswing.upmusic.requestModel;

public class GoogleRequest {

    private String id_token;

    public GoogleRequest(String id_token) {
        this.id_token = id_token;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
}

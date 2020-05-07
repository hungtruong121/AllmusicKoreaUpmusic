package com.strawberryswing.upmusic.activity;

public class AuthToken {
    private String token;
    private String id;
    private String email;
    private String displayName;
    private String photoURL;

    //userId, email, displayName, photoURL

    public AuthToken(String token) {
        this.token = token;
    }

    public AuthToken(String token, String id, String email, String displayName, String photoURL) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.photoURL = photoURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}

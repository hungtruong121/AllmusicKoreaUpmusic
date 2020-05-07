package com.strawberryswing.upmusic.model;

import android.util.Log;

public class MusicTrack {

    private String adminUrl;
    private String artistId;
    private String artistNick;
    private String artistUrl;
    private String coverImageUrl;
    private String duration;
    private String filenameUrl;
    private String id;
    private String liked;
    private String musicAlbumId;
    private String rejectedReason;
    private String subject;
    private String[] themeNames;
    private String titleTrack;
    private String url;

    private String heartCnt;

    /// FOR MULTIPLE SELECTION.
    private boolean isSelected = false;



    public String getHeartCnt() {
        return heartCnt;
    }

    public void setHeartCnt(String heartCnt) {
        this.heartCnt = heartCnt;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public String getArtistNick() {
        return artistNick;
    }

    public void setArtistNick(String artistNick) {
        this.artistNick = artistNick;
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    public void setArtistUrl(String artistUrl) {
        this.artistUrl = artistUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFilenameUrl() {
        return filenameUrl;
    }

    public void setFilenameUrl(String filenameUrl) {
        this.filenameUrl = filenameUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }


    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getThemeNames() {
        return themeNames;
    }

    public void setThemeNames(String[] themeNames) {
        this.themeNames = themeNames;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getMusicAlbumId() {
        return musicAlbumId;
    }

    public void setMusicAlbumId(String musicAlbumId) {
        this.musicAlbumId = musicAlbumId;
    }

    public String getTitleTrack() {
        return titleTrack;
    }

    public void setTitleTrack(String titleTrack) {
        this.titleTrack = titleTrack;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return this.adminUrl + "   \n, "+ this.artistId + "   \n, "+ this.artistNick + "  \n, "+ this.artistUrl + "  \n, "+ this.coverImageUrl + "  \n,  " + this.filenameUrl
                + "\n.";
    }
    @Override
    public boolean equals(Object obj) {

        if (this.id.equals(((MusicTrack)obj).getId())) {
            return true;
        }

        return false;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

}

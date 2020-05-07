package com.strawberryswing.upmusic.model;

public class MusicAlbum {

    private int acceptedTrackCnt;
    private MusicTrack acceptedTracks;
    private String albumLocal; // HOME OR ABROAD
    private String albumType; // SA OR GA
    private String artistNick;
    private String artistUrl;
    private String coverImageUrl;
    private String createdAt;
    private String description;
    private String genreName;
    private int heartCnt;
    private int hitCnt;
    private int hotPoint;
    private String id;
    private String imageFilename;
    private boolean liked;
    private Member member;
    private String subject;
    private MusicTrack tracks;
    private String url;

    public int getAcceptedTrackCnt() {
        return acceptedTrackCnt;
    }

    public void setAcceptedTrackCnt(int acceptedTrackCnt) {
        this.acceptedTrackCnt = acceptedTrackCnt;
    }

    public MusicTrack getAcceptedTracks() {
        return acceptedTracks;
    }

    public void setAcceptedTracks(MusicTrack acceptedTracks) {
        this.acceptedTracks = acceptedTracks;
    }

    public String getAlbumLocal() {
        return albumLocal;
    }

    public void setAlbumLocal(String albumLocal) {
        this.albumLocal = albumLocal;
    }

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getHeartCnt() {
        return heartCnt;
    }

    public void setHeartCnt(int heartCnt) {
        this.heartCnt = heartCnt;
    }

    public int getHitCnt() {
        return hitCnt;
    }

    public void setHitCnt(int hitCnt) {
        this.hitCnt = hitCnt;
    }

    public int getHotPoint() {
        return hotPoint;
    }

    public void setHotPoint(int hotPoint) {
        this.hotPoint = hotPoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MusicTrack getTracks() {
        return tracks;
    }

    public void setTracks(MusicTrack tracks) {
        this.tracks = tracks;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

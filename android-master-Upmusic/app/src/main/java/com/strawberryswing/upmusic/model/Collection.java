package com.strawberryswing.upmusic.model;

public class Collection {


    private String createdAt;
    private String id;
    private Member member;
    private String subject;
    private int trackCnt;
    private String updatedAt;


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getTrackCnt() {
        return trackCnt;
    }

    public void setTrackCnt(int trackCnt) {
        this.trackCnt = trackCnt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

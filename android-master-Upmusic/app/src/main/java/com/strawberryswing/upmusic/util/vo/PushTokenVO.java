package com.strawberryswing.upmusic.util.vo;

import com.google.gson.annotations.SerializedName;

public class PushTokenVO {
    @SerializedName("status")
    public String status;
    @SerializedName("code")
    public String code;
    @SerializedName("msg")
    public String msg;
}

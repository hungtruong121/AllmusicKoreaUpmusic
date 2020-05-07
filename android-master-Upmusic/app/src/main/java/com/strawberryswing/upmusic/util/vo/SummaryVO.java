package com.strawberryswing.upmusic.util.vo;

import com.google.gson.annotations.SerializedName;

public class SummaryVO {
    @SerializedName("status")
    public String status;
    @SerializedName("code")
    public String code;
    @SerializedName("data")
    public DataVO data;

    public class DataVO {
        @SerializedName("order_cnt")
        public String order_cnt;
        @SerializedName("wait_cnt")
        public String wait_cnt;
        @SerializedName("push_cnt")
        public String push_cnt;
    }
}

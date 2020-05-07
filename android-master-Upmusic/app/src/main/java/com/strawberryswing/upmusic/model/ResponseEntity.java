package com.strawberryswing.upmusic.model;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseEntity {

    private JsonArray body;
    private String statusCode;
    private String statusCodeValue;

    public JsonArray getBody() {
        return body;
    }

    public void setBody(JsonArray body) {
        this.body = body;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCodeValue() {
        return statusCodeValue;
    }

    public void setStatusCodeValue(String statusCodeValue) {
        this.statusCodeValue = statusCodeValue;
    }
}

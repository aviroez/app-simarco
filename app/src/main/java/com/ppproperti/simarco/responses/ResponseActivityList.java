package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Activity;

import java.util.List;

public class ResponseActivityList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<Activity> data;

    @SerializedName("message")
    private String message;

    public ResponseActivityList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Activity> getData() {
        return data;
    }

    public void setData(List<Activity> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

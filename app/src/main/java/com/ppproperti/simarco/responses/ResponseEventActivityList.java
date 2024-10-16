package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.EventActivity;

import java.util.List;

public class ResponseEventActivityList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<EventActivity> data;

    @SerializedName("message")
    private String message;

    public ResponseEventActivityList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<EventActivity> getData() {
        return data;
    }

    public void setData(List<EventActivity> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

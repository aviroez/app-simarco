package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.EventActivityLog;

import java.util.List;

public class ResponseEventActivityLogList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<EventActivityLog> data;

    @SerializedName("message")
    private String message;

    public ResponseEventActivityLogList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<EventActivityLog> getData() {
        return data;
    }

    public void setData(List<EventActivityLog> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

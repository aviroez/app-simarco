package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Event;

import java.util.List;

public class ResponseEventList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<Event> data;

    @SerializedName("message")
    private String message;

    public ResponseEventList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

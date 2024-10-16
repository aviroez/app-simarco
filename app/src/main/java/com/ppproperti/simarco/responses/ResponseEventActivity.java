package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.EventActivity;

public class ResponseEventActivity {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private EventActivity data;

    @SerializedName("message")
    private String message;

    public ResponseEventActivity() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public EventActivity getData() {
        return data;
    }

    public void setData(EventActivity data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

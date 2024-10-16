package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.EventActivityLog;

public class ResponseEventActivityLog {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private EventActivityLog data;

    @SerializedName("message")
    private String message;

    public ResponseEventActivityLog() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public EventActivityLog getData() {
        return data;
    }

    public void setData(EventActivityLog data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

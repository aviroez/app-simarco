package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Event;

public class ResponseEvent {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Event data;

    @SerializedName("message")
    private String message;

    public ResponseEvent() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Event getData() {
        return data;
    }

    public void setData(Event data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

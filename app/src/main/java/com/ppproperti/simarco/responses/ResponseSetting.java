package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Setting;

public class ResponseSetting {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Setting data;

    @SerializedName("message")
    private String message;

    public ResponseSetting() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Setting getData() {
        return data;
    }

    public void setData(Setting data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Setting;

import java.util.List;

public class ResponseSettingList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<Setting> data;

    @SerializedName("message")
    private String message;

    public ResponseSettingList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Setting> getData() {
        return data;
    }

    public void setData(List<Setting> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

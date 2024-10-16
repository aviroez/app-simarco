package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Order;

import java.util.List;

public class ResponseOrderList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<Order> data;

    @SerializedName("message")
    private String message;

    public ResponseOrderList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Order;

public class ResponseOrder {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Order data;

    @SerializedName("message")
    private String message;

    public ResponseOrder() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Order getData() {
        return data;
    }

    public void setData(Order data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.PaymentType;

import java.util.List;

public class ResponsePaymentTypeList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<PaymentType> data;

    @SerializedName("message")
    private String message;

    public ResponsePaymentTypeList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PaymentType> getData() {
        return data;
    }

    public void setData(List<PaymentType> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.PaymentSchema;

import java.util.List;

public class ResponsePaymentSchemaList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<PaymentSchema> data;

    @SerializedName("message")
    private String message;

    public ResponsePaymentSchemaList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PaymentSchema> getData() {
        return data;
    }

    public void setData(List<PaymentSchema> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

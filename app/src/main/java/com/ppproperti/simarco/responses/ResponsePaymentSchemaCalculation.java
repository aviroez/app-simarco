package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;

public class ResponsePaymentSchemaCalculation {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private PaymentSchemaCalculation data;

    @SerializedName("message")
    private String message;

    public ResponsePaymentSchemaCalculation() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public PaymentSchemaCalculation getData() {
        return data;
    }

    public void setData(PaymentSchemaCalculation data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

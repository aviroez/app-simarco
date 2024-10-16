package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.ApartmentUnitPrice;

public class ResponseApartmentUnitPrice {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private ApartmentUnitPrice data;

    @SerializedName("message")
    private String message;

    public ResponseApartmentUnitPrice() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ApartmentUnitPrice getData() {
        return data;
    }

    public void setData(ApartmentUnitPrice data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

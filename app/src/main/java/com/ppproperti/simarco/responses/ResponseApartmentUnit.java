package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.ApartmentUnit;

public class ResponseApartmentUnit {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private ApartmentUnit data;

    @SerializedName("message")
    private String message;

    public ResponseApartmentUnit() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ApartmentUnit getData() {
        return data;
    }

    public void setData(ApartmentUnit data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Apartment;

import java.util.List;

public class ResponseApartmentList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<Apartment> data;

    @SerializedName("message")
    private String message;

    public ResponseApartmentList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Apartment> getData() {
        return data;
    }

    public void setData(List<Apartment> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

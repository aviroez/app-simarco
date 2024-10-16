package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.ApartmentFloor;

import java.util.List;

public class ResponseApartmentFloorList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<ApartmentFloor> data;

    @SerializedName("message")
    private String message;

    public ResponseApartmentFloorList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ApartmentFloor> getData() {
        return data;
    }

    public void setData(List<ApartmentFloor> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.ApartmentUnit;

import java.util.List;

public class ResponseApartmentUnitList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<ApartmentUnit> data;

    @SerializedName("message")
    private String message;

    public ResponseApartmentUnitList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ApartmentUnit> getData() {
        return data;
    }

    public void setData(List<ApartmentUnit> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.ApartmentTower;

import java.util.List;

public class ResponseApartmentTowerList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<ApartmentTower> data;

    @SerializedName("message")
    private String message;

    public ResponseApartmentTowerList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ApartmentTower> getData() {
        return data;
    }

    public void setData(List<ApartmentTower> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

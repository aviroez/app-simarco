package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.CustomerLeadDetail;

import java.util.List;

public class ResponseCustomerLeadDetailList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<CustomerLeadDetail> data;

    @SerializedName("message")
    private String message;

    public ResponseCustomerLeadDetailList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<CustomerLeadDetail> getData() {
        return data;
    }

    public void setData(List<CustomerLeadDetail> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

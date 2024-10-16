package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.CustomerLead;

import java.util.List;

public class ResponseCustomerLeadList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<CustomerLead> data;

    @SerializedName("message")
    private String message;

    public ResponseCustomerLeadList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<CustomerLead> getData() {
        return data;
    }

    public void setData(List<CustomerLead> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

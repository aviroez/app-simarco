package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.CustomerLead;

public class ResponseCustomerLead {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private CustomerLead data;

    @SerializedName("message")
    private String message;

    public ResponseCustomerLead() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CustomerLead getData() {
        return data;
    }

    public void setData(CustomerLead data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.CustomerLeadDetail;

public class ResponseCustomerLeadDetail {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private CustomerLeadDetail data = new CustomerLeadDetail();

    @SerializedName("message")
    private String message;

    public ResponseCustomerLeadDetail() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CustomerLeadDetail getData() {
        return data;
    }

    public void setData(CustomerLeadDetail data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.CustomerLeadPagination;

public class ResponseCustomerLeadListPagination {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private CustomerLeadPagination data;

    @SerializedName("message")
    private String message;

    public ResponseCustomerLeadListPagination() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CustomerLeadPagination getData() {
        return data;
    }

    public void setData(CustomerLeadPagination data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Customer;

import java.util.List;

public class ResponseCustomerList {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<Customer> data;

    @SerializedName("message")
    private String message;

    public ResponseCustomerList() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Customer> getData() {
        return data;
    }

    public void setData(List<Customer> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

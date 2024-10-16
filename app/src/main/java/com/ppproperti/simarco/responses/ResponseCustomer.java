package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Customer;

public class ResponseCustomer {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Customer data;

    @SerializedName("message")
    private String message;

    public ResponseCustomer() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Customer getData() {
        return data;
    }

    public void setData(Customer data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

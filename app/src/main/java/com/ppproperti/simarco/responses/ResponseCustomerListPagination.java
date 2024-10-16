package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.CustomerPagination;

public class ResponseCustomerListPagination {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private CustomerPagination data;

    @SerializedName("message")
    private String message;

    public ResponseCustomerListPagination() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CustomerPagination getData() {
        return data;
    }

    public void setData(CustomerPagination data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

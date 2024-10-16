package com.ppproperti.simarco.responses;

import com.google.gson.annotations.SerializedName;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.OrderPagination;

import java.util.List;

public class ResponseOrderListPagination {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private OrderPagination data;

    @SerializedName("message")
    private String message;

    public ResponseOrderListPagination() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OrderPagination getData() {
        return data;
    }

    public void setData(OrderPagination data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

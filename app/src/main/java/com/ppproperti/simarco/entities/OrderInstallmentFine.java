package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OrderInstallmentFine implements Parcelable {
    private int id;
    private String description;
    private int number;
    private String name;
    private String status;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("payment_date")
    private String paymentDate;
    private double price;
    private double fine;

    @SerializedName("order_installment_id")
    private int orderInstallmentId;
    private OrderInstallment orderInstallment;

    public OrderInstallmentFine() {
    }

    protected OrderInstallmentFine(Parcel in) {
        id = in.readInt();
        description = in.readString();
        number = in.readInt();
        name = in.readString();
        status = in.readString();
        dueDate = in.readString();
        paymentDate = in.readString();
        price = in.readDouble();
        fine = in.readDouble();
        orderInstallmentId = in.readInt();
        orderInstallment = in.readParcelable(OrderInstallment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
        dest.writeInt(number);
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(dueDate);
        dest.writeString(paymentDate);
        dest.writeDouble(price);
        dest.writeDouble(fine);
        dest.writeInt(orderInstallmentId);
        dest.writeParcelable(orderInstallment, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInstallmentFine> CREATOR = new Creator<OrderInstallmentFine>() {
        @Override
        public OrderInstallmentFine createFromParcel(Parcel in) {
            return new OrderInstallmentFine(in);
        }

        @Override
        public OrderInstallmentFine[] newArray(int size) {
            return new OrderInstallmentFine[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public int getOrderInstallmentId() {
        return orderInstallmentId;
    }

    public void setOrderInstallmentId(int orderInstallmentId) {
        this.orderInstallmentId = orderInstallmentId;
    }

    public OrderInstallment getOrderInstallment() {
        return orderInstallment;
    }

    public void setOrderInstallment(OrderInstallment orderInstallment) {
        this.orderInstallment = orderInstallment;
    }
}

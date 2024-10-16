package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OrderInstallmentPayment implements Parcelable {
    private int id;
    private String name;
    private String description;
    private int number;
    private String status;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("payment_to")
    private String paymentTo;

    @SerializedName("payment_date")
    private String paymentDate;
    private double price;
    private double fine;

    @SerializedName("payment_schema_id")
    private int paymentSchemaId;
    private PaymentSchema paymentSchema;

    @SerializedName("order_installment_id")
    private int orderInstallmentId;
    private OrderInstallment orderInstallment;

    @SerializedName("order_installment_fine_id")
    private int orderInstallmentFineId;
    private OrderInstallmentFine orderInstallmentFine;

    public OrderInstallmentPayment() {
    }

    protected OrderInstallmentPayment(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        number = in.readInt();
        status = in.readString();
        dueDate = in.readString();
        paymentTo = in.readString();
        paymentDate = in.readString();
        price = in.readDouble();
        fine = in.readDouble();
        paymentSchemaId = in.readInt();
        paymentSchema = in.readParcelable(PaymentSchema.class.getClassLoader());
        orderInstallmentId = in.readInt();
        orderInstallment = in.readParcelable(OrderInstallment.class.getClassLoader());
        orderInstallmentFineId = in.readInt();
        orderInstallmentFine = in.readParcelable(OrderInstallmentFine.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(number);
        dest.writeString(status);
        dest.writeString(dueDate);
        dest.writeString(paymentTo);
        dest.writeString(paymentDate);
        dest.writeDouble(price);
        dest.writeDouble(fine);
        dest.writeInt(paymentSchemaId);
        dest.writeParcelable(paymentSchema, flags);
        dest.writeInt(orderInstallmentId);
        dest.writeParcelable(orderInstallment, flags);
        dest.writeInt(orderInstallmentFineId);
        dest.writeParcelable(orderInstallmentFine, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInstallmentPayment> CREATOR = new Creator<OrderInstallmentPayment>() {
        @Override
        public OrderInstallmentPayment createFromParcel(Parcel in) {
            return new OrderInstallmentPayment(in);
        }

        @Override
        public OrderInstallmentPayment[] newArray(int size) {
            return new OrderInstallmentPayment[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPaymentTo() {
        return paymentTo;
    }

    public void setPaymentTo(String paymentTo) {
        this.paymentTo = paymentTo;
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

    public int getPaymentSchemaId() {
        return paymentSchemaId;
    }

    public void setPaymentSchemaId(int paymentSchemaId) {
        this.paymentSchemaId = paymentSchemaId;
    }

    public PaymentSchema getPaymentSchema() {
        return paymentSchema;
    }

    public void setPaymentSchema(PaymentSchema paymentSchema) {
        this.paymentSchema = paymentSchema;
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

    public int getOrderInstallmentFineId() {
        return orderInstallmentFineId;
    }

    public void setOrderInstallmentFineId(int orderInstallmentFineId) {
        this.orderInstallmentFineId = orderInstallmentFineId;
    }

    public OrderInstallmentFine getOrderInstallmentFine() {
        return orderInstallmentFine;
    }

    public void setOrderInstallmentFine(OrderInstallmentFine orderInstallmentFine) {
        this.orderInstallmentFine = orderInstallmentFine;
    }
}

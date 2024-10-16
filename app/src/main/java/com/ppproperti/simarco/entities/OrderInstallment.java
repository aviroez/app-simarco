package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OrderInstallment implements Parcelable {
    private int id;
    private String description;
    private int number;
    private String status;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("payment_date")
    private String paymentDate;
    private double price;
    private double fine;

    @SerializedName("payment_nominal")
    private double paymentNominal;

    @SerializedName("payment_schema_id")
    private int paymentSchemaId;
    private PaymentSchema paymentSchema;

    @SerializedName("order_id")
    private int orderId;
    private Order order;

    public OrderInstallment() {
    }

    protected OrderInstallment(Parcel in) {
        id = in.readInt();
        description = in.readString();
        number = in.readInt();
        status = in.readString();
        dueDate = in.readString();
        paymentDate = in.readString();
        price = in.readDouble();
        fine = in.readDouble();
        paymentNominal = in.readDouble();
        paymentSchemaId = in.readInt();
        paymentSchema = in.readParcelable(PaymentSchema.class.getClassLoader());
        orderId = in.readInt();
        order = in.readParcelable(Order.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
        dest.writeInt(number);
        dest.writeString(status);
        dest.writeString(dueDate);
        dest.writeString(paymentDate);
        dest.writeDouble(price);
        dest.writeDouble(fine);
        dest.writeDouble(paymentNominal);
        dest.writeInt(paymentSchemaId);
        dest.writeParcelable(paymentSchema, flags);
        dest.writeInt(orderId);
        dest.writeParcelable(order, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInstallment> CREATOR = new Creator<OrderInstallment>() {
        @Override
        public OrderInstallment createFromParcel(Parcel in) {
            return new OrderInstallment(in);
        }

        @Override
        public OrderInstallment[] newArray(int size) {
            return new OrderInstallment[size];
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

    public double getPaymentNominal() {
        return paymentNominal;
    }

    public void setPaymentNominal(double paymentNominal) {
        this.paymentNominal = paymentNominal;
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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

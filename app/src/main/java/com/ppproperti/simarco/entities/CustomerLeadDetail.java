package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CustomerLeadDetail implements Parcelable {
    private int id;

    @SerializedName("customer_lead_id")
    private int customerLeadId;

    private String classification;
    private String response;
    private String type;
    private String description;
    private String product;
    private double price;
    private String unit;

    @SerializedName("buy_date")
    private String buyDate;

    @SerializedName("customer_lead")
    private CustomerLead customerLead = new CustomerLead();

    public CustomerLeadDetail() {
    }

    protected CustomerLeadDetail(Parcel in) {
        id = in.readInt();
        customerLeadId = in.readInt();
        classification = in.readString();
        response = in.readString();
        type = in.readString();
        description = in.readString();
        product = in.readString();
        price = in.readDouble();
        unit = in.readString();
        buyDate = in.readString();
        customerLead = in.readParcelable(CustomerLead.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(customerLeadId);
        dest.writeString(classification);
        dest.writeString(response);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeString(product);
        dest.writeDouble(price);
        dest.writeString(unit);
        dest.writeString(buyDate);
        dest.writeParcelable(customerLead, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomerLeadDetail> CREATOR = new Creator<CustomerLeadDetail>() {
        @Override
        public CustomerLeadDetail createFromParcel(Parcel in) {
            return new CustomerLeadDetail(in);
        }

        @Override
        public CustomerLeadDetail[] newArray(int size) {
            return new CustomerLeadDetail[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerLeadId() {
        return customerLeadId;
    }

    public void setCustomerLeadId(int customerLeadId) {
        this.customerLeadId = customerLeadId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public CustomerLead getCustomerLead() {
        return customerLead;
    }

    public void setCustomerLead(CustomerLead customerLead) {
        this.customerLead = customerLead;
    }
}

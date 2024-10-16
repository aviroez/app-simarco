package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CustomerLeadJoin implements Parcelable {
    private int customerId;
    private int customerLeadId;
    private String name;
    private Customer customer = new Customer();
    private CustomerLead customerLead = new CustomerLead();

    public CustomerLeadJoin() {
    }

    protected CustomerLeadJoin(Parcel in) {
        customerId = in.readInt();
        customerLeadId = in.readInt();
        name = in.readString();
        customer = in.readParcelable(Customer.class.getClassLoader());
        customerLead = in.readParcelable(CustomerLead.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(customerId);
        dest.writeInt(customerLeadId);
        dest.writeString(name);
        dest.writeParcelable(customer, flags);
        dest.writeParcelable(customerLead, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomerLeadJoin> CREATOR = new Creator<CustomerLeadJoin>() {
        @Override
        public CustomerLeadJoin createFromParcel(Parcel in) {
            return new CustomerLeadJoin(in);
        }

        @Override
        public CustomerLeadJoin[] newArray(int size) {
            return new CustomerLeadJoin[size];
        }
    };

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerLeadId() {
        return customerLeadId;
    }

    public void setCustomerLeadId(int customerLeadId) {
        this.customerLeadId = customerLeadId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerLead getCustomerLead() {
        return customerLead;
    }

    public void setCustomerLead(CustomerLead customerLead) {
        this.customerLead = customerLead;
    }
}
